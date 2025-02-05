package com.crewmeister.cmcodingchallenge.bootsrap;

import com.crewmeister.cmcodingchallenge.currency.model.Currency;
import com.crewmeister.cmcodingchallenge.currency.repository.CurrencyRepository;
import com.crewmeister.cmcodingchallenge.exchangerate.model.ExchangeRate;
import com.crewmeister.cmcodingchallenge.exchangerate.repository.ExchangeRateRepository;
import com.crewmeister.cmcodingchallenge.exchangerate.service.ExchangeRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateService exchangeRateService;

    public DataLoader(CurrencyRepository currencyRepository, ExchangeRateRepository exchangeRateRepository, ExchangeRateService exchangeRateService) {
        this.currencyRepository = currencyRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.exchangeRateService = exchangeRateService;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting data loading process...");
        try {
            // Load all CSV files under the "datasets" folder in the classpath
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath:datasets/*.csv");
            for (Resource resource : resources) {
                processCsvFile(resource);
            }
            // Update from the Public Service
            exchangeRateService.updateExchangeRates();
        } catch (Exception e) {
            logger.error("Failed to load CSV files", e);
        }
    }

    /**
     * Processes a single CSV file.
     */
    private void processCsvFile(Resource resource) {
        logger.info("Processing file: {}", resource.getFilename());
        String currencyCode = null;
        String currencyDesc = null;
        String lastUpdatedValue = null;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean processingRates = false;
            while ((line = reader.readLine()) != null) {
                // handle metadata lines
                if (!processingRates) {
                    // Look for the "last update" line
                    if (line.trim().toLowerCase().startsWith("last update")) {
                        String[] tokens = line.split(",");
                        if (tokens.length >= 2) {
                            lastUpdatedValue = tokens[1].trim();
                            logger.debug("Extracted last update: {}", lastUpdatedValue);
                        }
                        continue;
                    }

                    // check if the current line contains the expected metadata (e.g. "EUR 1 =")
                    if ((currencyCode == null || currencyDesc == null) && line.contains("EUR 1 =")) {
                        currencyCode = extractCurrencyCode(line);
                        currencyDesc = extractCurrencyDescription(line);
                        continue;
                    }

                    // Check if this line is the first rate line (starts with a date)
                    if (line.matches("^\\d{4}-\\d{2}-\\d{2}.*")) {
                        processingRates = true;
                        processRateLine(line, currencyCode, currencyDesc, lastUpdatedValue);
                        continue;
                    }
                } else {
                    processRateLine(line, currencyCode, currencyDesc, lastUpdatedValue);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing file {}: {}", resource.getFilename(), e.getMessage(), e);
        }
    }


    /**
     * Extracts the currency code from a metadata line.
     * For example, if the line contains "EUR 1 = AUD", this method returns "AUD".
     */
    private String extractCurrencyCode(String line) {
        int idx = line.indexOf("EUR 1 =");
        if (idx > -1) {
            String after = line.substring(idx + 7).trim(); // "AUD ... / Australia"
            String[] tokens = after.split("\\s+");
            String code = tokens[0].replaceAll("[^A-Za-z]", "");
            return code;
        }
        return "UNKNOWN";
    }

    /**
     * Extracts a simple description (e.g. country name) from a metadata line.
     * <p>
     * The method first attempts to use a regular expression that looks for the ellipsis ("...")
     * followed by a slash and then captures the next token (excluding any commas or extra characters).
     * If that fails (for example, if the expected pattern isn’t found), it splits the line by '/'
     * and looks for the token immediately following the token that contains "...".
     * </p>
     *
     * For example, given:
     * <br>
     * "Euro foreign exchange reference rate of the ECB / EUR 1 = LTL ... / Lithuania / up to the"
     * <br>
     * this method will return "Lithuania" (without any trailing comma).
     *
     * @param line the metadata line to parse
     * @return the extracted currency description, or null if none is found
     */
    private String extractCurrencyDescription(String line) {
        Pattern pattern = Pattern.compile("EUR 1 =\\s*\\w+\\s*\\.\\.\\.\\s*/\\s*(.*)$");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String desc = matcher.group(1).trim();
            // Optionally remove trailing commas
            desc = desc.replaceAll("[,]+$", "").trim();
            return desc;
        }

        // Fallback: split by "/" and look for the token immediately following the token that contains "..."
        String[] tokens = line.split("/");
        for (int i = 0; i < tokens.length - 1; i++) {
            if (tokens[i].contains("...")) {
                String desc = tokens[i + 1].trim();
                // Remove any trailing commas from the description
                desc = desc.replaceAll("[,]+$", "").trim();
                return desc;
            }
        }

        return null;
    }


    /**
     * Processes a single line of exchange rate data.
     */
    private void processRateLine(String line, String currencyCode, String currencyDesc, String lastUpdatedValue) {
        String[] fields = line.split(",");
        if (fields.length < 2) {
            return; // Malformed line, skip it.
        }

        String dateStr = fields[0].trim();
        if (!dateStr.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            return; // Not a valid date line.
        }

        LocalDate date = LocalDate.parse(dateStr);
        String rateStr = fields[1].trim();
        if (rateStr.equals(".") || rateStr.isEmpty()) {
            return; // No valid rate data.
        }

        BigDecimal rateValue = new BigDecimal(rateStr);
        Currency currency = findOrCreateCurrency(currencyCode, currencyDesc, lastUpdatedValue);

        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setCurrency(currency);
        exchangeRate.setRateDate(date);
        exchangeRate.setRate(rateValue);

        exchangeRateRepository.save(exchangeRate);
    }

    /**
     * Finds an existing Currency or creates a new one if not found.
     */
    private Currency findOrCreateCurrency(String code, String description, String lastUpdated) {
        Optional<Currency> optionalCurrency = currencyRepository.findById(code);
        Currency currency;
        if (optionalCurrency.isPresent()) {
            currency = optionalCurrency.get();
        } else {
            currency = new Currency();
            currency.setCode(code);
            currency.setDescription(description);
        }
        // Set or update the lastUpdated field for this currency
        currency.setLastUpdated(lastUpdated);
        return currencyRepository.save(currency);
    }

}

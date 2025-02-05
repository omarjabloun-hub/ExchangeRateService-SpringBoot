package com.crewmeister.cmcodingchallenge;

import com.crewmeister.cmcodingchallenge.conversion.dto.ConversionDto;
import com.crewmeister.cmcodingchallenge.conversion.service.ConversionServiceImpl;
import com.crewmeister.cmcodingchallenge.currency.model.Currency;
import com.crewmeister.cmcodingchallenge.exchangerate.model.ExchangeRate;
import com.crewmeister.cmcodingchallenge.exchangerate.repository.ExchangeRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CmCodingChallengeApplicationTests {
	private static final Logger logger = LoggerFactory.getLogger(CmCodingChallengeApplicationTests.class);

	@Mock
	private ExchangeRateRepository exchangeRateRepository;

	@InjectMocks
	private ConversionServiceImpl conversionService;

	@Test
	public void testConvert() {
		logger.info("Starting testConvert");
		// Arrange: Set up our test data.
		String currencyCode = "USD";
		LocalDate date = LocalDate.of(2025, 2, 4);
		BigDecimal originalAmount = new BigDecimal("100");
		BigDecimal exchangeRateValue = new BigDecimal("1.0335");

		Currency currency = new Currency();
		currency.setCode(currencyCode);

		ExchangeRate exchangeRate = new ExchangeRate(currency, date, exchangeRateValue);

		// Stub the repository method to return a singleton list containing our exchange rate.
		when(exchangeRateRepository.findExchangeRateByCurrency_CodeAndRateDate(currencyCode, date))
				.thenReturn(Collections.singletonList(exchangeRate));

		// Act: Call the convert method.
		ConversionDto conversionDto = conversionService.convert(currencyCode, date, originalAmount);
		logger.info("Conversion result: currency: {}, date: {}, originalAmount: {}, exchangeRate: {}, convertedAmount: {}",
				conversionDto.getCurrency(),
				conversionDto.getDate(),
				conversionDto.getOriginalAmount(),
				conversionDto.getExchangeRate(),
				conversionDto.getConvertedAmount());

		// Assert: Verify that the returned ConversionDto contains the expected values.
		assertEquals(currencyCode, conversionDto.getCurrency());
		assertEquals(date, conversionDto.getDate());
		assertEquals(originalAmount, conversionDto.getOriginalAmount());
		assertEquals(exchangeRateValue, conversionDto.getExchangeRate());
		assertEquals(new BigDecimal("96.76"), conversionDto.getConvertedAmount());
		logger.info("testConvert completed successfully");
	}
}

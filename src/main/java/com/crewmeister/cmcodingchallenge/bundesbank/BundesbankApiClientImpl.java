package com.crewmeister.cmcodingchallenge.bundesbank;


import com.crewmeister.cmcodingchallenge.exchangerate.dto.ExchangeRateDto;
import com.crewmeister.cmcodingchallenge.util.DocumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class BundesbankApiClientImpl implements BundesbankApiClient {
    @Value("${bundesbank.api.url}")
    private String bundesbankApiUrl;

    private final Logger logger = LoggerFactory.getLogger(BundesbankApiClientImpl.class);
    private final MessageSource messageSource;
    private final Locale locale = LocaleContextHolder.getLocale();

    public BundesbankApiClientImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String getExchangeRateURL(String currencyCode, String startPeriod, String endPeriod) {
        return bundesbankApiUrl
                + "D." + currencyCode + ".EUR.BB.AC.000"
                + "?format=xml"
                + "&startPeriod=" + startPeriod
                + "&endPeriod=" + endPeriod
                + "&detail=dataonly";
    }

    @Cacheable(value = "bundesbankData", key = "{#currencyCode, #startPeriod, #endPeriod}")
    @Override
    public List<ExchangeRateDto> fetchExchangeRates(String currencyCode, String startPeriod, String endPeriod) {
        List<ExchangeRateDto> exchangeRateDTOList = new ArrayList<>();
        try {
            final Document document = DocumentUtil.getDocumentFromRestAsXml(getExchangeRateURL(currencyCode, startPeriod, endPeriod));
            final NodeList nodeList = document.getElementsByTagName("generic:Obs");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final NodeList valuesNodeList = nodeList.item(i).getChildNodes();
                String date = "";
                String value = "";
                for (int j = 0; j < valuesNodeList.getLength(); j++) {
                    if (valuesNodeList.item(j).getNodeName().equalsIgnoreCase("generic:ObsDimension")) {
                        date = valuesNodeList.item(j).getAttributes().getNamedItem("value").getNodeValue();
                    }
                    if (valuesNodeList.item(j).getNodeName().equalsIgnoreCase("generic:ObsValue")) {
                        value = valuesNodeList.item(j).getAttributes().getNamedItem("value").getNodeValue();
                    }
                }
                if (value != null && !value.isEmpty()) {
                    exchangeRateDTOList.add(new ExchangeRateDto(currencyCode, date, value));
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            logger.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, messageSource.getMessage("CannotParseXml", null, locale));
        } catch (HttpClientErrorException ex) {
            logger.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, messageSource.getMessage("CannotGetDataFromService", null, locale));
        }
        return exchangeRateDTOList;
    }

}

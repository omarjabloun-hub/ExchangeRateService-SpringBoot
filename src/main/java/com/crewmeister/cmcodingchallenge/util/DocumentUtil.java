package com.crewmeister.cmcodingchallenge.util;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DocumentUtil {

    public static Document getDocumentFromRestAsXml(String ur) throws ParserConfigurationException, IOException, SAXException {
        URL url = new URL(ur);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/xml");
        InputStreamReader in = new InputStreamReader(conn.getInputStream());
        BufferedReader br = new BufferedReader(in);
        String output;
        String xmlFile = "";
        while ((output = br.readLine()) != null) {
            xmlFile = xmlFile + output;
        }
        conn.disconnect();

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final InputSource is = new InputSource(new StringReader(xmlFile));
        return builder.parse(is);

    }
}
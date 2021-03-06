/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.sruimport;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.kitodo.api.externaldatamanagement.Record;
import org.kitodo.api.externaldatamanagement.SearchResult;
import org.kitodo.exceptions.ConfigException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class ResponseHandler {

    private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private static XMLOutputter xmlOutputter = new XMLOutputter();

    static {
        documentBuilderFactory.setNamespaceAware(true);
        xmlOutputter.setFormat(Format.getPrettyFormat());
    }

    private static final String MODS_NAMESPACE = "http://www.loc.gov/mods/v3";
    private static final String SRW_NAMESPACE = "http://www.loc.gov/zing/srw/";
    private static final String SRW_RECORD_TAG = "record";
    private static final String SRW_NUMBER_OF_RECORDS_TAG = "numberOfRecords";
    private static final String MODS_TAG = "mods";
    private static final String RECORD_ID_TAG = "recordIdentifier";
    private static final String RECORD_TITLE_TAG = "title";

    /**
     * Private constructor.
     */
    private ResponseHandler() {
    }

    /**
     * Create and return SearchResult for given HttpResponse.
     * @param response HttpResponse for which a SearchResult is created
     * @return SearchResult created from given HttpResponse
     */
    static SearchResult getSearchResult(HttpResponse response) {
        SearchResult searchResult = new SearchResult();
        Document resultDocument = transformResponseToDocument(response);
        if (Objects.nonNull(resultDocument)) {
            searchResult.setHits(extractHits(resultDocument));
            searchResult.setNumberOfRecords(extractNumberOfRecords(resultDocument));
        }
        return searchResult;
    }

    /**
     * Transform given HttpResponse into Document and return it.
     * @param response HttpResponse that is transformed into a Document
     * @return Document into which given HttpResponse has been transformed
     */
    static Document transformResponseToDocument(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        if (Objects.nonNull(entity)) {
            try {
                return parseXML(entity.getContent());
            } catch (IOException e) {
                throw new ConfigException(e.getMessage());
            }
        }
        throw new ConfigException("SRU response is null");
    }

    private static Document parseXML(InputStream xmlSteam) {
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            return documentBuilder.parse(new InputSource(xmlSteam));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new ConfigException(e.getMessage());
        }
    }

    private static int extractNumberOfRecords(Document document) {
        NodeList numberOfRecordNodes = document.getElementsByTagNameNS(SRW_NAMESPACE, SRW_NUMBER_OF_RECORDS_TAG);
        assert numberOfRecordNodes.getLength() == 1;
        Element numberOfRecordsElement = (Element) numberOfRecordNodes.item(0);
        if (Objects.nonNull(numberOfRecordsElement)) {
            return Integer.parseInt(numberOfRecordsElement.getTextContent().trim());
        }
        return 0;
    }

    private static LinkedList<Record> extractHits(Document document) {
        LinkedList<Record> hits = new LinkedList<>();
        NodeList records = document.getElementsByTagNameNS(SRW_NAMESPACE, SRW_RECORD_TAG);
        for (int i = 0; i < records.getLength(); i++) {
            Element recordElement = (Element) records.item(i);
            hits.add(new Record(getRecordTitle(recordElement), getRecordID(recordElement)));
        }
        return hits;
    }

    private static String getRecordID(Element record) {
        Element recordIdentifier = getXmlElement(record, RECORD_ID_TAG);
        return recordIdentifier.getTextContent().trim();
    }

    private static String getRecordTitle(Element record) {
        Element modsElement = getXmlElement(record, MODS_TAG);
        Element recordTitle = getXmlElement(modsElement, RECORD_TITLE_TAG);
        return recordTitle.getTextContent().trim();
    }

    private static Element getXmlElement(Element parentNode, String elementTag) {
        NodeList nodeList = parentNode.getElementsByTagNameNS(ResponseHandler.MODS_NAMESPACE, elementTag);
        return (Element) nodeList.item(0);
    }
}

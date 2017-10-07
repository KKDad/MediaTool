package org.westfield.media;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;


public class MediaInfoParser
{
    private static final Logger logger = LoggerFactory.getLogger(MediaInfoParser.class);

    private Document xmlDocument;
    private final File file;

    private static final String TRACKS_SELECTOR = "/Mediainfo/File/track";   // /Mediainfo/File/track[@type='General']


    public static MediaInfoParser get(Path item) {
        Preconditions.checkNotNull(item);
        File file = item.toFile();
        Preconditions.checkArgument(file.exists() && !file.isDirectory());
        MediaInfoParser mi = new MediaInfoParser(file);
        if (mi.parse())
            return mi;
        return null;
    }

    private MediaInfoParser(File theFile)
    {
        this.file = theFile;
    }

    private boolean parse()
    {
        try {
            logger.info("Parsing : {}", file.getName());
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            this.xmlDocument = documentBuilder.parse(file);
            logger.info("Parsed : {}", file.getName());
            return true;

        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("Caught Exception: {}", e.getMessage(), e);
        }
        return false;
    }

    private static Object executeXpathExpression(Document xmlDocument, String expression, XPath xPath, QName returnType)
    {
        Object result = null;
        try {
            XPathExpression xPathExpression = xPath.compile(expression);
            result = xPathExpression.evaluate(xmlDocument, returnType);

        } catch (XPathExpressionException e) {
            logger.error("XPathExpressionException: {}", e.getMessage(), e);
        }
        return result;
    }



//            XPathFactory factory = XPathFactory.newInstance();
//            XPath xPath = factory.newXPath();
//
//            String selectFirstNode = "/Mediainfo/track[@type='General']/Complete_name";
//            Object result = executeXpathExpression(xmlDocument, selectFirstNode, xPath, XPathConstants.NODE);
//            Node firstNode = (Node) result;
//            String nodeName = firstNode.getNodeName();

}

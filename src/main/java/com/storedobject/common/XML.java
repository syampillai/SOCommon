/*
 * Copyright 2018 Syam Pillai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.storedobject.common;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple XML utility for XPath element extraction.
 *
 * @author Syam
 */
public class XML {

    private DocumentBuilder documentBuilder;
    private Document document;
    private XPath xPath;
    private XPathExpression xPathExpression;

    public XML() {
    }

    public XML(String xml) throws Exception {
        set(xml);
    }

    public XML(InputStream stream) throws Exception {
        set(stream);
    }

    public XML(Reader reader) throws Exception {
        set(reader);
    }

    public XML(URL url) throws Exception {
        set(url);
    }

    public XML(Document document) throws Exception {
        set(document);
    }

    private void init() throws Exception {
        if(documentBuilder != null) {
            return;
        }
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        customizeBuilderFactory(documentBuilderFactory);
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        customizeBuilder(documentBuilder);
        XPathFactory xPathFactory = XPathFactory.newInstance();
        customizePathFactory(xPathFactory);
        xPath = xPathFactory.newXPath();
    }

    protected void customizeBuilderFactory(@SuppressWarnings("unused") DocumentBuilderFactory documentBuilderFactory) {
    }

    protected void customizeBuilder(@SuppressWarnings("unused") DocumentBuilder documentBuilder) {
    }

    protected void customizePathFactory(@SuppressWarnings("unused") XPathFactory pathFactory) {
    }

    public void ignoreDTDs() {
        try {
            init();
        } catch (Exception e) {
            return;
        }
        documentBuilder.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));
    }

    public XML copy() throws Exception {
        return new XML(toString());
    }

    public void set(Document document) throws Exception {
        init();
        this.document = document;
        setNamespace();
    }

    public void set(String xml) throws Exception {
        if(xml == null) {
            return;
        }
        set(new StringReader(xml));
    }

    public void set(InputStream stream) throws Exception {
        if(stream == null) {
            return;
        }
        set(IO.getReader(stream));
    }

    public void set(Reader reader) throws Exception {
        if(reader == null) {
            return;
        }
        init();
        InputSource source = new InputSource(reader);
        document = documentBuilder.parse(source);
        try {
            reader.close();
        } catch(Exception ignore) {
        }
        setNamespace();
    }

    private void setNamespace() {
        Node root = document.getDocumentElement();
        NamedNodeMap rootList = root.getAttributes();
        HashMap<String, String> nsMap = new HashMap<>();
        int i = 0;
        String ns;
        while(true) {
            root = rootList.item(i++);
            if(root == null) {
                break;
            }
            if(root.getNodeType() != Node.ATTRIBUTE_NODE) {
                continue;
            }
            ns = root.getNodeName();
            if(!ns.startsWith("xmlns:")) {
                continue;
            }
            nsMap.put(ns.substring(6), root.getNodeValue());
        }
        xPath.setNamespaceContext(new SimpleNamespaceContext(nsMap));
    }

    public void set(URL url) throws Exception {
        if(url == null) {
            return;
        }
        HTTP http = new HTTP(url);
        set(http.getInputStream());
    }

    public boolean check(String xpath) throws Exception {
        return check(document, xpath);
    }

    public String get(String xpath) throws Exception {
        return get(document, xpath);
    }

    public Number getNumber(String xpath) throws Exception {
        return getNumber(document, xpath);
    }

    public ArrayList<String> list(String xpath) throws Exception {
        return list(document, xpath);
    }

    public ArrayList<Node> listNodes(String xpath) throws Exception {
        return listNodes(document, xpath);
    }

    public Node getNode(String xpath) throws Exception {
        return getNode(document, xpath);
    }

    public boolean check(Node node, String xpath) throws Exception {
        if(node != document && xpath.startsWith("/")) {
            xpath = "." + xpath;
        }
        xPathExpression = xPath.compile(xpath);
        return (Boolean) xPathExpression.evaluate(node, XPathConstants.BOOLEAN);
    }

    public String get(Node node, String xpath) throws Exception {
        if(node != document && xpath.startsWith("/")) {
            xpath = "." + xpath;
        }
        xPathExpression = xPath.compile(xpath);
        return (String) xPathExpression.evaluate(node, XPathConstants.STRING);
    }

    public Number getNumber(Node node, String xpath) throws Exception {
        if(node != document && xpath.startsWith("/")) {
            xpath = "." + xpath;
        }
        xPathExpression = xPath.compile(xpath);
        return (Number) xPathExpression.evaluate(node, XPathConstants.NUMBER);
    }

    public ArrayList<String> list(Node node, String xpath) throws Exception {
        if(node != document && xpath.startsWith("/")) {
            xpath = "." + xpath;
        }
        ArrayList<String> results = new ArrayList<>();
        xPathExpression = xPath.compile(xpath);
        NodeList nodes = (NodeList) xPathExpression.evaluate(node, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++){
            node = nodes.item(i);
            results.add(value(node));
        }
        return results;
    }

    private static String value(Node node) {
        if(node == null) {
            return "";
        }
        if(node.getNodeType() == Node.TEXT_NODE) {
            return node.getNodeValue();
        }
        return value(node.getFirstChild());
    }

    public ArrayList<Node> listNodes(Node node, String xpath) throws Exception {
        if(node != document && xpath.startsWith("/")) {
            xpath = "." + xpath;
        }
        ArrayList<Node> results = new ArrayList<>();
        xPathExpression = xPath.compile(xpath);
        NodeList nodes = (NodeList) xPathExpression.evaluate(node, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++){
            results.add(nodes.item(i));
        }
        return results;
    }

    public Node getNode(Node node, String xpath) throws Exception {
        if(node != document && xpath.startsWith("/")) {
            xpath = "." + xpath;
        }
        xPathExpression = xPath.compile(xpath);
        return (Node) xPathExpression.evaluate(node, XPathConstants.NODE);
    }

    public String toString() {
        return toString(document);
    }

    public String toString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            write(node, sw);
        } catch (Exception e) {
            sw.append("\nERROR");
        }
        return sw.toString();
    }

    public void write(OutputStream stream) throws Exception {
        write(document, stream);
    }

    public void write(Writer writer) throws Exception {
        write(document, writer);
    }

    public void write(Node node, OutputStream stream) throws Exception {
        write(new DOMSource(node), new StreamResult(IO.getWriter(stream)));
    }

    public void write(Node node, Writer writer) throws Exception {
        write(new DOMSource(node), new StreamResult(writer));
    }

    private void write(DOMSource source, StreamResult result) throws Exception {
        createTransformer().transform(source, result);
        result.getWriter().flush();
    }

    private Transformer createTransformer() throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        if (document.getDoctype() != null){
            String systemValue = (new File(document.getDoctype().getSystemId())).getName();
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemValue);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        }
        return transformer;
    }

    public Document getDocument() {
        return document;
    }

    private static class SimpleNamespaceContext implements NamespaceContext {

        private final Map<String, String> prefMap;

        public SimpleNamespaceContext(final Map<String, String> prefMap) {
            this.prefMap = prefMap;
            this.prefMap.put(XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI);
            this.prefMap.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
            this.prefMap.put(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        }

        public String getNamespaceURI(String prefix) {
            return prefMap.get(prefix);
        }

        public String getPrefix(String uri) {
            throw new UnsupportedOperationException();
        }

        public Iterator<String> getPrefixes(String uri) {
            throw new UnsupportedOperationException();
        }
    }
}
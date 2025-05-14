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
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Simple XML utility for XPath element extraction.
 *
 * @author Syam
 */
public class XML {

    private DocumentBuilder documentBuilder;
    private Document document;
    private XPath xPath;
    private final NamespaceContextMap nsMap = new NamespaceContextMap();
    private String prefix = XMLConstants.DEFAULT_NS_PREFIX;
    private boolean pack = true;

    /**
     * XML represents an XML document and provides various methods for manipulating and extracting information from the XML structure.
     */
    public XML() {
    }

    /**
     * Represents an XML document.
     *
     * @param xml The XML as a string.
     * @throws Exception If an error occurs.
     */
    public XML(String xml) throws Exception {
        set(xml);
    }

    /**
     * Creates a new XML object by setting the contents of the XML from the given input stream.
     *
     * @param stream The input stream containing the XML content.
     * @throws Exception If any exception occurs.
     */
    public XML(InputStream stream) throws Exception {
        set(stream);
    }

    /**
     * Constructs an XML object using the provided Reader.
     *
     * @param reader The Reader object to read XML from.
     * @throws Exception If any error occurs during the XML parsing.
     */
    public XML(Reader reader) throws Exception {
        set(reader);
    }

    /**
     * XML class represents an XML document.
     */
    public XML(URL url) throws Exception {
        set(url);
    }

    /**
     * XML constructor with a Document object that represents the XML structure.
     *
     * @param document The Document object representing the XML structure.
     * @throws Exception If any error occurs.
     */
    public XML(Document document) throws Exception {
        set(document);
    }

    /**
     * Initializes the XML document builder and XPath objects if they are not already initialized.
     * Throws an Exception if an error occurs during initialization.
     *
     * @throws Exception if an error occurs during initialization
     */
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

    /**
     * Customize the DocumentBuilderFactory before creating a new DocumentBuilder instance.
     *
     * @param documentBuilderFactory The DocumentBuilderFactory to customize.
     */
    protected void customizeBuilderFactory(@SuppressWarnings("unused") DocumentBuilderFactory documentBuilderFactory) {
    }

    /**
     * Customizes the DocumentBuilder by providing additional configuration options.
     *
     * @param documentBuilder The DocumentBuilder to customize.
     */
    protected void customizeBuilder(@SuppressWarnings("unused") DocumentBuilder documentBuilder) {
    }

    /**
     * Customize the XPathFactory used by the XML class.
     *
     * @param pathFactory The XPathFactory to customize.
     */
    protected void customizePathFactory(@SuppressWarnings("unused") XPathFactory pathFactory) {
    }

    /**
     * Whether to pack the XML structure by removing all the unwanted text nodes or not. The default value is
     * <code>true</code>. Packing reduces human readability.
     *
     * @param pack True/false.
     */
    public void setPack(boolean pack) {
        this.pack = pack;
    }

    /**
     * Sets the entity resolver of the document builder to ignore DTDs. This is useful when working with XML documents
     * that reference DTDs, but you want to ignore them during parsing.
     */
    public void ignoreDTDs() {
        try {
            init();
        } catch (Exception e) {
            return;
        }
        documentBuilder.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));
    }

    /**
     * Creates a new XML object with the same content as the current object.
     *
     * @return The new XML object.
     * @throws Exception If an error occurs.
     */
    public XML copy() throws Exception {
        return new XML(toString());
    }

    /**
     * Sets the Document object representing the XML structure.
     *
     * @param document The Document object representing the XML structure.
     * @throws Exception If any error occurs.
     */
    public void set(Document document) throws Exception {
        init();
        this.document = document;
        setNamespace();
    }

    /**
     * Sets the value of the object using the provided XML representation.
     *
     * @param xml  the XML representation of the object
     * @throws Exception if an error occurs during the XML parsing or object setting process
     */
    public void set(String xml) throws Exception {
        if(xml == null) {
            return;
        }
        set(new StringReader(xml));
    }

    /**
     * Sets the input stream.
     *
     * @param stream The input stream to set.
     * @throws Exception If an error occurs.
     */
    public void set(InputStream stream) throws Exception {
        if(stream == null) {
            return;
        }
        set(IO.getReader(stream));
    }

    /**
     * Sets the XML document using the provided Reader object.
     *
     * @param reader The Reader object to read XML from.
     * @throws Exception If any error occurs during the XML parsing.
     */
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

    /**
     * Sets the namespace prefix for the XML document.
     *
     * @param prefix The desired namespace prefix. If null, the default namespace prefix will be used.
     */
    public void setNamespacePrefix(String prefix) {
        if(prefix == null) {
            prefix = XMLConstants.DEFAULT_NS_PREFIX;
        }
        String current = nsMap.remove(this.prefix);
        this.prefix = prefix;
        if(current != null) {
            nsMap.put(prefix, current);
        }
    }

    /**
     * Returns the namespace prefix associated with the given URI.
     *
     * @param uri The URI to get the prefix for.
     * @return The namespace prefix for the given URI, or null if not found.
     */
    public String getNamespacePrefix(String uri) {
        return nsMap.getPrefix(uri);
    }

    /**
     * Retrieves the namespace URI associated with the given prefix.
     *
     * @param prefix The prefix to retrieve the namespace URI for.
     * @return The namespace URI for the given prefix.
     */
    public String getNamespaceURI(String prefix) {
        return nsMap.getNamespaceURI(prefix);
    }

    private void setNamespace() {
        if(pack) {
            document.getDocumentElement().normalize();
            clean(document);
        }
        setNamespace(document.getDocumentElement());
        xPath.setNamespaceContext(nsMap);
    }

    private void setNamespace(final Node node) {
        if(node == null) {
            return;
        }
        Node curr = node;
        NamedNodeMap attributes = curr.getAttributes();
        if(attributes == null) {
            return;
        }
        int i = 0;
        String ns;
        while(true) {
            curr = attributes.item(i++);
            if(curr == null) {
                break;
            }
            if(curr.getNodeType() != Node.ATTRIBUTE_NODE) {
                continue;
            }
            ns = curr.getNodeName();
            if(ns.equals("xmlns")) {
                nsMap.put(prefix, curr.getNodeValue());
            } else if(ns.startsWith("xmlns:")) {
                nsMap.put(ns.substring(6), curr.getNodeValue());
            }
        }
        NodeList nodes = node.getChildNodes();
        i = 0;
        while(true) {
            curr = nodes.item(i++);
            if(curr == null) {
                return;
            }
            setNamespace(curr);
        }
    }

    private static void clean(Node node) {
        NodeList children = node.getChildNodes();
        for (int n = children.getLength() - 1; n >= 0; n--) {
            Node child = children.item(n);
            short nodeType = child.getNodeType();
            if (nodeType == Node.ELEMENT_NODE) {
                clean(child);
            } else if (nodeType == Node.TEXT_NODE) {
                if (child.getNodeValue().trim().isEmpty()){
                    node.removeChild(child);
                }
            } else if (nodeType == Node.COMMENT_NODE) {
                node.removeChild(child);
            }
        }
    }

    /**
     * Sets the input stream using the given URL.
     *
     * @param url the URL to set the input stream from
     * @throws Exception if an error occurs while setting the input stream
     */
    public void set(URL url) throws Exception {
        if(url == null) {
            return;
        }
        HTTP http = new HTTP(url);
        set(http.getInputStream());
    }

    /**
     * Returns a list of strings matching the given XPath expression.
     *
     * @param xpath the XPath expression to evaluate
     * @return an ArrayList of strings that match the given XPath expression
     * @throws Exception if an error occurs while evaluating the XPath expression
     */
    public ArrayList<String> list(String xpath) throws Exception {
        return list(document, xpath);
    }

    /**
     * Retrieves a list of nodes from the XML document that match a given XPath expression.
     *
     * @param xpath The XPath expression to match nodes in the XML document.
     * @return An ArrayList of nodes that match the given XPath expression.
     * @throws Exception If an error occurs while retrieving the nodes.
     */
    public ArrayList<Node> listNodes(String xpath) throws Exception {
        return listNodes(document, xpath);
    }

    /**
     * Retrieves the Node object corresponding to the given XPath expression.
     *
     * @param xpath the XPath expression to search for the Node object
     * @return the Node object matching the XPath expression
     * @throws Exception if there is an error while retrieving the Node object
     */
    public Node getNode(String xpath) throws Exception {
        return getNode(document, xpath);
    }

    /**
     * Extract the boolean value of a path.
     *
     * @param xpath XPath.
     * @return Value.
     * @throws Exception If any error occurs.
     */
    public boolean check(String xpath) throws Exception {
        return check(document, xpath);
    }

    /**
     * Extract the text value of a path.
     *
     * @param xpath XPath.
     * @return Value.
     * @throws Exception If any error occurs.
     * @deprecated Use {@link #getText(String)} instead.
     */
    @Deprecated
    public String get(String xpath) throws Exception {
        return getText(xpath);
    }

    /**
     * Extract the text value of a path.
     *
     * @param xpath XPath.
     * @return Value.
     * @throws Exception If any error occurs.
     */
    public String getText(String xpath) throws Exception {
        return getText(document, xpath);
    }

    /**
     * Extract the numeric value of a path.
     *
     * @param xpath XPath.
     * @return Value.
     * @throws Exception If any error occurs.
     */
    public Number getNumber(String xpath) throws Exception {
        return getNumber(document, xpath);
    }

    /**
     * Extract the boolean value of a path.
     *
     * @param node Node.
     * @param xpath XPath.
     * @return Value.
     * @throws Exception If any error occurs.
     */
    public boolean check(Node node, String xpath) throws Exception {
        if(node != document && xpath.startsWith("/")) {
            xpath = "." + xpath;
        }
        return (Boolean) xPath.evaluate(xpath, node, XPathConstants.BOOLEAN);
    }

    /**
     * Set a text value to the given node if it is a "text node". If the node is not a "text node", the value will
     * be set to its first child if that is a "text node". Else, it will recursively go down until a "text node" is
     * found. Please note that the recursive traversal happens only through the first children.
     *
     * @param xpathToNode XPath's expression to reach the node.
     * @param value Value to set.
     * @return True if the value was successfully set. Otherwise, false.
     */
    public boolean setText(String xpathToNode, String value) {
        try {
            return setText(getNode(xpathToNode), value);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Set a text value to the given node if it is a "text node". If the node is not a "text node", the value will
     * be set to its first child if that is a "text node". Else, it will recursively go down until a "text node" is
     * found. Please note that the recursive traversal happens only through the first children.
     *
     * @param node Node for which value to be set.
     * @param value Value to set.
     * @return True if the value was successfully set. Otherwise, false.
     */
    public boolean setText(Node node, String value) {
        if(node == null) {
            return false;
        }
        if(value == null) {
            value = "";
        }
        int type = node.getNodeType();
        if(type == Node.TEXT_NODE || type == Node.ATTRIBUTE_NODE) {
            node.setNodeValue(value);
            return value.equals(node.getNodeValue());
        }
        if(type == Node.ELEMENT_NODE) {
            NodeList nodes = node.getChildNodes();
            if(nodes.getLength() == 0) {
                node.appendChild(document.createTextNode(value));
                return true;
            }
            return setText(nodes.item(0), value);
        }
        return false;
    }

    /**
     * Extract the text value of a path under a given node.
     *
     * @param node Node.
     * @param xpath XPath.
     * @return Value.
     * @throws Exception If any error occurs.
     * @deprecated Use {@link #getText(Node, String)} instead.
     */
    @Deprecated
    public String get(Node node, String xpath) throws Exception {
        return getText(node, xpath);
    }

    /**
     * Extract the text value of a path under a given node.
     *
     * @param node Node.
     * @param xpath XPath.
     * @return Value.
     * @throws Exception If any error occurs.
     */
    public String getText(Node node, String xpath) throws Exception {
        if(node != document && xpath.startsWith("/")) {
            xpath = "." + xpath;
        }
        return (String) xPath.evaluate(xpath, node, XPathConstants.STRING);
    }

    /**
     * Extract the numeric value of a path under a given node.
     *
     * @param node Node.
     * @param xpath XPath.
     * @return Value.
     * @throws Exception If any error occurs.
     */
    public Number getNumber(Node node, String xpath) throws Exception {
        if(node != document && xpath.startsWith("/")) {
            xpath = "." + xpath;
        }
        return (Number) xPath.evaluate(xpath, node, XPathConstants.NUMBER);
    }

    /**
     * Returns a list of string values from the given XML node using the specified XPath.
     *
     * @param node The XML node to extract values from.
     * @param xpath The XPath expression to be evaluated.
     * @return An ArrayList of string values that match the given XPath expression.
     * @throws Exception If an error occurs during the XML parsing or XPath evaluation.
     */
    public ArrayList<String> list(Node node, String xpath) throws Exception {
        return listX(node, xpath, XML::value);
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

    /**
     * Returns a list of nodes matching the given XPath expression within the given node.
     *
     * @param node The node to search within.
     * @param xpath The XPath expression to match nodes.
     * @return A list of nodes matching the XPath expression.
     * @throws Exception if an error occurs during the evaluation of the XPath expression.
     */
    public ArrayList<Node> listNodes(Node node, String xpath) throws Exception {
        return listX(node, xpath, n -> n);
    }

    private <T> ArrayList<T> listX(Node node, String xpath, Function<Node, T> func) throws Exception {
        if(node != document && xpath.startsWith("/")) {
            xpath = "." + xpath;
        }
        ArrayList<T> results = new ArrayList<>();
        NodeList nodes = (NodeList) xPath.evaluate(xpath, node, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++){
            results.add(func.apply(nodes.item(i)));
        }
        return results;
    }

    /**
     * Retrieves the specific node based on the given XPath expression.
     *
     * @param node The starting node from which to search for the target node.
     * @param xpath The XPath expression used to locate the target node.
     * @return The target node if found, otherwise null.
     * @throws Exception if an error occurs during the evaluation of the XPath expression.
     */
    public Node getNode(Node node, String xpath) throws Exception {
        if(node != document && xpath.startsWith("/")) {
            xpath = "." + xpath;
        }
        return (Node) xPath.evaluate(xpath, node, XPathConstants.NODE);
    }

    /**
     * Converts the document to a formatted string representation.
     *
     * @return A string representing the document in a pretty format.
     */
    public String toPrettyString() {
        return toPrettyString(document);
    }

    /**
     * Converts the given XML {@link Node} to a pretty formatted XML string.
     *
     * @param node the XML Node to be converted
     * @return a pretty formatted XML string representation of the given Node
     */
    public String toPrettyString(Node node) {
        try {
            Transformer transformer = createTransformer(4);
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, node == document ? "no" : "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            Writer out = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(out));
            return out.toString();
        } catch (Exception e) {
            return toString();
        }
    }

    /**
     * Returns a string representation of the current object.
     *
     * @return the string representation of the current object.
     */
    public String toString() {
        return toString(document);
    }

    /**
     * Converts a Node object to its string representation.
     *
     * @param node the Node object to convert
     * @return the string representation of the Node object, or "ERROR" in case of an exception
     */
    public String toString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            write(node, sw);
        } catch (Exception e) {
            sw.append("\nERROR");
        }
        return sw.toString();
    }

    /**
     * Writes the content of the document to the specified output stream.
     *
     * @param stream the output stream to write the content to
     * @throws Exception if an error occurs while writing the content
     */
    public void write(OutputStream stream) throws Exception {
        write(document, stream);
    }

    /**
     * Write the document to the given writer.
     *
     * @param writer the writer to write the document to
     * @throws Exception if an error occurs while writing the document
     */
    public void write(Writer writer) throws Exception {
        write(document, writer);
    }

    /**
     * Writes the given XML node to the specified output stream.
     *
     * @param node the XML node to be written
     * @param stream the output stream to write the XML to
     * @throws Exception if an error occurs during the writing process
     */
    public void write(Node node, OutputStream stream) throws Exception {
        write(new DOMSource(node), new StreamResult(IO.getWriter(stream)));
    }

    /**
     * Write the XML content of a given Node to a Writer.
     *
     * @param node   the Node containing the XML content
     * @param writer the Writer to write the XML content to
     * @throws Exception if an error occurs while writing the XML content
     */
    public void write(Node node, Writer writer) throws Exception {
        write(new DOMSource(node), new StreamResult(writer));
    }

    private void write(DOMSource source, StreamResult result) throws Exception {
        createTransformer(0).transform(source, result);
        result.getWriter().flush();
    }

    private Transformer createTransformer(int intent) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        if(intent > 0) {
            transformerFactory.setAttribute("indent-number", 4);
        }
        Transformer transformer = transformerFactory.newTransformer();
        if (document.getDoctype() != null){
            String systemValue = (new File(document.getDoctype().getSystemId())).getName();
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemValue);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        }
        return transformer;
    }

    /**
     * Retrieves the document.
     *
     * @return The document object.
     */
    public Document getDocument() {
        return document;
    }

    private static class NamespaceContextMap extends HashMap<String, String> implements NamespaceContext {

        public NamespaceContextMap() {
            put(XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI);
            put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
            put(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        }

        @Override
        public String getNamespaceURI(String prefix) {
            return get(prefix);
        }

        @Override
        public String getPrefix(String uri) {
            for(String k: keySet()) {
                if(get(k).equals(uri)) {
                    return k;
                }
            }
            return null;
        }

        @Override
        public Iterator<String> getPrefixes(String uri) {
            List<String> prefixes = new ArrayList<>();
            keySet().forEach(k -> {
                if(get(k).equals(uri)) {
                    prefixes.add(k);
                }
            });
            return prefixes.iterator();
        }
    }
}
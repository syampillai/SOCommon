/*
 * Copyright (c) 2018-2023 Syam Pillai
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

import org.w3c.dom.Node;

import java.net.MalformedURLException;

/**
 * SOAP client - A simple SOAP utility for sending/receiving/manipulating SOAP messages. The default SOAP
 * version supported is "1.2".
 *
 * @author Syam
 */
public class SOAP {

    /**
     * SOAP Version
     *
     * @author Syam
     */
    public enum Version { V_1_1, V_1_2 }

    private static final String[] ENVELOPE_NS = new String[] {
            "http://schemas.xmlsoap.org/soap/envelope/",
            "http://www.w3.org/2003/05/soap-envelope",
    };
    private int version;
    private String prefixSOAP = "SOAP-Env";
    private final XML xml = new XML();
    private final HTTP http;
    private final String actionSOAP;

    /**
     * Constructor.
     *
     * @param serviceURL Service URL.
     */
    public SOAP(String serviceURL) throws MalformedURLException {
        this(serviceURL, null);
    }

    /**
     * Constructor.
     *
     * @param serviceURL Service URL.
     * @param actionSOAP SOAP action.
     */
    public SOAP(String serviceURL, String actionSOAP) throws MalformedURLException {
        this.actionSOAP = actionSOAP == null ? "" : actionSOAP;
        this.xml.setNamespacePrefix("b");
        this.http = new HTTP(serviceURL, true);
        setVersionSOAP(Version.V_1_2);
    }

    /**
     * Set the SOAP version.
     * <p>Warning: This should be set before invoking any XML related methods!</p>
     *
     * @param version Version.
     */
    public final void setVersionSOAP(Version version) {
        this.version = version == Version.V_1_1 ? 0 : 1;
        http.setContentType(this.version == 0 ? "text/xml" : "application/soap+xml");
    }

    /**
     * Get the HTTP connection associated with this SOAP.
     *
     * @return HTTP connection instance.
     */
    public HTTP getHTTP() {
        return http;
    }

    /**
     * Whether to pack the SOAP XML structure by removing all the unwanted text nodes or not. The default value is
     * <code>true</code>. Packing reduces human readability.
     *
     * @param pack True/false.
     */
    public void setPack(boolean pack) {
        xml.setPack(pack);
    }

    /**
     * Get the current "SOAP-Env" prefix.
     *
     * @return "SOAP-Env" prefix.
     */
    public final String getPrefixSOAP() {
        return prefixSOAP;
    }

    /**
     * Set the prefix to be used as the "SOAP-Env" prefix.
     *
     * @param prefixSOAP Prefix to set.
     */
    public final void setPrefixSOAP(String prefixSOAP) {
        this.prefixSOAP = prefixSOAP == null || prefixSOAP.isBlank() ? "SOAP-Env" : prefixSOAP;
    }

    /**
     * Get the XML associated with this SOAP request.
     *
     * @return XML.
     */
    public XML getXML() {
        if(xml.getDocument() == null) {
            try {
                setTemplate(null);
            } catch(Exception ignored) {
            }
        }
        return xml;
    }

    /**
     * Get a node from the SOAP Body.
     *
     * @param xpath Path. Note: If Body elements don't have a prefix, use "b:" as the prefix.
     *
     * @return Node within the SOAP Body.
     */
    public Node getNode(String xpath) throws Exception {
        return getXML().getNode(getBody(), xpath);
    }

    /**
     * Set a text value to the given path in the body.
     *
     * @param xpath Path. Note: If Body elements don't have a prefix, use "b:" as the prefix.
     * @return True if the value was successfully set. Otherwise, false.
     */
    public boolean setText(String xpath, String value) throws Exception {
        return getXML().setText(getNode(xpath), value);
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
        return getXML().setText(node, value);
    }

    /**
     * Extract the text value of a path within the SOAP Body.
     *
     * @param xpath Path. Note: If Body elements don't have a prefix, use "b:" as the prefix.
     * @return Value.
     * @throws Exception If any error occurs.
     */
    public String getText(String xpath) throws Exception {
        return getXML().getText(getBody(), xpath);
    }

    /**
     * Extract the numeric value of a path within the SOAP Body.
     *
     * @param xpath Path. Note: If Body elements don't have a prefix, use "b:" as the prefix.
     * @return Value.
     * @throws Exception If any error occurs.
     */
    public Number getNumber(String xpath) throws Exception {
        return getXML().getNumber(getBody(), xpath);
    }

    /**
     * Extract the boolean value of a path within the SOAP Body.
     *
     * @param node Node.
     * @param xpath Path. Note: If Body elements don't have a prefix, use "b:" as the prefix.
     * @return Value.
     * @throws Exception If any error occurs.
     */
    public boolean check(Node node, String xpath) throws Exception {
        return getXML().check(getBody(), xpath);
    }

    /**
     * Get the SOAP Header node.
     *
     * @return Header node.
     */
    public Node getHeader() throws Exception {
        return getNodeInt("Header");
    }

    /**
     * Get the SOAP Body node.
     *
     * @return Body node.
     */
    public Node getBody() throws Exception {
        return getNodeInt("Body");
    }

    /**
     * Get the SOAP Fault node (Available when response is received).
     *
     * @return Fault node. Null is returned if no Fault node exists.
     */
    public Node getFault() throws Exception {
        return getNodeInt("Body/" + prefixSOAP + ":Fault");
    }

    private Node getNodeInt(String node) throws Exception {
        return getXML().getNode("/" + prefixSOAP + ":Envelope/" + prefixSOAP + ":" + node);
    }

    /**
     * Set the SOAP template. This method allows you to set a template XML for creating the SOAP request.
     * <p>Note: If you don't have a template, you can just construct the XML by adding necessary sub-structures
     * to the Header and/or Body nodes obtained via {@link #getHeader()}/{@link #getBody()}.</p>
     *
     * @param templateSOAP String representing the SOAP. If it is null or empty, a minimal SOAP template is
     *                     automatically created.
     * @throws Exception If the template is not a valid SOAP XML.
     */
    public void setTemplate(String templateSOAP) throws Exception {
        if(templateSOAP == null || templateSOAP.isBlank()) {
            templateSOAP = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + "<" + prefixSOAP + ":Envelope xmlns:" +
                    prefixSOAP + "=\"" + ENVELOPE_NS[version] + "\"" +
                    " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
                    " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<" +
                    prefixSOAP + ":Header></" + prefixSOAP + ":Header>\n<" +
                    prefixSOAP + ":Body></" + prefixSOAP + ":Body>\n</" +
                    prefixSOAP + ":Envelope>";
        }
        xml.set(templateSOAP);
        int v = -1;
        String p;
        for(int i = 0; i < ENVELOPE_NS.length; i++) {
            if((p = xml.getNamespacePrefix(ENVELOPE_NS[i])) != null) {
                this.prefixSOAP = p;
                v = i;
                break;
            }
        }
        if(v == -1 || getBody() == null || xml.getNode("/" + prefixSOAP + ":Envelope") == null) {
            throw new Exception("Invalid SOAP template");
        }
        this.version = v;
    }

    /**
     * Send the request. After this call, the XML returned by {@link #getXML()} will contain the SOAP response until
     * it is reset via a call to {@link #setTemplate(String)}.
     *
     * @exception Exception Raised if any error occurs and in that case, the SOAP structure will be in an erroneous
     * state.
     */
    public void request() throws Exception {
        http.done();
        http.setRequestProperty("SOAPAction", actionSOAP);
        requesting();
        http.post(getXML().toString());
        String response  = http.read();
        try {
            xml.set(response);
        } catch (Exception e) {
            throw new Exception("XML:\n" + response, e);
        }
    }

    /**
     * This method is invoked just before the request is sent to the server. You may apply additional controls to
     * the HTTP request.
     */
    public void requesting() {
    }

    /**
     * Close the resources associated with this instance. This instance may not be used after calling this.
     */
    public void close() {
       http.close();
    }

    @Override
    public String toString() {
        return getXML().toString();
    }

    /**
     * Returns the XML associated with this SOAP request in a pretty string format.
     *
     * @return The XML in pretty string format.
     */
    public String toPrettyString() {
        return getXML().toPrettyString();
    }

    /**
     * Set the connection timeout for the HTTP request.
     *
     * @param timeout the connection timeout in milliseconds
     */
    public void setConnectTimeout(int timeout) {
        http.setConnectTimeout(timeout);
    }

    /**
     * Sets the read timeout for the HTTP connection.
     *
     * @param timeout The read timeout in milliseconds.
     */
    public void setReadTimeout(int timeout) {
        http.setReadTimeout(timeout);
    }
}

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

/**
 * Build HTML text.
 *
 * @author Syam
 */
public class HTMLText implements StyledBuilder {

    public static boolean ALLOW_TOP_LEVEL = false;
    private static final String NEW_LINE = "<BR/>";
    private static final String space = "&nbsp;";
    protected final StringBuilder value = new StringBuilder();
    private boolean newline = true;

    /**
     * Constructor.
     */
    public HTMLText() {
        this(null, EMPTY_STRINGS);
    }

    /**
     * Constructor.
     *
     * @param object Object to be added.
     * @param style Styles to be set.
     */
    public HTMLText(Object object, String... style) {
        append(object, style);
    }

    /**
     * Constructor.
     *
     * @param object Object to be added.
     * @param style Styles to be set.
     */
    public HTMLText(Object object, Style... style) {
        append(object, style);
    }

    /**
     * Constructor.
     *
     * @param object Object to be added.
     */
    public HTMLText(Object object) {
        this(object, EMPTY_STRINGS);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * Get the current text of the HTML.
     *
     * @return Current text.
     */
    public StringBuilder getText() {
        return value;
    }

    /**
     * Set text.
     *
     * @param text Text to be set.
     * @param style Style to be applied.
     */
    public void setText(String text, String... style) {
        setValue(text, style);
    }

    /**
     * Encode an object as an HTML string. (The string representation of the object is encoded).
     *
     * @param object Object to be encoded.
     * @return Encoded string.
     */
    public static String encode(Object object) {
        if(object == null) {
            return "";
        }
        String text = StringUtility.toString(object);
        if(text == null) {
            return "";
        }
        text = text.replace("&", "&amp;").
                replace("<", "&lt;").
                replace(">", "&gt;").
                replace("\"", "&quot;").
                replace("'", "&apos;");
        if(text.indexOf('\n') >= 0) {
            text = text.replace("\n", NEW_LINE);
        }
        text = text.replace("  ", space + " ");
        if(text.startsWith(" ")) {
            text = space + text.substring(1);
        }
        if(text.endsWith(" ")) {
            text = text.substring(0, text.length() - 1) + space;
        }
        return text;
    }

    @Override
    public HTMLText newLine(boolean force) {
        if(force || !newline) {
            return newLine();
        }
        return this;
    }

    @Override
    public HTMLText newLine() {
        value.append(NEW_LINE);
        newline = true;
        return this;
    }

    /**
     * Append a line using the <HR> tag.
     *
     * @return Self-reference is returned.
     */
    public HTMLText drawLine() {
        value.append("<HR/>");
        newline = true;
        return this;
    }

    @Override
    public HTMLText append(Object object, String color) {
        if(color == null) {
            return append(object);
        }
        return append(object, new String[] { color });
    }

    @Override
    public HTMLText append(Object object, String... style) {
        if(object == null && style.length == 0) {
            return this;
        }
        String text = encode(object);
        newline = StringUtility.pack(text).toUpperCase().endsWith(NEW_LINE);
        styleS(style).append(text).append(styleE(style));
        return this;
    }

    private static boolean allNulls(Style... styles) {
        if(styles == null) {
            return true;
        }
        for(Style s: styles) {
            if(s != null) {
                return false;
            }
        }
        return true;
    }

    private void addStyle(Style style) {
        if(style == null) {
            return;
        }
        String n = style.name().trim(), v = style.value().trim();
        if(n.isBlank()) {
            return;
        }
        while(v.endsWith(";")) {
            v = v.substring(0, v.length() - 1);
        }
        if(v.isBlank()) {
            return;
        }
        value.append(n).append(":").append(v).append(";");
    }

    @Override
    public HTMLText append(Object object, Style... styles) {
        if(allNulls(styles)) {
            return append(object);
        }
        if(object == null) {
            return this;
        }
        String text = encode(object);
        newline = StringUtility.pack(text).toUpperCase().endsWith(NEW_LINE);
        value.append("<span style=\"display:inline;");
        for(Style s: styles) {
            addStyle(s);
        }
        value.append("\">").append(text).append("</span>");
        return this;
    }

    /**
     * Append valid HTML text. It should not contain any top-level elements such as html, body or head. Also, no
     * scripts and style tags can be added.
     *
     * @return Self-reference is returned.
     */
    public HTMLText appendHTML(String html) {
        String h = StringUtility.pack(html).toUpperCase();
        if(h.contains("<HTML") || h.contains("<BODY") || h.contains("<HEAD")
                || h.contains("<SCRIPT") || h.contains("<STYLE")) {
            if(!ALLOW_TOP_LEVEL) {
                throw new SORuntimeException("HTML should not contain top-level elements, style tags & scripts");
            }
        }
        value.append(html);
        newline = h.endsWith(NEW_LINE);
        return this;
    }

    @Override
    public HTMLText clearContent() {
        return clear();
    }

    /**
     * Clear the content.
     *
     * @return Self-reference is returned.
     */
    public HTMLText clear() {
        newline = true;
        value.delete(0, value.length());
        return this;
    }

    @Override
    public boolean isEmpty() {
        return value.length() == 0;
    }

    @Override
    public boolean isNewLine() {
        return newline;
    }

    /**
     * Add spaces.
     *
     * @param count Number of spaces to be added.
     * @return Self-reference is returned.
     */
    public HTMLText space(int count) {
        return append(StringUtility.makeString(count));
    }

    private StringBuilder styleS(String[] styles) {
        if(styles == null || styles.length == 0) {
            return value;
        }
        value.append("<span style=\"display:inline;");
        if(styles.length == 1 && styles[0] != null && styles[0].indexOf(':') < 0) {
            styles[0] = "color:" + styles[0];
        }
        for(String style: styles) {
            if(style == null) {
                continue;
            }
            value.append(style);
            if(!style.endsWith(";")) {
                value.append(";");
            }
        }
        value.append("\">");
        return value;
    }

    private String styleE(String[] styles) {
        return styles != null && styles.length > 0 ? "</span>" : "";
    }

    @Override
    public HTMLText append(Object object) {
        return append(object, EMPTY_STRINGS);
    }

    /**
     * If this method is invoked ever, top-leve HTML elements will be allowed while building the HTML content. This
     * could cause unexpected changes to the existing HTML on the page.
     */
    public static void setAllowTopLevelHTML() {
        ALLOW_TOP_LEVEL = true;
    }

    /**
     * Whether top-level HTML elements are allowed or not.
     *
     * @return True/false.
     */
    public static boolean isAllowTopLevelHTML() {
        return ALLOW_TOP_LEVEL;
    }
}

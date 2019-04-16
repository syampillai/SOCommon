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

import java.util.Date;

public class HTMLText implements StyledBuilder {

    private static final String space = "&nbsp;";
    protected final StringBuilder value = new StringBuilder();
    private boolean newline = true;

    public HTMLText() {
        this(null);
    }

    public HTMLText(Object object, String... style) {
        append(object, style);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public StringBuilder getText() {
        return value;
    }

    public void setText(String text, String... style) {
        setValue(text, style);
    }

    public static String encode(Object object) {
        if(object == null) {
            return "";
        }
        if(object instanceof StyledBuilder) {
            return object.toString();
        }
        if(object instanceof Date) {
            return DateUtility.formatDate((Date)object);
        }
        String text = object.toString();
        if(text == null) {
            return "";
        }
        text = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
        if(text.indexOf('\n') >= 0) {
            text = text.replace("\n", "<BR/>");
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

    public HTMLText newLine(boolean force) {
        if(force || !newline) {
            return newLine();
        }
        return this;
    }

    public HTMLText newLine() {
        value.append("<BR/>");
        newline = true;
        return this;
    }

    public HTMLText drawLine() {
        value.append("<HR/>");
        newline = true;
        return this;
    }

    public HTMLText append(Object object, String color) {
        if(color == null) {
            return append(object);
        }
        return append(object, new String[] { color });
    }

    public HTMLText append(Object object, String... style) {
        if(object == null && style.length == 0) {
            return this;
        }
        if(object instanceof Boolean) {
            object = (Boolean) object ? "Yes" : "No";
        }
        String text = encode(object);
        newline = StringUtility.pack(text).toUpperCase().endsWith("<BR/>");
        styleS(style).append(text).append(styleE(style));
        return this;
    }

    public HTMLText appendHTML(String html) {
        value.append(html);
        newline = StringUtility.pack(html).toUpperCase().endsWith("<BR/>");
        return this;
    }

    public HTMLText clearContent() {
        return clear();
    }

    public HTMLText clear() {
        newline = true;
        value.delete(0, value.length());
        return this;
    }

    public boolean isEmpty() {
        return value.length() == 0;
    }

    public boolean isNewLine() {
        return newline;
    }

    public HTMLText space(int count) {
        return append(StringUtility.makeString(count));
    }

    public void setValue(Object object, String... style) {
        clear().append(object, style);
    }

    private StringBuilder styleS(String[] styles) {
        if(styles.length == 0) {
            return value;
        }
        value.append("<span style=\"");
        if(styles.length == 1 && styles[0].indexOf(':') < 0) {
            styles[0] = "color:" + styles[0];
        }
        for(String style: styles) {
            value.append(style);
            if(!style.endsWith(";")) {
                value.append(";");
            }
        }
        value.append("\">");
        return value;
    }

    private String styleE(String[] styles) {
        return styles.length > 0 ? "</span>" : "";
    }

    @Override
    public HTMLText append(Object object) {
        return append(object, new String[] { });
    }
}

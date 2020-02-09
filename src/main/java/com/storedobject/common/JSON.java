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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class JSON {

    public enum Type { NULL, STRING, NUMBER, BOOLEAN, ARRAY, JSON }
    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonNode value = null;

    public JSON() {
    }

    public JSON(String json) throws IOException {
        set(json);
    }

    public JSON(InputStream stream) throws IOException {
        set(stream);
    }

    public JSON(Reader reader) throws IOException {
        set(reader);
    }

    public JSON(URL url) throws Exception {
        set(url);
    }

    public JSON(JsonNode json) {
        value = json;
    }

    public JSON(Map<String, Object> map) {
        set(map);
    }

    public void set(Map<String, Object> map) {
        value = mapper.valueToTree(map);
    }

    public void set(String json) throws IOException {
        if(json == null) {
            return;
        }
        set(new StringReader(json));
    }

    public void set(InputStream stream) throws IOException {
        if(stream == null) {
            return;
        }
        set(IO.getReader(stream));
    }

    public void set(Reader reader) throws IOException {
        if(reader == null) {
            return;
        }
        value = mapper.readTree(reader);
        try {
            reader.close();
        } catch(Exception ignore) {
        }
    }

    public void set(URL url) throws Exception {
        if(url == null) {
            return;
        }
        HTTP http = new HTTP(url);
        set(http.getInputStream());
    }

    public List<String> keys() {
        return value == null ? new ArrayList<>() : ListUtility.list(value.fieldNames());
    }

    public Type getType() {
        if(value == null) {
            return Type.NULL;
        }
        if(value.isTextual()) {
            return Type.STRING;
        }
        if(value.isNumber()) {
            return Type.NUMBER;
        }
        if(value.isBoolean()) {
            return Type.BOOLEAN;
        }
        if(value.isArray()) {
            return Type.ARRAY;
        }
        return Type.JSON;
    }

    public String getString() {
        return value == null || !value.isTextual() ? null : value.textValue();
    }

    public Number getNumber() {
        return value == null || !value.isNumber() ? null : value.numberValue();
    }

    public Boolean getBoolean() {
        return value == null || !value.isBoolean() ? null : value.booleanValue();
    }

    public int getArraySize() {
        return value == null || !value.isArray() ? 0 : value.size();
    }

    public JSON get(int n) {
        if(value == null || !value.isArray() || n < 0) {
            return null;
        }
        JsonNode node = value.get(n);
        return node.isMissingNode() ? null : new JSON(node);
    }

    public JSON get(String key) {
        if(value == null) {
            return null;
        }
        JsonNode node = value.get(key);
        return node.isMissingNode() ? null : new JSON(node);
    }

    public boolean containsKey(String key) {
        return keys().contains(key);
    }

    public String getString(String key) {
        JSON json = get(key);
        return json == null ? null : json.getString();
    }

    public Number getNumber(String key) {
        JSON json = get(key);
        return json == null ? null : json.getNumber();
    }

    public Boolean getBoolean(String key) {
        JSON json = get(key);
        return json == null ? null : json.getBoolean();
    }

    private JsonNode value(String key, int n) {
        if(n < 0 || value == null) {
            return null;
        }
        JsonNode v = value.get(key);
        if(v.isMissingNode() || !v.isArray()) {
            return null;
        }
        if(n >= v.size()) {
            return null;
        }
        v = v.get(n);
        return v.isMissingNode() ? null : v;
    }

    public JSON get(String key, int n) {
        JsonNode node = value(key, n);
        return node == null ? null : new JSON(node);
    }

    public String getString(String key, int n) {
        return new JSON(value(key, n)).getString();
    }

    public Number getNumber(String key, int n) {
        return new JSON(value(key, n)).getNumber();
    }

    public Boolean getBoolean(String key, int n) {
        return new JSON(value(key, n)).getBoolean();
    }

    @Override
    public String toString() {
        return value == null ? null : pretty(value.toString()).toString();
    }

    public static String encode(String s) {
        if(s.contains("\\")) {
            s = s.replace("\\", "\\\\");
        }
        if(s.contains("\"")) {
            s = s.replace("\"", "\\\"");
        }
        return s;
    }

    private static StringBuilder pretty(String s) {
        StringBuilder b = new StringBuilder();
        String tab = "";
        char c = '\n', previous;
        boolean insideQuote = false;
        for(int i = 0; i < s.length(); i++) {
            previous = c;
            c = s.charAt(i);
            if(insideQuote) {
                b.append(c);
                if(previous != '\\' && c == '"') {
                    insideQuote = false;
                }
                continue;
            }
            if(c == '"') {
                insideQuote = true;
                if(previous == '\n') {
                    b.append(tab);
                }
                b.append(c);
                continue;
            }
            if(c == ' ') {
                continue;
            }
            if(c == ',') {
                if(previous == '\n') {
                    b.deleteCharAt(b.length() - 1);
                }
                b.append(c).append(c = '\n');
                continue;
            }
            if(c == ':') {
                b.append(": ");
                c = ' ';
                continue;
            }
            if(c == '{' || c == '[') {
                if(previous == '\n') {
                    b.append(tab);
                } else if(previous != ' ') {
                    b.append(' ');
                }
                b.append(c).append(c = '\n');
                tab += "   ";
                continue;
            }
            if(c == '}' || c == ']') {
                if(tab.length() >= 3) {
                    tab = tab.substring(0, tab.length() - 3);
                }
                if(previous != '\n') {
                    b.append('\n');
                }
                b.append(tab).append(c).append(c = '\n');
                continue;
            }
            if(previous == '\n') {
                b.append(tab);
            }
            b.append(c);
        }
        return b;
    }

    public static String toString(Map<String, Object> map) {
        return pretty(new JSON(map).toString()).toString();
    }

    public static void write(Map<String, Object> map, Writer writer) throws IOException {
        writer.write(new JSON(map).toString());
    }
}
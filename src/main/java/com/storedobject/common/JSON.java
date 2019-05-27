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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.URL;
import java.util.Map;
import java.util.stream.Stream;

public class JSON {

    public enum Type { NULL, STRING, NUMBER, BOOLEAN, ARRAY, JSON }

    private Object value = null;

    public JSON() {
    }

    public JSON(String json) throws Exception {
        set(json);
    }

    public JSON(InputStream stream) throws Exception {
        set(stream);
    }

    public JSON(Reader reader) throws Exception {
        set(reader);
    }

    public JSON(URL url) throws Exception {
        set(url);
    }

    public JSON(JSONObject json) {
        value = json;
    }

    public JSON(JSONArray jsonArray) {
        value = jsonArray;
    }

    public JSON(Map<String, Object> map) {
        set(map);
    }

    public void set(Map<String, Object> map) {
        value = new JSONObject(map);
    }

    public void set(String json) throws Exception {
        if(json == null) {
            return;
        }
        set(new StringReader(json));
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
        value = JSONValue.parseWithException(reader);
        try {
            reader.close();
        } catch(Exception ignore) {
        }
        if(getType() == Type.JSON && !(value instanceof JSONObject)) {
            value = value.toString();
        }
    }

    public void set(URL url) throws Exception {
        if(url == null) {
            return;
        }
        HTTP http = new HTTP(url);
        set(http.getInputStream());
    }

    public Stream<String> keys() {
        if(getType() == Type.JSON) {
            //noinspection unchecked
            return ((JSONObject)value).keySet().stream();
        }
        return null;
    }

    public Type getType() {
        if(value == null) {
            return Type.NULL;
        }
        if(value instanceof String) {
            return Type.STRING;
        }
        if(value instanceof Number) {
            return Type.NUMBER;
        }
        if(value instanceof Boolean) {
            return Type.BOOLEAN;
        }
        if(value instanceof JSONArray) {
            return Type.ARRAY;
        }
        return Type.JSON;
    }

    public String getString() {
        if(value instanceof String) {
            return (String)value;
        }
        return null;
    }

    public Number getNumber() {
        if(value instanceof Number) {
            return (Number)value;
        }
        return null;
    }

    public Boolean getBoolean() {
        if(value instanceof Boolean) {
            return (Boolean)value;
        }
        return null;
    }

    public int getArraySize() {
        if(value instanceof JSONArray) {
            return ((JSONArray)value).size();
        }
        return 0;
    }

    public JSON get(int n) {
        if(value instanceof JSONArray) {
            if(n >= 0 && n < ((JSONArray)value).size()) {
                JSON json = new JSON();
                json.value = ((JSONArray)value).get(n);
                return json;
            }
        }
        return null;
    }

    public JSON get(String key) {
        if(value instanceof JSONObject) {
            JSON json = new JSON();
            json.value = ((JSONObject)value).get(key);
            return json;
        }
        return null;
    }

    public boolean containsKey(String key) {
        if(value instanceof JSONObject) {
            return ((JSONObject)value).containsKey(key);
        }
        return false;
    }

    public String getString(String key) {
        if(value != null && value instanceof JSONObject) {
            Object v = ((JSONObject)value).get(key);
            if(v instanceof String) {
                return (String)v;
            }
        }
        return null;
    }

    public Number getNumber(String key) {
        if(value != null && value instanceof JSONObject) {
            Object v = ((JSONObject)value).get(key);
            if(v instanceof Number) {
                return (Number)v;
            }
        }
        return null;
    }

    public Boolean getBoolean(String key) {
        if(value != null && value instanceof JSONObject) {
            Object v = ((JSONObject)value).get(key);
            if(v instanceof Boolean) {
                return (Boolean)v;
            }
        }
        return null;
    }

    private Object value(String key, int n) {
        if(n < 0 || value == null || !(value instanceof JSONObject)) {
            return null;
        }
        Object v = ((JSONObject)value).get(key);
        if(!(v instanceof JSONArray)) {
            return null;
        }
        JSONArray a = (JSONArray)v;
        if(n < a.size()) {
            return a.get(n);
        }
        return null;
    }

    public JSON get(String key, int n) {
        Object v = value(key, n);
        if(v == null) {
            return null;
        }
        JSON j = new JSON();
        j.value = v;
        return j;
    }

    public String getString(String key, int n) {
        Object v = value(key, n);
        if(v instanceof String) {
            return (String)v;
        }
        return null;
    }

    public Number getNumber(String key, int n) {
        Object v = value(key, n);
        if(v instanceof Number) {
            return (Number)v;
        }
        return null;
    }

    public Boolean getBoolean(String key, int n) {
        Object v = value(key, n);
        if(v instanceof Boolean) {
            return (Boolean)v;
        }
        return null;
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
        return pretty(JSONValue.toJSONString(map)).toString();
    }

    public static void write(Map<String, Object> map, Writer writer) throws IOException {
        JSONValue.writeJSONString(map, writer);
    }
}
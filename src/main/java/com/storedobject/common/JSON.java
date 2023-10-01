/*
 * Copyright 2018-2020 Syam Pillai
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

/**
 * A simple wrapper around JSON processing classes. Developers can rely on this class rather than delving into
 * the underlying implementation. The underlying implementation may change in the future.
 *
 * @author Syam
 */
public class JSON {

    /**
     * Type of JSON values.
     */
    public enum Type { NULL, STRING, NUMBER, BOOLEAN, ARRAY, JSON }
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JSON EMPTY = createInt();
    private static final String EMPTY_STRING = "\"\"";

    private JsonNode value = null;

    /**
     * Construct an empty JSON.
     */
    public JSON() {
        try {
            set(EMPTY_STRING);
        } catch (IOException ignored) {
        }
    }

    /**
     * Construct JSON from a String.
     *
     * @param json JSON to construct.
     * @throws IOException If any exception happens while parsing.
     */
    public JSON(String json) throws IOException {
        set(json);
    }

    /**
     * Construct JSON from a stream.
     *
     * @param stream JSON to construct from this stream.
     * @throws IOException If any exception happens while parsing.
     */
    public JSON(InputStream stream) throws IOException {
        set(stream);
    }

    /**
     * Construct JSON from a reader.
     *
     * @param reader JSON to construct from this reader.
     * @throws IOException If any exception happens while parsing.
     */
    public JSON(Reader reader) throws IOException {
        set(reader);
    }

    /**
     * Construct JSON from a URL.
     *
     * @param url JSON to construct from this URL.
     * @throws Exception If any exception happens while parsing.
     */
    public JSON(URL url) throws Exception {
        set(url);
    }

    /**
     * Construct JSON from another JSON node.
     *
     * @param json JSON to construct from this JSON node.
     */
    public JSON(JsonNode json) {
        value = json;
    }

    /**
     * Construct JSON from an Object that could hopefully parsed into a JSON compatible String. Typically, it could be
     * a {@link Map} or some sort of an array or collection. It could also be a standalone object that can be converted
     * to a valid JSON string.
     *
     * @param object JSON to construct from this Object.
     */
    public JSON(Object object) {
        set(object);
    }

    /**
     * Create a blank JSON.
     *
     * @return Empty JSON.
     */
    public static JSON create() {
        return EMPTY;
    }

    private static JSON createInt() {
        try {
            return new JSON(EMPTY_STRING);
        } catch(IOException e) {
            return null;
        }
    }

    /**
     * Create a JSON object by parsing the string passed. If the string can nt be parsed to a valid JSON, an
     * empty JSON is created and no exceptions are thrown.
     *
     * @param json JSON string to parse.
     * @return Returns the JSON object or an empty JSON if parsing failed.
     */
    public static JSON create(String json) {
        if(json == null || json.isEmpty()) {
            return EMPTY;
        }
        try {
            return new JSON(json);
        } catch(IOException e) {
            return EMPTY;
        }
    }

    /**
     * Set JSON from an Object that could hopefully parsed into a JSON compatible String. Typically, it could be
     * a {@link Map} or some sort of an array or collection. It could also be a standalone object that can be converted
     * to a valid JSON string.
     *
     * @param object JSON to construct from this Object.
     */
    public void set(Object object) {
        if(object == null) {
            try {
                set(EMPTY_STRING);
            } catch (IOException ignored) {
            }
            return;
        }
        value = mapper.valueToTree(object);
    }

    /**
     * Set from a String.
     *
     * @param json JSON to set from this String.
     * @throws IOException If any exception happens while parsing.
     */
    public void set(String json) throws IOException {
        if(json == null) {
            set(EMPTY_STRING);
            return;
        }
        set(new StringReader(json));
    }

    /**
     * Set from a stream.
     *
     * @param stream JSON to set from this stream.
     * @throws IOException If any exception happens while parsing.
     */
    public void set(InputStream stream) throws IOException {
        if(stream == null) {
            set(EMPTY_STRING);
            return;
        }
        set(IO.getReader(stream));
    }

    /**
     * Set from a Reader.
     *
     * @param reader JSON to set from this Reader.
     * @throws IOException If any exception happens while parsing.
     */
    public void set(Reader reader) throws IOException {
        if(reader == null) {
            set(EMPTY_STRING);
            return;
        }
        value = mapper.readTree(reader);
        IO.close(reader);
    }

    /**
     * Set from a URL.
     *
     * @param url JSON to set from this URL.
     * @throws Exception If any exception happens while parsing.
     */
    public void set(URL url) throws Exception {
        if(url == null) {
            set(EMPTY_STRING);
            return;
        }
        HTTP http = new HTTP(url);
        set(http.getInputStream());
    }

    /**
     * Get the top-level keys.
     *
     * @return Keys as a List.
     */
    public List<String> keys() {
        return value == null ? new ArrayList<>() : ListUtility.list(value.fieldNames());
    }

    /**
     * Get the {@link Type} of the JSON.
     *
     * @return Type.
     */
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

    /**
     * Get this as a String. If value is <code>null</code> or if the {@link Type} is different,
     * <code>null</code> will be returned.
     *
     * @return The value.
     */
    public String getString() {
        return value == null || !value.isTextual() ? null : value.textValue();
    }

    /**
     * Get this as a {@link Number}. If value is <code>null</code> or if the {@link Type} is different,
     * <code>null</code> will be returned.
     *
     * @return The value.
     */
    public Number getNumber() {
        return value == null || !value.isNumber() ? null : value.numberValue();
    }

    /**
     * Get this as a {@link BigDecimal} value. If value is <code>null</code> or if the {@link Type} is different,
     * <code>null</code> will be returned.
     *
     * @return The value.
     */
    public BigDecimal getDecimal() {
        return value == null || !value.isNumber() ? null : value.decimalValue();
    }

    /**
     * Get this as a Boolean value. If value is <code>null</code> or if the {@link Type} is different,
     * <code>null</code> will be returned.
     *
     * @return The value.
     */
    public Boolean getBoolean() {
        return value == null || !value.isBoolean() ? null : value.booleanValue();
    }

    /**
     * Get the array size. (If the {@link Type} is not an array or if it is null, 0 is returned).
     *
     * @return Size of the array.
     */
    public int getArraySize() {
        return value == null || !value.isArray() ? 0 : value.size();
    }

    /**
     * Get the value at position 'n' assuming that this is an array. <code>Null</code> will be returned if this
     * is <code>null</code>, not an array or position is outside the range.
     *
     * @param n Position.
     * @return Value at the position.
     */
    public JSON get(int n) {
        if(value == null || !value.isArray() || n < 0) {
            return null;
        }
        JsonNode node = value.get(n);
        return node == null || node.isMissingNode() ? null : new JSON(node);
    }

    /**
     * Get the value for the given key. <code>Null</code> will be returned if this
     * is <code>null</code>, key is <code>null</code> or no value exists for the given key.
     *
     * @param key Key.
     * @return Value for the key.
     */
    public JSON get(String key) {
        if(value == null || key == null) {
            return null;
        }
        JsonNode node = value.get(key);
        return node == null || node.isMissingNode() ? null : new JSON(node);
    }

    /**
     * Check if this contains the given key or not.
     *
     * @param key Key.
     * @return True or false.
     */
    public boolean containsKey(String key) {
        if(key == null) {
            return false;
        }
        return keys().contains(key);
    }

    /**
     * Get the value for the given key as a String. <code>Null</code> will be returned if this
     * is <code>null</code>, key is <code>null</code>, no value exists for the given key or the value type is
     * not matching.
     *
     * @param key Key.
     * @return Value for the key.
     */
    public String getString(String key) {
        JSON json = get(key);
        return json == null ? null : json.getString();
    }

    /**
     * Get the value for the given key as a {@link Number}. <code>Null</code> will be returned if this
     * is <code>null</code>, key is <code>null</code>, no value exists for the given key or the value type is
     * not matching.
     *
     * @param key Key.
     * @return Value for the key.
     */
    public Number getNumber(String key) {
        JSON json = get(key);
        return json == null ? null : json.getNumber();
    }

    /**
     * Get the value for the given key as a {@link BigDecimal}. <code>Null</code> will be returned if this
     * is <code>null</code>, key is <code>null</code>, no value exists for the given key or the value type is
     * not matching.
     *
     * @param key Key.
     * @return Value for the key.
     */
    public BigDecimal getDecimal(String key) {
        JSON json = get(key);
        return json == null ? null : json.getDecimal();
    }

    /**
     * Get the value for the given key as a Boolean. <code>Null</code> will be returned if this
     * is <code>null</code>, key is <code>null</code>, no value exists for the given key or the value type is
     * not matching.
     *
     * @param key Key.
     * @return Value for the key.
     */
    public Boolean getBoolean(String key) {
        JSON json = get(key);
        return json == null ? null : json.getBoolean();
    }

    private JsonNode value(String key, int n) {
        if(n < 0 || value == null || key == null) {
            return null;
        }
        JsonNode v = value.get(key);
        if(v == null || v.isMissingNode() || !v.isArray()) {
            return null;
        }
        if(n >= v.size()) {
            return null;
        }
        v = v.get(n);
        return v == null || v.isMissingNode() ? null : v;
    }

    /**
     * Retrieve the value for the given key and get the value from the given position from that value, assuming that
     * value is an array type. <code>Null</code> will be returned if this
     * is <code>null</code>, key is <code>null</code>, no value exists for the given key, the value type is
     * not an array or nothing found at the given position.
     *
     * @param key Key.
     * @param n Position.
     * @return Value for the key.
     */
    public JSON get(String key, int n) {
        JsonNode node = value(key, n);
        return node == null ? null : new JSON(node);
    }

    /**
     * Retrieve the value for the given key and get the value from the given position as a String from that value,
     * assuming that value is an array type. <code>Null</code> will be returned if this
     * is <code>null</code>, key is <code>null</code>, no value exists for the given key, the value type is
     * not an array, nothing found at the given position or the value type at the position is not matching.
     *
     * @param key Key.
     * @param n Position.
     * @return Value for the key.
     */
    public String getString(String key, int n) {
        return new JSON(value(key, n)).getString();
    }

    /**
     * Retrieve the value for the given key and get the value from the given position as a {@link Number} from that
     * value, assuming that value is an array type. <code>Null</code> will be returned if this
     * is <code>null</code>, key is <code>null</code>, no value exists for the given key, the value type is
     * not an array, nothing found at the given position or the value type at the position is not matching.
     *
     * @param key Key.
     * @param n Position.
     * @return Value for the key.
     */
    public Number getNumber(String key, int n) {
        return new JSON(value(key, n)).getNumber();
    }

    /**
     * Retrieve the value for the given key and get the value from the given position as a {@link BigDecimal} from that
     * value, assuming that value is an array type. <code>Null</code> will be returned if this
     * is <code>null</code>, key is <code>null</code>, no value exists for the given key, the value type is
     * not an array, nothing found at the given position or the value type at the position is not matching.
     *
     * @param key Key.
     * @param n Position.
     * @return Value for the key.
     */
    public BigDecimal getDecimal(String key, int n) {
        return new JSON(value(key, n)).getDecimal();
    }

    /**
     * Retrieve the value for the given key and get the value from the given position as a Boolean from that value,
     * assuming that value is an array type. <code>Null</code> will be returned if this
     * is <code>null</code>, key is <code>null</code>, no value exists for the given key, the value type is
     * not an array, nothing found at the given position or the value type at the position is not matching.
     *
     * @param key Key.
     * @param n Position.
     * @return Value for the key.
     */
    public Boolean getBoolean(String key, int n) {
        return new JSON(value(key, n)).getBoolean();
    }

    /**
     * Check whether the value is null or not.
     *
     * @return True/false.
     */
    public boolean isNull() {
        return value == null;
    }

    /**
     * Return the JSON as a {@link Map} of key/value pairs.
     *
     * @return Map of key/value pairs.
     */
    public Map<String, Object> toMap() {
        if(value == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(value, new TypeReference<>() {
        });
    }

    /**
     * Return the JSON as a String.
     *
     * @return String representation.
     */
    @Override
    public String toString() {
        return value == null ? null : value.toString();
    }

    /**
     * Return the JSON as a formatted (more human-readable) String.
     *
     * @return String representation.
     */
    public String toPrettyString() {
        return value == null ? null : value.toPrettyString();
    }

    /**
     * Encode a String to replace escape double-quotes so that it can be stuffed into as a part of
     * JSON string.
     *
     * @param s String to encode.
     * @return Encoded String.
     */
    public static String encode(String s) {
        if(s.contains("\\")) {
            s = s.replace("\\", "\\\\");
        }
        if(s.contains("\"")) {
            s = s.replace("\"", "\\\"");
        }
        return s;
    }

    /**
     * Convert a map to JSON representation.
     *
     * @param map Map to convert.
     * @return String in JSON representation.
     */
    public static String toString(Map<String, Object> map) {
        return new JSON(map).toString();
    }

    /**
     * Convert a map to formatted JSON representation.
     *
     * @param map Map to convert.
     * @return String in JSON representation.
     */
    public static String toPrettyString(Map<String, Object> map) {
        return new JSON(map).toPrettyString();
    }

    /**
     * Convert a map to JSON representation and write to a given Writer.
     *
     * @param map Map to convert.
     * @param writer Writer to write to.
     * @throws IOException If writing fails.
     */
    public static void write(Map<String, Object> map, Writer writer) throws IOException {
        writer.write(new JSON(map).toString());
    }

    /**
     * Convert a map to formatted JSON representation and write to a given Writer.
     *
     * @param map Map to convert.
     * @param writer Writer to write to.
     * @throws IOException If writing fails.
     */
    public static void prettyWrite(Map<String, Object> map, Writer writer) throws IOException {
        writer.write(new JSON(map).toPrettyString());
    }

    /**
     * Create JSON from JSS (JavaScript Structure). A JavaScript structure looks similar to JSON string with
     * some differences: (a) Keys are not in double-quotes, (b) String values may use single-quotes or double-quotes,
     * (c) Extraneous commas may be there after the last items. This method (a) inserts double-quotes around the keys,
     * (b) replaces single-quotes with double-quotes and (c) removes extraneous commas. For escaping literal
     * single-quotes or double-quotes, you should use a '\' character (backslash) just before it.
     * In fact, any character following the backslash character will be copied to the output without any
     * special processing. Example of a JSS is: { person: { name: 'Syam Pillai', age: 25 } }
     *
     * @param jss JSS string.
     * @return JSON created from the parsed JSS.
     * @throws IOException If any exception happens while parsing.
     */
    public static JSON fromJSS(String jss) throws IOException {
        jss = parseJSS(jss);
        return jss == null ? new JSON() : new JSON(jss);
    }

    /**
     * Parse a JSS (JavaScript Structure) string to a JSON compatible string. A JavaScript structure looks similar to
     * JSON string with some differences: (a) Keys are not in double-quotes, (b) String values may use single-quotes
     * or double-quotes, (c) Extraneous commas may be there after the last items. This method (a) inserts double-quotes
     * around the keys, (b) replaces single-quotes with double-quotes and (c) removes all extraneous commas.
     * For escaping literal single-quotes or double-quotes, you should use a '\' character (backslash)
     * just before it. In fact, any character following the backslash character will be copied to the output
     * without any special processing. Example of a JSS is: { person: { name: 'Syam Pillai', age: 25 } }
     *
     * @param jss JSS string.
     * @return A JSON compatible string.
     */
    public static String parseJSS(String jss) {
        if(jss != null) {
            jss = jss.trim();
            if(jss.isEmpty()) {
                return null;
            }
        } else {
            return null;
        }
        if(!(jss.startsWith("[") || jss.startsWith("{"))) {
            jss = "{" + jss + "}";
        }
        StringBuilder sb = new StringBuilder();
        String delimiters = "[]{}:,\r\n\t\"'\\ ";
        String token, word = null;
        char c;
        boolean backslash = false, quote = false, comma = false;
        StringTokenizer st = new StringTokenizer(jss, delimiters, true);
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if(token.length() == 1) {
                c = token.charAt(0);
                if(backslash) {
                    backslash = false;
                    if(word != null) {
                        sb.append(word);
                        word = null;
                    }
                    sb.append(c);
                    continue;
                }
                if(c == '\\') {
                    backslash = true;
                    continue;
                }
                if(c == '\r') {
                    continue;
                }
                if(quote) {
                    if(c == '\'' || c == '"') {
                        quote = false;
                    } else {
                        sb.append(c);
                        continue;
                    }
                } else if(c == '\'' || c == '"') {
                    quote = true;
                }
                if(c == ':') {
                    if(word != null) {
                        if(!word.startsWith("\"")) {
                            sb.append('"');
                        }
                        sb.append(word);
                        if(!word.endsWith("\"")) {
                            sb.append('"');
                        }
                        word = null;
                    }
                }
                if(Character.isWhitespace(c)) {
                    continue;
                }
                if(word != null) {
                    sb.append(word);
                    word = null;
                }
                if(c == '\'') {
                    c = '"';
                }
                if(comma) {
                    switch (c) {
                        case ']':
                        case '}':
                            break;
                        default:
                            sb.append(',');
                    }
                }
                comma = c == ',';
                if(!comma) {
                    sb.append(c);
                }
            } else {
                if(comma) {
                    sb.append(',');
                    comma = false;
                }
                if (word != null) {
                    sb.append(word);
                }
                if(quote) {
                    sb.append(token);
                    continue;
                }
                if (backslash) {
                    backslash = false;
                    sb.append(token.charAt(0));
                    token = token.substring(1);
                }
                word = token;
            }
        }
        if(word != null) {
            sb.append(word);
        }
        return sb.toString();
    }
}
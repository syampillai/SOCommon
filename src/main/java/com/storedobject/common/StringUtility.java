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

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.*;

/**
 * String utility functions.
 *
 * @author Syam
 */
public class StringUtility {

    /**
     * Convert a string to camelcase.
     * @param text Text to convert.
     * @return Converted text.
     */
    public static String toCamelCase(String text) {
        StringBuilder builder = new StringBuilder();
        char p = ' ', c;
        for(int i = 0; i < text.length(); i++) {
            c = text.charAt(i);
            builder.append(Character.isLetterOrDigit(p) ? Character.toLowerCase(c) : Character.toUpperCase(c));
            p = c;
        }
        return builder.toString();
    }

    /**
     * Convert to lower case.
     * @param s String array to be converted.
     * @return Array
     */
    public static String[] toLowerCase(String[] s) {
        if(s == null) {
            return null;
        }
        for(int i=0; i<s.length; i++) {
            if(s[i] != null) {
                s[i] = s[i].toLowerCase();
            }
        }
        return s;
    }

    /**
     * Convert to upper case.
     * @param s String array to be converted.
     * @return Array
     */
    public static String[] toUpperCase(String[] s) {
        if(s == null) {
            return null;
        }
        for(int i=0; i<s.length; i++) {
            if(s[i] != null) {
                s[i] = s[i].toUpperCase();
            }
        }
        return s;
    }

    /**
     * Convert to lower case.
     * @param s Char array to be converted.
     * @return Array
     */
    public static char[] toLowerCase(char[] s) {
        if(s == null) {
            return null;
        }
        for(int i=0; i<s.length; i++) {
            s[i] = Character.toLowerCase(s[i]);
        }
        return s;
    }

    /**
     * Convert to upper case.
     * @param s Char array to be converted.
     * @return Array
     */
    public static char[] toUpperCase(char[] s) {
        if(s == null) {
            return null;
        }
        for(int i=0; i<s.length; i++) {
            s[i] = Character.toUpperCase(s[i]);
        }
        return s;
    }

    /**
     * Tag each element of a String array if it is a duplicate. For example, if the array is
     * { "Name", "ShortName", "Name", "Test", "Name" }, it will become
     * { "Name", "ShortName", "Name (2)", "Test", "Name (3)" }. Null values will be untouched.
     * @param s String array to be tagged
     * @return Array
     */
    public static String[] tagDuplicates(String[] s) {
        int i, j, tag;
        String t;
        for(i = 1; i < s.length; i++) {
            if(s[i] == null) {
                continue;
            }
            tag = 1;
            t = s[i];
            for(j = 0; j < i; j++) {
                if(s[j] != null && s[j].equals(t)) {
                    ++tag;
                    t = s[i] + " (" + tag + ")";
                    j = -1;
                }
            }
            s[i] = t;
        }
        return s;
    }

    /**
     * Tag each element of a String list if it is a duplicate. For example, if the list is
     * [ "Name", "ShortName", "Name", "Test", "Name" ], it will become
     * [ "Name", "ShortName", "Name (2)", "Test", "Name (3)" ]. Null values will be untouched.
     * @param s String list to be tagged
     * @return Array
     */
    public static java.util.List<String> tagDuplicates(java.util.List<String> s) {
        int i, j, tag;
        String t;
        for(i = 1; i < s.size(); i++) {
            if(s.get(i) == null) {
                continue;
            }
            tag = 1;
            t = s.get(i);
            for(j = 0; j < i; j++) {
                if(s.get(j) != null && s.get(j).equals(t)) {
                    ++tag;
                    t = s.get(i) + " (" + tag + ")";
                    j = -1;
                }
            }
            s.set(i, t);
        }
        return s;
    }

    /**
     * Convert the list to a String array. Null values will be converted to empty strings.
     * @param list List to be converted
     * @return String array.
     */
    public static String[] toArray(java.util.List<?> list) {
        String[] s = new String[list.size()];
        int i = 0;
        for(Object obj: list) {
            s[i++] = obj == null ? "" : obj.toString();
        }
        return s;
    }

    /**
     * Trim each element of a String array
     * @param s String array to be trimmed
     * @return Array
     */
    public static String[] trim(String[] s) {
        for(int i=0; i<s.length; i++) {
            s[i] = s[i] == null ? "" : s[i].trim();
        }
        return s;
    }

    /**
     * Right trim each element of a String array
     * @param s String array to be trimmed
     * @return Array
     */
    public static String[] trimRight(String[] s) {
        for(int i=0; i<s.length; i++) {
            s[i] = s[i] == null ? "" : trimRight(s[i]);
        }
        return s;
    }

    /**
     * Left trim each element of a String array
     * @param s String array to be trimmed
     * @return Array
     */
    public static String[] trimLeft(String[] s) {
        for(int i=0; i<s.length; i++) {
            s[i] = s[i] == null ? "" : trimLeft(s[i]);
        }
        return s;
    }

    /**
     * Right trim a string
     * @param s String to be trimmed
     * @return The result String
     */
    public static String trimRight(String s) {
        int i = s.length() - 1;
        while(i >= 0) {
            if(!Character.isWhitespace(s.charAt(i))) break;
            --i;
        }
        ++i;
        return s.substring(0, i);
    }

    /**
     * Left trim a string
     * @param s String to be trimmed
     * @return The result String
     */
    public static String trimLeft(String s) {
        int i;
        for(i=0; i<s.length(); i++) {
            if(!Character.isWhitespace(s.charAt(i))) break;
        }
        return s.substring(i);
    }

    /**
     * Replicate a String several times
     * @param s String to be replicated
     * @param times Number of times to replicate
     * @return The result String
     */
    public static String replicate(String s, int times) {
        StringBuilder x = new StringBuilder();
        while(times > 0) {
            x.append(s);
            --times;
        }
        return x.toString();
    }

    /**
     * Right pad a String with spaces
     * @param s String to be padded. A null String is treated as "".
     * @param length Length of the String after padding. Warning: The resulting String may be shorter than
     * the passed, one depending on this parameter.
     * @return The result String
     */
    public static String padRight(String s, int length) {
        return padRight(s, length, ' ');
    }

    /**
     * Right pad a String with the padChar passed
     * @param s String to be padded. A null String is treated as "".
     * @param length Length of the String after padding. Warning: The resulting String may be shorter than
     * the passed, one depending on this parameter.
     * @param padChar The character to be used for padding.
     * @return The result String
     */
    public static String padRight(String s, int length, char padChar) {
        return makeString(s, length, padChar, false);
    }

    /**
     * Left pad a String with spaces
     * @param s String to be padded. A null String is treated as "".
     * @param length Length of the String after padding. Warning: The resulting String may be shorter than
     * the passed, one depending on this parameter.
     * @return The result String
     */
    public static String padLeft(String s, int length) {
        return padLeft(s, length, ' ');
    }

    /**
     * Left pad a String with the padChar passed
     * @param s String to be padded. A null String is treated as "".
     * @param length Length of the String after padding. Warning: The resulting String may be shorter than
     * the passed, one depending on this parameter.
     * @param padChar The character to be used for padding.
     * @return The result String
     */
    public static String padLeft(String s, int length, char padChar) {
        return makeString(s, length, padChar, true);
    }

    /**
     * Make a String of spaces.
     * @param length The length of the resulting String
     * @return The result String
     */
    public static String makeString(int length) {
        return makeString("", length);
    }

    /**
     * Make a new String by padding spaces to the String passed
     * @param s Input string
     * @param length Length of the String after padding. Warning: The resulting String may be shorter than
     * the passed one, depending on this parameter.
     * @return The result String
     */
    public static String makeString(String s, int length) {
        return makeString(s, length, false);
    }

    /**
     * Make a new String by padding spaces to the String passed
     * @param s Input string
     * @param length Length of the String after padding. Warning: The resulting String may be shorter than
     * the passed one, depending on this parameter.
     * @param padLeft If passed "true", left padding will happen, otherwise padding will happen on the right
     * @return The result String
     */
    public static String makeString(String s, int length, boolean padLeft) {
        return makeString(s, length, ' ', padLeft);
    }

    /**
     * Make a new String by padding the padChar to the String passed
     * @param s Input string
     * @param length Length of the String after padding. Warning: The resulting String may be shorter than
     * the passed one, depending on this parameter.
     * @param padChar The character to be used for padding.
     * @param padLeft If passed "true", left padding will happen, otherwise padding will happen on the right
     * @return The result String
     */
    public static String makeString(String s, int length, char padChar, boolean padLeft) {
        if(length == 0) {
            return "";
        }
        if(length < 0) {
            return s;
        }
        if(s == null) {
            s = "" + padChar;
        }
        if(s.length() > length) {
            if(padLeft) {
                return s.substring(s.length()-length);
            }
            return s.substring(0, length);
        }
        StringBuilder sBuilder = new StringBuilder(s);
        while(sBuilder.length() < length) {
            if(padLeft) {
                sBuilder.insert(0, padChar);
            } else {
                sBuilder.append(padChar);
            }
        }
        s = sBuilder.toString();
        return s;
    }

    /**
     * Smoothen a string by trimming and replacing double spaces with single space. Null will be converted to "". Also, output will be trimmed.
     * @param s Input string
     * @return Smoothened string
     */
    public static String smoothen(String s) {
        if(s == null) {
            return "";
        }
        s = s.trim();
        while(s.contains("  ")) {
            s = s.replace("  ", " ");
        }
        return s.trim();
    }

    /**
     * Pack a string by removing white spaces. Note: Null value is converted to "".
     *
     * @param s String to be packed.
     * @return String value after removing all white spaces.
     */
    public static String pack(String s) {
        if(s == null) {
            return "";
        }
        return s.replaceAll("\\s", "");
    }

    /**
     * Pack all the strings in an array by removing white spaces. Note: Null values are converted to "".
     *
     * @param s String array to be packed.
     * @return Converted array value after removing all white spaces from the elements.
     */
    public static String[] pack(String[] s) {
        if(s == null) {
            return null;
        }
        for(int i = 0; i < s.length; i++) {
            s[i] = pack(s[i]);
        }
        return s;
    }

    /**
     * Remove null values from the array of Strings.
     *
     * @param s String array from which null values need to be removed.
     * @return Converted array containing no null values. Array length may shrink because of the removals.
     */
    public static String[] removeNulls(String[] s) {
        if(s == null) {
            return null;
        }
        int n = 0;
        for(String t: s) {
            if(t != null) {
                ++n;
            }
        }
        String[] v = new String[n];
        n = 0;
        for(String t: s) {
            if(t != null) {
                v[n++] = t;
            }
        }
        return v;
    }

    /**
     * Convert a byte array into a String of hex values. Each is converted to 2 hex charaters.
     * For example, StringUtility.toHex( new byte[ ] { 10, 17, 19, 4, 11 } ) will return "0A1113040B".
     * @param bytes The byte array to be converted.
     * @return The result String
     */
    public static String toHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        int b;
        for (byte aByte : bytes) {
            b = aByte;
            if (b < 0) {
                b += 256;
            }
            if (b <= 0xF) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b));
        }
        return hex.toString().toUpperCase();
    }

    /**
     * Check if a String contains only space characters
     * @param s String to be checked
     * @return True if the String contains only spaces, otherwise false.
     */
    public static boolean isBlank(String s) {
        if(s == null) return true;
        int i;
        for(i=0; i<s.length(); i++) {
            if(s.charAt(i) != ' ') return false;
        }
        return true;
    }

    /**
     * Check if a String contains only white space characters
     * @param s String to be checked
     * @return True if the String contains only white spaces, otherwise false.
     */
    public static boolean isWhite(String s) {
        if(s == null) {
            return true;
        }
        int i;
        for(i = 0; i < s.length(); i++) {
            if(!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the character is a white space or not
     * @param c Character to be checked.
     * @return True if the character is a white space, otherwise false.
     */
    public static boolean isWhite(char c) {
        return Character.isWhitespace(c);
    }

    /**
     * Check if a String contains only letters and digits
     * @param s String to be checked
     * @return True if the String contains only letters and digits, otherwise false.
     */
    public static boolean isLetterOrDigit(String s) {
        if(s == null || s.length() == 0) {
            return false;
        }
        int i;
        char c;
        for(i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if(!Character.isLetter(c) && !Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if a String contains only letters
     * @param s String to be checked
     * @return True if the String contains only letters, otherwise false.
     */
    public static boolean isLetter(String s) {
        if(s == null || s.length() == 0) {
            return false;
        }
        int i;
        char c;
        for(i=0; i<s.length(); i++) {
            c = s.charAt(i);
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if a String contains only digits
     * @param s String to be checked
     * @return True if the String contains only digits, otherwise false.
     */
    public static boolean isDigit(String s) {
        if(s == null || s.isEmpty()) {
            return false;
        }
        int i;
        char c;
        for(i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if(!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if a String contains a numeric value or not.
     * @param s String to be checked
     * @return True if the String contains a proper numeric value. (Can contain an optional minus '-' sign and a decimal point).
     */
    public static boolean isNumber(String s) {
        if(s == null || s.isEmpty()) {
            return false;
        }
        if(s.indexOf('.') != s.lastIndexOf('.')) {
            return false;
        }
        if(s.indexOf('-') != s.lastIndexOf('-')) {
            return false;
        }
        if(s.charAt(0) == '-') {
            s = "0" + s.substring(1);
        }
        s = s.replace('.', '0');
        return isDigit(s);
    }

    /**
     * Convert a string to proper case (first letter of each word will be capitalized).
     * @param s String to be converted.
     * @return Converted string.
     */
    public static String firstCaps(String s) {
        if(s == null || s.length() == 0) {
            return s;
        }
        boolean ugly = s.equals(s.toUpperCase());
        StringBuilder b = new StringBuilder();
        int i = 0;
        char c;
        while(i < s.length()) {
            c = s.charAt(i++);
            if(!Character.isLetter(c)) {
                b.append(c);
                continue;
            }
            b.append(Character.toUpperCase(c));
            while(i < s.length()) {
                c = s.charAt(i++);
                if(ugly) {
                    b.append(Character.toLowerCase(c));
                } else {
                    b.append(c);
                }
                if(!Character.isLetter(c) && c != '\'') {
                    break;
                }
            }
        }
        return b.toString();
    }

    /**
     * Convert each element of the string array to proper case (first letter of each word will be capitalized).
     * @param a String array to be converted.
     * @return Array
     */
    public static String[] firstCaps(String[] a) {
        if(a == null) {
            return null;
        }
        for(int i = 0; i < a.length; i++) {
            a[i] = firstCaps(a[i]);
        }
        return a;
    }

    /**
     * Find the next whitespace position.
     * @param s String to be searched.
     * @param from The position from which the search should start.
     * @return The next whitespace position or the string length if nothing found.
     */
    public int findWhitespace(String s, int from) {
        while(!Character.isWhitespace(s.charAt(from))) {
            ++from;
        }
        return from;
    }

    /**
     * Find the next non-whitespace position.
     * @param s String to be searched.
     * @param from The position from which the search should start.
     * @return The next non-whitespace position or the string length if nothing found.
     */
    public int findNonWhitespace(String s, int from) {
        while(Character.isWhitespace(s.charAt(from))) {
            ++from;
        }
        return from;
    }

    /**
     * Converts time duration to human readable string "hours:minutes".
     *
     * @param duration Time duration to convert in minutes.
     * @return Human readable string. Example: 63 will be converted to "01:03".
     */
    public static String minutesToString(int duration) {
        int m = Math.abs(duration) % 60;
        return (duration / 60) + ":" + (m < 10 ? "0" : "") + m;
    }

    /**
     * Converts minutes of the day to human readable AM/PM string.
     *
     * @param time Time to convert in minutes (will be converted to the range 0 &lt;= minutes &lt; 1448).
     * @return Human readable string. Example: 63 will be converted to "01:03 AM".
     */
    public static String minutesToAMPM(int time) {
        while(time < 0) {
            time += 1440;
        }
        while(time >= 1440) {
            time -= 1440;
        }
        String s;
        int h, m;
        h = time / 60;
        if(h < 12) {
            if(h == 0) {
                h = 12;
            }
            s = " AM";
        } else {
            s = " PM";
            if(h > 12) {
                h -= 12;
            }
        }
        m = time % 60;
        return (h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m + s;
    }

    private final static String[] propositions = new String[] {
            "A", "After", "And", "As", "At", "Be", "Before", "By", "Down", "For", "From", "Of", "Off", "On", "Or", "Out", "Over", "In", "Not",
            "Per", "The", "Till", "To", "Under", "Until", "Up", "With",
    };

    /**
     * <p>Make label by inserting a space before each capital letter. Also, all dash characters ("-", "_") are
     * converted into " - ". Single quotes will be copied as such. If "as" keyword is found, the word sequences
     * after that keyword will be used for generating the label.</p>
     * <p>Examples: "ShortName" =&gt; "Short Name", "CountryName" =&gt; "Country Name",
     * "CountryOfDestination as Shipment Country" =&gt; "Shipment Country",
     * "KGB" =&gt; "KGB", "KGBAgent" =&gt; "KGB Agent", "eDocument" =&gt; "eDocument",
     * "eGovernanceInitiative" =&gt; "eGovernance Initiative",
     * "Document_PartI" =&gt; "Document - Part I.</p>
     * <p>Also,</p>
     * <p>(1) class casting type constructs will be removed. Example: "(com.storedobject.core.Person)Employee" =&gt;
     * "Employee"</p>
     * <p>(2) "A.B.C." =&gt; "A.B.C.", "Animal.Lion" =&gt; "Lion" (because the letter just before ".Lion" is in
     * small-case).</p>
     * @param s String to be converted.
     * @param lowerPropositions Whether to convert propositions to lower case or not.
     * @return Converted string.
     */
    public static String makeLabel(String s, boolean lowerPropositions) {
        if(isWhite(s)) {
            return s;
        }
        int i = s.toLowerCase().indexOf(" as ");
        if(i > 0) {
            String t = s.substring(i + 4).trim();
            if(t.startsWith("/")) {
                int p = t.indexOf(' ');
                if(p > 0) {
                    s = t.substring(p + 1).trim();
                } else {
                    s = s.substring(0, i).trim();
                }
            } else {
                s = t;
            }
            return s;
        }
        while(s.contains("..")) {
            s = s.replace("..", ".");
        }
        if(s.startsWith(".")) {
            s = s.substring(1);
        }
        if(s.endsWith(".") && Character.isLowerCase(s.charAt(s.length() - 1))) {
            s = s.substring(0, s.length() - 1);
        }
        int p = 0;
        while((p = s.indexOf('.', p + 1)) > 0 && Character.isLowerCase(s.charAt(p - 1))) {
            s = s.substring(p + 1);
            p = 0;
        }
        if(s.startsWith("\"")) {
            s = s.substring(1);
        }
        if(s.endsWith("\"")) {
            s = s.substring(0, s.length() - 1);
        }
        if(isWhite(s) || s.contains(" ")) {
            return s;
        }
        char c;
        if(i < 0) { // No 'as'
            i = s.indexOf('(');
            p = s.indexOf(')', i);
            if(i >= 0 && p > i) {
                String t;
                boolean pack;
                int j;
                while(i >= 0 && p > i) {
                    t = s.substring(i + 1, p);
                    pack = true;
                    for(j = 0; pack && j < t.length(); j++) {
                        c = t.charAt(j);
                        pack = c == '.' || Character.isLetterOrDigit(c);
                    }
                    if(pack) {
                        s = s.replace("(" + t + ")", "");
                    } else {
                        i = p;
                    }
                    i = s.indexOf('(', i);
                    p = s.indexOf(')', i);
                }
            }
        }
        StringBuilder t = new StringBuilder();
        i = 0;
        while(Character.isWhitespace(s.charAt(i))) {
            ++i;
        }
        char pc;
        c = s.charAt(i);
        boolean wasSpace = !Character.isLetterOrDigit(c), prefix= false;
        if(c == '_') {
            c = '-';
        } else {
            if(!(s.length() > (i+1) && Character.isUpperCase(s.charAt(i + 1)))) {
                c = Character.toUpperCase(c);
            } else {
                prefix = true;
            }
        }
        t.append(c);
        for(++i; i < s.length(); i++) {
            pc = c;
            c = s.charAt(i);
            if(Character.isWhitespace(c)) {
                wasSpace = true;
                continue;
            }
            if(c == '\'') {
                wasSpace = false;
                t.append(c);
                continue;
            }
            if(c == '_') {
                c = '-';
            }
            if(!Character.isLetterOrDigit(c)) {
                if(!(c == ',' || c == ';' || c == ')' || c == ']' || c == '}')) {
                    t.append(' ');
                }
                t.append(c);
                wasSpace = true;
                continue;
            }
            if(Character.isUpperCase(c)) {
                if(prefix) {
                    prefix = false;
                } else {
                    if(!Character.isUpperCase(pc)) {
                        if(!(pc == '(' || pc == '[' || pc == '{')) {
                            t.append(' ');
                        }
                    } else {
                        pc = s.length() > (i + 1) ? s.charAt(i + 1) : 'X';
                        if(Character.isLetterOrDigit(pc) && !Character.isUpperCase(pc)) {
                            t.append(' ');
                        }
                    }
                }
                t.append(c);
                wasSpace = false;
                continue;
            }
            if(wasSpace) {
                if(!(s.length() > (i+1) && Character.isUpperCase(s.charAt(i + 1)))) {
                    c = Character.toUpperCase(c);
                } else {
                    prefix = true;
                }
                if(!(pc == '(' || pc == '[' || pc == '{')) {
                    t.append(' ');
                }
                t.append(c);
                wasSpace = false;
            } else {
                t.append(c);
            }
        }
        String label = t.toString();
        if(label.endsWith(" .")) {
            label = label.substring(0, label.length() - 2) + ".";
        }
        if(!lowerPropositions) {
            return label;
        }
        String r;
        for(String proposition: propositions) {
            r = " " + proposition + " ";
            if(label.contains(r)) {
                label = label.replace(r, r.toLowerCase());
            }
            if(label.endsWith(" " + proposition)) {
                label = label.substring(0, label.length() - proposition.length()) + proposition.toLowerCase();
            }
        }
        label = label.replace(" . ", ".");
        s = "In-house";
        if(label.startsWith("In House")) {
            label = s + label.substring(8);
        }
        if(label.endsWith(" in House")) {
            label = label.substring(0, label.length() - 8) + s;
        }
        s = " " + s + " ";
        label = label.replace(" in House ", s);
        return label;
    }

    /**
     * <p>Make label by inserting a space before each capital letter. Also, all dash characters ("-", "_") are
     * converted into " - ". Single quotes will be copied as such. If "as" keyword is found, the word sequences after
     * that keyword will be used for generating the label.</p>
     * <p>Examples: "ShortName" =&gt; "Short Name", "CountryName" =&gt; "Country Name",
     * "CountryOfDestination as Shipment Country" =&gt; "Shipment Country",
     * "KGB" =&gt; "KGB", "KGBAgent" =&gt; "KGB Agent", "eDocument" =&gt; "eDocument", "eGovernanceInitiative" =&gt;
     * "eGovernance Initiative",
     * "Document_PartI" =&gt; "Document - Part I.</p>
     * <p>Also,</p>
     * <p>(1) class casting type constructs will be removed. Example: "(com.storedobject.core.Person)Employee" =&gt;
     * "Employee"</p>
     * <p>(2) "A.B.C." =&gt; "A.B.C.", "Animal.Lion" =&gt; "Lion" (because the letter just before ".Lion" is in
     * small-case).</p>
     * @param s String to be converted.
     * @return Converted string.
     */
    public static String makeLabel(String s) {
        return makeLabel(s, true);
    }

    /**
     * Convert an integer list to a range string.
     * For example, { 1, 2, 3, 5, 7, 8, 9 } will be converted to "1 - 3, 5, 7 - 9".
     *
     * @param list List of numbers to be converted.
     * @return Range string
     */
    public static String toRangeString(Collection<Integer> list) {
        int i = 0;
        int[] n = new int[list.size()];
        for(Integer v: list) {
            n[i++] = v;
        }
        return toRangeString(n);
    }

    /**
     * Convert an int array into a range string.
     * For example, an array { 1, 2, 3, 5, 7, 8, 9 } will be converted to "1 - 3, 5, 7 - 9".
     *
     * @param n int array of numbers to be converted.
     * @return Range string
     */
    public static String toRangeString(int[] n) {
        if(n == null || n.length == 0) {
            return "";
        }
        Arrays.sort(n);
        int p = n[0];
        StringBuilder s = new StringBuilder("" + p);
        for(int i=1; i<n.length; i++) {
            if(n[i] == (p+1)) {
                p = n[i];
                if(!s.toString().endsWith("-")) {
                    s.append("-");
                }
                continue;
            }
            if(s.toString().endsWith("-")) {
                s.append(p);
            }
            p = n[i];
            s.append(", ").append(p);
        }
        if(s.toString().endsWith("-")) {
            s.append(p);
        }
        return s.toString();
    }

    /**
     * Convert a range string to an int array.
     * For example, "1 - 3, 5, 7 - 9" will be converted to an array { 1, 2, 3, 5, 7, 8, 9 }.
     *
     * @param s String to be converted.
     * @return int array containing the numbers. Null will be returned if the parameter passed contains errors.
     */
    public static int[] toNumericRange(String s) {
        if(s == null) {
            return new int[0];
        }
        s = s.trim();
        while(s.contains(" , ")) {
            s = s.replaceAll(" , ", ",");
        }
        while(s.contains(", ")) {
            s = s.replaceAll(", ", ",");
        }
        while(s.contains(" ,")) {
            s = s.replaceAll(" ,", ",");
        }
        while(s.contains(" - ")) {
            s = s.replaceAll(" - ", "-");
        }
        while(s.contains("- ")) {
            s = s.replaceAll("- ", "-");
        }
        while(s.contains(" -")) {
            s = s.replaceAll(" -", "-");
        }
        if(s.length() == 0) {
            return new int[0];
        }
        if(s.endsWith(",") || s.endsWith("-")) {
            return null;
        }
        int i, r1, r2;
        char c;
        for(i=0; i<s.length(); i++) {
            c = s.charAt(i);
            if(c == ',' || c == '-') {
                if(i == 0) {
                    return null;
                }
                if(!Character.isDigit(s.charAt(i-1))) {
                    return null;
                }
                continue;
            }
            if(!Character.isDigit(c)) {
                return null;
            }
        }
        ArrayList<Integer> v = new ArrayList<>();
        String[] t = s.split(",");
        for(i=0; i<t.length; i++) {
            s = t[i];
            try {
                if(s.indexOf('-') > 0) {
                    r1 = Integer.parseInt(s.substring(0, s.indexOf('-')));
                    v.add(r1);
                    r2 = Integer.parseInt(s.substring(s.indexOf('-')+1));
                    while((++r1) < r2) {
                        v.add(r1);
                    }
                    v.add(r2);
                } else {
                    v.add(Integer.parseInt(s));
                }
            } catch(Exception ignored) {
            }
        }
        int[] n = new int[v.size()];
        for(i=0; i<n.length; i++) {
            n[i] = v.get(i);
        }
        Arrays.sort(n);
        return n;
    }

    /**
     * Converts a string into a 4 character soundex code.
     *
     * @param string The string to be converted.
     * @return The soundex coded string.
     */
    public static String soundex(String string) {
        int i;
        char[] x = string.toUpperCase().toCharArray();
        char firstLetter = x[0];
        for(i = 0; i < x.length; i++) {
            switch(x[i]) {
                case 'B', 'F', 'P', 'V' -> x[i] = '1';
                case 'C', 'G', 'J', 'K', 'Q', 'S', 'X', 'Z' -> x[i] = '2';
                case 'D', 'T' -> x[i] = '3';
                case 'L' -> x[i] = '4';
                case 'M', 'N' -> x[i] = '5';
                case 'R' -> x[i] = '6';
                default -> x[i] = '0';
            }
        }
        StringBuilder output = new StringBuilder("" + firstLetter);
        char last = x[0];
        for(i = 1; i < x.length; i++) {
            if(x[i] != '0' && x[i] != last) {
                last = x[i];
                output.append(last);
            }
        }
        for(i = output.length(); i < 4; i++) {
            output.append('0');
        }
        return output.substring(0, 4);
    }

    /**
     * Gets the number of occurrences of a given character in the string.
     *
     * @param text String to be scanned.
     * @param c Character to look for.
     * @return Occurrences
     */
    public static int getCharCount(String text, char c) {
        if(text == null) {
            return 0;
        }
        int p = 0, count = 0;
        while((p = text.indexOf(c, p)) >= 0) {
            ++count;
            ++p;
        }
        return count;
    }

    /**
     * Gets the number of occurrences of a given string in this string.
     *
     * @param text String to be scanned.
     * @param pattern String to look for.
     * @return Occurrences
     */
    public static int getStringCount(String text, String pattern) {
        if(text == null || pattern == null || pattern.length() > text.length()) {
            return 0;
        }
        int p = 0, count = 0;
        while((p = text.indexOf(pattern, p)) >= 0) {
            ++count;
            p += pattern.length();
        }
        return count;
    }

    /**
     * Replaces all occurrences of 'target' with 'replacement' outside the quoted portion (single or double quotes).
     * @param text String to operated on.
     * @param target Target sequence to replace
     * @param replacement Replacement text
     * @return Replaced string
     */
    public static String replaceOutside(String text, CharSequence target, CharSequence replacement) {
        if(!text.contains(target)) {
            return text;
        }
        StringBuilder s = new StringBuilder();
        int p = 0, n = 0;
        char c, quote = 0;
        while(n < text.length()) {
            c = text.charAt(n);
            if(quote == 0) {
                if(c == '\'' || c == '"') {
                    quote = c;
                    if(p < n) {
                        s.append(text.substring(p, n).replace(target, replacement));
                    }
                    p = n;
                }
            } else {
                if(c == quote) {
                    quote = 0;
                    s.append(text, p, n + 1);
                    p = n + 1;
                }
            }
            n++;
        }
        if(p < n) {
            s.append(text.substring(p, n).replace(target, replacement));
        }
        return s.toString();
    }

    /**
     * Encode and return the string representation of the object passed. This encoding is useful for
     * creating strings that can be used in XML texts. That is, it replaces characters such as "&lt;", "&amp;", "&gt;" etc. the respective
     * encoded values "&amp;lt;", "&amp;amp;", "&amp;gt;" etc.
     *
     * @param obj The object to convert.
     * @return XML encoded string.
     */
    public static String encodeForXML(Object obj) {
        if(obj == null) {
            return "";
        }
        String text = obj.toString();
        if(text.indexOf('&') >= 0) {
            text = text.replace("&", "&amp;");
        }
        if(text.indexOf('<') >= 0) {
            text = text.replace("<", "&lt;");
        }
        if(text.indexOf('>') >= 0) {
            text = text.replace(">", "&gt;");
        }
        if(text.indexOf('"') >= 0) {
            text = text.replace("\"", "&quot;");
        }
        if(text.indexOf('\'') >= 0) {
            text = text.replace("'", "&apos;");
        }
        return text;
    }

    /**
     * Compute the modulus 11 check digit for the number passed.
     *
     * @param number Number
     * @return Check digit. -1 is returned for invalid numbers.
     */
    public static int modulus11(long number) {
        return modulus11("" + number);
    }

    /**
     * Compute the modulus 11 check digit for the alphanumeric string passed.
     *
     * @param alphanumeric Input
     * @return Check digit. -1 is returned for invalid numbers and -2 is returned for invalid strings.
     */
    public static int modulus11(String alphanumeric) {
        if(alphanumeric == null) {
            return -2;
        }
        if(alphanumeric.startsWith("-") || alphanumeric.startsWith("+")) {
            alphanumeric = alphanumeric.substring(1);
        }
        if(alphanumeric.length() == 0) {
            return -2;
        }
        char c;
        int w = 2, d, total = 0;
        for(int i = alphanumeric.length() - 1; i >= 0; i--) {
            c = alphanumeric.charAt(i);
            if(Character.isWhitespace(c)) {
                d = 0;
            } else if(Character.isDigit(c)) {
                d = c - '0';
            } else if(Character.isLetter(c)) {
                c = Character.toUpperCase(c);
                d = ((c - 'A') % 10) + 1;
            } else {
                return -2;
            }
            total += w * d;
            if(++w == 8) {
                w = 2;
            }
        }
        total = 11 - (total % 11);
        return total == 11 ? 0 : (total == 10 ? -1 : total);
    }

    /**
     * Check the alphanumeric string passed to see if its ends with a modulus 11 check digit.
     *
     * @param alphanumeric Input
     * @return True or false.
     */
    public static boolean isModulus11(String alphanumeric) {
        if(alphanumeric == null || alphanumeric.length() < 2) {
            return false;
        }
        String s = alphanumeric.substring(0, alphanumeric.length() - 1);
        return (s + modulus11(s)).equals(alphanumeric);
    }

    /**
     * Check the number passed to see if its ends with a modulus 11 check digit.
     *
     * @param number Number
     * @return True or false.
     */
    public static boolean isModulus11(long number) {
        return isModulus11("" + number);
    }

    /**
     * Stringify the object
     *
     * @param object Object to be stringified.
     * @return String obtained from object.toString(). If object is null or toString method generate errors zero-length string will return.
     */
    public static String stringify(Object object) {
        String s;
        try {
            s = object.toString();
            return s == null ? "" : s;
        } catch(Throwable ignore) {
        }
        return "";
    }

    /**
     * Get a duplicate string array copy of the array passed. Each element from the source array will be stringified.
     *
     * @param objects Array of any object copy.
     * @return Copy of the array as string array. If the input is null, output will also be null.
     * Null elements in the source array will generate null element in the output array.
     */
    public static String[] copy(Object[] objects) {
        if(objects == null) {
            return null;
        }
        String[] t = new String[objects.length];
        int i = 0;
        for(Object object: objects) {
            t[i++] = object == null ? null : object.toString();
        }
        return t;
    }

    /**
     * Get a char array from a String array.
     *
     * @param s Array to be converted.
     * @return character array created by taking the first characters of the String array. For null values and values of length zeros,
     * a space is set for the corresponding character values.
     */
    public static char[] toCharArray(String[] s) {
        if(s == null) {
            return null;
        }
        char[] t = new char[s.length];
        for(int i = 0; i < s.length; i++) {
            t[i] = s[i] == null || s[i].length() == 0 ? ' ' : s[i].charAt(0);
        }
        return t;
    }

    /**
     * Insert a new element at the given index.
     *
     * @param a Array for inserting the the new element.
     * @param element Element to be inserted.
     * @param index Index
     * @return Resultant array with length greater than the length of the array passed.
     */
    public static String[] insert(String[] a, String element, int index) {
        if(a == null || index >= a.length) {
            return append(a, element);
        }
        String[] t = new String[a.length + 1];
        t[index] = element;
        if(index > 0) {
            System.arraycopy(a, 0, t, 0, index);
        }
        if(index < (t.length - 1)) {
            System.arraycopy(a, index, t, index + 1, a.length - index);
        }
        return t;
    }

    /**
     * Append a new element to an array.
     *
     * @param a Array to be increased.
     * @param element Element to be added.
     * @return Resultant array with length greater than the length of the array passed.
     *
     */
    public static String[] append(String[] a, String element) {
        String[] t = new String[a == null ? 1 : a.length + 1];
        if(a != null && a.length > 0) {
            System.arraycopy(a, 0, t, 0, a.length);
        }
        t[t.length - 1] = element;
        return t;
    }

    /**
     * Remove the first instance of an element from an array.
     *
     * @param a Array.
     * @param element Element to be removed.
     * @return Resultant array.
     *
     */
    public static String[] remove(String[] a, String element) {
        if(a == null) {
            return null;
        }
        int i;
        for(i = 0; i < a.length; i++) {
            if(a[i].equals(element)) {
                String[] t = new String[a.length - 1];
                if(t.length == 0) {
                    return t;
                }
                System.arraycopy(a, 0, t, 0, i);
                if(i == t.length) {
                    return t;
                }
                System.arraycopy(a, i + 1, t, i, t.length - i);
                return t;
            }
        }
        return a;
    }

    /**
     * Remove all instances of an element from an array.
     *
     * @param a Array.
     * @param element Element to be removed.
     * @return Resultant array.
     *
     */
    public static String[] removeAll(String[] a, String element) {
        if(element == null) {
            return removeNulls(a);
        }
        while(indexOf(a, element) >= 0) {
            a = remove(a, element);
        }
        return a;
    }

    /**
     * Concatenate two strings.
     *
     * @param first First string.
     * @param second Second string.
     * @return Concatenated string.
     */
    public static String[] concat(String[] first, String[] second) {
        if(first == null && second == null) {
            return new String[0];
        }
        if(first == null || first.length == 0) {
            return second;
        }
        if(second == null || second.length == 0) {
            return first;
        }
        String[] s = new String[first.length + second.length];
        int i = 0;
        for(String t: first) {
            s[i++] = t;
        }
        for(String t: second) {
            s[i++] = t;
        }
        return s;
    }

    /**
     * Find the index of an element in an array.
     *
     * @param a Array.
     * @param element Element to be searched.
     * @return First index of the element. -1 is returned if the array is null or the element is not found.
     *
     */
    public static int indexOf(String[] a, String element) {
        return indexOf(a, element, 0);
    }

    /**
     * Find the index of an element in an array.
     *
     * @param a Array.
     * @param element Element to be searched.
     * @param from From index
     * @return First index of the element. -1 is returned if the array is null or the element is not found.
     *
     */
    public static int indexOf(String[] a, String element, int from) {
        return a == null ? -1 : indexOf(a, element, from, a.length);
    }

    /**
     * Find the index of an element in an array.
     *
     * @param a Array.
     * @param element Element to be searched.
     * @param from From index
     * @param to To index
     * @return First index of the element. -1 is returned if the array is null or the element is not found.
     *
     */
    public static int indexOf(String[] a, String element, int from, int to) {
        if(a == null) {
            return -1;
        }
        if(from < 0) {
            from = 0;
        }
        int i;
        if(element == null) {
            for(i = from; i < a.length; i++) {
                if(i >= to) {
                    return -1;
                }
                if(a[i] == null) {
                    return i;
                }
            }
            return -1;
        }
        for(i = from; i < a.length; i++) {
            if(i >= to) {
                return -1;
            }
            if(element.equals(a[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Split the string at the given length into an array. New line characters will be treated
     * as if splitting is needed at that position. An attempt is made to split at word boundaries
     * if possible. "\r" (carriage return) characters will be removed.
     *
     * @param s String to split.
     * @param length Length at which split happens.
     * @return Array of strings containing each part of the string.
     **/
    public static String[] split(String s, int length) {
        if(s == null) {
            return new String[0];
        }
        if(s.indexOf('\r') >= 0) {
            s = s.replace("\r", "");
        }
        int pos = s.indexOf('\n');
        if(pos >= 0) {
            return concat(split(s.substring(0, pos), length), split(s.substring(pos + 1), length));
        }
        s = s.trim();
        if(s.length() <= length || length <= 0) {
            return new String[] { s };
        }
        pos = length;
        while(pos >= 0) {
            if(Character.isWhitespace(s.charAt(pos))) {
                return concat(new String[] { s.substring(0, pos).trim() }, split(s.substring(pos + 1), length));
            }
            --pos;
        }
        return concat(new String[] { s.substring(0, length) }, split(s.substring(length), length));
    }

    /**
     * Parse the string array into "properties". This is useful to parse "command line" parameters.
     *
     * @param args Argument list as string array.
     * @return Properties containing all the argument values passed as the array.
     * @throws SOException if anything in the argument does not look like a "command line" parameter.
     */
    public static Properties parseToProperties(String[] args) throws SOException {
        Properties p = new MultipleKeysProperties();
        if(args == null || args.length == 0) {
            return p;
        }
        int i = 0;
        try {
            if(args.length == 1 && !args[0].startsWith("-")) {
                String f = args[0];
                InputStream in = IO.getInput(f);
                if(f.endsWith(".XML") || f.endsWith(".xml")) {
                    p.loadFromXML(in);
                } else {
                    p.load(in);
                }
                return p;
            }
            String key = null;
            while(i < args.length) {
                if(args[i].equals("-")) {
                    ++i;
                    key = args[i];
                    p.put(key, "1");
                } else if(args[i].startsWith("-")) {
                    key = args[i].substring(1);
                    p.put(key, "1");
                } else {
                    if(key == null) {
                        throw new SOException("Invalid_Value '" + args[i] + "'");
                    }
                    p.put(key, args[i]);
                    key = null;
                }
                ++i;
            }
        } catch(Exception e) {
            throw new SOException("Invalid_Value '" + args[i] + "'");
        }
        return p;
    }

    @SuppressWarnings("serial")
    static class MultipleKeysProperties extends Properties {

        @Override
        public String getProperty(String key) {
            if(key.indexOf(',') < 0) {
                return super.getProperty(key);
            }
            String v;
            String[] keys = key.split(",");
            for(String k: keys) {
                v = super.getProperty(k.trim());
                if(v != null) {
                    return v;
                }
            }
            return null;
        }

        @Override
        public String getProperty(String key, String value) {
            String v = getProperty(key);
            return v == null ? value : v;
        }
    }

    /**
     * Find the Levenshtein distance between two strings.
     *
     * @param s First string
     * @param t Second string
     * @return Levenstein distance
     */
    public static int distanceLevenshtein(String s, String t) {
        if(s == null) {
            s = "";
        }
        if(t == null) {
            t = "";
        }
        s = s.toUpperCase();
        t = t.toUpperCase();
        int n = s.length(); // length of s
        int m = t.length(); // length of t
        if(n == 0) {
            return m;
        } else if(m == 0) {
            return n;
        }
        int[] p = new int[n + 1]; //'previous' cost array, horizontally
        int[] d = new int[n + 1]; // cost array, horizontally
        int[] _d; //placeholder to assist in swapping p and d
        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t
        char t_j; // jth character of t
        int cost; // cost
        for (i = 0; i<=n; i++) {
            p[i] = i;
        }
        for (j = 1; j<=m; j++) {
            t_j = t.charAt(j-1);
            d[0] = j;
            for (i=1; i<=n; i++) {
                cost = s.charAt(i-1) == t_j ? 0 : 1;
                // minimum of cell to the left + 1, to the top + 1, diagonally left and up +cost
                d[i] = Math.min(Math.min(d[i-1] + 1, p[i] + 1),  p[i-1] + cost);
            }
            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }
        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        return p[n];
    }

    public static String bitsValue(int value, String[] valueLabels) {
        return bitsValue(value, StringList.create(valueLabels));
    }

    public static String bitsValue(int value, StringList valueLabels) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while(value > 0 && i < valueLabels.size()) {
            if((value & 1) == 1) {
                if(sb.length() > 0) {
                    sb.append(" & ");
                }
                sb.append(valueLabels.get(i));
            }
            ++i;
            value >>= 1;
        }
        return sb.toString();
    }

    @SuppressWarnings("serial")
    public static class List extends ArrayList<String> {

        public List(String[] array) {
            if(array == null) {
                return;
            }
            this.addAll(Arrays.asList(array));
        }
    }

    public static boolean same(CharSequence one, CharSequence two) {
        int len = one.length();
        if(len != two.length()) {
            return false;
        }
        for(int i = 0; i < len; i++) {
            if(one.charAt(i) != two.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Formats a double value as a numeric string. Up to 14 fractional digits will be considered. If you want the
     * thousands separation, please use {@link #format(double, boolean)}.
     *
     * @param value Double value to be converted.
     * @return Formatted value
     */
    public static String format(double value) {
        return format(value, -1, false);
    }

    /**
     * Formats a double value as a numeric string. Up to 14 fractional digits will be considered.
     *
     * @param value Double value to be converted.
     * @param separated True if thousands separation is needed in the output.
     * @return Formatted value
     */
    public static String format(double value, boolean separated) {
        return format(value, -1, separated);
    }

    /**
     * Formats a double value as a numeric string (Indian style). Up to 14 fractional digits will be considered.
     *
     * @param value Double value to be converted.
     * @param separated True if thousands separation is needed in the output.
     * @return Formatted value
     */
    public static String formatIndian(double value, boolean separated) {
        return formatIndian(value, -1, separated);
    }

    /**
     * Formats a double value as a numeric string with thousands separation.
     *
     * @param value Double value to be converted.
     * @param decimals Number of decimals required in the output string. Passing -1 causes all non-zero decimals up to
     * 14 positions to be incorporated.
     * @return Formatted value
     */
    public static String format(double value, int decimals) {
        return format(value, decimals,false);
    }

    private static final NumberFormat format = NumberFormat.getNumberInstance();
    static {
        format.setMaximumIntegerDigits(30);
        format.setRoundingMode(RoundingMode.HALF_UP);
    }

    /**
     * Formats a double value as a numeric string with thousands separation.
     *
     * @param value Double value to be converted.
     * @param decimals Number of decimals required in the output string. Passing -1 causes all non-zero decimals up to
     * 14 positions to be incorporated.
     * @param separated True if thousands separation is needed in the output.
     * @return Formatted value
     */
    public static String format(double value, int decimals, boolean separated) {
        return format(value, decimals, separated,false);
    }

    /**
     * Formats a double value as a numeric string with thousands separation (Indian style).
     *
     * @param value Double value to be converted.
     * @param decimals Number of decimals required in the output string. Passing -1 causes all non-zero decimals up to
     * 14 positions to be incorporated.
     * @param separated True if thousands separation is needed in the output.
     * @return Formatted value
     */
    public static String formatIndian(double value, int decimals, boolean separated) {
        return format(value, decimals, separated,true);
    }

    private static String format(double value, int decimals, boolean separated, boolean indian) {
        boolean negative = value < 0;
        if(negative) {
            value = -value;
        }
        if(decimals < 0) {
            format.setMinimumFractionDigits(decimals < -1 ? -decimals : 14);
            format.setMaximumFractionDigits(decimals < -1 ? -decimals : 14);
        } else {
            format.setMinimumFractionDigits(decimals);
            format.setMaximumFractionDigits(decimals);
        }
        String s = format.format(value);
        if(decimals < 0) {
            if(s.indexOf('.') >= 0) {
                while(s.endsWith("0") && !s.endsWith(".0")) {
                    s = s.substring(0, s.length() - 1);
                }
            }
            return (negative ? "-" : "") + format(s, s.endsWith(".0") ? 0 : s.length() - s.indexOf('.') - 1,
                    separated, indian);
        }
        return (negative ? "-" : "") + format(s, decimals, separated, indian);
    }

    /**
     * Formats a string as a numeric string with thousands separation.
     *
     * @param s String of digits (can contain a decimal point).
     * @param decimals Number of decimals required in the output string.
     * @param separated True if thousands separation is needed in the output.
     * @return Formatted value
     */
    public static String format(String s, int decimals, boolean separated) {
        return format(s, decimals, separated,false);
    }

    /**
     * Formats a string as a numeric string with thousands separation (Indian style).
     *
     * @param s String of digits (can contain a decimal point).
     * @param decimals Number of decimals required in the output string.
     * @param separated True if thousands separation is needed in the output.
     * @return Formatted value
     */
    public static String formatIndian(String s, int decimals, boolean separated) {
        return format(s, decimals, separated,true);
    }

    private static String format(String s, int decimals, boolean separated, boolean indian) {
        if(s == null) {
            s = "0";
        }
        if(s.startsWith("+")) {
            s = s.substring(1);
        } else if(s.endsWith("+")) {
            s = s.substring(0, s.length()-1);
        }
        int neg = 0;
        if(s.startsWith("-")) {
            neg = 1;
            s = s.substring(1);
        } else if(s.endsWith("-")) {
            neg = 2;
            s = s.substring(0, s.length()-1);
        } else {
            if(s.endsWith("DB") || s.endsWith("CR")) {
                String substring = s.substring(0, s.length() - 2);
                if(s.endsWith("DB")) {
                    neg = 3;
                } else {
                    neg = 4;
                }
                s = substring.trim();
            }
        }
        StringBuilder sb = new StringBuilder();
        char c;
        boolean decimalFound = false;
        for(int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if(c == '.') {
                if(decimalFound) {
                    break;
                }
                decimalFound = true;
            } else if(!Character.isDigit(c)) {
                if(c == ',') {
                    continue;
                }
                break;
            }
            sb.append(c);
        }
        s = sb.toString();
        int d = decimals;
        int p = s.indexOf('.');
        String decimalDigits = null;
        if(d < 0 && p >= 0) {
            decimalDigits = s.substring(p);
            s = s.substring(0, p);
            while(true) {
                if(decimalDigits.length() <= 1) {
                    decimalDigits = null;
                    break;
                }
                if(decimalDigits.endsWith("0")) {
                    decimalDigits = decimalDigits.substring(0, decimalDigits.length() - 1);
                } else {
                    break;
                }
            }
            p = -1;
        }
        StringBuilder t;
        if(p >= 0) {
            t = new StringBuilder(s.substring(p));
            if(p > 0) {
                s = s.substring(0, p);
            } else {
                s = "0";
            }
        } else {
            t = new StringBuilder(".");
        }
        if(d == 0) {
            t = new StringBuilder();
        } else {
            d -= t.length();
            ++d;
            while(d > 0) {
                t.append("0");
                --d;
            }
            if(t.length() > (decimals+1)) {
                t = new StringBuilder(t.substring(0, decimals + 1));
            }
        }
        if(separated) {
            int group = 3;
            while (true) {
                p = s.length() - group;
                if (p <= 0) {
                    break;
                }
                t.insert(0, "," + s.substring(p));
                s = s.substring(0, p);
                if(indian && group == 3) {
                    group = 2;
                }
            }
        }
        s += t;
        if(decimalDigits != null) {
            s += decimalDigits;
        }
        switch(neg) {
            case 1 -> s = "-" + s;
            case 2 -> s += "-";
            case 3 -> s += " DB";
            case 4 -> s += " CR";
        }
        return s;
    }

    private static final String[] set1 = { "one", "two", "three", "four", "five", "six", "seven", "eight", "nine" };
    private static final String[] set2 = { "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
            "seventeen", "eighteen", "nineteen" };
    private static final String[] set3 = { "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty",
            "ninety" };

    /**
     * Internal function to convert a 3 digit number to words. It will return empty string for zero.
     * @param digit A 3 digit number as string
     * @return 3 digit number in words.
     */
    private static String words3(String digit) {
        StringBuilder digitBuilder = new StringBuilder(digit);
        while(digitBuilder.length() < 3) {
            digitBuilder.insert(0, "0");
        }
        digit = digitBuilder.toString();
        char c0 = digit.charAt(0), c1 = digit.charAt(1), c2 = digit.charAt(2);
        String v = "";
        if(c0 != '0') {
            v = set1[c0 - '1'] + " hundred";
            if((c1 != '0') || (c2 != '0')) {
                v += " and ";
            }
        }
        if(c1 != '0') {
            if(c1 == '1') {
                v += set2[c2 - '0'];
                return v;
            } else {
                v += set3[c1 - '2'];
            }
            if(c2 != '0') {
                v += " " ;
            }
        }
        if( c2 != '0' ) {
            v += set1[c2 - '1'];
        }
        return v;
    }

    private final static String zero = "zero";

    /**
     * Converts a numeric value into words. Decimal portion and negative part will be ignored
     * @param value Value
     * @return Value in words
     */
    public static String words(Number value) {
        if(value == null) {
            return "zero";
        }
        if(value instanceof Integer || value instanceof Long) {
            return words(BigInteger.valueOf(value.longValue()));
        }
        if(value instanceof Float || value instanceof Double) {
            return words(BigDecimal.valueOf(value.doubleValue()));
        }
        if(value instanceof BigInteger) {
            return words((BigInteger)value);
        }
        if(value instanceof BigDecimal) {
            return words((BigDecimal) value);
        }
        return words(BigDecimal.valueOf(value.doubleValue()));
    }

    /**
     * Converts a numeric value into words. Decimal portion and negative part will be ignored
     * @param value Value
     * @return Value in words
     */
    public static String words(BigDecimal value) {
        return value == null ? zero : words(value.toBigInteger());
    }

    /**
     * Converts a numeric value into words. Negative part will be ignored
     * @param value Value
     * @return Value in words
     */
    public static String words(BigInteger value) {
        if(value == null) {
            return zero;
        }
        if(value.signum() < 0) {
            value = value.negate();
        }
        return words(value.toString());
    }

    /**
     * Converts a numeric value into words in Indian format. Decimal portion and negative part will be ignored
     * @param value Value
     * @return Value in words in Indian format
     */
    public static String wordsIndian(BigDecimal value) {
        return value == null ? zero : wordsIndian(value.toBigInteger());
    }

    /**
     * Converts a numeric value into words in Indian format. Decimal portion and negative part will be ignored
     * @param value Value
     * @return Value in words in Indian format
     */
    public static String wordsIndian(BigInteger value) {
        if(value == null) {
            return zero;
        }
        if(value.signum() < 0) {
            value = value.negate();
        }
        return wordsIndian(value.toString()).replace(",", "");
    }

    private final static String[] crores = new String[]{"", " thousand", " lakhs", " crores"};
    private final static String[] millions = new String[]{"", " thousand", " million", " billion", " trillion",
            " quadrillion", " quintillion", " sextillion", " septillion"};

    /**
     * Internal method to convert a numerical non-empty string to words.
     * @param s Numerial non-empty string
     * @return Words
     */
    private static String words(String s) {
        if(s.length() > (3 * millions.length)) {
            int n = s.length() - (millions.length - 1) * 3;
            return words(s.substring(0, n)) + millions[millions.length - 1] + ", " + words(s.substring(n));
        }
        if(s.equals("0")) {
            return zero;
        }
        if(s.length() <= 3) {
            return words3(s);
        }
        return words(s, 0);
    }

    private static String words(String s, int i) {
        String w;
        if(s.length() <= 3) {
            w = words3(s);
            return w.isEmpty() ? w : (w + millions[i]);
        }
        String p = words(s.substring(0, s.length() - 3), i + 1);
        w = words(s.substring(s.length() - 3));
        if(w.isEmpty()) {
            return p;
        }
        return p + ", " + w + millions[i];
    }

    /**
     * Internal method to convert a numerical non-empty string to words in Indian style.
     * @param s Numerial non-empty string
     * @return Words
     */
    private static String wordsIndian(String s) {
        if(s.length() > (3 * (crores.length - 1) + 2)) {
            int n = s.length() - ((crores.length - 2) * 2 + 3);
            return wordsIndian(s.substring(0, n)) + crores[crores.length - 1] + ", " + wordsIndian(s.substring(n));
        }
        if(s.equals("0")) {
            return zero;
        }
        if(s.length() <= 3) {
            return words3(s);
        }
        return wordsIndian(s, 0);
    }

    private static String wordsIndian(String s, int i) {
        String w;
        if(s.length() <= 2) {
            w = words3(s);
            return w.isEmpty() ? w : (w + crores[i]);
        }
        int endIndex = s.length() - (i == 0 ? 3 : 2);
        String p = wordsIndian(s.substring(0, endIndex), i + 1);
        w = wordsIndian(s.substring(endIndex));
        if(w.isEmpty()) {
            return p;
        }
        return p + ", " + w + crores[i];
    }

    /**
     * See if 2 character sequences contain same sequence of characters
     *
     * @param one First character sequence.
     * @param two Second character sequence.
     * @return True/False.
     */
    public static boolean equals(CharSequence one, CharSequence two) {
        if(one == null && two == null) {
            return true;
        }
        if(one == null || two == null) {
            return false;
        }
        int i = 0;
        while(true) {
            if(one.length() == i && two.length() == i) {
                return true;
            }
            if(one.length() == i || two.length() == i) {
                return false;
            }
            if(one.charAt(i) != two.charAt(i)) {
                return false;
            }
            ++i;
        }
    }

    public static String getTrace(Throwable error) {
        return SORuntimeException.getTrace(error);
    }

    public static String getTrace(Thread thread) {
        return SORuntimeException.getTrace(thread);
    }

    public static String toString(Object message) {
        if(message instanceof Displayable) {
            return ((Displayable)message).toDisplay();
        }
        return toStringInt(message);
    }

    static String toStringInt(Object message) {
        if(message == null) {
            return "[No data]";
        }
        if(message instanceof Double) {
            String s = message.toString();
            if(s.endsWith(".0") && s.length() > 2) {
                s = s.substring(0, s.length() - 2);
            }
            return s;
        }
        if(message instanceof String) {
            return (String)message;
        }
        if(message instanceof java.sql.Time) {
            return DateUtility.timeFormat().format((java.sql.Time)message);
        }
        if(message instanceof java.util.Date) {
            return DateUtility.format((java.util.Date)message);
        }
        if(message instanceof Boolean) {
            return (Boolean) message ? "Yes" : "No";
        }
        if(message instanceof EndUserMessage) {
            return ((EndUserMessage) message).getEndUserMessage();
        }
        if(message instanceof Throwable) {
            message = new SORuntimeException((Throwable)message);
        }
        String m = message.toString();
        return m == null ? ("[NULL value] of " + message.getClass()) : m;
    }

    public static String toString(InputStream stream) throws Exception {
        return toString(IO.getReader(stream));
    }

    public static String toString(Reader reader) throws Exception {
        StringCollector sc = new StringCollector(reader);
        String s = sc.getString();
        Exception e = sc.getException();
        if(e != null) {
            throw e;
        }
        return s;
    }

    private static class CSVField {
        private String field;
        private boolean more = false;
        private int nextPosition;
    }

    private static CSVField nextField(String line, int p, boolean inside, int lineNumber) throws Exception {
        if(p > line.length()) {
            return null;
        }
        if(p == line.length()) {
            CSVField f = new CSVField();
            f.field = "";
            f.nextPosition = ++p;
            f.more = inside;
            return f;
        }
        StringBuilder s = new StringBuilder();
        char c;
        while(true) {
            if(p >= line.length()) {
                break;
            }
            c = line.charAt(p++);
            if(inside) {
                if(c == '"') {
                    if(p >= line.length()) {
                        inside = false;
                        break;
                    }
                    c = line.charAt(p);
                    if(c == '"') {
                        ++p;
                    } else {
                        inside = false;
                        break;
                    }
                }
                s.append(c);
                continue;
            }
            if(c == '"' && s.length() == 0) {
                inside = true;
                continue;
            } else if(c == ',') {
                --p;
                break;
            }
            s.append(c);
        }
        if(!inside && p < line.length() && line.charAt(p) != ',') {
            throw new SOException("Comma expected at position " + p +
                    (lineNumber > -1 ? (", line " + lineNumber) : ""));
        }
        ++p;
        CSVField f = new CSVField();
        f.field = s.toString();
        f.nextPosition = p;
        f.more = inside;
        return f;
    }

    public static String[] getCSV(BufferedReader reader) throws Exception {
        String line = reader.readLine();
        if(line == null || line.length() == 0) {
            return null;
        }
        ArrayList<String> v = new ArrayList<>();
        CSVField field;
        int p, i = 0, lineNumber;
        lineNumber = reader instanceof LineNumberReader ? ((LineNumberReader)reader).getLineNumber() : -1;
        boolean more = false;
        while(i <= line.length()) {
            field = nextField(line, i, more, lineNumber);
            if(field == null) {
                break;
            }
            i = field.nextPosition;
            if(more) {
                p = v.size() - 1;
                v.set(p, v.get(p) + field.field);
            } else {
                v.add(field.field);
            }
            more = field.more;
            if(!more) {
                continue;
            }
            do {
                line = reader.readLine();
                if(line == null) {
                    throw new SOException("Line termination" + (lineNumber > -1 ? (", line " + lineNumber) : ""));
                }
                p = v.size() - 1;
                v.set(p, v.get(p) + "\n");
            } while(line.length() <= 0);
            i = 0;
        }
        return v.size() == 0 ? null : toArray(v);
    }

    /**
     * Replace variables in the source string with values returned by the filler. Variables in the source string are of the
     * form ${VariableName}.
     * A sample source may look like "My name is ${name} and my age is ${age}". Here, 'name' and 'age' are the variables and
     * the values returned by filler.fill("name") and filler.fill("age") will be used to replace the variables in the string. So,
     * the resulting string will look like "My name is Syam Pillai and my age is 40" if the return values are "Syam Pillai" and "40"
     * respectively.
     *
     * @param source The source string.
     * @param filler The filler logic.
     * @return Result of substituting the variables in the source string with respective values returned by the filler logic.
     */
    public static String fill(String source, StringFiller filler) {
        if(filler == null) {
            return source;
        }
        StringBuilder s = new StringBuilder();
        StringTokenizer st = new StringTokenizer(source, "${}", true);
        String t, v;
        boolean expectVar = false;
        while(st.hasMoreTokens()) {
            t = st.nextToken();
            if(t.equals("$")) {
                if(expectVar) {
                    s.append('$');
                }
                expectVar = true;
                if(st.hasMoreTokens()) {
                    t = st.nextToken();
                } else {
                    break;
                }
            }
            if(expectVar && t.equals("{")) {
                if(st.hasMoreTokens()) {
                    v = st.nextToken();
                    if(v.equals("$")) {
                        s.append("${");
                        continue;
                    }
                    if(st.hasMoreTokens()) {
                        t = st.nextToken();
                        if(t.equals("}") && Character.isLetter(v.charAt(0)) &&
                                isLetterOrDigit(v.replace(":", "").replace(".", "").
                                        replace("-", ""))) {
                            s.append(filler.fill(v));
                        } else {
                            s.append("${").append(v).append(t);
                        }
                    } else {
                        s.append('$').append(t).append(v);
                    }
                } else {
                    s.append('$').append(t);
                }
                expectVar = false;
            } else {
                if(expectVar) {
                    s.append('$');
                    expectVar = false;
                }
                s.append(t);
            }
        }
        if(expectVar) {
            s.append('$');
        }
        return s.toString();
    }

    public static <T extends java.util.List<String>> T collect(T list, String[] items) {
        return collect(list, StringList.create(items));
    }

    public static <T extends java.util.List<String>> T collect(T list, StringList items) {
        list.addAll(items);
        return list;
    }

    private static final char[] hexDigits = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * MD5 encryption
     *
     * @param input Input string to encrypt
     * @return Encrypted output in hex-coded form
     */
    public static String md5(String input) {
        byte[] source;
        source = input.getBytes(StandardCharsets.UTF_8);
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            byte[] temp = md.digest();
            char[] str = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = temp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            result = new String(str);
        } catch (Exception ignored) {
        }
        return result;
    }
}
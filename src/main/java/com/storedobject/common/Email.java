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
 * Class used to check email address format.
 *
 * @author Syam
 */
public final class Email {

    /**
     * Check email format for the given email.
     *
     * @param email Email
     * @return Email (may be modified).
     * @throws SOException If the email format is not valid
     */
    public static String check(String email) throws SOException {
        return check(email, false);
    }

    /**
     * Check email format for the given email.
     *
     * @param email Email
     * @param allowEmpty Whether empty should be taken as valid or not
     * @return Email (may be modified).
     * @throws SOException If the email format is not valid
     */
    public static String check(String email, boolean allowEmpty) throws SOException {
        if(StringUtility.isWhite(email)) {
            if(allowEmpty) {
                return "";
            }
            throw new SOException("Empty email");
        }
        email = email.trim();
        //noinspection LoopStatementThatDoesntLoop
        while(true) {
            int p;
            if((p = email.indexOf('@')) < 0) {
                break;
            }
            if(p != email.lastIndexOf('@')) {
                break;
            }
            if(invalidEmailPart(email.substring(0, p), "!#$%&'*+-/=?^_`{|}~".toCharArray())) {
                break;
            }
            String server = email.substring(p + 1);
            if(server.indexOf('.') < 0) {
                break;
            }
            if(invalidEmailPart(server, new char[]{'-'})) {
                break;
            }
            return email;
        }
        throw new SOException("Invalid Email - " + email);
    }

    private static boolean invalidEmailPart(String part, char[] specials) {
        if(part.isEmpty()) {
            return true;
        }
        if(StringUtility.isLetterOrDigit(part)) {
            return false;
        }
        if('.' == part.charAt(0) || '.' == part.charAt(part.length() - 1)) {
            return true;
        }
        boolean valid;
        char previous = 0;
        for(char cc: part.toCharArray()) {
            if(Character.isLetterOrDigit(cc)) {
                previous = cc;
                continue;
            }
            if(cc == '.' && previous == '.') {
                return true;
            }
            previous = cc;
            if(cc == '.') {
                continue;
            }
            valid = false;
            for(char c: specials) {
                if(c == cc) {
                    valid = true;
                    break;
                }
            }
            if(!valid) {
                return true;
            }
        }
        return false;
    }
}

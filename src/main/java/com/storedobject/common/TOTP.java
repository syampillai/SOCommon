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

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

public class TOTP {

    private static final int[] DIGITS_POWER  = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000 };
    private static final String AlgorithmKEY = "RAW";
    private static final long ONE_MINUTE = 60000L;

    private TOTP() {
    }

    private static byte[] hmacSha(String crypto, byte[] keyBytes, byte[] text) {
        try {
            Mac hmac = Mac.getInstance(crypto);
            SecretKeySpec macKey = new SecretKeySpec(keyBytes, AlgorithmKEY);
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }

    /**
     * This method generates a TOTP value for the given set of parameters.
     *
     * @param key Secret key
     * @param time Time
     * @param digits Number of digits to return (Maximum 8)
     * @param crypto The crypto function to use (HmacSHA1, HmacSHA256, HmacSHA512)
     *
     * @return OTP
     */
    public static int generate(byte[] key, long time, int digits, String crypto) {
        if(digits > 8 || digits < 0) {
            digits = 8;
        }
        byte[] msg = ByteBuffer.allocate(8).putLong(time).array();
        byte[] hash = hmacSha(crypto, key, msg);
        int offset = hash[hash.length - 1] & 0xf;
        int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16) | ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);
        return binary % DIGITS_POWER[digits];
    }

    /**
     * This method generates a TOTP value using HmacSHA512 algorithm for the given set of parameters.
     *
     * @param key Secret key (AES key)
     * @param time Time
     * @param digits Number of digits to return (Maximum 8)
     *
     * @return OTP
     */
    public static int generate(byte[] key, long time, int digits) {
        return generate(key, time, digits, "HmacSHA512");
    }

    /**
     * This method generates a TOTP value using HmacSHA512 for the current system time.
     *
     * @param key Secret key
     * @param digits Number of digits to return (Maximum 8)
     * @param resolutionInMinutes Resolution in minutes
     *
     * @return OTP
     */
    public static int generate(byte[] key, int digits, int resolutionInMinutes) {
        long time = System.currentTimeMillis();
        long t = (time + ((resolutionInMinutes * ONE_MINUTE) >> 1)) / (resolutionInMinutes * ONE_MINUTE);
        return generate(key, t, digits, "HmacSHA512");
    }

    /**
     * Generate a key usable for obtaining TOTP.
     *
     * @return Key as byte array
     */
    public static byte[] generateKey() {
        try {
            KeyGenerator g = KeyGenerator.getInstance("AES");
            SecretKey k = g.generateKey();
            return k.getEncoded();
        } catch (NoSuchAlgorithmException ignored) {
        }
        return null;
    }
}
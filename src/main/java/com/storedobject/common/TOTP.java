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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

/**
 * TOTP implementation.
 *
 * @author Syam
 */
public class TOTP {

    private byte[] key;
    private int digits;
    private String algorithm;
    private int timeDrift = 30;
    private int periodDrift = 1;

    /**
     * Constructor. Default digits: 6, Default algorithm: SHA1. Default time-drift: 30 seconds. Default
     * period-drift: 1.
     *
     * @param key Key to be used (Must have been generated earlier with {@link #generateKey()}).
     */
    public TOTP(byte[] key) {
        this(key, 6, null);
    }

    /**
     * Constructor. Default algorithm: SHA1. Default time-drift: 30 seconds. Default period-drift: 1.
     *
     * @param key Key to be used (Must have been generated earlier with {@link #generateKey()}).
     * @param digits Number of digits in the TOTP.
     */
    public TOTP(byte[] key, int digits) {
        this(key, digits, null);
    }

    /**
     * Constructor. Default time-drift: 30 seconds. Default period-drift: 1.
     *
     * @param key Key to be used (Must have been generated earlier with {@link #generateKey()}).
     * @param digits Number of digits in the TOTP.
     * @param algorithm Algorithm to use - "SHA1", "SHA256", "SHA512".
     */
    public TOTP(byte[] key, int digits, String algorithm) {
        this.key = key;
        this.digits = digits;
        this.algorithm = "Hmac" + (algorithm == null ? "SHA1" : algorithm);
    }

    /**
     * Set another key.
     *
     * @param key Key to be used (Must have been generated earlier with {@link #generateKey()}).
     */
    public void setKey(byte[] key) {
        this.key = key;
    }

    /**
     * Set number of digits.
     *
     * @param digits Number of digits in the TOTP.
     */
    public void setDigits(int digits) {
        this.digits = digits;
    }

    /**
     * Set algorithm.
     *
     * @param algorithm Algorithm to use - "SHA1", "SHA256", "SHA512".
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Set allowed time-drift. Default is 30 seconds.
     *
     * @param timeDrift Time-drift to set.
     */
    public void setTimeDrift(int timeDrift) {
        this.timeDrift = timeDrift;
    }

    /**
     * Set period-drift. Default is 1 period.
     *
     * @param periodDrift Period-drift to set.
     */
    public void setPeriodDrift(int periodDrift) {
        this.periodDrift = periodDrift;
    }

    /**
     * Verify the TOTP code.
     *
     * @param code Code to verify.
     * @return True/false.
     */
    public boolean verify(String code) {
        try {
            return verify(Integer.parseInt(code));
        } catch(Throwable ignored) {
        }
        return false;
    }

    /**
     * Verify the TOTP code.
     *
     * @param code Code to verify.
     * @return True/false.
     */
    public boolean verify(int code) {
        long time = Math.floorDiv(Instant.now().getEpochSecond(), timeDrift);
        for (int i = -periodDrift; i <= periodDrift; i++) {
            try {
                if(generateCode(time + i) == code) {
                    return true;
                }
            } catch(Exception e) {
                break;
            }
        }
        return false;
    }

    private int generateCode(long time) throws Exception {
        byte[] data = new byte[8];
        long value = time;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(signKey);
        return getInt(mac.doFinal(data));
    }

    private int getInt(byte[] bytes) {
        int offset = bytes[bytes.length - 1] & 0xF;
        long value = 0;
        for (int i = 0; i < 4; ++i) {
            value <<= 8;
            value |= (bytes[offset + i] & 0xFF);
        }
        value &= 0x7FFFFFFF;
        value %= Math.pow(10, digits);
        return (int)value;
    }

    /**
     * Generate a suitable key.
     *
     * @return Generated key.
     */
    public static byte[] generateKey() {
        byte[] key = new byte[32];
        try {
            SecureRandom.getInstance("SHA1PRNG").nextBytes(key);
        } catch(NoSuchAlgorithmException ignored) {
        }
        return key;
    }
}
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

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
* Math utility functions
 *
 * @author Syam
*/
public class MathUtility {

    public static BigDecimal toBigDecimal(Object value) {
        String v;
        if(value == null) {
            return null;
        }
        if(value instanceof BigValue) {
        	value = ((BigValue)value).getValue();
        }
        if(value instanceof BigDecimal) {
            return (BigDecimal)value;
        }
        v = value.toString().trim();
        if(v.length() == 0) {
            return null;
        }
        try {
            return new BigDecimal(v.trim().replace(",", ""));
        } catch(Throwable ignored) {
        }
        return null;
    }

    public static BigDecimal toBigDecimal(Object value, int decimals, boolean checkDecimals) {
    	BigDecimal bd = toBigDecimal(value);
        if(bd == null || decimals < 0 || bd.scale() == decimals) {
            return bd;
        }
        try {
            return bd.setScale(decimals, checkDecimals ? RoundingMode.UNNECESSARY : RoundingMode.DOWN);
        } catch(Throwable ignored) {
        }
        return null;
    }

    public static BigDecimal toBigDecimal(Object value, int decimals) {
    	return toBigDecimal(value, decimals, true);
    }

    private static double error(int decimals) {
    	double d = 1;
    	while(decimals-- > 0) {
    		d /= 10.0;
    	}
    	return d;
    }

    private static boolean isZero(double v, double error) {
    	return v < error && v > -error;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isZero(double value) {
    	return isZero(value, 0.000001);
    }

    public static boolean isZero(double value, int decimals) {
    	return isZero(value, error(decimals));
    }

    public static boolean equals(double one, double two) {
    	return isZero(one - two, 0.000001);
    }

    public static boolean equals(double one, double two, int decimals) {
    	return isZero(one - two, error(decimals));
    }

    public static boolean equals(double one, double two, double error) {
    	return isZero(one - two, error);
    }

    public static int compare(double one, double two) {
    	return compare(one, two, 0.000001);
    }

    public static int compare(double one, double two, int decimals) {
    	return compare(one, two, error(decimals));
    }

    public static int compare(double one, double two, double error) {
    	if(equals(one, two, error)) {
    		return 0;
    	}
    	if((two - one) >= error) {
    		return -1;
    	}
    	return 1;
    }

    public static int countBits(long value) {
    	int c = 0;
    	while(value > 0) {
    		if((value & 1) == 1) {
    			++c;
    		}
    		value >>= 1;
    	}
    	return c;
    }
}

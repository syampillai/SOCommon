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
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.atomic.*;

/**
* Math utility functions
 *
 * @author Syam
*/
public class MathUtility {

    private MathUtility() {
    }

    /**
     * Converts the given object into a {@link BigDecimal}. The method handles
     * various types such as {@link BigDecimal}, {@link BigInteger}, primitive
     * numeric types, and several Java concurrency utilities. If the input is
     * not a numeric type, the method attempts to parse it as a {@link String}.
     *
     * @param value the object to convert to {@link BigDecimal}. It can be null,
     *              a numeric type, a {@link BigValue} instance, or an object with
     *              a valid {@link String} representation of a number.
     * @return the resulting {@link BigDecimal} or null if the input value is null,
     *         empty, or cannot be converted to a {@link BigDecimal}.
     */
    public static BigDecimal toBigDecimal(Object value) {
        if(value == null) {
            return null;
        }
        if(value instanceof BigValue) {
        	value = ((BigValue)value).getValue();
        }
        if(value instanceof Number) {
            switch (value) {
                case BigDecimal v -> {
                    return v;
                }
                case BigInteger v -> {
                    return new BigDecimal(v);
                }
                case Double v -> {
                    return BigDecimal.valueOf(v);
                }
                case Float v -> {
                    return BigDecimal.valueOf(v);
                }
                case Long v -> {
                    return BigDecimal.valueOf(v);
                }
                case Integer v -> {
                    return BigDecimal.valueOf(v);
                }
                case Short v -> {
                    return BigDecimal.valueOf(v);
                }
                case Byte v -> {
                    return BigDecimal.valueOf(v);
                }
                case AtomicInteger v -> {
                    return BigDecimal.valueOf(v.get());
                }
                case AtomicLong v -> {
                    return BigDecimal.valueOf(v.get());
                }
                case DoubleAccumulator v -> {
                    return BigDecimal.valueOf(v.get());
                }
                case DoubleAdder v -> {
                    return BigDecimal.valueOf(v.sum());
                }
                case LongAccumulator v -> {
                    return BigDecimal.valueOf(v.get());
                }
                case LongAdder v -> {
                    return BigDecimal.valueOf(v.sum());
                }
                default -> {
                }
            }
        }
        String v;
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

    /**
     * Converts the given object into a {@link BigDecimal} with the specified number
     * of decimal places and an optional check for exact scaling. The method handles
     * various types such as {@link BigDecimal}, {@link BigInteger}, primitive numeric
     * types, and several Java concurrency utilities. If the input is not a numeric type,
     * the method attempts to parse it as a {@link String}.
     *
     * @param value         the object to convert to {@link BigDecimal}. It can be null,
     *                      a numeric type, a {@link BigValue} instance, or an object with
     *                      a valid {@link String} representation of a number.
     * @param decimals      the number of decimal places to scale the resulting {@link BigDecimal} to.
     *                      Must be a non-negative integer.
     * @param checkDecimals a flag indicating whether scaling should fail if it introduces rounding.
     *                      If true, uses {@link RoundingMode#UNNECESSARY} to throw an exception
     *                      when rounding is required. Otherwise, uses {@link RoundingMode#DOWN}.
     *
     * @return the resulting {@link BigDecimal} scaled to the specified number of decimal places,
     *         or null if the input value is null, empty, invalid, or if scaling fails unexpectedly.
     */
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

    /**
     * Converts the given object to a {@code BigDecimal} with the specified number of decimals.
     *
     * @param value the object to be converted, which can be a {@code String}, {@code Number}, or other supported types
     * @param decimals the number of decimal places to round the resulting {@code BigDecimal} to
     * @return a {@code BigDecimal} representation of the given value, rounded to the specified number of decimals
     */
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

    /**
     * Checks if a given double value is approximately zero within a default allowable error margin.
     *
     * @param value the value to be checked for being approximately zero
     * @return {@code true} if the value is approximately zero within the default error margin,
     *         otherwise {@code false}
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isZero(double value) {
    	return isZero(value, 0.000001);
    }

    /**
     * Checks if a given double value is approximately zero within the error margin
     * determined by the specified number of decimal places.
     *
     * @param value    the value to be checked for being approximately zero
     * @param decimals the number of decimal places to determine the error margin
     * @return {@code true} if the value is approximately zero within the specified error margin,
     *         otherwise {@code false}
     */
    public static boolean isZero(double value, int decimals) {
    	return isZero(value, error(decimals));
    }

    /**
     * Compares two double values for approximate equality within a default error margin.
     *
     * @param one the first double value to compare
     * @param two the second double value to compare
     * @return {@code true} if the two values are approximately equal within the default error margin,
     *         otherwise {@code false}
     */
    public static boolean equals(double one, double two) {
    	return isZero(one - two, 0.000001);
    }

    /**
     * Compares two double values for approximate equality based on a specified number
     * of decimal places. The method calculates the allowable error margin depending on
     * the given decimal precision and checks if the difference between the two values
     * is within that margin.
     *
     * @param one      the first double value to compare
     * @param two      the second double value to compare
     * @param decimals the number of decimal places to determine the error margin; must be non-negative
     * @return {@code true} if the two values are approximately equal within the specified
     *         decimal precision, otherwise {@code false}
     */
    public static boolean equals(double one, double two, int decimals) {
    	return isZero(one - two, error(decimals));
    }

    /**
     * Compares two double values for approximate equality based on a specified error margin.
     * The method checks whether the absolute difference between the two values is within the given error margin.
     *
     * @param one   the first double value to compare
     * @param two   the second double value to compare
     * @param error the allowable error margin for the comparison; must be a non-negative double
     * @return {@code true} if the two values are approximately equal within the specified error margin,
     *         otherwise {@code false}
     */
    public static boolean equals(double one, double two, double error) {
    	return isZero(one - two, error);
    }

    /**
     * Compares two double values for order using a default error margin for approximate equality.
     * The comparison accounts for floating-point precision issues by allowing an allowable margin
     * of error, which is set by default to 0.000001.
     *
     * @param one the first double value to compare
     * @param two the second double value to compare
     * @return 0 if the two values are approximately equal within the default error margin,
     *         -1 if {@code one} is less than {@code two} taking the error margin into account, or
     *         1 if {@code one} is greater than {@code two} taking the error margin into account
     */
    public static int compare(double one, double two) {
    	return compare(one, two, 0.000001);
    }

    /**
     * Compares two double values for order using a precision determined by the specified
     * number of decimal places. The comparison accounts for floating-point precision issues
     * by allowing an error margin calculated from the given decimal places.
     *
     * @param one      the first double value to compare
     * @param two      the second double value to compare
     * @param decimals the number of decimal places to determine the error margin; must be non-negative
     * @return 0 if the two values are approximately equal within the calculated error margin,
     *         -1 if {@code one} is less than {@code two} considering the error margin, or
     *         1 if {@code one} is greater than {@code two} considering the error margin
     */
    public static int compare(double one, double two, int decimals) {
    	return compare(one, two, error(decimals));
    }

    /**
     * Compares two double values for order using a specified error margin to account for approximate equality.
     * If the values are considered approximately equal within the given error margin, the method returns 0.
     * Otherwise, it returns -1 if the first value is less than the second value considering the error margin,
     * or 1 if the first value is greater than the second value considering the error margin.
     *
     * @param one   the first double value to compare
     * @param two   the second double value to compare
     * @param error the allowable error margin for the comparison; must be a non-negative double
     * @return 0 if the two values are approximately equal within the specified error margin,
     *         -1 if {@code one} is less than {@code two} considering the error margin,
     *         or 1 if {@code one} is greater than {@code two} considering the error margin
     */
    public static int compare(double one, double two, double error) {
    	if(equals(one, two, error)) {
    		return 0;
    	}
    	if((two - one) >= error) {
    		return -1;
    	}
    	return 1;
    }

    /**
     * Counts the number of set bits (1s) in the binary representation of the given long value.
     *
     * @param value the long value whose set bits are to be counted
     * @return the number of set bits in the binary representation of the input value
     */
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

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

/**
 * Representation of Geolocation.
 *
 * @author Syam
 */
public class Geolocation implements Storable {

    private static final int SECONDS_90 = 324000, SECONDS_180 = 648000, SECONDS_360 = 1296000;
    private static final BigDecimal BIG_60 = new BigDecimal(60);
    private static final char DEGREE = '\u00B0', MINUTE = '\'', SECOND = '"';
    private int latitude = 0, longitude = 0, altitude = 0;

    /**
     * Default constructor for the Geolocation class.
     * Initializes a Geolocation object with no specific location data.
     */
    public Geolocation() {
    }

    /**
     * Constructs a Geolocation object with the specified latitude and longitude values
     * in seconds. The latitude and longitude are internally set using the {@code set}
     * method.
     *
     * @param latitudeInSeconds  The latitude value in seconds. Positive values indicate
     *                           North, while negative values indicate South.
     * @param longitudeInSeconds The longitude value in seconds. Positive values indicate
     *                           East, while negative values indicate West.
     */
    public Geolocation(int latitudeInSeconds, int longitudeInSeconds) {
        set(latitudeInSeconds, longitudeInSeconds);
    }

    /**
     * Constructs a Geolocation object with the specified latitude and longitude values in degrees.
     * The latitude and longitude are internally set as {@code BigDecimal} values using the {@code set} method.
     *
     * @param latitudeInDegrees  The latitude value in degrees. Positive values indicate North, while negative values indicate South.
     * @param longitudeInDegrees The longitude value in degrees. Positive values indicate East, while negative values indicate West.
     */
    public Geolocation(double latitudeInDegrees, double longitudeInDegrees) {
        set(new BigDecimal(latitudeInDegrees), new BigDecimal(longitudeInDegrees));
    }

    /**
     * Constructs a Geolocation object with the specified latitude, longitude,
     * and altitude values. The latitude and longitude are provided in seconds,
     * and the altitude is provided in meters. The values are internally set
     * using the {@code set} method.
     *
     * @param latitudeInSeconds  The latitude value in seconds. Positive values indicate
     *                           North, while negative values indicate South.
     * @param longitudeInSeconds The longitude value in seconds. Positive values indicate
     *                           East, while negative values indicate West.
     * @param altitudeInMeters   The altitude value in meters above sea level.
     */
    public Geolocation(int latitudeInSeconds, int longitudeInSeconds, int altitudeInMeters) {
        set(latitudeInSeconds, longitudeInSeconds, altitudeInMeters);
    }

    /**
     * Copy constructor for the {@code Geolocation} class.
     * Creates a new {@code Geolocation} instance by copying the values from another {@code Geolocation} object.
     *
     * @param location The {@code Geolocation} object to copy. If {@code null}, the new instance will be initialized with default values.
     */
    @SuppressWarnings("CopyConstructorMissesField")
    public Geolocation(Geolocation location) {
        set(location);
    }

    /**
     * Constructs a Geolocation object with the specified latitude and longitude values,
     * provided in degrees, minutes, seconds, and direction.
     *
     * @param latitudeDegrees  The degrees component of the latitude value. Must be a non-negative integer.
     * @param latitudeMinutes  The minutes component of the latitude value. Must be a non-negative integer less than 60.
     * @param latitudeSeconds  The seconds component of the latitude value. Must be a non-negative integer less than 60.
     * @param latitudeDirection  The direction of the latitude ('N' for North or 'S' for South).
     * @param longitudeDegrees  The degrees component of the longitude value. Must be a non-negative integer.
     * @param longitudeMinutes  The minutes component of the longitude value. Must be a non-negative integer less than 60.
     * @param longitudeSeconds  The seconds component of the longitude value. Must be a non-negative integer less than 60.
     * @param longitudeDirection  The direction of the longitude ('E' for East or 'W' for West).
     */
    public Geolocation(int latitudeDegrees, int latitudeMinutes, int latitudeSeconds, char latitudeDirection,
                       int longitudeDegrees, int longitudeMinutes, int longitudeSeconds, char longitudeDirection) {
        set(new L(latitudeDegrees, latitudeMinutes, latitudeSeconds, latitudeDirection),
                new L(longitudeDegrees, longitudeMinutes, longitudeSeconds, longitudeDirection));
    }

    /**
     * Constructs a Geolocation object with the specified latitude and longitude values,
     * provided in degrees and cardinal direction indicators.
     *
     * @param latitudeInDegrees  The latitude value in degrees. Must be non-negative.
     *                           Positive values indicate North, while negative values are
     *                           determined by the latitudeDirection ('N' or 'S').
     * @param latitudeDirection  The direction of the latitude. Valid values are 'N'
     *                           (North) and 'S' (South).
     * @param longitudeInDegrees The longitude value in degrees. Must be non-negative.
     *                           Positive values indicate East, while negative values are
     *                           determined by the longitudeDirection ('E' or 'W').
     * @param longitudeDirection The direction of the longitude. Valid values are 'E'
     *                           (East) and 'W' (West).
     */
    public Geolocation(double latitudeInDegrees, char latitudeDirection, double longitudeInDegrees, char longitudeDirection) {
        set(new L(latitudeInDegrees, latitudeDirection), new L(longitudeInDegrees, longitudeDirection));
    }

    /**
     * Constructs a Geolocation object with the specified latitude and longitude values,
     * provided in degrees and cardinal direction indicators.
     *
     * @param latitudeInDegrees  The latitude value in degrees. Must be a non-negative BigDecimal.
     *                           The latitude direction determines whether the value is North or South.
     * @param latitudeDirection  The direction of the latitude. Valid values are 'N' (North)
     *                           and 'S' (South).
     * @param longitudeInDegrees The longitude value in degrees. Must be a non-negative BigDecimal.
     *                           The longitude direction determines whether the value is East or West.
     * @param longitudeDirection The direction of the longitude. Valid values are 'E' (East)
     *                           and 'W' (West).
     */
    public Geolocation(BigDecimal latitudeInDegrees, char latitudeDirection, BigDecimal longitudeInDegrees, char longitudeDirection) {
        set(latitudeInDegrees, latitudeDirection, longitudeInDegrees, longitudeDirection);
    }

    /**
     * Constructs a Geolocation object and initializes it with the provided value.
     *
     * @param value the geolocation value to be set, typically in a format such as latitude and longitude.
     */
    public Geolocation(String value) {
        set(value);
    }

    /**
     * Constructs a Geolocation object and initializes it with the provided value.
     *
     * @param value the initial value to set for the Geolocation object.
     *              The type and format of this value depend on the implementation of the set method.
     */
    public Geolocation(Object value) {
        set(value);
    }

    /**
     * Updates the geographical coordinates and altitude of the current object.
     *
     * @param location the Geolocation object containing latitude, longitude,
     *                 and altitude values. If null, default values of 0 for
     *                 latitude, longitude, and altitude are used.
     */
    public void set(Geolocation location) {
        if(location == null) {
            set(0, 0, 0);
        } else {
            set(location.latitude, location.longitude, location.altitude);
        }
    }

    private void set(L latitude, L longitude) {
        setLatitude(latitude.v());
        setLongitude(longitude.v());
        altitude = 0;
    }

    /**
     * Updates the geographical coordinates and altitude for the object.
     *
     * @param latitudeInSeconds the latitude value in seconds, where positive values indicate north and negative values indicate south
     * @param longitudeInSeconds the longitude value in seconds, where positive values indicate east and negative values indicate west
     * @param altitudeInMeters the altitude value in meters above sea level
     */
    public void set(int latitudeInSeconds, int longitudeInSeconds, int altitudeInMeters) {
        setLatitude(latitudeInSeconds);
        setLongitude(longitudeInSeconds);
        altitude = altitudeInMeters;
    }

    /**
     * Sets the geographical coordinates using latitude and longitude values in seconds.
     * The altitude is implicitly set to 0.
     *
     * @param latitudeInSeconds the latitude value in seconds
     * @param longitudeInSeconds the longitude value in seconds
     */
    public void set(int latitudeInSeconds, int longitudeInSeconds) {
        set(latitudeInSeconds, longitudeInSeconds, 0);
    }

    /**
     * Sets the coordinates for latitude and longitude using their respective values
     * in seconds and directional characters.
     *
     * @param latitudeInSeconds the latitude value in seconds, where positive indicates North and negative indicates South
     * @param latitudeDirection the direction of the latitude, either 'N' for North or 'S' for South
     * @param longitudeInSeconds the longitude value in seconds, where positive indicates East and negative indicates West
     * @param longitudeDirection the direction of the longitude, either 'E' for East or 'W' for West
     */
    public void set(int latitudeInSeconds, char latitudeDirection, int longitudeInSeconds, char longitudeDirection) {
        if(reverse(latitudeDirection, longitudeDirection)) {
            set(longitudeInSeconds, longitudeDirection, latitudeInSeconds, latitudeDirection);
            return;
        }
        latitudeDirection = Character.toUpperCase(latitudeDirection);
        longitudeDirection = Character.toUpperCase(longitudeDirection);
        if(latitudeDirection == 'S') {
            latitudeInSeconds = -latitudeInSeconds;
        }
        if(longitudeDirection == 'W') {
            longitudeInSeconds = -longitudeInSeconds;
        }
        set(latitudeInSeconds, longitudeInSeconds, 0);
    }

    /**
     * Sets the geographic coordinates with the provided latitude and longitude values,
     * expressed in degrees, minutes, seconds, and direction.
     *
     * @param latitudeDegrees the degrees part of the latitude
     * @param latitudeMinutes the minutes part of the latitude
     * @param latitudeSeconds the seconds part of the latitude
     * @param latitudeDirection the direction of the latitude ('N' for north, 'S' for south)
     * @param longitudeDegrees the degrees part of the longitude
     * @param longitudeMinutes the minutes part of the longitude
     * @param longitudeSeconds the seconds part of the longitude
     * @param longitudeDirection the direction of the longitude ('E' for east, 'W' for west)
     */
    public void set(int latitudeDegrees, int latitudeMinutes, int latitudeSeconds, char latitudeDirection,
                    int longitudeDegrees, int longitudeMinutes, int longitudeSeconds, char longitudeDirection) {
        if(reverse(latitudeDirection, longitudeDirection)) {
            set(longitudeDegrees, longitudeMinutes, longitudeSeconds, longitudeDirection,
                    latitudeDegrees, latitudeMinutes, latitudeSeconds, latitudeDirection);
            return;
        }
        latitude = latitudeDegrees * 3600 + latitudeMinutes * 60 + latitudeSeconds;
        longitude = longitudeDegrees * 3600 + longitudeMinutes * 60 + longitudeSeconds;
        if(latitudeDirection == 'S') {
            latitude = -latitude;
        }
        if(longitudeDirection == 'W') {
            longitude = -longitude;
        }
        set(latitude, longitude, 0);
    }

    /**
     * Sets the latitude and longitude values using the provided degrees.
     *
     * @param latitudeInDegrees the latitude value in degrees
     * @param longitudeInDegrees the longitude value in degrees
     */
    public void set(double latitudeInDegrees, double longitudeInDegrees) {
        set(new BigDecimal(latitudeInDegrees), new BigDecimal(longitudeInDegrees));
    }

    /**
     * Sets the geographic coordinates using latitude and longitude in degrees.
     *
     * @param latitudeInDegrees the latitude value in degrees
     * @param longitudeInDegrees the longitude value in degrees
     */
    public void set(BigDecimal latitudeInDegrees, BigDecimal longitudeInDegrees) {
        set(latitudeInDegrees, 'N', longitudeInDegrees, 'E');
    }

    /**
     * Sets the geographical coordinates using latitude and longitude values along with their directions.
     *
     * @param latitudeInDegrees the latitude value in degrees
     * @param latitudeDirection the direction of latitude, e.g., 'N' for North or 'S' for South
     * @param longitudeInDegrees the longitude value in degrees
     * @param longitudeDirection the direction of longitude, e.g., 'E' for East or 'W' for West
     */
    public void set(BigDecimal latitudeInDegrees, char latitudeDirection, BigDecimal longitudeInDegrees, char longitudeDirection) {
        set(new L(latitudeInDegrees, latitudeDirection), new L(longitudeInDegrees, longitudeDirection));
    }

    /**
     * Sets the geographic coordinates based on the provided latitude and longitude strings.
     * The method attempts to recognize and parse standard latitude and longitude formats such as:
     * - ddNdddE
     * - ddmmNdddmmE
     * - ddmmNddmmE
     *
     * If the input strings do not match any recognizable pattern, default values are assigned.
     *
     * @param latitude The latitude string representing the north-south position.
     *                 It may end with 'N' or 'S' to indicate direction.
     * @param longitude The longitude string representing the east-west position.
     *                  It may end with 'E' or 'W' to indicate direction.
     */
    public void set(String latitude, String longitude) {
        char c1, c2;
        //noinspection LoopStatementThatDoesntLoop
        while(true) { // Recognize standard patterns
            if(latitude.endsWith("N") || latitude.endsWith("S")) {
                c1 = latitude.charAt(latitude.length() - 1);
            } else {
                break;
            }
            if(longitude.endsWith("E") || longitude.endsWith("W")) {
                c2 = longitude.charAt(longitude.length() - 1);
            } else {
                break;
            }
            if(latitude.length() < 3 || longitude.length() < 4) {
                break;
            }
            String s1 = latitude.substring(0, latitude.length() - 1), s2 = longitude.substring(0, longitude.length() - 1);
            if(!StringUtility.isDigit(s1) || !StringUtility.isDigit(s2)) {
                break;
            }
            if(latitude.length() == 3 && longitude.length() == 4) { // Standard ddNdddE pattern
                set(Integer.parseInt(s1), 0, 0, c1, Integer.parseInt(s2), 0, 0, c2);
                return;
            }
            if(latitude.length() == 5 && longitude.length() == 6) { // Standard ddmmNdddmmE pattern
                set(Integer.parseInt(s1.substring(0, 2)), Integer.parseInt(s1.substring(2)), 0, c1,
                        Integer.parseInt(s2.substring(0, 3)), Integer.parseInt(s2.substring(3)), 0, c2);
                return;
            }
            if(latitude.length() == 5 && longitude.length() == 5) { // Standard ddmmNddmmE pattern
                set(Integer.parseInt(s1.substring(0, 2)), Integer.parseInt(s1.substring(2)), 0, c1,
                        Integer.parseInt(s2.substring(0, 2)), Integer.parseInt(s2.substring(2)), 0, c2);
                return;
            }
            break;
        }
        c1 = parseChar(latitude);
        if(c1 == 0) {
            c1 = 'N';
        }
        c2 = parseChar(longitude);
        if(c2 == 0) {
            c2 = 'E';
        }
        set(parseValue(latitude), c1, parseValue(longitude), c2);
    }

    /**
     * Sets the internal coordinates and altitude based on the provided object. The method interprets the input
     * in various formats and extracts the relevant components (latitude, longitude, altitude) accordingly.
     * If the input format does not conform to the expected patterns, appropriate error handling is executed.
     *
     * @param object The input object containing coordinate data. It can be in different formats such as:
     *               - A string representation in degree-minute-second format.
     *               - A string with latitude and longitude separated by a delimiter (e.g., spaces or commas).
     *               - A string wrapped in parentheses containing coordinates.
     *               - A numeric representation or other parsable forms.
     */
    public void set(Object object) {
        String value = object == null ? null : object.toString();
        if(StringUtility.isWhite(value)) {
            longitude = latitude = altitude = 0;
            return;
        }
        value = purge(value);
        StringList list = null;
        if(value.startsWith("(") && value.endsWith(")")) {
            try {
                value = value.substring(1);
                value = value.substring(0, value.length() - 1);
                list = StringList.create(value);
                set(Integer.parseInt(list.get(0)), Integer.parseInt(list.get(1)));
                if(list.size() == 3) {
                    altitude = Integer.parseInt(list.get(2));
                }
            } catch(Throwable e1) {
                try {
                    //noinspection ConstantConditions
                    set(new BigDecimal(list.get(0)),new BigDecimal(list.get(1)));
                    if(list.size() == 3) {
                        altitude = new BigDecimal(list.get(2)).intValue();
                    }
                } catch(Throwable e2) {
                    error(object);
                }
            }
            return;
        }
        int commas = StringUtility.getCharCount(value, ',');
        if(commas == 1 || commas == 2) {
            list = StringList.create(value);
            set(list.get(0), list.get(1));
            if(list.size() == 3) {
                setAlt(list.get(2));
            }
            return;
        }
        if(commas > 0) {
            error(object);
        }
        int p1 = value.indexOf('N');
        if(p1 < 0) {
            p1 = value.indexOf('S');
        }
        int p2 = value.indexOf('E');
        if(p2 < 0) {
            p2 = value.indexOf('W');
        }
        if(p1 >= 0 && p2 >= 0) {
            if(p1 < p2) {
                set(value.substring(0, p1 + 1), value.substring(p1 + 1));
            } else {
                set(value.substring(0, p2 + 1), value.substring(p2 + 1));
            }
            return;
        }
        if(p1 >= 0) {
            if(p1 == (value.length() - 1)) {
                set(value, "");
                return;
            }
            error(object);
        }
        if(p2 >= 0) {
            if(p2 == (value.length() - 1)) {
                set("", value);
                return;
            }
            error(object);
        }
        commas = StringUtility.getCharCount(value, ' ');
        if(commas == 1) {
            p1 = value.indexOf(' ');
            set(value.substring(0, p1), value.substring(p1 + 1));
            return;
        }
        if(commas == 5 || commas == 6) {
            p1 = value.indexOf(' ');
            p1 = value.indexOf(' ', p1 + 1);
            p1 = value.indexOf(' ', p1 + 1);
            set(value.substring(0, p1), value.substring(p1 + 1));
            if(commas == 6) {
                p1 = value.lastIndexOf(' ');
                setAlt(value.substring(p1 + 1));
            }
            return;
        }
        if(commas == 0 || commas == 2) {
            set(value, "");
            return;
        }
        p2 = -1;
        p1 = StringUtility.getCharCount(value, DEGREE);
        if(p1 == 2) {
            p2 = value.lastIndexOf(DEGREE);
        } else if(p1 == 1) {
            p2 = value.indexOf(DEGREE);
            p1 = value.indexOf(SECOND);
            if(p2 < p1) {
                p1 = value.indexOf(MINUTE);
                if(p2 < p1) {
                    set(value, "");
                    return;
                }
            }
            p2 = p1;
        }
        if(p2 < 0) {
            error(object);
        }
        --p2;
        if(!Character.isDigit(value.charAt(p2))) {
            error(object);
        }
        do {
            --p2;
        } while (Character.isDigit(value.charAt(p2)));
        set(value.substring(0, p2), value.substring(p2 + 1));
        value = value.substring(p2 + 1);
        if(StringUtility.getCharCount(value, ' ') == 3) {
            setAlt(value.substring(value.lastIndexOf(' ')));
            return;
        }
        p2 = value.indexOf(SECOND);
        if(p2 < 0) {
            p2 = value.indexOf(MINUTE);
        }
        if(p2 < 0) {
            p2 = value.indexOf(DEGREE);
        }
        if(p2 >= 0) {
            setAlt(value.substring(p2 + 1));
        }
    }

    private void setAlt(String v) {
        v = v.trim();
        try {
            altitude = Integer.parseInt(v);
        } catch(Throwable e1) {
            try {
                altitude = new BigDecimal(v).intValue();
            } catch(Throwable e2) {
                error("Altitude part (" + v + ")");
            }
        }
    }

    private static char parseChar(String v) {
        v = v.trim();
        if(v.length() == 0) {
            return 0;
        }
        char c = v.charAt(v.length() - 1);
        if(c == DEGREE || c == MINUTE || c == SECOND || Character.isDigit(c)) {
            return 0;
        }
        return c;
    }

    private static String purge(String v) {
        while(v.contains("  ")) {
            v = v.replace("  ", " ");
        }
        while(v.contains(" " + DEGREE)) {
            v = v.replace(" " + DEGREE, "" + DEGREE);
        }
        while(v.contains(" " + MINUTE)) {
            v = v.replace(" " + MINUTE, "" + MINUTE);
        }
        while(v.contains(" " + SECOND)) {
            v = v.replace(" " + SECOND, "" + SECOND);
        }
        return v.trim().toUpperCase();
    }

    private static int parseValue(String v) {
        String original = v;
        if(parseChar(v) != 0) {
            v = v.substring(0, v.length() - 1);
        }
        v = purge(v);
        if(v.length() == 0) {
            return 0;
        }
        int i = letterPosition(v);
        if(i == 0) {
            error(original);
        }
        char c = i < 0 ? '.' : v.charAt(i);
        if(c == '.') {
            try {
                int n = Integer.parseInt(v);
                if(!v.contains(".") && v.length() >= 3) {
                    return (n / 100) * 3600 + (n % 100) * 60;
                }
                return n * 3600;
            } catch(Throwable ignored) {
            }
            try {
                return new L(new BigDecimal(v)).v();
            } catch(Throwable ignored) {
            }
            error(original);
        }
        if(c == ' ') {
            c = DEGREE;
        }
        int k, d = Integer.MAX_VALUE, m = Integer.MAX_VALUE, s = Integer.MAX_VALUE;
        int round = 3;
        while(round > 0) {
            if(c == DEGREE || c == MINUTE || c == SECOND) {
                k = Integer.parseInt(v.substring(0, i));
                if(i >= (v.length() - 1)) {
                    v = "";
                } else {
                    v = v.substring(i + 1).trim();
                }
                switch(c) {
                    case DEGREE:
                        if(d != Integer.MAX_VALUE) {
                            error(original);
                        }
                        d = k;
                        break;
                    case MINUTE:
                        if(m != Integer.MAX_VALUE) {
                            error(original);
                        }
                        m = k;
                        break;
                    case SECOND:
                        if(s != Integer.MAX_VALUE) {
                            error(original);
                        }
                        s = k;
                        break;
                }
            } else {
                error(original);
            }
            if(v.isEmpty()) {
                break;
            }
            i = letterPosition(v);
            if(i == 0) {
                error(original);
            }
            if(i < 0) {
                i = v.length();
                c = ' ';
            } else {
                c = v.charAt(i);
            }
            --round;
            if(c == ' ') {
                if(round == 2) {
                    c = MINUTE;
                } else {
                    c = SECOND;
                }
            }
        }
        return (d == Integer.MAX_VALUE ? 0 : d) * 3600 + (m == Integer.MAX_VALUE ? 0 : m) * 60 + (s == Integer.MAX_VALUE ? 0 : s);
    }

    private static int letterPosition(String v) {
        for(int i = 0; i < v.length(); i++) {
            if(Character.isDigit(v.charAt(i))) {
                continue;
            }
            return i;
        }
        return -1;
    }

    private int[] array(int latitude, int longitude) {
        L lat = new L(latitude), lon = new L(longitude);
        return new int[] { lat.degrees, lat.minutes, lat.seconds, lat.direction ? 'N' : 'S',
                lon.degrees, lon.minutes, lon.seconds, lon.direction ? 'E' : 'W' };
    }

    private boolean reverse(char ns, char ew) {
        ns = Character.toUpperCase(ns);
        ew = Character.toUpperCase(ew);
        if((ns == 'N' || ns == 'S') && (ew == 'E' || ew == 'W')) {
            return false;
        }
        if((ns == 'E' || ns == 'W') && (ew == 'N' || ew == 'S')) {
            return true;
        }
        error("Unable to determine N/E/W/S");
        return true;
    }

    @Override
    public String toString() {
        int[] a = array(latitude, longitude);
        StringBuilder s = new StringBuilder();
        s.append(a[0]).append(DEGREE);
        if(a[1] > 0 || a[2] > 0) {
            s.append(' ').append(a[1]).append(MINUTE);
            if(a[2] > 0) {
                s.append(' ').append(a[2]).append(SECOND);
            }
        }
        s.append(' ').append((char)a[3]).append(", ").append(a[4]).append(DEGREE);
        if(a[5] > 0 || a[6] > 0) {
            s.append(' ').append(a[5]).append(MINUTE);
            if(a[6] > 0) {
                s.append(' ').append(a[6]).append(SECOND);
            }
        }
        s.append(' ').append((char)a[7]);
        if(altitude != 0) {
            s.append(", ").append(altitude);
        }
        return s.toString();
    }

    /**
     * Converts the object into a string representation based on the specified format.
     * If the aviation standard is true, it generates a string representation
     * following aviation-specific formatting rules.
     *
     * @param aviationStandard a boolean flag indicating whether the output should
     *                         follow aviation-specific formatting rules. If false,
     *                         the default string representation is returned.
     * @return a string representation of the object. The format of the returned
     *         string depends on the value of the {@code aviationStandard} parameter.
     */
    public String toString(boolean aviationStandard) {
        if(!aviationStandard) {
            return toString();
        }
        int[] a = array(latitude, longitude);
        StringBuilder s = new StringBuilder();
        if(a[0] < 10) {
            s.append('0');
        }
        s.append(a[0]);
        if(a[1] > 0 || a[5] > 0) {
            if(a[1] < 10) {
                s.append('0');
            }
            s.append(a[1]);
        }
        s.append((char)a[3]);
        if(a[4] < 10) {
            s.append('0');
        }
        if(a[4] < 100) {
            s.append('0');
        }
        s.append(a[4]);
        if(a[1] > 0 || a[5] > 0) {
            if(a[5] < 10) {
                s.append('0');
            }
            s.append(a[5]);
        }
        s.append((char)a[7]);
        return s.toString();
    }

    private static void error(Object v) {
        if(v != null) {
            v = ": " + v;
        }
        throw new SORuntimeException("Invalid Geographic Location Value" + v);
    }

    /**
     * Sets the latitude in degrees by converting it to seconds
     * and passing it to the method that accepts latitude in seconds.
     *
     * @param latitudeInDegrees the latitude value in degrees to be converted
     *                          and set as latitude in seconds
     */
    public void setLatitudeDegree(double latitudeInDegrees) {
        setLatitude((int)(latitudeInDegrees * 3600.0));
    }

    /**
     * Sets the geographical longitude in degrees.
     *
     * @param longitudeInDegrees the longitude value in decimal degrees to be set.
     *                            This value is expected to represent the
     *                            geographical longitude as a double.
     */
    public void setLongitudeDegree(double longitudeInDegrees) {
        setLongitude((int)(longitudeInDegrees * 3600.0));
    }

    /**
     * Retrieves the latitude value.
     *
     * @return the latitude as an integer
     */
    public int getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude value in seconds, adjusting it to ensure it remains within the valid range.
     * The latitude value is normalized to the range [-SECONDS_90, SECONDS_90].
     *
     * @param latitudeInSeconds the latitude in seconds to be set. Values exceeding the range will be normalized.
     */
    public void setLatitude(int latitudeInSeconds) {
        while(latitudeInSeconds > SECONDS_90 || latitudeInSeconds < -SECONDS_90) {
            while(latitudeInSeconds > SECONDS_90) {
                latitudeInSeconds = SECONDS_180 - latitudeInSeconds;
            }
            while(latitudeInSeconds < -SECONDS_90) {
                latitudeInSeconds = -SECONDS_180 - latitudeInSeconds;
            }
        }
        this.latitude = latitudeInSeconds;
    }

    /**
     * Retrieves the longitude value.
     *
     * @return the longitude as an integer.
     */
    public int getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude value in seconds. The input value is normalized to ensure
     * it stays within the valid range of -180 to 180 degrees, represented in seconds.
     *
     * @param longitudeInSeconds the longitude value in seconds, which may initially
     *                           fall outside the valid range. The method adjusts
     *                           it as necessary to fit within the proper limits.
     */
    public void setLongitude(int longitudeInSeconds) {
        while(longitudeInSeconds > SECONDS_180 || longitudeInSeconds < -SECONDS_180) {
            while(longitudeInSeconds > SECONDS_180) {
                longitudeInSeconds = SECONDS_360 - longitudeInSeconds;
            }
            while(longitudeInSeconds < -SECONDS_180) {
                longitudeInSeconds = -SECONDS_360 - longitudeInSeconds;
            }
        }
        this.longitude = longitudeInSeconds;
    }

    /**
     * Retrieves the current altitude.
     *
     * @return the altitude as an integer value
     */
    public int getAltitude() {
        return altitude;
    }

    /**
     * Sets the altitude value in meters.
     *
     * @param altitudeInMeters the altitude to set, specified in meters
     */
    public void setAltitude(int altitudeInMeters) {
        this.altitude = altitudeInMeters;
    }

    @Override
    public String getStorableValue() {
        return "ROW(" + latitude + "," + longitude + "," + altitude + ")::GL";
    }

    /**
     * Retrieves the latitude value in degree representation.
     *
     * @return the latitude in degrees as a double, converted from seconds.
     */
    public double getLatitudeDegree() {
        return (double)latitude / 3600.0;
    }

    /**
     * Converts the longitude value from arcseconds to degrees.
     *
     * @return the longitude in degrees as a double value
     */
    public double getLongitudeDegree() {
        return (double)longitude / 3600.0;
    }

    /**
     * Distance between this location and another location.
     *
     * @param location Another location
     * @return Distance in meters
     */
    public int distance(Geolocation location) {
        return (int)distanceAsDouble(location);
    }

    /**
     * Distance between this location and another location.
     *
     * @param location Another location
     * @return Distance in meters
     */
    public double distanceAsDouble(Geolocation location) {
        double lat1 = (double)(latitude) * Math.PI / 180.0 / 3600.0;
        double lat2 = (double)(location.latitude) * Math.PI / 180.0 / 3600.0;
        double lon = (double)(Math.abs(longitude - location.longitude)) * Math.PI / 180.0 / 3600.0;
        lon = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon));
        return 6371000.0 * lon;
    }

    /**
     * Calculates the directional heading in seconds of arc from the current location
     * to the specified target location.
     *
     * @param location the target location of type Geolocation, containing latitude and longitude
     * @return the heading in seconds of arc, representing the angle relative to true north
     */
    public int getHeading(Geolocation location) {
        double theta;
        if(location.longitude == longitude) {
            return (location.latitude >= latitude ? 0 : 180) * 3600;
        }
        if(location.latitude == latitude) {
            return (location.longitude >= longitude ? 90 : 270) * 3600;
        }
        theta = (double)(location.latitude - latitude) / ((double)(location.longitude - longitude));
        theta = (Math.atan(theta) * 180.0 / Math.PI) * 3600.0;
        int shift = 90;
        if(location.longitude < longitude && location.latitude > latitude) {
            shift = -90;
        } else if(location.latitude < latitude && location.longitude < longitude) {
            shift = 270;
        }
        return (shift * 3600) - ((int) theta);
    }

    /**
     * Calculates the time difference in minutes based on the longitude.
     *
     * The method uses the formula:
     * (1440 * longitude) / 360 / 3600
     * where:
     * - 1440 is the total number of minutes in a day.
     * - longitude is measured in degrees.
     * - 360 represents the total degrees of longitude.
     * - 3600 accounts for conversion to seconds.
     *
     * @return the time difference in minutes as an integer value.
     */
    public int getTimeDifferenceInMinutes() {
        return 1440 * longitude / 360 / 3600;
    }

    @Override
    public boolean equals(Object another) {
        if(!(another instanceof Geolocation a)) {
            return false;
        }
        return latitude == a.latitude && longitude == a.longitude && altitude == a.altitude;
    }

    private static class L {

        boolean direction = true;
        int degrees, minutes, seconds;

        L(int v) {
            if(v < 0) {
                direction = false;
                v = -v;
            }
            degrees = v / 3600;
            minutes = (v - degrees * 3600) / 60;
            seconds = v - degrees * 3600 - minutes * 60;
        }

        L(double v, char direction) {
            this(BigDecimal.valueOf(v), direction);
        }

        L(int degrees, int minutes, int seconds, char direction) {
            this.degrees = degrees;
            this.minutes = minutes;
            this.seconds = seconds;
            this.direction = switch (direction) {
                case 'N', 'E', 'n', 'e' -> true;
                default -> false;
            };
        }

        L(BigDecimal v) {
            set(v);
        }

        L(BigDecimal v, char direction) {
            set(v, direction);
        }

        void set(BigDecimal v) {
            set(v, 'N');
        }

        void set(BigDecimal v, char direction) {
            set(direction);
            if(v == null) {
                v = BigDecimal.ZERO;
            }
            if(v.signum() < 0) {
                v = v.negate();
                this.direction = !this.direction;
            }
            degrees = v.intValue();
            v = v.subtract(new BigDecimal(degrees));
            v = v.multiply(BIG_60);
            minutes = v.intValue();
            v = v.subtract(new BigDecimal(minutes));
            v = v.multiply(BIG_60);
            seconds = v.intValue();
        }

        void set(char direction) {
            this.direction = switch (direction) {
                case 'N', 'E', 'n', 'e' -> true;
                default -> false;
            };
        }

        int v() {
            int v = degrees * 3600 + minutes * 60 + seconds;
            return direction ? v : -v;
        }
    }
}
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

    public Geolocation() {
    }

    public Geolocation(int latitudeInSeconds, int longitudeInSeconds) {
        set(latitudeInSeconds, longitudeInSeconds);
    }

    public Geolocation(double latitudeInDegrees, double longitudeInDegrees) {
        set(new BigDecimal(latitudeInDegrees), new BigDecimal(longitudeInDegrees));
    }

    public Geolocation(int latitudeInSeconds, int longitudeInSeconds, int altitudeInMeters) {
        set(latitudeInSeconds, longitudeInSeconds, altitudeInMeters);
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public Geolocation(Geolocation location) {
        set(location);
    }

    public Geolocation(int latitudeDegrees, int latitudeMinutes, int latitudeSeconds, char latitudeDirection,
                       int longitudeDegrees, int longitudeMinutes, int longitudeSeconds, char longitudeDirection) {
        set(latitudeDegrees, latitudeMinutes, latitudeSeconds, latitudeDirection,
                longitudeDegrees, longitudeMinutes, longitudeSeconds, longitudeDirection);
    }

    public Geolocation(double latitudeInDegrees, char latitudeDirection, double longitudeInDegrees, char longitudeDirection) {
        this(new BigDecimal(latitudeInDegrees), latitudeDirection, new BigDecimal(longitudeInDegrees), longitudeDirection);
    }

    public Geolocation(BigDecimal latitudeInDegrees, char latitudeDirection, BigDecimal longitudeInDegrees, char longitudeDirection) {
        set(latitudeInDegrees, latitudeDirection, longitudeInDegrees, longitudeDirection);
    }

    public Geolocation(String value) {
        set(value);
    }

    public Geolocation(Object value) {
        set(value);
    }

    public void set(Geolocation location) {
        if(location == null) {
            set(0, 0, 0);
        } else {
            set(location.latitude, location.longitude, location.altitude);
        }
    }

    public void set(int latitudeInSeconds, int longitudeInSeconds, int altitudeInMeters) {
        setLatitude(latitudeInSeconds);
        setLongitude(longitudeInSeconds);
        altitude = altitudeInMeters;
    }

    public void set(int latitudeInSeconds, int longitudeInSeconds) {
        set(latitudeInSeconds, longitudeInSeconds, 0);
    }

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

    public void set(double latitudeInDegrees, double longitudeInDegrees) {
        set(new BigDecimal(latitudeInDegrees), new BigDecimal(longitudeInDegrees));
    }

    public void set(BigDecimal latitudeInDegrees, BigDecimal longitudeInDegrees) {
        set(latitudeInDegrees, 'N', longitudeInDegrees, 'E');
    }

    public void set(BigDecimal latitudeInDegrees, char latitudeDirection, BigDecimal longitudeInDegrees, char longitudeDirection) {
        if(latitudeInDegrees == null) {
            latitudeInDegrees = BigDecimal.ZERO;
        }
        if(longitudeInDegrees == null) {
            longitudeInDegrees = BigDecimal.ZERO;
        }
        int latitudeDegrees = latitudeInDegrees.intValue();
        latitudeInDegrees = latitudeInDegrees.subtract(new BigDecimal(latitudeDegrees));
        latitudeInDegrees = latitudeInDegrees.multiply(BIG_60);
        int latitudeMinutes = latitudeInDegrees.intValue();
        latitudeInDegrees = latitudeInDegrees.subtract(new BigDecimal(latitudeMinutes));
        latitudeInDegrees = latitudeInDegrees.multiply(BIG_60);
        int latitudeSeconds = latitudeInDegrees.intValue();
        int longitudeDegrees = longitudeInDegrees.intValue();
        longitudeInDegrees = longitudeInDegrees.subtract(new BigDecimal(longitudeDegrees));
        longitudeInDegrees = longitudeInDegrees.multiply(BIG_60);
        int longitudeMinutes = longitudeInDegrees.intValue();
        longitudeInDegrees = longitudeInDegrees.subtract(new BigDecimal(longitudeMinutes));
        longitudeInDegrees = longitudeInDegrees.multiply(BIG_60);
        int longitudeSeconds = longitudeInDegrees.intValue();
        set(latitudeDegrees, latitudeMinutes, latitudeSeconds, latitudeDirection,
                longitudeDegrees, longitudeMinutes, longitudeSeconds, longitudeDirection);
    }

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
                list = new StringList(value);
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
            list = new StringList(value);
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
        --p2;
        while(Character.isDigit(value.charAt(p2))) {
            --p2;
        }
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
        int i = nondigit(v);
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
                return convert(new BigDecimal(v));
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
            i = nondigit(v);
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

    private static int nondigit(String v) {
        for(int i = 0; i < v.length(); i++) {
            if(Character.isDigit(v.charAt(i))) {
                continue;
            }
            return i;
        }
        return -1;
    }

    private static int convert(BigDecimal v) {
        int degrees = v.intValue();
        v = v.subtract(new BigDecimal(degrees));
        v = v.multiply(BIG_60);
        int minutes = v.intValue();
        v = v.subtract(new BigDecimal(minutes));
        v = v.multiply(BIG_60);
        return degrees * 3600 + minutes * 60 + v.intValue();
    }

    private int[] array(int latitude, int longitude) {
        int lat = latitude;
        boolean latitudeDirection = lat >= 0;
        if(lat < 0) {
            lat = -lat;
        }
        int latitudeDegrees = lat / 3600;
        int latitudeMinutes = (lat - latitudeDegrees * 3600) / 60;
        int latitudeSeconds = lat - latitudeDegrees * 3600 - latitudeMinutes * 60;
        int lon = longitude;
        boolean longitudeDirection = lon >= 0;
        if(lon < 0) {
            lon = -lon;
        }
        int longitudeDegrees = lon / 3600;
        int longitudeMinutes = (lon - longitudeDegrees * 3600) / 60;
        int longitudeSeconds = lon - longitudeDegrees * 3600 - longitudeMinutes * 60;
        return new int[] { latitudeDegrees, latitudeMinutes, latitudeSeconds, latitudeDirection ? 'N' : 'S',
                longitudeDegrees, longitudeMinutes, longitudeSeconds, longitudeDirection ? 'E' : 'W' };
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

    public void setLatitudeDegree(double latitudeInDegrees) {
        setLatitude((int)(latitudeInDegrees * 3600.0));
    }

    public void setLongitudeDegree(double longitudeInDegrees) {
        setLongitude((int)(longitudeInDegrees * 3600.0));
    }

    public int getLatitude() {
        return latitude;
    }

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

    public int getLongitude() {
        return longitude;
    }

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

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitudeInMeters) {
        this.altitude = altitudeInMeters;
    }

    @Override
    public String getStorableValue() {
        return "ROW(" + latitude + "," + longitude + "," + altitude + ")::GL";
    }

    public double getLatitudeDegree() {
        return (double)latitude / 3600.0;
    }

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

    public int getTimeDifferenceInMinutes() {
        return 1440 * longitude / 360 / 3600;
    }

    @Override
    public boolean equals(Object another) {
        if(!(another instanceof Geolocation)) {
            return false;
        }
        Geolocation a = (Geolocation)another;
        return latitude == a.latitude && longitude == a.longitude && altitude == a.altitude;
    }
}
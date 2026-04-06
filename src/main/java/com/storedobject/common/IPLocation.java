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
 * The IPLocation class fetches location and network-related information of a given IP address
 * by querying the external API provided by `ipinfo.io`. If no IP address is provided, the class
 * defaults to retrieving information for the IP address of the local machine or network.
 *
 * @author Syam
 */
public class IPLocation {

    private JSON details = null;
    private String ip;

    /**
     * Constructs an instance of the IPLocation class with the local machine's IP address.
     * This constructor initializes the `IPLocation` object to fetch location and network-related
     * information for the default IP address, which corresponds to the machine or network
     * being used to execute the application.
     */
    public IPLocation() {
        this("");
    }

    /**
     * Constructs an instance of the IPLocation class with a specified IP address.
     * This constructor initializes the `IPLocation` object to fetch location and network-related
     * information for the given IP address. If the provided IP address is invalid or empty,
     * it will default to retrieving information based on the local machine's IP address.
     *
     * @param ip the IP address for which location and network-related information will be fetched.
     *           If an empty string is provided, the local machine's IP address will be used.
     */
    public IPLocation(String ip) {
        this.ip = ip;
    }

    private void set() {
        if(ip == null) {
            return;
        }
        details = HTTP2.builder("https://ipinfo.io" + (ip.isEmpty() ? "" : ("/" + ip)) + "/json").json();
        ip = null;
    }

    private boolean error() {
        if(details == null && ip == null) {
            return true;
        }
        if(details == null) {
            set();
        }
        return details == null;
    }

    private String get(String key) {
        return error() ? null : details.getString(key);
    }

    /**
     * Retrieves the geographical location associated with the current or specified IP address.
     * The location is parsed from a comma-separated string containing latitude and longitude values.
     * If the location cannot be determined, this method returns null.
     *
     * @return a Geolocation object representing the latitude and longitude of the IP address,
     *         or null if the location is unavailable or cannot be parsed.
     */
    public Geolocation getLocation() {
        String loc = get("loc");
        if(loc == null) {
            return null;
        }
        int comma = loc.indexOf(',');
        return new Geolocation(Double.parseDouble(loc.substring(0, comma)), Double.parseDouble(loc.substring(comma + 1)));
    }

    /**
     * Retrieves the IP address associated with the current IPLocation instance.
     * If an error occurs or the IP address is unavailable, this method returns null.
     *
     * @return the IP address as a String, or null if the IP address is unavailable or an error occurs.
     */
    public String getIP() {
        return error() ? null : get("ip");
    }

    /**
     * Retrieves the hostname associated with the current IPLocation instance.
     * If an error occurs or the hostname is unavailable, this method returns null.
     *
     * @return the hostname as a String, or null if the hostname is unavailable or an error occurs.
     */
    public String getHostName() {
        return error() ? null : get("hostname");
    }

    /**
     * Retrieves the city associated with the current IP address.
     * If an error occurs or the city information is unavailable, this method returns null.
     *
     * @return the city as a String, or null if the city information is unavailable or an error occurs.
     */
    public String getCity() {
        return error() ? null : get("city");
    }

    /**
     * Retrieves the region associated with the current IP address.
     * If an error occurs or the region information is unavailable, this method returns null.
     *
     * @return the region as a String, or null if the region information is unavailable or an error occurs.
     */
    public String getRegion() {
        return error() ? null : get("region");
    }

    /**
     * Retrieves the country associated with the current IP address.
     * If an error occurs or the country information is unavailable, this method returns null.
     *
     * @return the country as a String, or null if the country information is unavailable or an error occurs.
     */
    public String getCountry() {
        return error() ? null : get("country");
    }

    /**
     * Retrieves the organization associated with the current IP address.
     * If an error occurs or the organization information is unavailable, this method returns null.
     *
     * @return the organization as a String, or null if the organization information is unavailable or an error occurs.
     */
    public String getOrganization() {
        return error() ? null : get("org");
    }

    @Override
    public String toString() {
        return "{ IP = " + getIP() + ", Host Name = " + getHostName() + ", City = " + getCity() + ", Region = " + getRegion() +
                ", Country = " + getCountry() + ", Organization = " + getOrganization() + ", Geolocation = " + getLocation() + " }";
    }
}
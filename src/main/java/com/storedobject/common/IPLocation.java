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

import java.net.URL;

public class IPLocation {

    private JSON details = null;
    private String ip;

    public IPLocation() {
        this("");
    }

    public IPLocation(String ip) {
        this.ip = ip;
    }

    private void set() {
        if(ip == null) {
            return;
        }
        try {
            details = new JSON(new URL("http://ipinfo.io" + (ip.isEmpty() ? "" : ("/" + ip)) + "/json"));
        } catch(Exception e) {
            details = null;
        }
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

    public Geolocation getLocation() {
        String loc = get("loc");
        if(loc == null) {
            return null;
        }
        int comma = loc.indexOf(',');
        return new Geolocation(Double.parseDouble(loc.substring(0, comma)), Double.parseDouble(loc.substring(comma + 1)));
    }

    public String getIP() {
        return error() ? null : get("ip");
    }

    public String getHostName() {
        return error() ? null : get("hostname");
    }

    public String getCity() {
        return error() ? null : get("city");
    }

    public String getRegion() {
        return error() ? null : get("region");
    }

    public String getCountry() {
        return error() ? null : get("country");
    }

    public String getOrganization() {
        return error() ? null : get("org");
    }

    @Override
    public String toString() {
        return "{ IP = " + getIP() + ", Host Name = " + getHostName() + ", City = " + getCity() + ", Region = " + getRegion() +
                ", Country = " + getCountry() + ", Organization = " + getOrganization() + ", Geolocation = " + getLocation() + " }";
    }
}
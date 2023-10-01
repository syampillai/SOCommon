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
 * IN Address<BR>
 * line[0]: Place name within the district<BR>
 * line[1]: District code<BR>
 * line[2]: Province code<BR>
 *
 * @author Syam
 */
public final class PKAddress extends Address {

    private final int POS_PROVINCE = 1, POS_DISTRICT = 2, POS_PLACE = 3;
    private static final String[] provinces = new String[] {
            "Balochistan", "FATA", "Islamabad", "Khyber Pakhtunkhwa", "Punjab", "Sindh"
    };
    private static final String[][] districts = new String[][]{
            { // Balochistan
                "Awaran", "Barkhan", "Chagai", "Dera Bugti", "Gwadar", "Harnai", "Jaffarabad", "Jhal Magsi",
                    "Kachhi (Bolan)", "Kalat", "Kech", "Kharan", "Khuzdar", "Killa Abdullah", "Killa Saifullah",
                    "Kohlu", "Lasbela", "Lehri", "Loralai", "Mastung", "Musakhel", "Nasirabad", "Nushki",
                    "Panjgur", "Pishin", "Quetta", "Sheerani (Sherani)", "Sibi", "Sohbatpur", "Washuk",
                    "Zhob", "Ziarat"
            },
            { // FATA
                "Bajaur", "Frontier Region Bannu", "Frontier Region Dera Ismail Khan",
                    "Frontier Region Kohat", "Frontier Region Lakki Marwat", "Frontier Region Peshawar",
                    "Frontier Region Tank", "Khyber", "Kurram", "Mohmand", "North Waziristan", "Orakzai",
                    "South Waziristan"
            },
            { // Islamabad
                "Islamabad"
            },
            { // Khyber Pakhtunkhwa
                "Abbottabad", "Bannu", "Batagram", "Buner", "Charsadda", "Chitral", "Dera Ismail Khan",
                    "Hangu", "Haripur", "Karak", "Kohat", "Kohistan", "Lakki Marwat", "Lower Dir",
                    "Malakand Protected Area", "Mansehra", "Mardan", "Nowshera", "Peshawar", "Shangla",
                    "Swabi", "Swat", "Tank", "Tor Ghar (Kala Dhaka)", "Upper Dir"
            },
            { // Punjab
                "Attock", "Bahawalnagar", "Bahawalpur", "Bhakkar", "Chakwal", "Chiniot", "Dera Ghazi Khan",
                    "Faisalabad", "Gujranwala", "Gujrat", "Hafizabad", "Jhang", "Jhelum", "Kasur",
                    "Khanewal", "Khushab", "Lahore", "Layyah", "Lodhran", "Mandi Bahauddin", "Mianwali",
                    "Multan", "Muzaffargarh", "Nankana Sahib", "Narowal", "Okara", "Pakpattan", "Rahim Yar Khan",
                    "Rajanpur", "Rawalpindi", "Sahiwal", "Sargodha", "Sheikhupura", "Sialkot",
                    "Toba Tek Singh", "Vehari"
            },
            { // Sindh
                "Badin", "Dadu", "Ghotki", "Hyderabad", "Jocobabad", "Jamshoro", "Karachi Central", "Karachi East",
                    "Karachi South", "Karachi West", "Kashmore", "Khairpur", "Korangi", "Larkana",
                    "Malir", "Matiari", "Mirpur Khas", "Naushahro Feroze", "Qambar Shahdadkot", "Sanghar",
                    "Shaheed Benazir Abad (Nawabshah)", "Shikapur", "Sujawal", "Sukkur", "Tando Allahyar",
                    "Tando Muhammad Khan", "Tharparkar", "Thatta", "Umerkot"
            },
    };

    PKAddress() {
    }

    @Override
    boolean parse() throws SOException {
        lines[lines.length - POS_PROVINCE] = match(lines[lines.length - POS_PROVINCE], provinces);
        int provice = getProvince();
        lines[lines.length - POS_DISTRICT] = match(lines[lines.length - POS_DISTRICT], districts[provice]);
        return true;
    }

    @Override
    public int getExtraLines() {
        return 3;
    }

    @Override
    String convert(int lineNumber) throws SOException {
        int n = lines.length - POS_PROVINCE;
        if(lineNumber == n) {
            return getProvinceName();
        }
        --n;
        if(lineNumber == n) {
            String d = getDistrictName();
            if(postalCode != null && !postalCode.isEmpty()) {
                d += " " + postalCode;
            }
            return d;
        }
        --n;
        if(lineNumber == n) {
            return getPlaceName();
        }
        return super.convert(lineNumber);
    }

    public String getPlaceName() {
        return lines[lines.length - POS_PLACE];
    }

    public void setPlaceName(String placeName) {
        lines[lines.length - POS_PLACE] = placeName == null ? "" : placeName;
    }

    public int getProvince() throws SOException {
        return extractNumber(lines.length - POS_PROVINCE);
    }

    public void setProvince(int province) {
        int district;
        try {
            district = getDistrict();
        } catch (SOException e) {
            district = 0;
        }
        lines[lines.length - POS_PROVINCE] = "" + (province % provinces.length);
        setDistrict(district);
    }

    public String getProvinceName() {
        return extractName(lines.length - POS_PROVINCE, provinces);
    }

    public int getDistrict() throws SOException {
        return extractNumber(lines.length - POS_DISTRICT);
    }

    public void setDistrict(int district) {
        int state;
        try {
            state = getProvince();
        } catch (SOException e) {
            state = 0;
        }
        String[] districts = getDistricts(state);
        lines[lines.length - POS_DISTRICT] = "" + (district % districts.length);
    }

    public String getDistrictName() throws SOException {
        return extractName(lines.length - POS_DISTRICT, districts[getProvince()]);
    }

    public static String[] getProvinces() {
        return provinces;
    }

    public static String[] getDistricts(int state) {
        return districts[state];
    }

    @Override
    int postalCodePosition() {
        return 100;
    }

    @Override
    public boolean isPostalCodeMandatory() {
        return false;
    }

    @Override
    public int getPostalCodeMaxLength() {
        return 5;
    }
}
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

import java.util.*;
import java.util.ArrayList;

/**
 * Representation of a Country.
 *
 * @author Syam
 */
public final class Country {

    private static final Map<String, Country> map = new HashMap<>();
    static {
        new Country("AD", 376);
        new Country("AE", 971);
        new Country("AF", 93);
        new Country("AG", 1268);
        new Country("AI", 1264);
        new Country("AL", 355);
        new Country("AM", 374);
        new Country("AN", 599);
        new Country("AO", 244);
        new Country("AQ", 672);
        new Country("AR", 54);
        new Country("AS", 1684);
        new Country("AT", 43);
        new Country("AW", 297);
        new Country("AU", 61);
        new Country("AZ", 994);
        new Country("BA", 387);
        new Country("BB", 1246);
        new Country("BD", 880);
        new Country("BE", 32);
        new Country("BF", 226);
        new Country("BG", 359);
        new Country("BH", 973);
        new Country("BI", 257);
        new Country("BJ", 229);
        new Country("BL", 590);
        new Country("BM", 1441);
        new Country("BN", 673);
        new Country("BO", 591);
        new Country("BR", 55);
        new Country("BS", 1242);
        new Country("BT", 975);
        new Country("BW", 267);
        new Country("BY", 375);
        new Country("BZ", 501);
        new Country("CA", 1);
        new Country("CC", 61);
        new Country("CF", 236);
        new Country("CG", 242);
        new Country("CH", 41);
        new Country("CI", 225);
        new Country("CK", 682);
        new Country("CL", 56);
        new Country("CM", 237);
        new Country("CN", 86);
        new Country("CO", 57);
        new Country("CR", 506);
        new Country("CU", 53);
        new Country("CV", 238);
        new Country("CW", 599);
        new Country("CX", 61);
        new Country("CY", 357);
        new Country("CZ", 420);
        new Country("DE", 49);
        new Country("DJ", 253);
        new Country("DK", 45);
        new Country("DM", 1767);
        new Country("DO", 1809);
        new Country("DZ", 213);
        new Country("EC", 593);
        new Country("EE", 372);
        new Country("EG", 20);
        new Country("EH", 212);
        new Country("ER", 291);
        new Country("ES", 34);
        new Country("ET", 251);
        new Country("FI", 358);
        new Country("FJ", 679);
        new Country("FK", 500);
        new Country("FM", 691);
        new Country("FO", 298);
        new Country("FR", 33);
        new Country("GA", 241);
        new Country("GD", 1473);
        new Country("GE", 995);
        new Country("GB", 44);
        new Country("GG", 441481);
        new Country("GQ", 240);
        new Country("GH", 233);
        new Country("GI", 350);
        new Country("GL", 299);
        new Country("GM", 220);
        new Country("GN", 224);
        new Country("GR", 30);
        new Country("GT", 502);
        new Country("GU", 1671);
        new Country("GW", 245);
        new Country("GY", 592);
        new Country("HK", 852);
        new Country("HN", 504);
        new Country("HR", 385);
        new Country("HT", 509);
        new Country("HU", 36);
        new Country("ID", 62);
        new Country("IE", 353);
        new Country("IL", 972);
        new Country("IM", 441624);
        new Country("IN", 91);
        new Country("IO", 246);
        new Country("IQ", 964);
        new Country("IR", 98);
        new Country("IS", 354);
        new Country("IT", 39);
        new Country("JE", 441534);
        new Country("JP", 81);
        new Country("JM", 1876);
        new Country("JO", 962);
        new Country("KE", 254);
        new Country("KG", 996);
        new Country("KH", 855);
        new Country("KI", 686);
        new Country("KM", 269);
        new Country("KN", 1869);
        new Country("KP", 850);
        new Country("KR", 82);
        new Country("KY", 1345);
        new Country("KW", 965);
        new Country("KZ", 7);
        new Country("LA", 856);
        new Country("LB", 961);
        new Country("LC", 1758);
        new Country("LI", 423);
        new Country("LK", 94);
        new Country("LR", 231);
        new Country("LS", 266);
        new Country("LT", 370);
        new Country("LU", 352);
        new Country("LV", 371);
        new Country("LY", 218);
        new Country("MA", 212);
        new Country("MC", 377);
        new Country("MD", 373);
        new Country("ME", 382);
        new Country("MF", 590);
        new Country("MG", 261);
        new Country("MK", 389);
        new Country("MH", 692);
        new Country("ML", 223);
        new Country("MM", 95);
        new Country("MN", 976);
        new Country("MO", 853);
        new Country("MP", 1670);
        new Country("MR", 222);
        new Country("MS", 1664);
        new Country("MT", 356);
        new Country("MU", 230);
        new Country("MV", 960);
        new Country("MW", 265);
        new Country("MX", 52);
        new Country("MY", 60);
        new Country("MZ", 258);
        new Country("NA", 264);
        new Country("NC", 687);
        new Country("NE", 227);
        new Country("NG", 234);
        new Country("NI", 505);
        new Country("NL", 31);
        new Country("NO", 47);
        new Country("NP", 977);
        new Country("NR", 674);
        new Country("NU", 683);
        new Country("NZ", 64);
        new Country("OM", 968);
        new Country("PA", 507);
        new Country("PE", 51);
        new Country("PF", 369);
        new Country("PG", 675);
        new Country("PH", 63);
        new Country("PL", 48);
        new Country("PM", 508);
        new Country("PN", 64);
        new Country("PK", 92);
        new Country("PR", 1787);
        new Country("PS", 970);
        new Country("PT", 351);
        new Country("PW", 680);
        new Country("PY", 595);
        new Country("QA", 974);
        new Country("RE", 262);
        new Country("RO", 40);
        new Country("RS", 381);
        new Country("RU", 7);
        new Country("RW", 250);
        new Country("SA", 966);
        new Country("SB", 677);
        new Country("SC", 248);
        new Country("Sd", 249);
        new Country("SE", 46);
        new Country("SG", 65);
        new Country("SH", 290);
        new Country("SI", 386);
        new Country("SJ", 47);
        new Country("SK", 421);
        new Country("SL", 232);
        new Country("SM", 378);
        new Country("SN", 221);
        new Country("SO", 252);
        new Country("Sr", 597);
        new Country("SS", 211);
        new Country("ST", 239);
        new Country("SV", 503);
        new Country("SX", 1721);
        new Country("SY", 963);
        new Country("SZ", 268);
        new Country("TC", 1649);
        new Country("TD", 235);
        new Country("TG", 228);
        new Country("TH", 66);
        new Country("TJ", 992);
        new Country("TK", 690);
        new Country("TL", 670);
        new Country("TM", 993);
        new Country("TN", 216);
        new Country("TO", 676);
        new Country("TR", 90);
        new Country("TT", 1868);
        new Country("TV", 688);
        new Country("TW", 886);
        new Country("TZ", 255);
        new Country("UA", 380);
        new Country("UG", 256);
        new Country("US", 1);
        new Country("UY", 598);
        new Country("UZ", 998);
        new Country("VA", 379);
        new Country("VC", 1784);
        new Country("VE", 58);
        new Country("VG", 1284);
        new Country("VI", 1340);
        new Country("VN", 84);
        new Country("VU", 678);
        new Country("XK", 283);
        new Country("YE", 967);
        new Country("YT", 262);
        new Country("WF", 681);
        new Country("WS", 685);
        new Country("ZA", 27);
        new Country("ZM", 260);
        new Country("ZW", 263);
    }

    private static List<Country> list;
    private final String shortName;
    private final int dialingCode;
    private Locale locale;

    private Country(String shortName, int dialingCode) {
        this.shortName = shortName;
        this.dialingCode = dialingCode;
        map.put(shortName, this);
    }

    /**
     * Get country for a given short name.
     *
     * @param shortName Short name of the country (as per ISO 2 letter code)
     * @return Country.
     */
    public Country get(String shortName) {
        if(shortName == null) {
            return null;
        }
        String name = shortName.toUpperCase();
        Country country = map.get(name);
        if(country != null) {
            return country;
        }
        return list().stream().filter(c -> name.equals(c.getName().toUpperCase())).findAny().orElse(null);
    }

    /**
     * List the list of countries.
     *
     * @return List of countries.
     */
    public static List<Country> list() {
        if(list == null) {
            List<Country> countries = new ArrayList<>(map.values());
            countries.sort(Comparator.comparing(Country::getName));
            list = Collections.unmodifiableList(countries);
        }
        return list;
    }

    /**
     * Gets the name of the Country
     *
     * @return The name
     */
    public String getName() {
        return getLocale().getDisplayName(Locale.ENGLISH);
    }

    /**
     * Gets the short name of the Country.
     *
     * @return The short name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Get the {@link Locale} of the country (with default language).
     *
     * @return Locale.
     */
    public Locale getLocale() {
        if(locale == null) {
            locale = new Locale("", shortName);
        }
        return locale;
    }

    /**
     * Get ISO 3 character code of the country.
     *
     * @return ISO 3 character code.
     */
    public String getISO3CharacterCode() {
        return getLocale().getISO3Country();
    }

    /**
     * Get the local name of the country.
     *
     * @return Country's name in local language.
     */
    public String getLocalName() {
        for(Locale locale: Locale.getAvailableLocales()) {
            if(locale.getCountry().equals(shortName)) {
                return locale.getDisplayCountry(locale);
            }
        }
        return getName();
    }

    /**
     * Get the ISD prefix for the country.
     *
     * @return ISD dialing prefix;
     */
    public String getISDCode() {
        if(dialingCode > 440000) {
            return "+44 " + (dialingCode - 440000);
        }
        if(dialingCode > 1000) {
            return "+1 " + (dialingCode - 1000);
        }
        return "+" + dialingCode;
    }

    /**
     * Get the flag as as an emoji that can be displayed in most of the computer systems.
     *
     * @return Flag emoji.
     */
    public String getFlag() {
        return flag(shortName);
    }

    private static String flag(String country) {
        StringBuilder s = new StringBuilder();
        char[] chars = country.toLowerCase().toCharArray();
        if(chars.length == 2) {
            for (char c : chars) {
                surrogate(s,0x1F1E6 + (c - 'a'));
            }
        } else {
            surrogate(s, 0x1F3F4);
            for (char c : chars) {
                surrogate(s,0xE0061 + (c - 'a'));
            }
            surrogate(s, 0xE007F);
        }
        return s.toString();
    }

    private static void surrogate(StringBuilder s, int unicode) {
        for(char c: Character.toChars(unicode)) {
            s.append(c);
        }
    }

    @Override
    public String toString() {
        return shortName;
    }
}
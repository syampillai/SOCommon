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
 * line[0]: Post Office<BR>
 * line[1]: PIN (must be digits)<BR>
 * line[2]: District code<BR>
 * line[3]: State code<BR>
 *
 * @author Syam
 */
public final class INAddress extends Address {

    private static String[] states = new String[] {
            "Andra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattishgarh",
            "Goa", "Gujarat", "Haryana", "Himachal Pradesh",
            "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Madya Pradesh", "Maharashtra",
            "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajastan",
            "Sikkim", "Tamilnadu", "Telengana", "Tripura", "Uttar Pradesh", "Uttarakhand",
            "West Bengal", "Andaman and Nicobar", "Chandigarh", "Dadra and Nagar Haveli", "Daman and Diu",
            "Lakhshadweep", "National Capital Territory of Delhi", "Pondicherry"
    };
    private static String[][] districts = new String[][] {
            { // Andra Pradesh
                "Anantapur", "Chittoor", "East Godavari", "Guntur", "Krishna", "Kurnool", "Prakasam",
                    "Sri Potti Sriramulu (Nellore)", "Srikakulam", "Visakhapatnam", "Vizianagaram",
                    "West Godavari", "YSR"
            },
            { // Arunachal Pradesh
                "Anjaw", "Changlang", "East Kameng", "East Siang", "Lohit", "Lower Subansiri", "Papum-Pare",
                    "Tawang Town", "Tirap", "Lower Dibang Vally", "Upper Siang", "Upper Subansiri", "West Kameng",
                    "West Siang", "Upper Dibang Vally", "Kurung Kumey"
            },
            { // Assam
                "Baksa", "Barpeta", "Biswanath", "Bongaigaon", "Cachar", "Charaideo", "Chirang",
                    "Darrang", "Dhemaji", "Dhubri", "Dibrugarh", "Dima Hasao", "Goalpara", "Golaghat",
                    "Hailakandi", "Hojai", "Jorhat", "Kamrup Metropolitan", "Kamrup", "Karbi Anglong",
                    "Karimganj", "Kokrajhar", "Lakhimpur", "Majuli", "Morigaon", "Nagaon", "Nalbari",
                    "Sivasagar", "Sonitpur", "South Salmara-Mankachar", "Tinsukia", "Udalguri",
                    "West Karbi Anglong"
            },
            { // Bihar
                "Araria", "Arwal", "Aurangabad", "Banka", "Begusarai", "Bhagalpur", "Bhojpur", "Buxar",
                    "Darbhanga", "East Champaran", "Gaya", "Gopalganj", "Jamui", "Jehanabad", "Khagaria",
                    "Kishanganj", "Kaimur", "Katihar", "Lakhisarai", "Madhubani", "Munger", "Madhepura",
                    "Muzaffarpur", "Nalanda", "Nawada", "Patna", "Purnia", "Rohtas", "Saharsa",
                    "Samastipur", "Sheohar", "Sheikhpura", "Saran", "Sitamarhi", "Supaul", "Siwan",
                    "Vaishali", "West Champaran"
            },
            { // Chhattishgarh
                "Balod", "Baloda Bazar", "Balrampur", "Bastar", "Bemetara", "Bijapur", "Bilaspur", "Dantewada",
                    "Dhamtari", "Durg", "Gariaband", "Janjgir-Champa", "Jashpur", "Kabirdham", "Kanker",
                    "Kondagaon", "Korba", "Koriya", "Mahasamund", "Mungeli", "Narayanpur", "Raigarh", "Raipur",
                    "Rajnandgaon", "Sukma", "Surajpur", "Surguja"
            },
            { // Goa
                "North Goa", "South Goa"
            },
            { // Gujarat
                "Ahmedabad", "Amreli", "Anand", "Aravalli", "Banaskantha", "Bharuch", "Bhavnagar", "Botad",
                    "Chhota Udaipur", "Dahod", "Dang", "Devbhoomi Dwarka", "Gandhinagar", "Gir Somnath",
                    "Jamnagar", "Junagadh", "Kutch", "Kheda", "Mahisagar", "Mehsana", "Morbi", "Narmada",
                    "Navsari", "Panchmahal", "Patan", "Porbandar", "Rajkot", "Sabarkantha", "Surat",
                    "Surendranagar", "Tapi", "Vadodara", "Valsad"
            },
            { // Haryana
                "Ambala", "Bhiwani", "Charkhi Dadri", "Faridabad", "Fatehabad", "Gurugram", "Hisar",
                    "Jhajjar", "Jind", "Kaithal", "Karnal", "Kurukshetra", "Mahendragarh", "Nuh",
                    "Palwal", "Panchkula", "Panipat", "Rewari", "Rohtak", "Sirsa", "Sonipat", "Yamunanagar"
            },
            { // Himachal Pradesh
                "Bilaspur", "Chamba", "Hamirpur", "Kangra", "Kinnaur", "Kullu", "Lahaul and Spiti", "Mandi",
                    "Shimla", "Sirmaur", "Solan", "Una"
            },
            { // Jammu Kashmir
                "Anantnag", "Badgam", "Bandipora", "Baramula", "Doda", "Ganderbal", "Jammu", "Kargil",
                    "Kathua", "Kishtwar", "Kulgam", "Kupwara", "Pulwama", "Punch", "Rajouri", "Ramban",
                    "Reasi", "Samba", "Shupiyan", "Srinagar", "Udhampur"
            },
            { // Jharkhand
                "Bokaro", "Chatra", "Deoghar", "Dhanbad", "Dumka", "East Singhbhum", "Garhwa", "Giridih", "Godda",
                    "Gumia", "Hazaribagh", "Jamtara", "Khunti", "Kodarma", "Latehar", "Lohardaga", "Pakur",
                    "Palamu", "Ramgarh", "Ranchi", "Sahibganj", "Saraikela Kharsawan", "Simdega", "West Singhbhum"
            },
            { // Karnataka
                "Bagalkot", "Bangalore", "Bangalore Rural", "Belgaum", "Bellary", "Bidar", "Bijapur",
                    "Chamarajanagar", "Chikkaballapura", "Chikmagalur", "Chitradurga", "Dakshina Kannada",
                    "Davanagere", "Dharwad", "Gadag", "Gulbarga", "Hassan", "Haveri", "Kodagu", "Kolar",
                    "Koppal", "Mandya", "Mysore", "Raichur", "Ramanagara", "Shimoga", "Tumkur", "Udupi",
                    "Uttara Kannada", "Yadgir"
            },
            { // Kerala
                "Alapuzha", "Ernakulam", "Idukki", "Kannur", "Kasaragod", "Kollam", "Kottayam", "Kozhikode",
                    "Malappuram", "Palakkad", "Pathanamthitta", "Thiruvanthapuram", "Trissur", "Wayanad"
            },
            { // Madya Pradesh
                "Agar", "Alirapur", "Anuppur", "Ashok Nagar", "Balaghat", "Barwani", "Betul", "Bhind",
                    "Bhopal", "Burhanpur", "Chhatarpur", "Chhindwara", "Damoh", "Datia", "Dewas", "Dhar",
                    "Dindori", "Guna", "Gwalior", "Harda", "Hoshangabad", "Indore", "Jabalpur", "Jhabua",
                    "Kanti", "Khandwa (East Nimar)", "Khargone (West Nimar)", "Mandla", "Mandsaur",
                    "Morena", "Narsinghpur", "Neemuch", "Panna", "Raisen", "Rajgarh", "Ratlam", "Rewa",
                    "Sagar", "Satna", "Sehore", "Seoni", "Shahdol", "Shajapur", "Sheopur", "Shivpuri",
                    "Sidhi", "Singrauli", "Tikamgarh", "Ujjain", "Umaria", "Vidisha"
            },
            { // Maharashtra
                "Ahmadnagar", "Akola", "Amravati", "Aurangabad", "Bhandara", "Bid", "Buldana", "Chandrapur",
                    "Dhule", "Gadchiroli", "Gondiya", "Hingoli", "Jalgaon", "Jalna", "Kolhapur", "Latur",
                    "Mumbai City", "Mumbai Suburban", "Nagpur", "Nanded", "Nandurbar", "Nashik", "Osmanabad",
                    "Palghar", "Parbhani", "Pune", "Raigarh", "Ratnagiri", "Sangli", "Satara", "Sindhudurg",
                    "Solapur", "Thane", "Wardha", "Washim", "Yavatmal"
            },
            { // Manipur
                "Bishnupur", "Chandel", "Churachandpur", "Imphal East", "Imphal West", "Senapati",
                    "Tamenglong", "Thoubal", "Ukhrul", "Jiribam", "Kangpokpi", "Kakching", "Tengnoupal",
                    "Kamjong", "Noney", "Pherzawl"
            },
            { // Meghalaya
                "East Garo Hills", "West Garo Hills", "North Garo Hills", "South Garo Hills", "South West Garo Hills",
                    "East Jaintia Hills", "West Jaintia Hills", "East Khasi Hills", "South West Khasi Hills",
                    "West Khasi Hills", "Ri-Bhoi"
            },
            { // Mizoram
                "Aizawl", "Champhai", "Kolasib", "Lawngtlai", "Lunglei", "Mamit", "Saiha", "Serchhip"
            },
            { // Nagaland
                "Dimapur", "Kiphire", "Kohima", "Longleng", "Mokokchung", "Mon", "Peren", "Phek", "Tuensang",
                    "Wokha", "Zunheboto"
            },
            { // Odissa
                "Angul", "Balangir", "Balasore", "Bargarh (Baragarh)", "Boudh (Bauda)", "Bhadrak", "Cuttack",
                    "Debagarh (Deogarh)", "Dhenkanal", "Gajapati", "Ganjam", "Jagatsinghpur", "Jajpur",
                    "Jharsuguda", "Kalahandi", "Kandhamal", "Kendrapara", "Kendujhar (Keonjhar)",
                    "Khordha", "Koraput", "Malkangiri", "Mayurbhanj", "Nabarangpur", "Nayagarh", "Nuapada",
                    "Puri", "Rayagada", "Sambalpur", "Subarnapur (Sonepur)", "Sundergarh"
            },
            { // Punjab
                "Amritsar", "Barnala", "Bathinda", "Fazilka", "Faridkot", "Fatehgarh Sahib", "Firozpur",
                    "Gurdaspur", "Hoshiarpur", "Jalandhar", "Kapurthala", "Ludhiana", "Mansa", "Moga",
                    "Mohali", "Muktsar", "Pathankot", "Patiala", "Rupnagar", "Sangrur",
                    "Shahid Bhagat Singh Nagar", "Tam Taran"
            },
            { // Rajastan
                "Ajmer", "Alwar", "Banswara", "Baran", "Barmer", "Bharatpur", "Bhilwara", "Bikaner", "Bundi",
                    "Chittaurgarh", "Churu", "Dausa", "Dhaulpur", "Dungarpur", "Ganganagar", "Hanumangarh",
                    "Jaipur", "Jaisalmer", "Jalor", "Jhalawar", "Jhunjhunun", "Jodhpur", "Karauli", "Kota",
                    "Nagaur", "Pali", "Pratapgarh", "Rajsamand", "Sawai Madhopur", "Sikar", "Sirohi", "Tonk",
                    "Udaipur"
            },
            { // Sikkim
                "East Sikkim", "North Sikkim", "South Sikkim", "West Sikkim"
            },
            { // Tamilnadu
                "Ariyalur", "Chennai", "Coimbatore", "Cuddalore", "Dharmapuri", "Dindigul", "Erode",
                    "Kancheepuram", "Kanniyakumari", "Karur", "Krishnagiri", "Madurai", "Nagapattinam",
                    "Namakkal", "Perambalur", "Pudukkottai", "Ramanathapuram", "Salem", "Sivaganga",
                    "Thanjavur", "The Nilgiris", "Theni", "Thiruvallur", "Thiruvarur", "Thoothukkudi",
                    "Tiruchirappalli", "Tirunelveli", "Tiruppur", "Tiruvannamalai", "Vellore",
                    "Viluppuram", "Virudhunagar"
            },
            { // Telengana
                "Adilabad", "Komram Bheem", "Bhadradri", "Jayashankar", "Gadwal", "Hyderabad", "Jagtial",
                    "Jangaon", "Kamareddy", "Karimnagar", "Khammam", "Mahabubabad", "Mahbubnagar",
                    "Mancherial", "Medak", "Medchal", "Nalgonda", "Nagarkurnool", "Nirmal", "Nizamabad",
                    "Ranga Reddy", "Peddapalli", "Rajanna", "Sangareddy", "Siddipet", "Suryapet", "Vikarabad",
                    "Wanaparthy", "Warangal (Urban)", "Warangal (Rural)", "Yadadri"
            },
            { // Tripura
                "Dhalai", "Gomati", "Khowai", "North Tripura", "Sepahijala", "South Tripura",
                    "Unakoti", "West Tripura"
            },
            { // Uttar Pradesh
                "Agra", "Aligarh", "Allahabad", "Ambedkar Nagar", "Amethi (Chhatrapati Shahuji Maharaj Nagar)",
                    "Auraiya", "Azamgarh", "Baghpat", "Bahraich", "Ballia", "Balrampur", "Banda",
                    "Barabanki", "Bareilly", "Basti", "Bijnor", "Budaun", "Bulandshahar", "Chandauli",
                    "Chitrakoot", "Deoria", "Etah", "Etawah", "Faizabad", "Farrukhabad", "Fatehpur", "Firozabad",
                    "Gautam Buddha Nagar", "Ghaziabad", "Ghazipur", "Gonda", "Gorakhpur", "Hamirpur",
                    "Hapur (Panchsheel Nagar)", "Hardoi", "Hathras (Mahamaya Nagar)", "Jalaun", "Jaunpur",
                    "Jhansi", "Jyotiba Phule Nagar", "Kannauj", "Kanpur Dehat (Ramabai Nagar)",
                    "Kanpur Nagar", "Kasganj (Kanshiram Nagar)", "Kaushambi", "Kheri", "Kushinagar", "Lalitpur",
                    "Lucknow", "Maharajganj", "Mahoba", "Mainpuri", "Mathura", "Mau", "Meerut", "Mirzapur",
                    "Moradabad", "Muzaffarnagar", "Pilibhit", "Pratapgarh", "Rae Bareli", "Rampur",
                    "Saharanpur", "Sambhal (Bheem Nagar)", "Sant Kabir Nagar", "Sant Ravidas Nagar",
                    "Shahjahanpur", "Shamli", "Shrawasti", "Siddharth Nagar", "Sitapur", "Sonbhadra",
                    "Sultanpur", "Unnao", "Varanasi"
            },
            { // Uttarakhand
                "Almora", "Bageshwar", "Chamoli", "Champawat", "Dehradun", "Haridwar", "Nainital",
                    "Pauri Garhwal", "Pithoragarh", "Rudraprayag", "Tehri Garhwal", "Udham Singh Nagar",
                    "Uttarkashi"
            },
            { // West Bengal
                "Alipurduar", "Bankura", "Barddhaman", "Birbhum", "Dakshin Dinajpur", "Darjiling", "Haora",
                    "Hugli", "Jalpaiguri", "Koch Bihar", "Kolkata", "Maldah", "Murshidabad", "Nadia",
                    "North Twenty Four Parganas", "Paschim Medinipur", "Purba Medinipur", "Puruliya",
                    "South Twenty Four Parganas", "Uttar Dinajpur"
            },
            { // Andaman and Nicobar
                    "Nicobars", "North And Middle Andaman", "South Andaman"
            },
            { // Chandigarh
                    "Chandigarh"
            },
            { // Dadra and Nagar Haveli
                    "Dadra and Nagar Haveli"
            },
            { // Daman and Diu
                "Daman", "Diu"
            },
            { // Lakhshadweep
                "Lakhshadweep"
            },
            { // Leh
                "Leh"
            },
            { // National Capital Territory of Delhi
                    "Central Delhi", "East Delhi", "North Delhi", "South Delhi", "North East Delhi",
                    "South West Delhi", "New Delhi", "North West Delhi", "West Delhi", "Shahdara",
                    "South East Delhi"
            },
            { // Pondicherry
                "Karaikal", "Male", "Puducherry", "Yanam"
            }
    };

    INAddress() {
    }

    @Override
    protected boolean parse() throws SOException {
        if(StringUtility.isWhite(getPostOfficeName())) {
            return false;
        }
        int code = extractNumber(lines[lines.length - 3]);
        if(code != 0) {
            if (code < 100000 || code > 999999) {
                return false;
            }
        }
        lines[lines.length - 3] = "" + code;
        lines[lines.length - 1] = match(lines[lines.length - 1], states);
        code = Integer.parseInt(lines[lines.length - 1]);
        lines[lines.length - 2] = match(lines[lines.length - 2], districts[code]);
        return true;
    }

    @Override
    protected int getLineCount() {
        return 4;
    }

    @Override
    protected int getReservedLines() {
        return 4;
    }

    @Override
    protected String convert(int lineNumber) throws SOException {
        int n = lines.length - 1;
        if(lineNumber == n) {
            return getStateName();
        }
        --n;
        if(lineNumber == n) {
            return getDistrictName();
        }
        --n;
        if(lineNumber == n) {
            int pin = Integer.parseInt(lines[n]);
            if(pin == 0) {
                return null;
            }
            return "PIN " + pin;
        }
        --n;
        if(lineNumber == n) {
            return lines[n] + " P.O.";
        }
        return super.convert(lineNumber);
    }

    public String getPostOfficeName() {
        return lines[lines.length - 4];
    }

    public int getState()throws SOException {
        return extractNumber(lines.length - 1);
    }

    public String getStateName() {
        return extractName(lines.length - 1, states);
    }

    public int getDistrict()throws SOException {
        return extractNumber(lines.length - 2);
    }

    public String getDistrictName() throws SOException {
        return extractName(lines.length - 2, districts[getState()]);
    }

    public static String[] getStates() {
        return states;
    }

    public static String[] getDistricts(int state) {
        return districts[state];
    }
}
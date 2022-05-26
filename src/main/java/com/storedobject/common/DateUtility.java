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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.Calendar.*;

/**
 * Date utility functions.(Old {@link java.util.Date} and its cousin {@link Date} are still used in JDBC).
 *
 * @author Syam
 */
public class DateUtility {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    }

    private final static Date MAX_VALUE = create(3000, 12, 31);
    private final static long DAY_IN_MILLIS = 86400000L;
    private final static long HALF_DAY_IN_MILLIS = 43200000L;
    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",  Locale.getDefault());
    private final static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",  Locale.getDefault());
    private final static DateFormat monthFormat = new SimpleDateFormat("MMM yy", Locale.getDefault());
    private final static DateFormat tinyFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
    private final static DateFormat shortFormat = new SimpleDateFormat("MMM dd, yy", Locale.getDefault());
    private final static DateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final static DateFormat longFormat = new SimpleDateFormat("MMM dd yyyy hh:mm:ss a", Locale.getDefault());
    private final static DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
    private final static DateFormat hhmmFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final static DateFormat hhmmDateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    private final static DateFormatSymbols formatSymbols = new DateFormatSymbols();
    private final static String[] monthNames = new String[]
            { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
    private final static String[] weekNames = new String[]
            { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

    /**
    * Get the current date
    * 
    * @return Current Date
    */
    public static Date today() {
        GregorianCalendar c = new GregorianCalendar();
        return create(c);
    }

    /**
    * Get one day after the current date
    * 
    * @return One day after current date
    */    
    public static Date tomorrow() {
        return addDay(today(), 1);
    }

    /**
    * Get one day prior to current date
    * 
    * @return One day prior to current date
    */
    public static Date yesterday() {
        return addDay(today(), -1);
    }

    /**
    * Get start date of the current year
    * 
    * @return Start date of the current year
    */
    public static Date startOfYear() {
        return startOfYear(today());
    }

    /**
    * Get start date of year for a give date
    * 
    * @param date Date for which the start of the year is to be returned
    * @return Start date of year for a give date
    */
    public static <D extends java.util.Date> D startOfYear(D date) {
        D d = setDay(date, 1);
        return setMonth(d, 1);
    }

    /**
    * Get end date of current year
    * 
    * @return End date of current year
    */
    public static Date endOfYear() {
        return endOfYear(today());
    }

    /**
    * Get end date of year for a give date
    * 
    * @param date Date for which the end of the year is to be returned
    * @return End date of year for a give date
    */
    public static <D extends java.util.Date> D endOfYear(D date) {
        D d = setMonth(date, 12);
        return setDay(d, 31);
    }

    /**
    * Get start date of the current month
    * 
    * @return Start date of current month
    */
    public static Date startOfMonth() {
        return setDay(today(), 1);
    }
    
    /**
    * Get start date of month for a give date
    * 
    * @param date Date for which the start of the month is to be returned
    * @return Start date of month for a give date
    */
    public static <D extends java.util.Date> D startOfMonth(D date) {
        return setDay(date, 1);
    }

    /**
    * Get start date of a month. Month is calculated by adding an offset to the current month
    * 
    * @param monthOffset An integer offset value that has to be added to the current month value
    * @return Start date of month
    */
    public static Date startOfMonth(int monthOffset) {
        Date d = addMonth(today(), monthOffset);
        return setDay(d, 1);
    }

    /**
    * Get start date of a month. Month is calculated by adding an offset to the month corresponding to a given date
    * 
    * @param monthOffset An integer offset value that has to be added to the month corresponding to a given date
    * @return Start date of month
    */
    public static <D extends java.util.Date> D startOfMonth(int monthOffset, D date) {
        D d = addMonth(date, monthOffset);
        return setDay(d, 1);
    }

    /**
    * Get end date of current month
    * 
    * @return End date of current month
    */
    public static Date endOfMonth() {
        return endOfMonth(0);
    }

    /**
    * Get end date of month for a give date
    * 
    * @param date Date for which the end of the month is to be returned
    * @return Start date of month for a give date
    */
    public static <D extends java.util.Date> D endOfMonth(D date) {
        return endOfMonth(0, date);
    }

    /**
    * Get end date of a month. Month is calculated by adding an offset to the current month
    * 
    * @param monthOffset An integer offset value that has to be added to the current month value
    * @return Start date of month
    */
    public static Date endOfMonth(int monthOffset) {
        return endOfMonth(monthOffset, today());
    }

    /**
    * Get end date of a month. Month is calculated by adding an offset to the month corresponding to a given date
    * 
    * @param monthOffset An integer offset value that has to be added to the month corresponding to a given date
    * @return End date of month
    */
    public static <D extends java.util.Date> D endOfMonth(int monthOffset, D date) {
        D d = addMonth(date, monthOffset + 1);
        d = setDay(d, 1);
        return addDay(d, -1);
    }

    /**
    * Create a Date instance by passing a Calendar instance
    * 
    * @param c Calendar instance
    * @return Date corresponding to the calendar instance
    */
    public static Date create(Calendar c) {
        c = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        return new Date(c.getTimeInMillis());
    }

    /**
    * Create and return Date instance for the current date
    * 
    * @return Current date
    */
    public static Date create() {
        return today();
    }

    /**
    * Create a Date instance for the specified day, month and year
    * 
    * @param year Year.
    * @param month Month (1-based, not 0-based).
    * @param day Day.
    * @return Date for the specified day, month and year
    */
    public static Date create(int year, int month, int day) {
        //noinspection MagicConstant
        GregorianCalendar c = new GregorianCalendar(year, month - 1, day);
        return new Date(c.getTimeInMillis());
    }

    /**
    * Get an java.sql.Date corresponding to a given java.util.Date
    * 
    * @param date Date of type java.util.Date
    * @return date
    */
    public static <D extends java.util.Date> Date create(D date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTimeInMillis(date.getTime());
        return create(c);
    }

    /**
    * Get date corresponding to a particular date value in a year
    * 
    * @param dateValue an integer representing the date
    * @return Date corresponding to the given date value in integer
    */
    public static Date create(int dateValue) {
        int d = dateValue % 100;
        dateValue /= 100;
        int m = dateValue % 100;
        dateValue /= 100;
        return create(dateValue, m, d);
    }

    /**
    * Get date by passing date string separated by comma or hyphen (, or -) in year, month and date format
    * 
    * @param text Date in text format
    * @return Date corresponding to the given date text
    */
    public static Date create(String text) {
        text = text.trim();
        int year = value(text);
        if(("" + year).equals(text)) {
            return create(year);
        }
        int month, day;
        String[] s;
        text = text.replace(",", " ");
        text = text.replace("-", " ");
        while(text.indexOf("  ") > 0) {
            text = text.replace("  ", " ");
        }
        s = text.split("\\s");
        if(s.length < 3) {
            return null;
        }
        int y = value(s[0]), m = value(s[1]), d = value(s[2]);
        if(y > 999 && m > 0 && m <=12 && d > 0 && d <= 31) {
            return create(y, m, d);
        }
        day = m;
        if(day == 0) {
            month = getMonth(s[1]);
            day = y;
        } else {
            month = getMonth(s[0]);
        }
        if(month == 0) {
            return null;
        }
        year = d;
        return create(year, month, day);
    }

    /*
    * Get java.sql.Date by passing java.time.LocalDate
    *
    * @param date LocalDate that has to be converted to java.sql.Date
    * @return date
    */
    public static Date create(LocalDate date) {
        return create(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    /*
    * Get time by passing LocalDateTime
    *
    * @param date LocalDateTime
    * @return Time
    */
    public static Time createTime(LocalDateTime date) {
        Date d = create(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        return time(d.getTime() + date.getHour() * 3600000L + date.getMinute() * 60000L + date.getSecond() * 1000L);
    }

    /*
    * Get TimeStamp by passing LocalDateTime
    *
    * @param date LocalDateTime
    * @return Timestamp
    */
    public static Timestamp createTimestamp(LocalDateTime date) {
        Date d = create(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        return timestamp(d.getTime() + date.getHour() * 3600000L + date.getMinute() * 60000L + date.getSecond() * 1000L);
    }

    /*
    *Get java.time.LocalDate by passing java.util.Date
    *
    * @param date java.util.Date
    * @return date LocalDate
    */
    public static <D extends java.util.Date> LocalDate local(D date) {
        return LocalDate.of(getYear(date), getMonth(date), getDay(date));
    }
    
    /*
    * Get java.time.LocalDateTime by passing java.util.Date
    *
    * @param date java.util.Date
    * @return date LocalDateTime
    */
    public static <D extends java.util.Date> LocalDateTime localTime(D date) {
        return LocalDateTime.of(getYear(date), getMonth(date), getDay(date),
                get(date, HOUR_OF_DAY), get(date, MINUTE), get(date, SECOND), get(date, MILLISECOND));
    }

    private static int value(String s) {
        try {
            return Integer.parseInt(s);
        } catch(Throwable ignored) {
        }
        return 0;
    }

    
    private static java.sql.Time time(long t) {
        java.sql.Time time = new java.sql.Time(t);
        time.setTime(t);
        return time;
    }

    private static java.sql.Timestamp timestamp(long t) {
        java.sql.Timestamp time = new java.sql.Timestamp(t);
        time.setTime(t);
        return time;
    }

    /*
    * Get the Time value corresponding to a Calendar instance
    *
    * @param calendar
    * @return time
    */
    public static Time createTime(Calendar calendar) {
        return time(calendar.getTimeInMillis());
    }
    
    /*
    * Get the Time value corresponding to a java.util.Date instance
    *
    * @param date
    * @return time
    */
    public static Time createTime(java.util.Date date) {
        return time(date.getTime());
    }

    public static Timestamp createTimestamp(long time) {
        return timestamp(time);
    }
    
    public static Timestamp createTimestamp(java.util.Date date) {
        return timestamp(date.getTime());
    }


    /*
    * Gets the current date and time value
    * 
    * @return time
    */
    public static Time time() {
        return time(System.currentTimeMillis());
    }
    
    public static Time trimMillis(Time time) {
        return trim(time, 10000L);
    }


    public static Time trimSeconds(Time time) {
        return trim(time, 60000L);
    }

    public static Time trimMinutes(Time time) {
        return trim(time, 3600000L);
    }


    public static Time trimHours(Time time) {
        return trim(time, 86400000L);
    }

    private static Time trim(Time time, long tail) {
        long t = time.getTime() / tail;
        return time(t * tail);
    }

    /*
    * Get the current timestamp
    *
    * @return The current (UTC) Date and time
    */
    public static Timestamp now() {
        return timestamp(System.currentTimeMillis());
    }

    /*
    * Gets a timestamp for the date provided and the time set to start time of that day
    *
    * @param date
    * @return timestamp corresponding to the date passed with time set to start time of that day
    */
    public static Timestamp startTime(java.util.Date date) {
        return timestamp(trim(date).getTime());
    }

    private static java.sql.Date trim(java.util.Date date) {
        return create(getYear(date), getMonth(date), getDay(date));
    }

    /*
    * Gets the timestamp for the date provided and the time set to end time of that day
    *
    * @param date
    * @result timestamp corresponding to the date passed with time set to end time of that day
    */
    public static Timestamp endTime(java.util.Date date) {
        Date d = addDay(trim(date), 1);
        return timestamp(d.getTime() - 1000);
    }

    /*
    * Gets the timestamp for the current date with the time set to end time of the day
    *
    * @param date
    * @result timestamp
    */    
    public static Timestamp endOfToday() {
        Date d = tomorrow();
        return timestamp(d.getTime() - 1000);
    }
    
    /*
    * Gets the timestamp for the previous date with the time set to end time of the day
    *
    * @param date
    * @result timestamp
    */ 
    public static Timestamp endOfYesterday() {
        Date d = today();
        return timestamp(d.getTime() - 1000);
    }

    /*
    * Gets the timestamp for the current date with the time set to start time of the day
    *
    * @param date
    * @result timestamp
    */  
    public static Timestamp startOfToday() {
        Date d = today();
        return timestamp(d.getTime());
    }

    /*
    * Gets the timestamp for the next date with the time set to start time of the day
    *
    * @param date
    * @result timestamp
    */  
    public static Timestamp startOfTomorrow() {
        Date d = tomorrow();
        return timestamp(d.getTime());
    }

    public static Timestamp trimMillis(Timestamp time) {
        return trim(time, 10000L);
    }

    public static Timestamp trimSeconds(Timestamp time) {
        return trim(time, 60000L);
    }

    public static Timestamp trimMinutes(Timestamp time) {
        return trim(time, 3600000L);
    }

    public static Timestamp trimHours(Timestamp time) {
        return trim(time, 86400000L);
    }

    private static Timestamp trim(Timestamp time, long tail) {
        long t = time.getTime() / tail;
        return timestamp(t * tail);
    }

    public static <D extends java.util.Date> String formatMonth(D date) {
        return monthFormat.format(date);
    }

    /*
    * Formats a given date in sql date format
    *
    * @param date
    * @return formatted date
    */
    public static <D extends java.util.Date> String formatDate(D date) {
        return format.format(date);
    }

    
    /*
    * Formats a given date and time
    *
    * @param date
    * @return formatted date and time
    */
    public static <D extends java.util.Date> String formatLongDate(D date) {
        return longFormat.format(date);
    }

    /*
    * Formats a given date in MMM dd, yy format
    *
    * @param date
    * @return formatted date
    */
    public static <D extends java.util.Date> String formatShortDate(D date) {
        return shortFormat.format(date);
    }

    /*
    * Formats a given date in MM yy format
    *
    * @param date
    * @return formatted date
    */
    public static <D extends java.util.Date> String formatTinyDate(D date) {
        return tinyFormat.format(date);
    }

    /*
    * Formats the given date
    *
    * @param date
    * @return formatted date
    */
    public static <D extends java.util.Date> String format(D date) {
        if(date == null) {
            return null;
        }
        String s = formatWithTime(date);
        return s.toUpperCase().contains("12:00:00 AM") || s.contains("00:00:00") ? format.format(date) : s;
    }
    
    /*
    * Formats the given date
    *
    * @param date
    * @return formatted date in MM dd yyyy HH:MM:SS
    */
    public static <D extends java.util.Date> String formatWithTime(D date) {
        if(date == null) {
            return null;
        }
        String s = longFormat.format(date);
        if(date instanceof Timestamp && ((Timestamp)date).getNanos() > 0) {
            return s.substring(0, s.length() - 3) + "." + ((Timestamp)date).getNanos() + s.substring(s.length() - 3);
        }
        return s;
    }

    /*
    * Formats the given date
    *
    * @param date
    * @return formatted date in MM dd, yyyy HH:MM:SS AM/PM
    */    
    public static <D extends java.util.Date> String formatWithTimeHHMM(D date) {
        return date == null ? null : hhmmDateFormat.format(date);
    }
    
    public static <D extends java.util.Date> String formatTime(D date) {
        return timeFormat.format(date);
    }

    public static <D extends java.util.Date> String formatHHMM(D date) {
        return hhmmFormat.format(date);
    }

    public static String formatHHMM(long time) {
        return hhmmFormat.format(createTimestamp(time));
    }

    /*
    * Check whether two dates are same
    * 
    * @param one first date to compare 
    * @param two second date to compare
    * @return a boolean true if one and two are same, otherwise false
    */
    public static <D extends java.util.Date> boolean equals(D one, D two) {
        return compareTo(one, two) == 0;
    }

    /*
    * Compare two dates and get the difference
    * 
    * @param one Date to compare
    * @param two Date to compare
    * @return 0 if one == two, +ve 1 if one > two and -ve 1 if one < two
    */
    public static <D extends java.util.Date> int compareTo(D one, D two) {
        int v1 = getYear(one), v2 = getYear(two);
        if(v1 > v2) {
            return 1;
        }
        if(v1 < v2) {
            return -1;
        }
        v1 = getMonth(one);
        v2 = getMonth(two);
        if(v1 > v2) {
            return 1;
        }
        if(v1 < v2) {
            return -1;
        }
        v1 = getDay(one);
        v2 = getDay(two);
        return Integer.compare(v1, v2);
    }

    public static int getMonth(String monthName) {
        String[] m = monthNames;
        int loop = 2, i;
        while(loop > 0) {
            for(i=0; i<m.length; i++)
                if(m[i].equalsIgnoreCase(monthName)) return i+1;
            if(loop == 2) m = formatSymbols.getMonths();
            --loop;
        }
        return 0;
    }

    public static String getMonthName(int m) {
        if(m < 1 || m > 12) return null;
        return monthNames[m-1];
    }

    public static <D extends java.util.Date> String getMonthName(D date) {
        return getMonthName(getMonth(date));
    }

    public static int getYear() {
        return getYear(today());
    }

    /*
    * Gets the year value for a particular date
    * 
    * @param date
    * @return year corresponding to the date passed
    */
    public static <D extends java.util.Date> int getYear(D date) {
        return get(date, YEAR);
    }

    
    public static <D extends java.util.Date> D setYear(D date, int year) {
        return set(date, YEAR, year);
    }


    public static int getMonth() {
        return getMonth(today());
    }

    public static <D extends java.util.Date> int getMonth(D date) {
        return get(date, MONTH) + 1;
    }

    public static int getMonthZeroBased() {
        return getMonthZeroBased(today());
    }

    public static <D extends java.util.Date> int getMonthZeroBased(D date) {
        return get(date, MONTH);
    }

    /*
    * Sets the month of this date to the specified value.
    * 
    * @param date The original date
    * @param month the month value that has to be set
    * @return Date after setting the month value
    */
    public static <D extends java.util.Date> D setMonth(D date, int month) {
        return set(date, MONTH, month - 1);
    }

    public static int getDay() {
        return getDay(today());
    }

    public static <D extends java.util.Date> int getDay(D date) {
        return get(date, DATE);
    }

    public static <D extends java.util.Date> D setDay(D date, int day) {
        return set(date, DATE, day);
    }

    /*
    * Adds a particular number of year to a specified date
    *
    * @param date The date to which the years are added
    * @param year The number of year to add to the date passed
    * @return The date after adding the year offset
    */
    public static <D extends java.util.Date> D addYear(D date, int year) {
        return add(date, YEAR, year);
    }

    /*
    * Adds a particular number of months to a specified date
    *
    * @param date The date to which the months are added
    * @param month The number of months to add to the date passed
    * @return The date after adding the month offset
    */
    public static <D extends java.util.Date> D addMonth(D date, int month) {
        return add(date, MONTH, month);
    }

    /*
    * Adds a particular number of days to a specified date
    *
    * @param date The date to which the days are added
    * @param day The number of days to add to the date passed
    * @return The date after adding the days offset
    */
    public static <D extends java.util.Date> D addDay(D date, int day) {
        return add(date, DATE, day);
    }

    /*
    * Duplicates the date object passed
    * 
    * @param date
    * @return A duplicate of the date passed
    */
    public static <D extends java.util.Date> D clone(D date) {
        D d = cook(date);
        d.setTime(date.getTime());
        return d;
    }

    /*
    * Checks if a particular year specified is leap year
    * @param year
    * @return true if year passed to the method is leap year
    */
    public static boolean isLeapYear(int year) {
        return (new GregorianCalendar()).isLeapYear(year);
    }

    
    /*
    * Checks if a particular year is leap year by taking in a date
    * @param date
    * @return true if year in the date passed to the method is leap year
    */
    public static <D extends java.util.Date> boolean isLeapYear(D date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        return c.isLeapYear(c.get(YEAR));
    }

    /*
    * Get difference in days between two dates
    *
    * @param one Date that has to be subtracted from the greater date
    * @param two The greater date from which the other date is subtracted
    * @return The difference between date two and date one in days
    */
    public static <D extends java.util.Date> int getPeriodInDays(D one, D two) {
        return getPeriodInDays(one, two, false);
    }

    /*
    * Get difference in days between two dates by considering the time as well
    *
    * @param one Date that has to be subtracted from the greater date
    * @param two The greater date from which the other date is subtracted
    * @return The difference between date two and date one in days
    */
    public static <D extends java.util.Date> int getPeriodInDays(D one, D two, boolean considerTime) {
        if(one.after(two)) {
            return -getPeriodInDays(two, one, considerTime);
        }
        return (int)((two.getTime() - one.getTime() + (considerTime ? 0 : HALF_DAY_IN_MILLIS)) / DAY_IN_MILLIS);
    }
    
    /*
    * Get difference in months between two dates
    *
    * @param one Date that has to be subtracted from the greater date
    * @param two The greater date from which the other date is subtracted
    * @return The difference between date two and date one in months
    */
    public static <D extends java.util.Date> int getPeriodInMonths(D one, D two) {
        GregorianCalendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(one);
        c2.setTime(two);
        int i = c1.after(c2) ? -12 : 12;
        int v = 0;
        while(c1.get(YEAR) != c2.get(YEAR)) {
            c1.add(MONTH, i);
            v += i;
        }
        c1.add(MONTH, -i);
        v -= i;
        i = i == 12 ? 1 : -1;
        while(!(c1.get(YEAR) == c2.get(YEAR) && c1.get(MONTH) == c2.get(MONTH))) {
            c1.add(MONTH, i);
            v += i;
        }
        return v;
    }

    public static <D extends java.util.Date> int get(D date, int field) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        return c.get(field);
    }

    @SuppressWarnings("unchecked")
    private static <D extends java.util.Date> D cook(D date) {
        if(date instanceof java.sql.Date) {
            return (D) new java.sql.Date(0L);
        }
        if(date instanceof java.sql.Time) {
            return (D) new java.sql.Time(0L);
        }
        if(date instanceof java.sql.Timestamp) {
            return (D) new java.sql.Timestamp(0L);
        }
        try {
            return (D)date.getClass().getConstructor().newInstance();
        } catch (Throwable e) {
            return (D) new java.util.Date();
        }
    }

    private static <D extends java.util.Date> D add(D date, int field, int value) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(field, value);
        D d = cook(date);
        d.setTime(c.getTimeInMillis());
        return d;
    }

    private static <D extends java.util.Date> D set(D date, int field, int value) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.set(field, value);
        D d = cook(date);
        d.setTime(c.getTimeInMillis());
        return d;
    }

    /*
    * Validates and returns true if two dates passed to the method are same
    * @param one
    * @param two
    * @return True if two dates passed to the method are same
    */
    public static <D extends java.util.Date> boolean isSameDate(D one, D two) {
        if(one == null && two == null) {
            return true;
        }
        if(one == null || two == null) {
            return false;
        }   
        return format(one).equals(format(two));
    }

    /*
    * Get the difference between two dates in-terms of year, month and date
    *
    * @param one
    * @param two
    * @return The difference between the two specified dates in month date and year format
    */
    public static <D extends java.util.Date> String difference(D one, D two) {
        if(two.before(one)) {
            return "-" + difference(two, one);
        }
        int y = getYear(two) - getYear(one);
        one = addYear(one, y);
        if(one.after(two)) {
            --y;
            one = addYear(one, -1);
        }
        int mm = 0;
        while(getYear(one) < getYear(two)) {
            ++mm;
            one = addMonth(one, 1);
        }
        int m = getMonth(two) - getMonth(one);
        one = addMonth(one, m);
        m += mm;
        if(one.after(two)) {
            --m;
            one = addMonth(one, -1);
        }
        int d = getPeriodInDays(one, two);
        if(y == 0 && m == 0 && d == 0) {
            return "0D";
        }
        StringBuilder s = new StringBuilder();
        if(y > 0) {
            s.append(y).append("Y");
        }
        if(m > 0) {
            if(s.length() > 0) {
                s.append(" ");
            }
            s.append(m).append("M");
        }
        if(d > 0) {
            if(s.length() > 0) {
                s.append(" ");
            }
            s.append(d).append("D");
        }
        return s.toString();
    }

    /*
    * Get the difference between two dates in-terms of year, month and date by passing a date and an offset 
    *
    * @param from From date for finding the difference 
    * @param days offset added to from date to get to date
    * @return Differences between the two dates
    */
    public static <D extends java.util.Date> String difference(D from, int days) {
        return difference(from, addDay(from, days));
    }

    public static Date maximumDate() {
        return MAX_VALUE;
    }

    public static String[] getMonthNames() {
        return monthNames;
    }

    public static String[] getWeekNames() {
        return weekNames;
    }

    public static DateFormat dateFormat() {
        return (DateFormat)dateFormat.clone();
    }

    public static DateFormat dateTimeFormat() {
        return (DateFormat)dateTimeFormat.clone();
    }

    public static DateFormat timeFormat() {
        return (DateFormat)timeFormat.clone();
    }
}

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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.Calendar.*;

/**
 * Date utility functions.
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
    private final static DateFormat monthFormat = new SimpleDateFormat("MMM yy", Locale.getDefault());
    private final static DateFormat tinyFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
    private final static DateFormat shortFormat = new SimpleDateFormat("MMM dd, yy", Locale.getDefault());
    private final static DateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final static DateFormat longFormat = new SimpleDateFormat("MMM dd yyyy hh:mm:ss a", Locale.getDefault());
    private final static DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
    private final static DateFormat hhmmFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final static DateFormat hhmmDateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    private final static DateFormatSymbols formatSymbols = new DateFormatSymbols();
    private final static String[] monthNames = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    public static Date today() {
        GregorianCalendar c = new GregorianCalendar();
        return create(c);
    }

    public static Date tomorrow() {
        return addDay(today(), 1);
    }

    public static Date yesterday() {
        return addDay(today(), -1);
    }

    public static Date startOfYear() {
        return startOfYear(today());
    }

    public static <D extends java.util.Date> D startOfYear(D date) {
        D d = setDay(date, 1);
        return setMonth(d, 1);
    }

    public static Date endOfYear() {
        return endOfYear(today());
    }

    public static <D extends java.util.Date> D endOfYear(D date) {
        D d = setDay(date, 31);
        return setMonth(d, 12);
    }

    public static Date startOfMonth() {
        return setDay(today(), 1);
    }

    public static <D extends java.util.Date> D startOfMonth(D date) {
        return setDay(date, 1);
    }

    public static Date startOfMonth(int monthOffset) {
        Date d = addMonth(today(), monthOffset);
        return setDay(d, 1);
    }

    public static <D extends java.util.Date> D startOfMonth(int monthOffset, D date) {
        D d = addMonth(date, monthOffset);
        return setDay(d, 1);
    }

    public static Date endOfMonth() {
        return endOfMonth(0);
    }

    public static <D extends java.util.Date> D endOfMonth(D date) {
        return endOfMonth(0, date);
    }

    public static Date endOfMonth(int monthOffset) {
        return endOfMonth(monthOffset, today());
    }

    public static <D extends java.util.Date> D endOfMonth(int monthOffset, D date) {
        D d = addMonth(date, monthOffset + 1);
        d = setDay(d, 1);
        return addDay(d, -1);
    }

    public static Date create(Calendar c) {
        c = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        return new Date(c.getTimeInMillis());
    }

    public static Date create() {
        return today();
    }

    public static Date create(int year, int month, int day) {
        //noinspection MagicConstant
        GregorianCalendar c = new GregorianCalendar(year, month - 1, day);
        return new Date(c.getTimeInMillis());
    }

    public static <D extends java.util.Date> Date create(D date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTimeInMillis(date.getTime());
        return create(c);
    }

    public static Date create(int dateValue) {
        int d = dateValue % 100;
        dateValue /= 100;
        int m = dateValue % 100;
        dateValue /= 100;
        return create(dateValue, m, d);
    }

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
        day = value(s[1]);
        if(day == 0) {
            month = getMonth(s[1]);
            day = value(s[0]);
        } else {
            month = getMonth(s[0]);
        }
        year = value(s[2]);
        if(month == 0) {
            return null;
        }
        return create(year, month, day);
    }

    public static Date create(LocalDate date) {
        return create(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public static <D extends java.util.Date> LocalDate local(D date) {
        return LocalDate.of(getYear(date), getMonth(date), getDay(date));
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

    public static Time createTime(Calendar calendar) {
        return time(calendar.getTimeInMillis());
    }

    public static Time createTime(java.util.Date date) {
        return time(date.getTime());
    }

    public static Timestamp createTimestamp(long time) {
        return timestamp(time);
    }

    public static Timestamp createTimestamp(java.util.Date date) {
        return timestamp(date.getTime());
    }

    public static Time time() {
        return time(System.currentTimeMillis());
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

    public static Timestamp now() {
        return timestamp(System.currentTimeMillis());
    }

    public static Timestamp startTime(java.util.Date date) {
        return timestamp(trim(date).getTime());
    }

    private static java.sql.Date trim(java.util.Date date) {
        return create(getYear(date), getMonth(date), getDay(date));
    }

    public static Timestamp endTime(java.util.Date date) {
        Date d = addDay(trim(date), 1);
        return timestamp(d.getTime() - 1000);
    }

    public static Timestamp endOfToday() {
        Date d = tomorrow();
        return timestamp(d.getTime() - 1000);
    }

    public static Timestamp endOfYesterday() {
        Date d = today();
        return timestamp(d.getTime() - 1000);
    }

    public static Timestamp startOfToday() {
        Date d = today();
        return timestamp(d.getTime());
    }

    public static Timestamp startOfTomorrow() {
        Date d = tomorrow();
        return timestamp(d.getTime());
    }

    public static Timestamp trimSeconds(Timestamp time) {
        return trim(time, 60000L);
    }

    public static Timestamp trimMinutes(Timestamp time) {
        return trim(time, 3600000L);
    }

    private static Timestamp trim(Timestamp time, long tail) {
        long t = time.getTime() / tail;
        return timestamp(t * tail);
    }

    public static <D extends java.util.Date> String formatMonth(D date) {
        return monthFormat.format(date);
    }

    public static <D extends java.util.Date> String formatDate(D date) {
        return format.format(date);
    }

    public static <D extends java.util.Date> String formatLongDate(D date) {
        return longFormat.format(date);
    }

    public static <D extends java.util.Date> String formatShortDate(D date) {
        return shortFormat.format(date);
    }

    public static <D extends java.util.Date> String formatTinyDate(D date) {
        return tinyFormat.format(date);
    }

    public static <D extends java.util.Date> String format(D date) {
        if(date == null) {
            return null;
        }
        String s = formatWithTime(date);
        return s.contains("12:00:00 AM") ? format.format(date) : s;
    }

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

    public static <D extends java.util.Date> boolean equals(D one, D two) {
        return compareTo(one, two) == 0;
    }

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

    public static <D extends java.util.Date> D addYear(D date, int year) {
        return add(date, YEAR, year);
    }

    public static <D extends java.util.Date> D addMonth(D date, int month) {
        return add(date, MONTH, month);
    }

    public static <D extends java.util.Date> D addDay(D date, int day) {
        return add(date, DATE, day);
    }

    public static boolean isLeapYear(int year) {
        return (new GregorianCalendar()).isLeapYear(year);
    }

    public static <D extends java.util.Date> boolean isLeapYear(D date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        return c.isLeapYear(c.get(YEAR));
    }

    public static <D extends java.util.Date> int getPeriodInDays(D one, D two) {
        return getPeriodInDays(one, two, false);
    }

    public static <D extends java.util.Date> int getPeriodInDays(D one, D two, boolean considerTime) {
        if(one.after(two)) {
            return -getPeriodInDays(two, one, considerTime);
        }
        return (int)((two.getTime() - one.getTime() + (considerTime ? 0 : HALF_DAY_IN_MILLIS)) / DAY_IN_MILLIS);
    }

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

    public static <D extends java.util.Date> boolean isSameDate(D one, D two) {
        if(one == null && two == null) {
            return true;
        }
        if(one == null || two == null) {
            return false;
        }
        return format(one).equals(format(two));
    }

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

    public static <D extends java.util.Date> String difference(D from, int days) {
        return difference(from, addDay(from, days));
    }

    public static Date maximumDate() {
        return MAX_VALUE;
    }

    public static String[] getMonthNames() {
        return monthNames;
    }

    public static DateFormat dateFormat() {
        return (DateFormat)dateFormat.clone();
    }

    public static DateFormat dateTimeFormat() {
        return (DateFormat)format.clone();
    }
}
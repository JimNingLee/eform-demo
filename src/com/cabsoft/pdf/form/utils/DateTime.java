package com.cabsoft.pdf.form.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author Administrator
 */
@SuppressWarnings("javadoc")
public final class DateTime {

    /*******************************************************************************
     * Don't let anyone instantiate this class
     *******************************************************************************/
    private DateTime() {
    }

    /*******************************************************************************
     * check date string validation with the default format "yyyy-MM-dd".
     * @param s date string you want to check with default format "yyyy-MM-dd".
     * @throws Exception
     *******************************************************************************/
    public static void check(String s) throws Exception {
        DateTime.check(s, "yyyy-MM-dd");
    }

    /*******************************************************************************
     * check date string validation with an user defined format.
     * @param s date string you want to check.
     * @param format string representation of the date format. For example, "yyyy-MM-dd".
     * @throws java.text.ParseException
     *******************************************************************************/
    public static void check(String s, String format) throws java.text.ParseException {
        if (s == null) {
            throw new NullPointerException("date string to check is null");
        }
        if (format == null) {
            throw new NullPointerException("format string to check date is null");
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.KOREA);
        Date date = null;
        try {
            date = formatter.parse(s);
        } catch (java.text.ParseException e) {
            throw new java.text.ParseException(e.getMessage() + " with format \"" + format + "\"", e.getErrorOffset());
        }

        if (!formatter.format(date).equals(s)) {
            throw new java.text.ParseException("Out of bound date:\"" + s + "\" with format \"" + format + "\"", 0);
        }
    }

    /*******************************************************************************
     * @return formatted string representation of current day with  "yyyy-MM-dd".
     *******************************************************************************/
    public static String getDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        return formatter.format(new Date());
    }

    /**
     *
     * @param frm
     * @return
     */
	public static String getDateTime(String frm) {
        String ret = "";
        SimpleDateFormat formatter = new SimpleDateFormat(frm, Locale.ENGLISH);
        long currentTimeM = System.currentTimeMillis();
        ret = formatter.format(new Date(currentTimeM));
        return ret;
    }

    /*******************************************************************************
     *
     * For example, String time = DateTime.getFormatString("yyyy-MM-dd HH:mm:ss");
     *
     * @return formatted string representation of current day and time with  your pattern.
     *******************************************************************************/
    public static int getDay() {
        return getNumberByPattern("dd");
    }

    /*******************************************************************************
     *
     * For example, String time = DateTime.getFormatString("yyyy-MM-dd HH:mm:ss");
     *
     * @param pattern
     * @return formatted string representation of current day and time with  your pattern.
     *******************************************************************************/
    public static String getFormatString(String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.KOREA);
        String dateString = formatter.format(new Date());
        return dateString;
    }

    /*******************************************************************************
     *
     * For example, String time = DateTime.getFormatString("yyyy-MM-dd HH:mm:ss");
     *
     * @return formatted string representation of current day and time with  your pattern.
     *******************************************************************************/
    public static int getMonth() {
        return getNumberByPattern("MM");
    }

    /*******************************************************************************
     *
     * For example, String time = DateTime.getFormatString("yyyy-MM-dd HH:mm:ss");
     *
     * @param pattern
     * @return formatted string representation of current day and time with  your pattern.
     *******************************************************************************/
    public static int getNumberByPattern(String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.KOREA);
        String dateString = formatter.format(new Date());
        return Integer.parseInt(dateString);
    }

    /*******************************************************************************
     * @return formatted string representation of current day with  "yyyyMMdd".
     *******************************************************************************/
    public static String getShortDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        return formatter.format(new Date());
    }

    /*******************************************************************************
     * @return formatted string representation of current time with  "HHmmss".
     *******************************************************************************/
    public static String getShortTimeString() {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss", Locale.KOREA);
        return formatter.format(new Date());
    }

    /*******************************************************************************
     * @return formatted string representation of current time with  "yyyy-MM-dd-HH:mm:ss".
     *******************************************************************************/
    public static String getTimeStampString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.KOREA);
        return formatter.format(new Date());
    }

    /*******************************************************************************
     * @return formatted string representation of current time with  "HH:mm:ss".
     *******************************************************************************/
    public static String getTimeString() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        return formatter.format(new Date());
    }

    /*******************************************************************************
     *
     * For example, String time = DateTime.getFormatString("yyyy-MM-dd HH:mm:ss");
     *
     * @return formatted string representation of current day and time with  your pattern.
     *******************************************************************************/
    public static int getYear() {
        return getNumberByPattern("yyyy");
    }

    /**
     *
     * @param d
     * @return
     */
    public static String getDate(Date d) {
        return getDate(d, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     *
     * @param d
     * @param format
     * @return
     */
    public static String getDate(Date d, String format) {
        TimeZone tz = TimeZone.getTimeZone("GMT+9");
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.KOREA);
        sdf.setTimeZone(tz);
        return sdf.format(d);
    }

    /**
     *
     * @param d
     * @param format
     * @param locale
     * @return
     */
    public static String getDate(Date d, String format, Locale locale) {
        TimeZone tz = TimeZone.getTimeZone("GMT+9");
        SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
        sdf.setTimeZone(tz);
        return sdf.format(d);
    }

    /**
     *
     * @param args
     */
//    public static void main(String[] args){
//        String cdate = "2007/03/21";
//
//        System.out.println(getDate(new Date(cdate), "MMM dd yyy", Locale.US));
//
//        System.out.println(new SimpleDateFormat("MMM dd yyyy", Locale.US).format(new Date(cdate)));
//    }
}

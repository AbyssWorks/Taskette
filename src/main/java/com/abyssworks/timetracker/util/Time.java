package com.abyssworks.timetracker.util;

import com.abyssworks.timetracker.TimeTracker;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The following class defines helper methods for dealing with
 * time.
 *
 * @author Dysterio
 */
public class Time {
    public static final String TIME_PATTERN = "HH:mm";
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat(Time.TIME_PATTERN);

    /**
     * Converts the time in minutes to hh:mm format.
     *
     * @param timeInMinutes The time in minutes to parse.
     * @return The time as a string.
     */
    public static String formatTimeInMinutes(int timeInMinutes) {
        String hour = String.valueOf(timeInMinutes / 60);
        String minutes = String.valueOf(timeInMinutes % 60);
        if (minutes.length() == 1) minutes = "0" + minutes;

        return hour + ":" + minutes;
    }

    /**
     * Compares two dates.
     *
     * @param c1 The first date.
     * @param c2 The second date.
     * @return -1, 0, or 1 depending on the relationship between the dates.
     */
    public static int compareDates(Calendar c1, Calendar c2) {
        try {
            Date d1 = TimeTracker.DATE_FORMAT.parse(TimeTracker.DATE_FORMAT.format(c1.getTime()));
            Date d2 = TimeTracker.DATE_FORMAT.parse(TimeTracker.DATE_FORMAT.format(c2.getTime()));
            return d1.compareTo(d2);
        } catch (ParseException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Creates a calendar object from a date represented as a string.
     *
     * @param date The date as a string in the following format: DD/MM/YYY
     * @return The calendar object representative of the date.
     */
    public static Calendar getCalendarFromDateString(String date) {
        String[] dateComponents = date.split("/");
        int day = Integer.parseInt(dateComponents[0]);
        int month = Integer.parseInt(dateComponents[1]) - 1;
        int year = Integer.parseInt(dateComponents[2]);
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal;
    }

    /** Returns the current time of day in minutes. */
    public static int getTimeOfDayInMinutes() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return hour * 60 + minute;
    }

    /**
     * Calculates the minutes based on the time in a
     * Date object.
     *
     * @param date The date to get the time from.
     * @return The time in minutes.
     */
    public static int getTimeInMinutesFromDate(Date date) {
        return Time.getTimeInMinutesFromString(Time.TIME_FORMAT.format(date));
    }

    /**
     * Calculates the minutes based on the time in a
     * String format
     *
     * @param time Time in format HH:mm.
     * @return The time as minutes.
     */
    public static int getTimeInMinutesFromString(String time) {
        String[] timeComponents = time.split(":");
        int hour = Integer.parseInt(timeComponents[0]);
        int min = Integer.parseInt(timeComponents[1]);
        return (hour * 60) + min;
    }

    /**
     * Converts time in minutes into a string representation of the
     * duration it represents in the following format: HhMm
     *
     * @param timeInMinutes The time in minutes.
     * @return The formatted duration as a string.
     */
    public static String formatDuration(int timeInMinutes) {
        String[] time = Time.formatTimeInMinutes(timeInMinutes).split(":");
        String hour = (Integer.parseInt(time[0]) == 0) ? "" : (time[0] + "h");
        String minute = (Integer.parseInt(time[1]) == 0) ? "" : (time[1] + "m");
        return hour + minute;
    }

    /**
     * Converts a Calendar object into its String representative,
     * expressing the date in the following format: DD/MM/YYYY
     *
     * @param date The date to get as a string.
     * @return The string version of the date passed.
     */
    public static String getDateAsJSONString(Calendar date) {
        return TimeTracker.JSON_DATE_FORMAT.format(date.getTime());
    }

    /**
     * Returns the path to a file in the resources' folder.
     *
     * @param resourceName The name of the file wanted.
     * @return The URL to the file.
     */
    public static URL getResourcePath(String resourceName) {
        return Time.class.getClassLoader().getResource(resourceName);
    }
}

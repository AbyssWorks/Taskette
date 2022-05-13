package com.abyssworks.timetracker;

import com.abyssworks.timetracker.model.data.DataManager;
import com.abyssworks.timetracker.model.data.Ticket;
import com.abyssworks.timetracker.model.week.Card;
import com.abyssworks.timetracker.model.week.WeekManager;
import com.abyssworks.timetracker.util.Time;
import com.abyssworks.timetracker.view.ColourTheme;
import com.abyssworks.timetracker.view.GUI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;

/**
 * The following class manages the program by linking the
 * front and back end.
 *
 * @author Dysterio
 */
public class TimeTracker {
    public static final String VERSION = "v0.2.0";
    public static final int MINIMUM_MINUTE_GAP = 10;
    public static final int MAXIMUM_MINUTE_GAP = 60;
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM/yyyy");
    public static final DateFormat JSON_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private static GUI gui;
    private static String userName;
    private static int minuteGap;
    private static boolean startScrollAtCurrentTime;
    private static int startScrollAtSetTime;
    private static boolean autoSave;
    private static DataManager dataManager;
    private static ColourTheme colourTheme;

    /** Executes the program. */
    public TimeTracker() {
        TimeTracker.dataManager = new DataManager(System.getProperty("user.dir") + "/data");
        TimeTracker.loadData();
        TimeTracker.gui = new GUI();
        TimeTracker.updatePixPerMin();
    }

    /**
     * Loads data in from the data files.
     */
    public static void loadData() {
        TimeTracker.loadSettings(false);
        TimeTracker.loadWorklogs();
    }

    /**
     * Loads the user's settings into memory.
     */
    private static void loadSettings(boolean guiInitialized) {
        TimeTracker.userName = TimeTracker.dataManager.getUserName();
        TimeTracker.minuteGap = TimeTracker.dataManager.getMinuteGap();
        TimeTracker.startScrollAtCurrentTime = TimeTracker.dataManager.getStartScrollAtCurrentTime();
        TimeTracker.startScrollAtSetTime = TimeTracker.dataManager.getStartScrollAtSetTime();
        TimeTracker.autoSave = TimeTracker.dataManager.getAutoSave();
        TimeTracker.colourTheme = new ColourTheme(TimeTracker.dataManager.getColourTheme());
        if (guiInitialized) TimeTracker.updatePixPerMin();
    }

    /**
     * Loads the user's worklogs into memory.
     */
    private static void loadWorklogs() {
        for (Card card : TimeTracker.dataManager.getCards()) {
            WeekManager.addCard(card);
        }
    }

    /**
     * Loads the epics and tickets data.
     */
    public static void loadEpicsData() {
        TimeTracker.dataManager.loadEpicsData();
    }

    /**
     * Gets all the tickets loaded in as an array.
     *
     * @return Array of tickets.
     */
    public static Ticket[] getAllTickets() {
        return TimeTracker.dataManager.getAllTickets();
    }

    /**
     * Saves the data from the current session.
     *
     * @param cards The cards stored in the current session.
     */
    public static void saveWorklogs(Set<Card> cards) {
        TimeTracker.dataManager.saveWorklogsToFile(cards);
    }

    /**
     * Check if any changes have been made to the work logs
     *
     * @param cards The current worklogs
     * @return True if changes have been made
     */
    public static boolean changesMadeToWorklog(Set<Card> cards) {
        return TimeTracker.dataManager.changesMadeToWorklog(cards);
    }

    /** Updates the username. */
    public static void setUserName(String name) {
        TimeTracker.userName = name;
    }
    /** Returns the username set. */
    public static String getUserName() {
        return TimeTracker.userName;
    }

    /** Updates the minute gap set. */
    public static void setMinuteGap(int minuteGap) {
        TimeTracker.minuteGap = minuteGap;
        TimeTracker.updatePixPerMin();
        TimeTracker.gui.repaint();
    }
    /** Returns the minute gap set. */
    public static int getMinuteGap() {
        return TimeTracker.minuteGap;
    }

    /** Returns whether the scroll should start at the current time. */
    public static boolean getStartScrollAtCurrentTime() {
        return TimeTracker.startScrollAtCurrentTime;
    }

    /** Updates the time at which the program should start. */
    public static void setScrollStart(boolean currTime, int setTime) {
        TimeTracker.startScrollAtCurrentTime = currTime;
        TimeTracker.startScrollAtSetTime = setTime;
    }

    /** Returns the hour the scroll should start at. */
    public static int getStartScrollAtSetTime() {
        return TimeTracker.startScrollAtSetTime;
    }

    /** Updates the auto-save property set. */
    public static void setAutoSave(boolean autoSave) {
        TimeTracker.autoSave = autoSave;
    }
    /** Returns the auto-save property set. */
    public static boolean getAutoSave() {
        return TimeTracker.autoSave;
    }

    /** Updates the color theme used. */
    public static void setColourTheme(String themeName) {
        TimeTracker.colourTheme = new ColourTheme(themeName);
        TimeTracker.gui.repaint();
    }
    /** Returns the colorTheme. */
    public static ColourTheme getColorTheme(){
        return TimeTracker.colourTheme;
    }

    /**
     * Resets user settings to what was last saved.
     */
    public static void resetSettings() {
        TimeTracker.loadSettings(true);
    }

    /**
     * Saves the user's settings.
     */
    public static void saveSettings() {
        TimeTracker.dataManager.saveUserSettingsToFile(
                TimeTracker.userName,
                TimeTracker.minuteGap,
                TimeTracker.startScrollAtCurrentTime,
                TimeTracker.startScrollAtSetTime,
                TimeTracker.autoSave,
                TimeTracker.colourTheme);
    }

    /**
     * Updates the pixels per minute property based on the minute gap.
     */
    private static void updatePixPerMin() {
        TimeTracker.gui.setPixPerMin(TimeTracker.minuteGap >= 20 ? 1 : 2);
    }

    /** Boots up the program. */
    public static void main(String[] args) {
        new TimeTracker();
    }
}

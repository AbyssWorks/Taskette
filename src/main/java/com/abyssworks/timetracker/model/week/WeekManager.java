package com.abyssworks.timetracker.model.week;

import com.abyssworks.timetracker.TimeTracker;
import com.abyssworks.timetracker.util.Time;
import com.abyssworks.timetracker.view.popup.CardPopup;
import com.abyssworks.timetracker.view.week.WeekHeader;

import java.util.*;

/**
 * This class handles the back-end associated with changing
 * and handling the weeks via the week header.
 *
 * @author Dysterio
 */
public class WeekManager {
    public static final int NEXT = 1;
    public static final int PREV = -1;

    private static final Map<String, Week> weeksData = new HashMap<>();
    private static final Calendar displayWeekStartDate = Calendar.getInstance();
    private static Week currentWeek;

    static {
        WeekManager.displayWeekStartDate.setFirstDayOfWeek(Calendar.SUNDAY);
        WeekManager.displayWeekStartDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        WeekManager.getDisplayWeek();
    }

    /** Returns the currently displayed week's start and end date. */
    public static String getDisplayWeekStartAndEndDate() {
        Calendar calendar = (Calendar) WeekManager.displayWeekStartDate.clone();
        String startDate = TimeTracker.DATE_FORMAT.format(calendar.getTime());
        calendar.add(Calendar.DATE, 6);
        String endDate = TimeTracker.DATE_FORMAT.format(calendar.getTime());
        return startDate + " - " + endDate;
    }

    /**
     * Returns the week that should be currently displayed.
     *
     * @return The week to be displayed.
     */
    public static Week getDisplayWeek() {
        return WeekManager.currentWeek = WeekManager.weeksData.getOrDefault(
                TimeTracker.DATE_FORMAT.format(WeekManager.displayWeekStartDate.getTime()),
                new Week(WeekManager.displayWeekStartDate));
    }

    /** Returns the present day's index. */
    public static int getCurrDayIndex() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * Changes the display week to the next or previous week.
     *
     * @param direction The direction to change the week in.
     */
    public static void changeWeek(int direction) {
        WeekManager.displayWeekStartDate.add(Calendar.DATE, 7 * direction);
    }

    /**
     * Returns the week object associated with the date
     * passed.
     *
     * @param date The date to check for.
     * @return The week which contains the date.
     */
    private static Week getWeekFromDate(Calendar date) {
        if (date == null) throw new IllegalArgumentException("Date can not be null.");

        Calendar weekStartDate = (Calendar) date.clone();
        weekStartDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        String startDate = TimeTracker.DATE_FORMAT.format(weekStartDate.getTime());
        if (WeekManager.weeksData.containsKey(startDate)) {
            return WeekManager.weeksData.get(startDate);
        } else {
            if (Time.compareDates(WeekManager.currentWeek.getStartDate(), weekStartDate) == 0)
                return WeekManager.currentWeek;
            else
                return new Week(weekStartDate);
        }
    }

    /**
     * Checks if there is space for a card to be added at
     * the date provided.
     *
     * @param date The date to check.
     * @param startTimeInMinutes The card's start time.
     * @param endTimeInMinutes The card's end time.
     * @return True if the card can be added to the date.
     */
    public static boolean checkIfCardCanBeAdded(Calendar date, int startTimeInMinutes, int endTimeInMinutes, Card exception) {
        if (date == null) throw new IllegalArgumentException("Date can not be null.");
        if (startTimeInMinutes < 0) throw new IllegalArgumentException("Start time passed to card can not be negative.");
        if (endTimeInMinutes < 0) throw new IllegalArgumentException("End time passed to card can not be negative.");
        if (startTimeInMinutes > 60 * 24) throw new IllegalArgumentException("Start time can not be greater than the number of minutes in the day.");
        if (endTimeInMinutes > 60 * 24) throw new IllegalArgumentException("End time can not be greater than the number of minutes in the day.");
        if (startTimeInMinutes >= endTimeInMinutes) throw new IllegalStateException("Start time can not be after end time.");
        if (startTimeInMinutes + Card.MINIMUM_CARD_DURATION > endTimeInMinutes) throw new IllegalStateException("Duration can not be less than " + Card.MINIMUM_CARD_DURATION + " minutes.");

        Week week = WeekManager.getWeekFromDate(date);
        if (week.isEmpty()) return true;
        int dayIndex = date.get(Calendar.DAY_OF_WEEK) - 1;
        return week.canAddCard(dayIndex, startTimeInMinutes, endTimeInMinutes, exception);
    }

    /**
     * Adds the card to the proper week.
     *
     * @param card The card to add.
     */
    public static void addCard(Card card) {
        if (card == null) throw new IllegalArgumentException("Card to be added can not be null.");

        Calendar weekStartDate = (Calendar) card.getDate().clone();
        TimeTracker.DATE_FORMAT.format(weekStartDate.getTime()); // Dummy line without which code breaks idk :'(
        weekStartDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        String startDate = TimeTracker.DATE_FORMAT.format(weekStartDate.getTime());
        Week week = WeekManager.getWeekFromDate(weekStartDate);
        if (!WeekManager.weeksData.containsKey(startDate)) WeekManager.weeksData.put(startDate, week);
        week.addCard(card);
    }

    /**
     * Updates a card using the information passed in the card
     * popup.
     *
     * @param card The card to update.
     * @param cardPopup The popup.
     */
    public static void updateCard(Card card, CardPopup cardPopup) {
        Week week = WeekManager.getWeekFromDate(card.getDate());
        week.removeCard(card);
        card.setDate(cardPopup.getDate());
        card.setTicket(cardPopup.getTicket());
        card.setDescription(cardPopup.getDescription());
        card.setStartTimeInMinutes(cardPopup.getTimeInMinutes(true));
        card.setEndTimeInMinutes(cardPopup.getTimeInMinutes(false));
        WeekManager.addCard(card);
    }

    /**
     * Returns all the cards currently stored in the program.
     *
     * @return A set of all the cards.
     */
    private static Set<Card> getAllCards() {
        Set<Card> cards = new HashSet<>();
        for (Week week : WeekManager.weeksData.values()) {
            cards.addAll(week.getAllCards());
        }
        return cards;
    }

    /**
     * Saves the data to the json files.
     */
    public static void saveWorklogs() {
        TimeTracker.saveWorklogs(WeekManager.getAllCards());
    }

    /**
     * Check if any changes have been made to the work logs
     *
     * @return True if changes have been made
     */
    public static boolean changesMadeToWorklogs() {
        return TimeTracker.changesMadeToWorklog(WeekManager.getAllCards());
    }
}

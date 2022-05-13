package com.abyssworks.timetracker.model.week;

import com.abyssworks.timetracker.util.Time;
import com.abyssworks.timetracker.view.GUI;

import java.util.*;

/**
 * The following class outlines a Week data type that is responsible
 * for handling the data associated with a particular week.
 *
 * @author Dysterio
 */
public class Week {
    private final Calendar startDate;
    private final Calendar endDate;
    private final List<Day> days = new ArrayList<>();

    /**
     * Creates a new Week data object.
     *
     * @param startDate The date of the first day of the week.
     */
    public Week(Calendar startDate) {
        if (startDate == null) throw new IllegalArgumentException("Week's start date can not be null.");
        if (startDate.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) throw new IllegalStateException("Week's start day must start on Sunday.");

        this.startDate = startDate;
        this.endDate = (Calendar) startDate.clone();
        this.instantiateDays();
    }

    /** Instantiates the Day objects for this week. */
    private void instantiateDays() {
        for (int i = 0; i < 7; i++) {
            this.days.add(new Day((Calendar) this.startDate.clone()));
            this.endDate.add(Calendar.DATE, 1);
        }
        this.endDate.add(Calendar.DATE, -1);
    }

    /**
     * Compares this week with the present week.
     *
     * @return -1, 0, or 1 if this week comes before, at the same time, or after the present week respectively.
     */
    public int weekRelationshipToPresentWeek() {
        Calendar calendar = Calendar.getInstance();
        if (Time.compareDates(calendar, this.startDate) < 0) return 1;
        else if (Time.compareDates(calendar, this.endDate) > 0) return -1;
        else return 0;
    }

    /**
     * Adds a card to the week.
     *
     * @param card The card to add.
     */
    public void addCard(Card card) {
        if (card == null) throw new IllegalArgumentException("Card to be added can not be null.");

        this.getDayAtIndex(card.getDayIndex()).addCard(card);
    }

    /**
     * Removes a card from this week.
     *
     * @param card The card to remove.
     */
    public void removeCard(Card card) {
        this.getDayAtIndex(card.getDayIndex()).removeCard(card);
    }

    /** Checks if a card can be added at the date and time passed. */
    public boolean canAddCard(int dayIndex, int startTimeInMinutes, int endTimeInMinutes, Card... exceptions) {
        return this.getDayAtIndex(dayIndex).canAddCard(exceptions, startTimeInMinutes, endTimeInMinutes);
    }

    /** Checks if a card can be added at the date and time passed. */
    public boolean canAddCard(int dayIndex, int startTimeInMinutes, int endTimeInMinutes) {
        return this.getDayAtIndex(dayIndex).canAddCard(new Card[]{}, startTimeInMinutes, endTimeInMinutes);
    }

    /**
     * Checks if a card is in this week.
     *
     * @param card The card to check for.
     * @return True if the card is in this week. Otherwise false.
     */
    public boolean checkForCard(Card card) {
        for (Day day : this.days) {
            if (day.getCards().contains(card))
                return true;
        }
        return false;
    }

    /**
     * Gets the day at a specific index.
     *
     * @param dayIndex The day's index.
     * @return The specified day.
     */
    public Day getDayAtIndex(int dayIndex) {
        if (dayIndex < 0) throw new IllegalArgumentException("Day index can not be less than 0.");
        if (dayIndex > 6) throw new IllegalArgumentException("Day index can not be greater than 6.");

        return this.days.get(dayIndex);
    }

    /**
     * Returns the card on the day and time provided.
     *
     * @param dayIndex The index of the day to check for the card.
     * @param timeInMinutes The time in minutes at which to check for a card.
     * @return The card object at that time and day.
     */
    public Card getCardAtDayAndTime(int dayIndex, int timeInMinutes) {
        return this.getDayAtIndex(dayIndex).getCardAtTime(timeInMinutes, timeInMinutes + GUI.getPixPerMin());
    }

    /**
     * Returns all the cards in this week.
     *
     * @return A set of all the cards.
     */
    public Set<Card> getAllCards() {
        Set<Card> cards = new HashSet<>();
        for (Day day : this.days) {
            cards.addAll(day.getCards());
        }
        return cards;
    }

    /**
     * Checks if this week has any cards assigned to it.
     *
     * @return True if this week has no cards assigned to it.
     */
    public boolean isEmpty() {
        for (Day d : this.days) {
            if (d.getCards().size() > 0) return false;
        }
        return true;
    }

    /** Returns this week's start date. */
    public Calendar getStartDate() {
        return (Calendar) this.startDate.clone();
    }
    /** Returns this week's end date. */
    public Calendar getEndDate() {
        return (Calendar) this.endDate.clone();
    }
}

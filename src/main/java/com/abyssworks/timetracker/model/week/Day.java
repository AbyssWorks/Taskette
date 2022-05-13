package com.abyssworks.timetracker.model.week;

import java.util.*;

/**
 * Represents a Day data type that stores the cards assigned to that day.
 *
 * @author Dysterio
 */
public class Day {
    private final Calendar startDate;
    private final Set<Card> cards = new HashSet<>();

    /**
     * Creates a Day data type.
     *
     * @param date The day's date.
     */
    public Day(Calendar date) {
        if (date == null) throw new IllegalArgumentException("Date passed to day can not be null.");

        this.startDate = date;
    }

    /**
     * Checks if the time slot passed intersects with an exiting card
     * in this day. If the time slot intersects with the exception
     * card, the function still returns false.
     *
     * @param exceptions The exception cards to not be considered.
     * @param startTimeInMinutes Timeslot start time.
     * @param endTimeInMinutes Timeslot end time.
     * @return True if a card with the passed timeslot can be added.
     */
    public boolean canAddCard(Card[] exceptions, int startTimeInMinutes, int endTimeInMinutes) {
        ArrayList<Card> exceptionsToIgnore = new ArrayList<>(Arrays.asList(exceptions));
        for (Card card : this.cards) {
            if (exceptionsToIgnore.contains(card)) continue;
            if (card.checkForCollision(startTimeInMinutes, endTimeInMinutes))
                return false;
        }
        if (startTimeInMinutes < 0) throw new IllegalArgumentException("Start time passed to card can not be negative.");
        if (endTimeInMinutes < 0) throw new IllegalArgumentException("End time passed to card can not be negative.");
        if (startTimeInMinutes > 60 * 24) throw new IllegalArgumentException("Start time can not be greater than the number of minutes in the day.");
        if (endTimeInMinutes > 60 * 24) throw new IllegalArgumentException("End time can not be greater than the number of minutes in the day,");
        return true;
    }

    /**
     * Gets the card at the time passed (if any). Otherwise, returns
     * null.
     *
     * @param startTimeInMinutes The start time in minutes.
     * @param endTimeInMinutes The end time in minutes.
     * @return The card at the provided time.
     */
    public Card getCardAtTime(int startTimeInMinutes, int endTimeInMinutes) {
        for (Card card : this.cards) {
            if (card.checkForCollision(startTimeInMinutes, endTimeInMinutes))
                return card;
        }
        return null;
    }

    /**
     * Adds the card provided to this day.
     *
     * @param card The card to add.
     */
    public void addCard(Card card) {
        if (card == null) throw new IllegalArgumentException("Card added can not be null.");

        this.cards.add(card);
    }

    /**
     * Removes the card provided from this day.
     *
     * @param card The card to remove.
     */
    public void removeCard(Card card) {
        if (card == null) throw new IllegalArgumentException("Card added can not be null.");
        if (!this.cards.contains(card)) throw new IllegalStateException("Non-existant card can not be removed.");

        this.cards.remove(card);
    }

    /** Returns an unmodifiable set of the cards. */
    public Set<Card> getCards() {
        return Collections.unmodifiableSet(this.cards);
    }
}

package com.abyssworks.timetracker.model.week;

import com.abyssworks.timetracker.model.data.Ticket;
import com.abyssworks.timetracker.util.Time;
import com.abyssworks.timetracker.view.GUI;

import java.util.*;

/**
 * The following class defines a Card data type which stores information
 * related to a planned task.
 *
 * @author Dysterio
 */
public class Card implements Cloneable{
    public static final int MINIMUM_CARD_DURATION = 1;
    public static int DRAGGABLE_CARD_MIN_DURATION = 5;

    public enum FontSize {Big, Small}
    public enum Components {Title, Description, TicketAndDuration}

    private Calendar date;
    private Ticket ticket;
    private String description;
    private int startTimeInMinutes;
    private int endTimeInMinutes;

    /**
     * Creates an instance of a Card data type.
     *
     * @param date The day that this card is assigned to.
     * @param ticket The ticket associated with this task.
     * @param description The task's description.
     * @param startTimeInMinutes The task's start time in minutes.
     * @param endTimeInMinutes The task's end time in minutes.
     */
    public Card(Calendar date, Ticket ticket, String description, int startTimeInMinutes, int endTimeInMinutes) {
        if (date == null) throw new IllegalArgumentException("Date passed to card can not be null.");
        if (ticket == null) throw new IllegalArgumentException("Ticket passed to card can not be null.");
        if (startTimeInMinutes < 0) throw new IllegalArgumentException("Start time passed to card can not be negative.");
        if (endTimeInMinutes < 0) throw new IllegalArgumentException("End time passed to card can not be negative.");
        if (startTimeInMinutes > 60 * 24) throw new IllegalArgumentException("Start time can not be greater than the number of minutes in the day.");
        if (endTimeInMinutes > 60 * 24) throw new IllegalArgumentException("End time can not be greater than the number of minutes in the day.");
        if (startTimeInMinutes >= endTimeInMinutes) throw new IllegalStateException("Start time can not be after end time.");
        if (startTimeInMinutes + Card.MINIMUM_CARD_DURATION > endTimeInMinutes) throw new IllegalStateException("Duration can not be less than " + Card.MINIMUM_CARD_DURATION + " minutes.");

        this.date = date;
        this.ticket = ticket;
        this.description = description;
        this.startTimeInMinutes = startTimeInMinutes;
        this.endTimeInMinutes = endTimeInMinutes;
    }

    /**
     * Checks if the timeslot provided collides with an
     * existing card.
     *
     * @param startTimeInMinutes The timeslot's start time in minutes.
     * @param endTimeInMinutes The timeslot's end time in minutes.
     * @return True if the timeslot overlaps with an existing card.
     */
    public boolean checkForCollision(int startTimeInMinutes, int endTimeInMinutes) {
        if (startTimeInMinutes < 0) throw new IllegalArgumentException("Start time passed to card can not be negative.");
        if (endTimeInMinutes < 0) throw new IllegalArgumentException("End time passed to card can not be negative.");
        if (startTimeInMinutes > 60 * 24) throw new IllegalArgumentException("Start time can not be greater than the number of minutes in the day.");
        if (endTimeInMinutes > 60 * 24) throw new IllegalArgumentException("End time can not be greater than the number of minutes in the day,");

        return (startTimeInMinutes < this.endTimeInMinutes) && (this.startTimeInMinutes < endTimeInMinutes);
    }

    /**
     * Checks if this card collides with the card passed.
     *
     * @param c The card to check against.
     * @return True if this card intersects with the card passed.
     */
    public boolean checkForCollision(Card c) {
        return (this.getDate().get(Calendar.DAY_OF_WEEK) == c.getDate().get(Calendar.DAY_OF_WEEK) &&
                this.checkForCollision(c.getStartTimeInMinutes(), c.getEndTimeInMinutes()));
    }

    /** Returns whether the card is draggable */
    public boolean isDraggable() {
        return this.getDurationInMinutes() >= Card.DRAGGABLE_CARD_MIN_DURATION/GUI.getPixPerMin();
    }

    /**
     * Returns the components to display on the card along with
     * their font size.
     *
     * @return a hashmap with the name of the component and text size
     * as key value pairs.
     */
    public Map<Card.Components, Card.FontSize> getComponentsToDisplay() {
        Map<Card.Components, Card.FontSize> componentsToDisplay = new HashMap<>();
        int duration = this.getDurationInMinutes();

        if (!this.isDraggable()) return componentsToDisplay;
        if (duration < 20/GUI.getPixPerMin()) {
            componentsToDisplay.put(Components.Title, FontSize.Small);
        } else if (duration < 30/GUI.getPixPerMin()) {
            componentsToDisplay.put(Components.Title, FontSize.Big);
        } else if (duration < 40/GUI.getPixPerMin()) {
            componentsToDisplay.put(Components.Title, FontSize.Big);
            componentsToDisplay.put(Components.TicketAndDuration, FontSize.Small);
        } else if (duration < 50/GUI.getPixPerMin()) {
            componentsToDisplay.put(Components.Title, FontSize.Big);
            componentsToDisplay.put(Components.TicketAndDuration, FontSize.Big);
        } else if (duration < 60/GUI.getPixPerMin()) {
            componentsToDisplay.put(Components.Title, FontSize.Big);
            componentsToDisplay.put(Components.Description, FontSize.Small);
            componentsToDisplay.put(Components.TicketAndDuration, FontSize.Big);
        } else {
            componentsToDisplay.put(Components.Title, FontSize.Big);
            componentsToDisplay.put(Components.Description, FontSize.Big);
            componentsToDisplay.put(Components.TicketAndDuration, FontSize.Big);
        }
        return componentsToDisplay;
    }

    /** Returns the card's duration. */
    public int getDurationInMinutes() {
        return this.endTimeInMinutes - this.startTimeInMinutes;
    }

    /** Returns the index of the day the card is on. */
    public int getDayIndex() {
        return this.date.get(Calendar.DAY_OF_WEEK) - 1;
    }
    /** Returns the date associated with this card. */
    public Calendar getDate() {
        return this.date;
    }
    /** Updates the date associated with this card. */
    public void setDate(Calendar date) {
        this.date = date;
    }

    /** Returns the ticket associated with this card. */
    public Ticket getTicket() {
        return this.ticket;
    }
    /** Updates the ticket associated with this card. */
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    /** Returns this card's description. */
    public String getDescription() {
        return this.description;
    }
    /** Updates this card's description. */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Returns this card's start time. */
    public int getStartTimeInMinutes() {
        return this.startTimeInMinutes;
    }
    /** Updates this card's start time. */
    public void setStartTimeInMinutes(int startTimeInMinutes) {
        this.startTimeInMinutes = startTimeInMinutes;
    }

    /** Returns this card's end time. */
    public int getEndTimeInMinutes() {
        return this.endTimeInMinutes;
    }
    /** Updates this card's end time. */
    public void setEndTimeInMinutes(int endTimeInMinutes) {
        this.endTimeInMinutes = endTimeInMinutes;
    }

    /** Returns a string representation of this card. */
    @Override
    public String toString() {
        // TODO: Discuss possible implications of Locale.UK
        return "Date: " + Time.getDateAsJSONString(this.date) + "\n" +
                "Ticket: " + this.ticket + "\n" +
                "Description: " + this.description + "\n" +
                "Start Time: " + Time.formatTimeInMinutes(this.startTimeInMinutes) + "\n" +
                "End Time: " + Time.formatTimeInMinutes(this.endTimeInMinutes);
    }

    /** Converts the Card object into its JSON worklog representative. */
    public String toJSONString() {
        return "{\n" +
                "\"date\": \"" + Time.getDateAsJSONString(this.date) + "\",\n" +
                "\"ticket\": \"" + this.ticket + "\",\n" +
                "\"description\": \"" + this.description + "\",\n" +
                "\"startTime\": \"" + Time.formatTimeInMinutes(this.startTimeInMinutes) + "\",\n" +
                "\"endTime\": \"" + Time.formatTimeInMinutes(this.endTimeInMinutes) + "\"\n" +
                "}";
    }

    /**
     * Creates a deep clone of this card object.
     *
     * @return Deep clone of this card.
     */
    @Override
    public Card clone() {
        return new Card((Calendar) this.date.clone(), this.ticket, this.description, this.startTimeInMinutes, this.endTimeInMinutes);
    }
}

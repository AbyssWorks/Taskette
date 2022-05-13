package com.abyssworks.timetracker.model.data;

/**
 * This class represents a Ticket data type.
 *
 * @author Dysterio
 */
public class Ticket {
    private String tag;
    private String title;

    /**
     * Creates a ticket object from the tag and title passed.
     *
     * @param tag The ticket's tag.
     * @param title The ticket's title.
     */
    public Ticket(String tag, String title) {
        this.tag = tag;
        this.title = title;
    }

    /** Returns the ticket's title. */
    public String getTitle() {
        return this.title;
    }

    /** Converts the ticket object into a string. */
    @Override
    public String toString() {
        return this.tag;
    }
}

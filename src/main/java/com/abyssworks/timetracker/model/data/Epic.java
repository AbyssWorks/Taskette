package com.abyssworks.timetracker.model.data;

import java.util.HashSet;
import java.util.Set;

/**
 * The Epic class represents an Epic data type. It stores a list of
 * tickets that fall under it.
 *
 * @author Dysterio
 */
public class Epic {
    private String tag;
    private Set<Ticket> tickets;

    /**
     * Constructs an Epic data object using the tag passed.
     *
     * @param tag The epic's tag
     */
    public Epic(String tag) {
        this.tag = tag;
        this.tickets = new HashSet<>();
    }

    /**
     * Adds a ticket to the epic.
     *
     * @param ticket The ticket to add to the epic.
     */
    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
    }
}

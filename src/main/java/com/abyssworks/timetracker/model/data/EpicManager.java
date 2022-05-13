package com.abyssworks.timetracker.model.data;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.*;

/**
 * The following class reads the epic related data files and stores
 * the information in the proper format.
 *
 * @author Dysterio
 */
public class EpicManager {
    private final File epicsFolder;
    public Map<String, Epic> epics = new HashMap<>();
    public Map<String, Ticket> tickets = new HashMap<>();

    /**
     * Creates an instance of the EpicManager from the
     * path to the epics data folders.
     *
     * @param pathToEpicsFolder The path to the epic folders.
     */
    public EpicManager(String pathToEpicsFolder) {
        this.epicsFolder = new File(pathToEpicsFolder);
    }

    /**
     * Reads and stores the information in the epic data
     * folders.
     */
    public void readEpicData() {
        this.epics.clear();
        this.tickets.clear();
        for (final File epicFolder : Objects.requireNonNull(this.epicsFolder.listFiles())) {
            if (!epicFolder.isDirectory()) continue;
            Epic epic = this.parseEpicFolder(epicFolder);
            this.epics.put(epicFolder.getName(), epic);
        }
    }

    /**
     * Parses a single epic folder.
     *
     * @param epicFolder The epic folder to parse.
     * @return The Epic object from the epic folder parsed.
     */
    private Epic parseEpicFolder(final File epicFolder) {
        String epicTag = epicFolder.getName();

        Epic epic = new Epic(epicTag);
        this.parseTicketsInEpicFolder(epic, epicFolder);
        return epic;
    }

    /**
     * Parses the ticket data files in the epic folder passed.
     *
     * @param epic The Epic object
     * @param epicFolder The epic folder as a File object
     */
    private void parseTicketsInEpicFolder(Epic epic, final File epicFolder) {
        for (final File ticketFile : Objects.requireNonNull(epicFolder.listFiles())) {
            String ticketTag = FilenameUtils.removeExtension(ticketFile.getName());
            JSONObject ticketData = DataManager.getJSONReader(ticketFile.getPath());
            Ticket ticket = new Ticket(ticketTag, ticketData.getString("ticketTitle"));
            epic.addTicket(ticket);
            this.tickets.put(ticketTag, ticket);
        }
    }

    /**
     * Returns all the tickets loaded as an array.
     *
     * @return Array of tickets loaded.
     */
    public Ticket[] getAllTickets() {
        Collection<Ticket> allTickets = this.tickets.values();
        return allTickets.toArray(new Ticket[allTickets.size()]);
    }

    /**
     * Returns the Ticket object that belongs to the tag.
     *
     * @param ticketTag The tag to check for.
     * @return The ticket object.
     */
    public Ticket getTicketFromTag(String ticketTag) {
        return this.tickets.get(ticketTag);
    }
}

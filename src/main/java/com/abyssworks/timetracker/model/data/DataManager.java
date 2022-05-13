package com.abyssworks.timetracker.model.data;

import com.abyssworks.timetracker.model.week.Card;
import com.abyssworks.timetracker.util.Time;
import com.abyssworks.timetracker.view.ColourTheme;
import com.abyssworks.timetracker.view.popup.Popup;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * The following class handles reading the data files and storing
 * information pertaining to the program and its settings.
 */
public class DataManager {
    private final String pathToDataFile;
    private JSONObject dataFile;
    private final EpicManager epicManager;
    private String userName;
    private int minuteGap;
    private boolean startScrollAtCurrentTime;
    private int startScrollAtSetTime;
    private String colourTheme;
    private String language;
    private String timeZone;
    private boolean autoSave;
    private final Set<Card> cards = new HashSet<>();
    private final Set<Integer> invalidWorklogsIndex = new HashSet<>();

    /**
     * Creates an instance of the DataManager class using the path
     * to the main data file as the input.
     *
     * @param pathToDataFile The path to the data files.
     */
    public DataManager(String pathToDataFile) {
        this.pathToDataFile = pathToDataFile;
        this.epicManager = new EpicManager(this.pathToDataFile + "/epics");
        this.readDataFiles();
    }

    /**
     * Checks if a valid data file exists.
     */
    private void checkDataFile() throws IOException {
        File dataFile = new File(this.pathToDataFile + "/timeTrackerData.json");
        dataFile.createNewFile();

        this.dataFile = DataManager.getJSONReader(this.pathToDataFile + "/timeTrackerData.json");
        if (!this.dataFile.has("colourTheme")) this.dataFile.put("colourTheme", "Dark");
        if (!this.dataFile.has("startScrollAtSetTime")) this.dataFile.put("startScrollAtSetTime", 540);
        if (!this.dataFile.has("minuteGap")) this.dataFile.put("minuteGap", 10);
        if (!this.dataFile.has("startScrollAtCurrentTime")) this.dataFile.put("startScrollAtCurrentTime", false);
        if (!this.dataFile.has("timeZone")) this.dataFile.put("timeZone", "computerDefault");
        if (!this.dataFile.has("language")) this.dataFile.put("language", "computerDefault");
        if (!this.dataFile.has("userName")) this.dataFile.put("userName", "User");
        if (!this.dataFile.has("autoSave")) this.dataFile.put("autoSave", false);
        if (!this.dataFile.has("worklogs")) this.dataFile.put("worklogs", new JSONArray());
        this.writeDataToFile();
    }

    /**
     * Saves the user's settings to the json file.
     * @param username
     * @param minuteGap
     * @param autoSave
     */
    public void saveUserSettingsToFile(String username, int minuteGap, boolean startScrollAtCurrentTime, int startScrollAtSetTime, boolean autoSave, ColourTheme colourTheme) {
        this.dataFile.put("colourTheme", this.colourTheme = colourTheme.NAME);
        this.dataFile.put("userName", this.userName = username);
        this.dataFile.put("minuteGap", this.minuteGap = minuteGap);
        this.dataFile.put("startScrollAtCurrentTime", this.startScrollAtCurrentTime = startScrollAtCurrentTime);
        this.dataFile.put("startScrollAtSetTime", this.startScrollAtSetTime = startScrollAtSetTime);
        this.dataFile.put("autoSave", this.autoSave = autoSave);
        this.writeDataToFile();
    }

    /**
     * Converts the cards into worklogs and saves them to
     * the main data file as a JSON array.
     *
     * @param cards The cards to save to the file.
     */
    public void saveWorklogsToFile(Set<Card> cards) {
        StringBuilder worklogs = new StringBuilder("[\n");
        // Convert cards into JSON array in string format
        int cardsParsed = 0;
        for (Card c : cards) {
            worklogs.append(c.toJSONString());
            cardsParsed++;
            if (cardsParsed != cards.size())
                worklogs.append(",\n");
        }
        worklogs.append("]");
        this.dataFile.put("worklogs", new JSONArray(worklogs.toString()));
        this.writeDataToFile();

        this.cards.clear();
        this.cards.addAll(cards);
    }

    /**
     * Writes the information in the fields into the
     * json data files.
     */
    private void writeDataToFile() {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(this.pathToDataFile + "/timeTrackerData.json");
            fileWriter.write(this.dataFile.toString(4));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads all the data files including the epic and tickets
     * information.
     */
    public void readDataFiles() {
        this.cards.clear();
        try {
            this.checkDataFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.epicManager.readEpicData();
        this.parseDataFile();
    }

    /**
     * Parses and stores the information in the main data file.
     */
    private void parseDataFile() {
        this.userName = this.dataFile.getString("userName");
        this.minuteGap = this.dataFile.getInt("minuteGap");
        this.startScrollAtCurrentTime = this.dataFile.getBoolean("startScrollAtCurrentTime");
        this.startScrollAtSetTime = this.dataFile.getInt("startScrollAtSetTime");
        this.colourTheme = this.dataFile.getString("colourTheme");
        this.language = this.dataFile.getString("language");
        this.timeZone = this.dataFile.getString("timeZone");
        this.autoSave = this.dataFile.getBoolean("autoSave");
        JSONArray worklogsArray = (JSONArray) this.dataFile.get("worklogs");
        this.parseWorklogs(worklogsArray);
        this.handledInvalidWorklogs(worklogsArray);
    }

    /**
     * Parses the user's worklogs and creates card objects
     * for each object which are then stored in a set.
     *
     * @param worklogs The worklogs as a JSONArray object.
     */
    private void parseWorklogs(JSONArray worklogs) {
        for (int i = 0; i < worklogs.length(); i++) {
            JSONObject worklog = (JSONObject) worklogs.get(i);
            String date = worklog.getString("date");
            String ticketTag = worklog.getString("ticket");
            String description = worklog.getString("description");
            String startTime = worklog.getString("startTime");
            String endTime = worklog.getString("endTime");
            // Check ticket
            Ticket ticket = this.epicManager.getTicketFromTag(ticketTag);
            if (ticket == null) {
                this.invalidWorklogsIndex.add(i);
                continue;
            }
            // Create card object from worklog
            Card card = new Card(Time.getCalendarFromDateString(date),
                    ticket,
                    description,
                    Time.getTimeInMinutesFromString(startTime),
                    Time.getTimeInMinutesFromString(endTime));
            this.cards.add(card);
        }
    }

    private void handledInvalidWorklogs(JSONArray worklogs) {
        if (this.invalidWorklogsIndex.isEmpty()) return;
        StringBuilder errorMessage = new StringBuilder(
                "Invalid worklogs detected. This is most likely due to tickets associated with certain worklogs being\n" +
                "deleted manually. If you wish to delete these corrupt worklogs, press Delete. Or if you'd like to recover\n" +
                "the corrupt worklogs, please click Close, and create the following tickets:\n");
        for (Integer worklogIndex : this.invalidWorklogsIndex) {
            JSONObject worklog = (JSONObject) worklogs.get(worklogIndex);
            errorMessage.append("    - ").append(worklog.getString("ticket")).append("\n");
        }
        String[] options = new String[] {"Delete", "Close"};
        int response = JOptionPane.showOptionDialog(Popup.MAIN_WINDOW, errorMessage, "Corrupt Worklogs", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, null);
        if (response != 0) System.exit(0);
        this.saveWorklogsToFile(this.cards);
    }

    /**
     * Check if any changes have been made to the work logs
     *
     * @param cards The current worklogs
     * @return True if changes have been made
     */
    public boolean changesMadeToWorklog(Set<Card> cards) {
        return !this.cards.equals(cards);
    }

    /**
     * Returns all the tickets loaded as an array.
     *
     * @return An array of all the tickets loaded.
     */
    public Ticket[] getAllTickets() {
        return this.epicManager.getAllTickets();
    }

    /**
     * Loads in the epics and tickets data.
     */
    public void loadEpicsData() {
        this.epicManager.readEpicData();
    }

    /** Returns the colourTheme as per the data file. */
    public String getColourTheme() {
        return this.colourTheme;
    }

    /** Returns the user's name as per the data file. */
    public String getUserName() {
        return this.userName;
    }

    /** Returns the minuteGap as per the data file. */
    public int getMinuteGap() {
        return this.minuteGap;
    }

    /** Returns whether the scroll should start at the current time. */
    public boolean getStartScrollAtCurrentTime() {
        return this.startScrollAtCurrentTime;
    }
    /** Returns the time set that the scroll should start at. */
    public int getStartScrollAtSetTime() {
        return this.startScrollAtSetTime;
    }

    /** Returns the auto-save property as per the data file. */
    public boolean getAutoSave() {
        return this.autoSave;
    }

    /** Returns the cards created from the worklogs in the data file. */
    public Set<Card> getCards() {
        return Collections.unmodifiableSet(this.cards);
    }

    /** Returns a JSON reader from the path to a json file. */
    public static JSONObject getJSONReader(String filePath) {
        // Get contents of file as a list.
        List<String> contentsAsList = null;
        try {
            contentsAsList = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Get contents of file as a string.
        StringBuilder contentsAsString = new StringBuilder();
        for (int i = 0; i < Objects.requireNonNull(contentsAsList).size(); i++) {
            contentsAsString.append(contentsAsList.get(i));
            if (i != contentsAsList.size() - 1)
                contentsAsString.append("\n");
        }

        if (contentsAsString.toString().isEmpty()) {
            contentsAsString.append("{\n}");
        }

        return new JSONObject(contentsAsString.toString());
    }
}

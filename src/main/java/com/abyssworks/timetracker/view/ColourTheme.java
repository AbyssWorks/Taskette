package com.abyssworks.timetracker.view;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The following class handles storing the colors for
 * the program's current theme.
 *
 * @author Aidan Lim
 */
public class ColourTheme {
    private static JSONObject themesData;

    static {
        InputStream is = Objects.requireNonNull(ColourTheme.class.getClassLoader().getResourceAsStream("themesData.json"));
        String fileContents = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining(System.lineSeparator()));
        ColourTheme.themesData = new JSONObject(fileContents);
    }

    public final String NAME;
    public final Color WeekRenderer_BG;
    public final Color WeekRenderer_PAST_BG;
    public final Color WeekRenderer_BORDER;
    public final Color WeekRenderer_VERTICAL_LINES;
    public final Color WeekRenderer_HORIZONTAL_LINES;
    public final Color WeekRenderer_PRESENT_TIME_INDICATOR;
    public final Color WeekRenderer_TIME_HIGHLIGHTED;
    public final Color WeekRenderer_HOUR_INDICATOR;
    public final Color WeekRenderer_CARD_BG;
    public final Color WeekRenderer_UNDRAGGABLE_CARD_BG;
    public final Color WeekRenderer_CARD_BORDER;
    public final Color WeekRenderer_CARD_CREATION_BG;
    public final Color WeekRenderer_CARD_HIGHLIGHT_BG;
    public final Color WeekRenderer_CARD_HIGHLIGHT_BORDER;
    public final Color WeekRenderer_CARD_TEXT;
    public final Color WeekHeader_BG;
    public final Color WeekHeader_BORDER;
    public final Color WeekHeader_HEADING;
    public final Color Header_BG;
    public final Color Header_SAVE_BUTTON_BG;
    public final Color Header_SAVE_BUTTON_TEXT;
    public final Color Header_LOAD_BUTTON_BG;
    public final Color Header_LOAD_BUTTON_TEXT;
    public final Color SettingsPopup_TOGGLE_ON_BG;
    public final Color SettingsPopup_TOGGLE_OFF_BG;
    public final Color SettingsPopup_TOGGLE_SWITCH_BG;

    public ColourTheme(String colourTheme){
        this.NAME = colourTheme;
        JSONObject theme = ColourTheme.themesData.getJSONObject(this.NAME);

        this.WeekRenderer_BG = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekRenderer_BG"));
        this.WeekRenderer_PAST_BG = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekRenderer_PAST_BG"));
        this.WeekRenderer_BORDER = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekRenderer_BORDER"));
        this.WeekRenderer_VERTICAL_LINES = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekRenderer_VERTICAL_LINES"));
        this.WeekRenderer_HORIZONTAL_LINES = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekRenderer_HORIZONTAL_LINES"));
        this.WeekRenderer_PRESENT_TIME_INDICATOR = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekRenderer_PRESENT_TIME_INDICATOR"));
        this.WeekRenderer_TIME_HIGHLIGHTED = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekRenderer_TIME_HIGHLIGHTED"));
        this.WeekRenderer_HOUR_INDICATOR = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekRenderer_HOUR_INDICATOR"));
        this.WeekRenderer_CARD_BG = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekRenderer_CARD_BG"));
        this.WeekRenderer_UNDRAGGABLE_CARD_BG = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekRenderer_UNDRAGGABLE_CARD_BG"));
        this.WeekRenderer_CARD_BORDER = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekRenderer_CARD_BORDER"));
        this.WeekRenderer_CARD_CREATION_BG = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekRenderer_CARD_CREATION_BG"));
        this.WeekRenderer_CARD_HIGHLIGHT_BG = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekRenderer_CARD_HIGHLIGHT_BG"));
        this.WeekRenderer_CARD_HIGHLIGHT_BORDER = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekRenderer_CARD_HIGHLIGHT_BORDER"));
        this.WeekRenderer_CARD_TEXT = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekRenderer_CARD_TEXT"));
        this.WeekHeader_BG = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekHeader_BG"));
        this.WeekHeader_BORDER = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekHeader_BORDER"));
        this.WeekHeader_HEADING = ColourTheme.colourFromJSONArray(theme.getJSONArray("WeekHeader_HEADING"));
        this.Header_BG = ColourTheme.colourFromJSONArray(theme.getJSONArray("Header_BG"));
        this.Header_SAVE_BUTTON_BG = ColourTheme.colourFromJSONArray(theme.getJSONArray("Header_SAVE_BUTTON_BG"));
        this.Header_SAVE_BUTTON_TEXT = ColourTheme.colourFromJSONArray(theme.getJSONArray("Header_SAVE_BUTTON_TEXT"));
        this.Header_LOAD_BUTTON_BG = ColourTheme.colourFromJSONArray(theme.getJSONArray("Header_LOAD_BUTTON_BG"));
        this.Header_LOAD_BUTTON_TEXT = ColourTheme.colourFromJSONArray(theme.getJSONArray("Header_LOAD_BUTTON_TEXT"));
        this.SettingsPopup_TOGGLE_ON_BG = ColourTheme.colourFromJSONArray(theme.getJSONArray("SettingsPopup_TOGGLE_ON_BG"));
        this.SettingsPopup_TOGGLE_OFF_BG = ColourTheme.colourFromJSONArray(theme.getJSONArray("SettingsPopup_TOGGLE_OFF_BG"));
        this.SettingsPopup_TOGGLE_SWITCH_BG = ColourTheme.colourFromJSONArray(theme.getJSONArray("SettingsPopup_TOGGLE_SWITCH_BG"));
    }

    /**
     * Creates a Color object from a JSONArray object.
     *
     * @param array The JSON array.
     * @return A Color object.
     */
    private static Color colourFromJSONArray(JSONArray array) {
        return new Color(array.getInt(0), array.getInt(1), array.getInt(2), array.getInt(3));
    }

    /**
     * Returns all the themes implemented.
     *
     * @return An array of the theme names.
     */
    public static String[] getAllThemeNames() {
        return ColourTheme.themesData.keySet().toArray(new String[0]);
    }
}

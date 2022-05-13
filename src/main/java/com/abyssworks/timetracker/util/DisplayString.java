package com.abyssworks.timetracker.util;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The following class handles storing the information for
 * a string to be displayed on the string. It also provides
 * utility functions for truncating strings and getting their
 * dimensions.
 *
 * @author Dysterio
 */
public class DisplayString {
    public final String TEXT;
    public final int WIDTH;
    public final int HEIGHT;

    /**
     * Creates an instance of the DisplayString object using the
     * text, text width, and text height passed.
     *
     * @param text The text to display.
     * @param width The text's width in pixels.
     * @param height The text's height in pixels.
     */
    public DisplayString(String text, int width, int height) {
        this.TEXT = text;
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    /**
     * Calculates a string's dimensions.
     *
     * @param g The graphics object.
     * @param text The text to get the dimensions of.
     * @return The string's dimensions as a DisplayString object.
     */
    public static DisplayString getStringDimensions(Graphics2D g, String text) {
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector gv = g.getFont().createGlyphVector(frc, text);
        Rectangle displayTextRectangle = gv.getPixelBounds(null, 0, 0);
        return new DisplayString(text, displayTextRectangle.width, displayTextRectangle.height);
    }

    /**
     * Truncates a string's length horizontally to make it fit inside
     * a number of pixels.
     *
     * @param g The graphics object.
     * @param text The text to truncate.
     * @param width The width that the text must fit inside.
     * @param ellipses Whether the text should be truncated using ellipses
     *                 (...) or a hyphen (-).
     * @return The truncated string and its dimensions as a DisplayString
     *                  object.
     */
    public static DisplayString truncateStringToFitWidth(Graphics2D g, String text, int width, boolean ellipses) {
        FontMetrics fm = g.getFontMetrics();
        StringBuilder displayString = new StringBuilder();
        int ending = ellipses ? fm.stringWidth("...") : fm.stringWidth("-");
        // Truncate string length
        int textWidth = 0;
        for (char c : text.toCharArray()) {
            int charWidth = fm.charWidth(c);
            if (textWidth + charWidth + ending <= width) {
                displayString.append(c);
            }
            textWidth += charWidth;
        }
        // Add truncated symbol to end
        String displayText = displayString.toString();
        if (textWidth > width) displayText += ellipses ? "..." : "-";
        else displayText = text;

        return DisplayString.getStringDimensions(g, displayText);
    }

    /**
     * Truncates a string both, vertically and horizontally to fit inside
     * the specified width and height.
     *
     * @param g The graphics object.
     * @param text The text to truncate.
     * @param width The width that the truncated text must fit inside.
     * @param height The height that the truncated text must fit inside.
     * @return The truncated string and its dimensions as an array of
     *              DisplayString objects.
     */
    public static List<DisplayString> truncateStringToFitWidthAndHeight(Graphics2D g, String text, int width, int height) {
        FontMetrics fm = g.getFontMetrics();
        int spaceWidth = fm.charWidth(' ');
        List<String> words = new ArrayList<>();
        StringBuilder wordBuilder = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c == ' ') {
                words.add(wordBuilder.toString());
                wordBuilder = new StringBuilder();
                continue;
            }
            wordBuilder.append(c);
            if (c == '\n') {
                words.add(wordBuilder.toString());
                wordBuilder = new StringBuilder();
            }
        }
        int wordIndex = 0;

        int numOfLinesThatWillFit = height / fm.getHeight();
        List<DisplayString> lines = new ArrayList<>();
        while (wordIndex < words.size()) {
            // Check if a single word is too big
            String oldWord = words.get(wordIndex);
            if (fm.stringWidth(oldWord) > width) {
                String newWord = DisplayString.truncateStringToFitWidth(g, oldWord, width, false).TEXT;
                words.set(wordIndex, newWord);
                words.add(wordIndex + 1, oldWord.substring(newWord.length() - 1));
            }
            // Break string based on width
            if (lines.size() == numOfLinesThatWillFit) break;
            StringBuilder line = new StringBuilder();
            int lineWidth = 0;
            while (lineWidth + fm.stringWidth(words.get(wordIndex)) <= width) {
                String word = words.get(wordIndex++);
                line.append(word).append(" ");
                lineWidth += fm.stringWidth(word) + spaceWidth;
                if (wordIndex >= words.size()) break;
                if (!word.endsWith("\n")) break;
            }
            lines.add(DisplayString.getStringDimensions(g, line.toString()));
        }
        // Add ellipses if text was truncated
        if (wordIndex < words.size()) {
            DisplayString lastLine = lines.get(numOfLinesThatWillFit-1);
            String lastLineText = lastLine.TEXT + "                         "; // Buffer to ensure ellipses.
            lastLine = DisplayString.truncateStringToFitWidth(g, lastLineText, width, true);
            lines.set(numOfLinesThatWillFit-1, lastLine);
        }

        return lines;
    }
}

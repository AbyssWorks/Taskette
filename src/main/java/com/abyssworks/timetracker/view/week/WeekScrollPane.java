package com.abyssworks.timetracker.view.week;

import com.abyssworks.timetracker.TimeTracker;
import com.abyssworks.timetracker.model.week.Week;
import com.abyssworks.timetracker.util.Time;
import com.abyssworks.timetracker.view.GUI;

import javax.swing.*;
import java.awt.*;

/**
 * The following class handles displaying the Week through
 * a scroll pane, allowing the user to scroll vertically.
 *
 * @author Dysterio
 */
public class WeekScrollPane extends JScrollPane {
    private int scrollSensitivity = 10;
    private WeekHeader weekHeader;

    /** Initializes the scroll pane. */
    public WeekScrollPane() {
        super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.verticalScrollBar.setUnitIncrement(this.scrollSensitivity);
    }

    /**
     * Changes the week being displayed in the scroll pane.
     *
     * @param newWeek The new week to be displayed.
     */
    public void updateWeekDisplayed(Week newWeek) {
        // Update scroll point
        if (this.weekHeader == null) {
            this.weekHeader = new WeekHeader(new WeekRenderer(newWeek));
            this.setViewport(this.weekHeader);
        } else {
            this.weekHeader.getView().updateDisplayedWeek(newWeek);
        }
        this.weekHeader.repaint();
    }

    /**
     * Updates the week view to properly display the week with
     * its new height.
     */
    public void updateWeekView(int oldPixPerMin) {
        int startScrollMinutes = this.weekHeader.getViewPosition().y / oldPixPerMin;
        this.weekHeader.setViewSize(new Dimension(0, 60 * 24 * GUI.getPixPerMin() + WeekHeader.HEADER_HEIGHT));
        this.weekHeader.getView().setPreferredSize(new Dimension(0, 60 * 24 * GUI.getPixPerMin() + WeekHeader.HEADER_HEIGHT));
        this.weekHeader.revalidate();
        this.weekHeader.repaint();

        int scrollPos;
        if (this.weekHeader.getViewPosition().y == 0)
            scrollPos = (TimeTracker.getStartScrollAtCurrentTime() ? Time.getTimeOfDayInMinutes() : TimeTracker.getStartScrollAtSetTime()) * GUI.getPixPerMin();
        else {
            scrollPos = startScrollMinutes * GUI.getPixPerMin();
        }
        this.weekHeader.setViewPosition(new Point(0, scrollPos));
    }
}

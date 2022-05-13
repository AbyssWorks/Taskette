package com.abyssworks.timetracker.view.week;

import com.abyssworks.timetracker.TimeTracker;
import com.abyssworks.timetracker.controller.keyboard.KeyActivityListener;
import com.abyssworks.timetracker.controller.mouse.WeekMouseListener;
import com.abyssworks.timetracker.model.week.Week;
import com.abyssworks.timetracker.model.week.WeekManager;
import com.abyssworks.timetracker.util.DisplayString;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * The following class is in charge of displaying the day names above
 * their respective columns i.e. Sunday, Monday, ...
 *
 * @author Dysterio
 */
public class WeekHeader extends JViewport {
    public static final int HEADER_HEIGHT = 30;
    public static final int SCROLL_BUFFER = 30;

    public final String[] DAYS = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    public final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");

    private final WeekMouseListener weekMouseListener;

    /**
     * Creates an instance of the week header.
     */
    public WeekHeader(WeekRenderer wr) {
        super();
        this.setView(wr);
        this.weekMouseListener = new WeekMouseListener(this);
        this.addChangeListener(this.weekMouseListener::scrollEvent);
        // Key Listener
        KeyActivityListener keyActivityListener = new KeyActivityListener(this);
        keyActivityListener.bindKey(KeyEvent.VK_ESCAPE, "esc", 0,
                () -> this.weekMouseListener.escKeyListener(true),
                () -> this.weekMouseListener.escKeyListener(false));
        keyActivityListener.bindKey(KeyEvent.VK_ALT, "alt", KeyEvent.ALT_MASK,
                () -> this.weekMouseListener.altKeyListener(true),
                () -> this.weekMouseListener.altKeyListener(false));
    }

    /** Returns the WeekRender associated with this Header. */
    @Override
    public WeekRenderer getView() {
        return (WeekRenderer) super.getView();
    }

    /**
     * Renders the day header.
     *
     * @param graphics The graphics object.
     */
    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D g = (Graphics2D) graphics;

        this.drawBackground(g);
        this.drawDaySeparator(g);
        this.drawDayNames(g);
        this.highlightCurrentDay(g);
    }

    /**
     * Draws the header's background.
     *
     * @param g The graphics object.
     */
    private void drawBackground(Graphics2D g) {
        g.setColor(TimeTracker.getColorTheme().WeekHeader_BG);
        g.fillRect(WeekRenderer.TIME_BAR_WIDTH, 0, this.getWidth() - WeekRenderer.TIME_BAR_WIDTH, WeekHeader.HEADER_HEIGHT);
    }

    /**
     * Renders vertical lines to separate days.
     *
     * @param g The graphics object.
     */
    private void drawDaySeparator(Graphics2D g) {
        int width = this.getWidth() - WeekRenderer.TIME_BAR_WIDTH;
        g.setColor(TimeTracker.getColorTheme().WeekHeader_BORDER);
        for (int i = 0; i < 7; i++) {
            int xPos = (int) Math.round(WeekRenderer.TIME_BAR_WIDTH + i * width/7.0);
            g.drawLine(xPos, 0, xPos, WeekHeader.HEADER_HEIGHT);
        }
    }

    /**
     * Renders the day names like Sunday, Monday...
     *
     * @param g The graphics object.
     */
    private void drawDayNames(Graphics2D g) {
        String[] dates = new String[7];

        Calendar currDate = (Calendar) this.getView().getCurrWeek().getStartDate().clone();
        for (int i = 0; i < 7; i++) {
            dates[i] = "(" + this.dateFormat.format(currDate.getTime()) + ")";
            currDate.add(Calendar.DATE, 1);
        }

        int weekWidth = this.getWidth() - WeekRenderer.TIME_BAR_WIDTH;
        double dayWidth = weekWidth/7.0;
        g.setColor(TimeTracker.getColorTheme().WeekHeader_HEADING);
        for (int i = 0; i < 7; i++) {
            DisplayString day = DisplayString.getStringDimensions(g, this.DAYS[i]);
            DisplayString date = DisplayString.getStringDimensions(g, dates[i]);

            int startXPos = (int) Math.round(WeekRenderer.TIME_BAR_WIDTH + i * dayWidth);
            g.drawString(day.TEXT, (int) (startXPos + dayWidth/2.0 - day.WIDTH/2.0), day.HEIGHT);
            g.drawString(date.TEXT, (int) (startXPos + dayWidth/2.0 - date.WIDTH/2.0), WeekHeader.HEADER_HEIGHT - date.HEIGHT/2);
        }
    }

    /**
     * Highlights the current day's label.
     *
     * @param g The graphics object.
     */
    private void highlightCurrentDay(Graphics2D g) {
        Week currWeek = this.getView().getCurrWeek();
        int when = currWeek.weekRelationshipToPresentWeek();
        if (when != 0) return;
        double dayWidth = (this.getWidth() - WeekRenderer.TIME_BAR_WIDTH)/7.0;
        int startXPos = (int) Math.round(WeekRenderer.TIME_BAR_WIDTH + WeekManager.getCurrDayIndex() * dayWidth);
        int endXPos = (int) Math.round(WeekRenderer.TIME_BAR_WIDTH + (WeekManager.getCurrDayIndex() + 1) * dayWidth);

        g.setColor(TimeTracker.getColorTheme().WeekRenderer_PRESENT_TIME_INDICATOR);
        g.drawLine(startXPos, 0, startXPos, WeekHeader.HEADER_HEIGHT);
        g.drawLine(endXPos, 0, endXPos, WeekHeader.HEADER_HEIGHT);
        g.drawLine(startXPos, 0, endXPos, 0);
    }
}

package com.abyssworks.timetracker.view;

import com.abyssworks.timetracker.TimeTracker;
import com.abyssworks.timetracker.model.week.WeekManager;
import com.abyssworks.timetracker.util.Time;
import com.abyssworks.timetracker.view.popup.SettingsPopup;
import com.abyssworks.timetracker.view.week.WeekScrollPane;
import com.abyssworks.timetracker.util.DisplayString;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * The following class handles displaying information about the
 * current week, and allowing the user to cycle through other weeks.
 *
 * @author Dysterio
 */
public class Header extends JPanel {
    public static final int HEADER_HEIGHT = 30;

    private final SpringLayout sprintLayout = new SpringLayout();
    private final WeekScrollPane weekScrollPane;
    private JLabel weekHeading;
    private JButton leftButton;
    private JButton rightButton;
    private JButton saveButton;
    private JButton loadButton;
    private JButton settingsButton;

    /** Creates a new instance of a Header. */
    public Header(WeekScrollPane weekScrollPane) {
        super();
        this.setLayout(this.sprintLayout);
        this.setMinimumSize(new Dimension(weekScrollPane.getWidth(), Header.HEADER_HEIGHT));

        this.weekScrollPane = weekScrollPane;
        this.addWeekHeading();
        this.addWeekChangingButtons();
        this.addLoadButton();
        this.addSaveButton();
        this.addSettingsButton();
        this.updateWeekDisplayed();
    }

    /**
     * Shows the currently displayed week's start and
     * end dates.
     */
    private void addWeekHeading() {
        this.weekHeading = new JLabel(WeekManager.getDisplayWeekStartAndEndDate());
        this.add(this.weekHeading);
        this.sprintLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, this.weekHeading,
                0,
                SpringLayout.HORIZONTAL_CENTER, this);
        this.sprintLayout.putConstraint(SpringLayout.VERTICAL_CENTER, this.weekHeading,
                0,
                SpringLayout.VERTICAL_CENTER, this);
    }

    /**
     * Displays buttons to allow the user to change
     * the currently displayed week.
     */
    private void addWeekChangingButtons() {
        this.leftButton = new JButton("<");
        this.leftButton.setFocusable(false);
        this.leftButton.addActionListener(this::weekChangeButtonListener);
        this.rightButton = new JButton(">");
        this.rightButton.setFocusable(false);
        this.rightButton.addActionListener(this::weekChangeButtonListener);

        this.add(this.leftButton);
        this.sprintLayout.putConstraint(SpringLayout.EAST, this.leftButton,
                -5,
                SpringLayout.WEST, this.weekHeading);
        this.sprintLayout.putConstraint(SpringLayout.VERTICAL_CENTER, this.leftButton,
                0,
                SpringLayout.VERTICAL_CENTER, this.weekHeading);

        this.add(this.rightButton);
        this.sprintLayout.putConstraint(SpringLayout.WEST, this.rightButton,
                5,
                SpringLayout.EAST, this.weekHeading);
        this.sprintLayout.putConstraint(SpringLayout.VERTICAL_CENTER, this.rightButton,
                0,
                SpringLayout.VERTICAL_CENTER, this.weekHeading);
    }

    /**
     * Displays a load button to allow the user to load the
     * epics data.
     */
    private void addLoadButton() {
        this.loadButton = new JButton("Load");
        this.loadButton.setFocusable(false);
        this.loadButton.setBackground(TimeTracker.getColorTheme().Header_LOAD_BUTTON_BG);
        this.loadButton.setForeground(TimeTracker.getColorTheme().Header_LOAD_BUTTON_TEXT);
        this.loadButton.addActionListener(e -> TimeTracker.loadEpicsData());

        this.add(this.loadButton);
        this.sprintLayout.putConstraint(SpringLayout.EAST, this.loadButton,
                -50,
                SpringLayout.WEST, this.leftButton);
        this.sprintLayout.putConstraint(SpringLayout.VERTICAL_CENTER, this.loadButton,
                0,
                SpringLayout.VERTICAL_CENTER, this.weekHeading);
    }

    /**
     * Displays a save button to allow the user to save the
     * data.
     */
    private void addSaveButton() {
        this.saveButton = new JButton("Save");
        this.saveButton.setFocusable(false);
        this.saveButton.setBackground(TimeTracker.getColorTheme().Header_SAVE_BUTTON_BG);
        this.saveButton.setForeground(TimeTracker.getColorTheme().Header_SAVE_BUTTON_TEXT);
        this.saveButton.addActionListener(e -> WeekManager.saveWorklogs());

        this.add(this.saveButton);
        this.sprintLayout.putConstraint(SpringLayout.WEST, this.saveButton,
                50,
                SpringLayout.EAST, this.rightButton);
        this.sprintLayout.putConstraint(SpringLayout.VERTICAL_CENTER, this.saveButton,
                0,
                SpringLayout.VERTICAL_CENTER, this.weekHeading);
    }

    /**
     * Displays a settings button in the top right of the screen.
     * Pressing this button opens the settings menu.
     */
    private void addSettingsButton() {
        ImageIcon cog = new ImageIcon(Time.getResourcePath("settingsCog.png"));
        Image scaledCog = cog.getImage().getScaledInstance(Header.HEADER_HEIGHT - 1, Header.HEADER_HEIGHT - 1, Image.SCALE_SMOOTH);
        this.settingsButton = new JButton(new ImageIcon(scaledCog));
        this.settingsButton.setFocusable(false);
        this.settingsButton.setBorder(BorderFactory.createEmptyBorder());
        this.settingsButton.setContentAreaFilled(false);
        this.settingsButton.addActionListener(e -> new SettingsPopup());

        this.add(this.settingsButton);
        this.sprintLayout.putConstraint(SpringLayout.EAST, this.settingsButton,
                0,
                SpringLayout.EAST, this);
        this.sprintLayout.putConstraint(SpringLayout.VERTICAL_CENTER, this.settingsButton,
                0,
                SpringLayout.VERTICAL_CENTER, this.weekHeading);
    }

    /**
     * Paints the header.
     *
     * @param graphics The graphics object.
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        this.setBackground(TimeTracker.getColorTheme().Header_BG);
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        DisplayString version = DisplayString.getStringDimensions(g, TimeTracker.VERSION);
        g.drawString(version.TEXT, 0, version.HEIGHT);
    }

    /**
     * Handles the action to be performed when any of the
     * week changing buttons is pressed.
     *
     * @param e The action event fired from the button press.
     */
    private void weekChangeButtonListener(ActionEvent e) {
        int changeDirection = e.getActionCommand().equals(">") ? WeekManager.NEXT : WeekManager.PREV;
        WeekManager.changeWeek(changeDirection);
        this.updateWeekDisplayed();
    }

    /** Updates the week displayed and its details */
    private void updateWeekDisplayed() {
        this.weekHeading.setText(WeekManager.getDisplayWeekStartAndEndDate());
        this.weekScrollPane.updateWeekDisplayed(WeekManager.getDisplayWeek());
    }
}

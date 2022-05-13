package com.abyssworks.timetracker.view.popup;

import com.abyssworks.timetracker.TimeTracker;
import com.abyssworks.timetracker.util.JSwitchButton;
import com.abyssworks.timetracker.util.Time;
import com.abyssworks.timetracker.view.ColourTheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.ParseException;
import java.util.Date;

/**
 * The following class displays a settings menu when the user
 * clicks on the settings cog in the top left of the window.
 *
 * @author Dysterio
 */
public class SettingsPopup extends Popup {
    private JLabel nameLabel;
    private JTextField nameTextField;
    private JLabel minuteGapLabel;
    private JSlider minuteGapSlider;
    private JLabel autoSaveSwitchLabel;
    private JSwitchButton autoSaveSwitch;
    private JLabel currentScrollTimeLabel;
    private JSwitchButton currentScrollTimeSwitch;
    private JSpinner setScrollTimeSpinner;
    private JLabel setScrollTimeLabel;
    private JLabel themeChooserLabel;
    private JComboBox<String> themeChooser;
    private JButton cancelButton;
    private JButton saveButton;

    /**
     * Creates a new popup.
     */
    public SettingsPopup() {
        super(TimeTracker::resetSettings);
        this.setTitle("Settings");
        this.setResizable(false);
        this.setLayout(new GridBagLayout());

        this.addNameField();
        this.addMinuteGapSlider();
        this.addAutoSaveSwitch();
        this.addThemeChooser();
        this.addScrollStartTimeChooser();
        this.addConfirmationButtons();

        this.setLocationRelativeTo(Popup.MAIN_WINDOW);
        this.pack();
        this.setVisible(true);
    }

    /**
     * Displays a text field that allows the user to
     * change their username.
     */
    private void addNameField() {
        this.nameLabel = new JLabel("Name:");
        this.nameTextField = new JTextField(TimeTracker.getUserName());
        this.nameLabel.setLabelFor(this.nameTextField);
        this.nameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                TimeTracker.setUserName(nameTextField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                TimeTracker.setUserName(nameTextField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                TimeTracker.setUserName(nameTextField.getText());
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(5, 5, 5, 5);
        this.add(this.nameLabel, c);
        c.insets = new Insets(5, 0, 5, 5);
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(this.nameTextField, c);
    }

    /**
     * Displays a slider to allow the user to change the
     * minute gap interval.
     */
    private void addMinuteGapSlider() {
        this.minuteGapLabel = new JLabel("Minute Gap:", JLabel.TRAILING);
        this.minuteGapSlider = new JSlider(SwingConstants.HORIZONTAL, TimeTracker.MINIMUM_MINUTE_GAP, TimeTracker.MAXIMUM_MINUTE_GAP, TimeTracker.getMinuteGap());
        this.minuteGapSlider.setMajorTickSpacing(10);
        this.minuteGapSlider.setMinorTickSpacing(1);
        this.minuteGapSlider.setPaintTicks(true);
        this.minuteGapSlider.setPaintLabels(true);
        this.minuteGapLabel.setLabelFor(this.minuteGapSlider);
        this.minuteGapSlider.addChangeListener(e -> TimeTracker.setMinuteGap(this.minuteGapSlider.getValue()));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(5, 5, 5, 5);
        this.add(this.minuteGapLabel, c);
        c.insets = new Insets(0, 0, 5, 5);
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(this.minuteGapSlider, c);
    }

    /**
     * Displays a switch to allow the user to toggle between
     * manual and automatic saving functionality.
     */
    private void addAutoSaveSwitch() {
//        this.autoSaveSwitchLabel = new JLabel("Auto-Save:", JLabel.TRAILING);
//        // Initialize toggle button
//        this.autoSaveSwitch = new JSwitchButton(TimeTracker.getAutoSave());
//        this.autoSaveSwitchLabel.setLabelFor(this.autoSaveSwitch);
//        // Assign listener
//        this.autoSaveSwitch.addItemListener(e -> TimeTracker.setAutoSave(this.autoSaveSwitch.isSelected()));
//
//        GridBagConstraints c = new GridBagConstraints();
//        c.gridx = 0;
//        c.gridy = 2;
//        c.weightx = 0;
//        c.fill = GridBagConstraints.NONE;
//        c.anchor = GridBagConstraints.LINE_END;
//        c.insets = new Insets(0, 5, 5, 5);
//        this.add(this.autoSaveSwitchLabel, c);
//        c.insets = new Insets(2, 0, 5, 5);
//        c.anchor = GridBagConstraints.LINE_START;
//        c.gridx = 1;
//        this.add(this.autoSaveSwitch, c);
    }

    /**
     * Adds a switch and spinner to allow the user to choose what time
     * the program should start at.
     */
    private void addScrollStartTimeChooser() {
        this.currentScrollTimeLabel = new JLabel("Open At Current Time", JLabel.LEADING);
        this.currentScrollTimeSwitch = new JSwitchButton(TimeTracker.getStartScrollAtCurrentTime());
        this.setScrollTimeLabel = new JLabel("Open To Set Time", JLabel.TRAILING);
        this.setScrollTimeSpinner = CardPopup.createTimeSpinner(0);
        this.switchAndSpinnerListener();
        this.setScrollTimeSpinner.addChangeListener(e -> {
            Date timeSet = (Date) this.setScrollTimeSpinner.getValue();
            int timeInMinutes = Time.getTimeInMinutesFromDate(timeSet);
            TimeTracker.setScrollStart(this.currentScrollTimeSwitch.isSelected(), timeInMinutes);
        });
        this.currentScrollTimeSwitch.addItemListener(e -> this.switchAndSpinnerListener());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 5, 0);
        this.add(this.setScrollTimeLabel, c);

        c.gridx = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        this.add(new JPanel() {
            {
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                c.insets = new Insets(2, 0, 5, 5);
                c.anchor = GridBagConstraints.LINE_END;
                c.fill = GridBagConstraints.BOTH;
                this.add(setScrollTimeSpinner, c);

                c.gridx = 1;
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.LINE_END;
                c.insets = new Insets(0, 5, 5, 5);
                this.add(currentScrollTimeSwitch, c);
                c.insets = new Insets(2, 0, 5, 5);
                c.anchor = GridBagConstraints.LINE_END;
                c.gridx = 2;
                this.add(currentScrollTimeLabel, c);
            }
        }, c);
    }

    /**
     * Handles toggling between using the current time, and the manually
     * entered time.
     */
    private void switchAndSpinnerListener() {
        int spinnerTimeInMinutes;
        if (this.currentScrollTimeSwitch.isSelected()) {
            spinnerTimeInMinutes = Time.getTimeOfDayInMinutes();
            this.setScrollTimeSpinner.setEnabled(false);
        } else {
            spinnerTimeInMinutes = TimeTracker.getStartScrollAtSetTime();
            this.setScrollTimeSpinner.setEnabled(true);
        }
        ((JSpinner.DefaultEditor) this.setScrollTimeSpinner.getEditor()).getTextField()
                .setText(Time.formatTimeInMinutes(spinnerTimeInMinutes));
        try {
            this.setScrollTimeSpinner.commitEdit();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        Date timeSet = (Date) this.setScrollTimeSpinner.getValue();
        int timeInMinutes = Time.getTimeInMinutesFromDate(timeSet);
        TimeTracker.setScrollStart(this.currentScrollTimeSwitch.isSelected(), timeInMinutes);
    }

    /**
     * Displays a drop-down list for the user to choose a theme.
     */
    private void addThemeChooser() {
        this.themeChooserLabel = new JLabel("Theme:", JLabel.TRAILING);
        this.themeChooser = new JComboBox<>(ColourTheme.getAllThemeNames());
        this.themeChooserLabel.setLabelFor(this.themeChooser);
        this.themeChooser.setEditable(false);
        this.themeChooser.setSelectedItem(TimeTracker.getColorTheme().NAME);

        this.themeChooser.addActionListener(e -> TimeTracker.setColourTheme((String) this.themeChooser.getSelectedItem()));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(5, 5, 5, 5);
        this.add(this.themeChooserLabel, c);
        c.insets = new Insets(0, 0, 5, 5);
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(this.themeChooser, c);
    }

    /**
     * Adds save and cancel buttons at the bottom of the
     * menu.
     */
    private void addConfirmationButtons() {
        this.cancelButton = new JButton("Cancel");
        this.cancelButton.setFocusable(false);
        this.cancelButton.addActionListener(e -> {
            TimeTracker.resetSettings();
            this.dispose();
        });
        this.saveButton = new JButton("Save");
        this.saveButton.addActionListener(e -> {
            TimeTracker.saveSettings();
            this.dispose();
        });
        this.saveButton.setFocusable(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        this.add(new JPanel() {
            {
                this.setLayout(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                c.insets = new Insets(5, 10, 5, 25);
                c.anchor = GridBagConstraints.CENTER;
                this.add(cancelButton, c);
                c.insets = new Insets(5, 25, 5, 10);
                c.gridx = 1;
                this.add(saveButton, c);
            }
        }, c);
    }
}

package com.abyssworks.timetracker.view.popup;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import com.abyssworks.timetracker.TimeTracker;
import com.abyssworks.timetracker.controller.keyboard.KeyActivityListener;
import com.abyssworks.timetracker.model.week.Card;
import com.abyssworks.timetracker.model.data.Ticket;
import com.abyssworks.timetracker.model.week.WeekManager;
import com.abyssworks.timetracker.util.Time;
import javafx.scene.control.SpinnerValueFactory;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * This class handles displaying the card pop to confirm
 * the user's card creation action.
 *
 * @author Dysterio
 */
public class CardPopup extends Popup {
    public static final int LABEL_TEXTFIELD_MARGIN = 5;
    public final Card ORIG_CARD;

    private JDatePickerImpl datePicker;
    private JComboBox<Ticket> ticket;
    private JTextArea description;
    private JSpinner startTime;
    private JSpinner endTime;

    /**
     * Displays a new card popup.
     *
     * @param date The card's date.
     * @param startTimeInMinutes The card's start time in minutes.
     * @param endTimeInMinutes The card's end time in minutes.
     */
    public CardPopup(Calendar date, int startTimeInMinutes, int endTimeInMinutes) {
        super();
        this.setMinimumSize(new Dimension(250, 250));
        this.setUndecorated(true);
        this.setLayout(new GridBagLayout());

        this.ORIG_CARD = null;
        this.addDatePicker(date);
        this.addTicketSelector(null);
        this.addDescriptionField("Description");
        this.addTimeSelectors(startTimeInMinutes, endTimeInMinutes);
        this.addButtons();

        this.setLocationRelativeTo(Popup.MAIN_WINDOW);
        this.pack();
        this.setVisible(true);
    }

    public CardPopup(Card card) {
        super();
        this.setMinimumSize(new Dimension(250, 250));
        this.setUndecorated(true);
        this.setLayout(new GridBagLayout());

        this.ORIG_CARD = card;
        this.addDatePicker((Calendar) card.getDate().clone());
        this.addTicketSelector(card.getTicket());
        this.addDescriptionField(card.getDescription());
        this.addTimeSelectors(card.getStartTimeInMinutes(), card.getEndTimeInMinutes());
        this.addButtons();

        this.setLocationRelativeTo(Popup.MAIN_WINDOW);
        this.pack();
        this.setVisible(true);
    }

    /**
     * Initializes the date picker to display the date
     * passed.
     *
     * @param date The initial date
     */
    private void initializeDatePicker(Calendar date) {
        UtilDateModel model = new UtilDateModel();
        model.setValue(date.getTime());
        model.setSelected(true);
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        this.datePicker = new JDatePickerImpl(datePanel, new JFormattedTextField.AbstractFormatter() {
            /**
             * Parses <code>text</code> returning an arbitrary Object. Some
             * formatters may return null.
             *
             * @param text String to convert
             * @return Object representation of text
             * @throws ParseException if there is an error in the conversion
             */
            @Override
            public Object stringToValue(String text) throws ParseException {
                return TimeTracker.DATE_FORMAT.parseObject(text);
            }

            /**
             * Returns the string value to display for <code>value</code>.
             *
             * @param value Value to convert
             * @return String representation of value
             */
            @Override
            public String valueToString(Object value) {
                if (value != null) {
                    Calendar calendar = (Calendar) value;
                    return TimeTracker.DATE_FORMAT.format(calendar.getTime());
                }
                return "";
            }
        });

        this.datePicker.setDoubleClickAction(true);
        this.datePicker.setShowYearButtons(false);
        this.datePicker.setButtonFocusable(false);
    }

    /**
     * Adds the date picker to the card popup.
     *
     * @param date The initial date on the date picker.
     */
    private void addDatePicker(Calendar date) {
        this.initializeDatePicker(date);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 1;
        this.add(this.datePicker, c);
    }

    /**
     * Adds ticket selector to the popup.
     */
    private void addTicketSelector(Ticket ticketSelected) {
        this.ticket = new JComboBox<>();
        AutoCompleteSupport.install(this.ticket, GlazedLists.eventListOf(TimeTracker.getAllTickets()));
        if (ticketSelected != null) this.ticket.setSelectedItem(ticketSelected);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        this.add(new JLabel("Ticket: "), c);
        c.gridx = 1;
        c.insets = new Insets(0, CardPopup.LABEL_TEXTFIELD_MARGIN, 0, 0);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        this.add(this.ticket, c);
    }

    /**
     * Adds the description text pane to the popup.
     */
    private void addDescriptionField(String desc) {
        this.description = new JTextArea();
        this.description.setText(desc);
        this.description.setLineWrap(true);
        this.description.setWrapStyleWord(true);
        this.description.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (description.getText().equals("Description"))
                    description.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (description.getText().trim().equals(""))
                    description.setText("Description");
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        this.add(new JScrollPane(this.description), c);
    }

    /**
     * Creates a JSpinner that can be used to select a time.
     *
     * @param timeInMinutes The initial time on the time selector.
     * @return The JSpinner.
     */
    public static JSpinner createTimeSpinner(int timeInMinutes) {
        SpinnerDateModel sdm;
        try {
            sdm = new SpinnerDateModel(Time.TIME_FORMAT.parse(Time.formatTimeInMinutes(timeInMinutes)), null, null, Calendar.HOUR_OF_DAY);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid time format");
        }
        JSpinner spinner = new JSpinner(sdm);
        JSpinner.DateEditor de = new JSpinner.DateEditor(spinner, Time.TIME_PATTERN);
        spinner.setEditor(de);

        JFormattedTextField spinnerTF = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        spinnerTF.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been released.
             *
             * @param e
             */
            @Override
            public void keyReleased(KeyEvent e) {
                String input = spinnerTF.getText();
                if(input.matches("\\d\\d:\\d\\d")) {
                    int caretPosition = spinnerTF.getCaretPosition();
                    try {
                        spinner.commitEdit();
                        spinnerTF.setCaretPosition(caretPosition);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        return spinner;
    }

    /**
     * Adds the time selectors to the popup.
     *
     * @param startTimeInMinutes The initial time on the start time picker.
     * @param endTimeInMinutes The initial time on the end time picker.
     */
    private void addTimeSelectors(int startTimeInMinutes, int endTimeInMinutes) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(0, 0, 0, CardPopup.LABEL_TEXTFIELD_MARGIN);
        this.add(new JLabel("From:"), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        this.startTime = CardPopup.createTimeSpinner(startTimeInMinutes);
        this.add(this.startTime, c);

        c = new GridBagConstraints();
        c.gridx = 2;
        c.insets = new Insets(0, 0, 0, CardPopup.LABEL_TEXTFIELD_MARGIN);
        this.add(new JLabel("To:"), c);
        c.gridx = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.endTime = this.createTimeSpinner(endTimeInMinutes);
        this.add(this.endTime, c);
    }

    /**
     * Adds the cancel and ok buttons to the popup.
     */
    private void addButtons() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = GridBagConstraints.RELATIVE;
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFocusable(false);
        this.add(cancelButton, c);
        cancelButton.addActionListener(e -> this.dispose());

        c.gridx = 3;
        c.gridwidth = GridBagConstraints.RELATIVE;
        JButton okButton = new JButton("OK");
        okButton.setFocusable(false);
        this.add(okButton, c);
        okButton.addActionListener(e -> this.validateInput());
    }

    /**
     * Checks if the date inputted is valid.
     *
     * @return True if the date is valid.
     */
    private boolean validateDate() {
        if (this.getDate() != null) return true;
        JOptionPane.showMessageDialog(Popup.MAIN_WINDOW, "Please select a valid date.", "Date Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    /**
     * Checks if the ticket selected is valid.
     *
     * @return Any errors caused by the ticket selected.
     */
    private String validateTicket() {
        if (this.getTicket() != null) return "";
        return "    - Please select a valid ticket.\n";
    }

    /**
     * Checks if the description entered is valid.
     *
     * @return Any errors that arise from the description entered.
     */
    private String validateDescription() {
        if (!this.getDescription().equals("Description")) return "";
        return "    - Empty description.\n";
    }

    /**
     * Checks that the times selected are valid.
     *
     * @return Any errors caused by the times selected.
     */
    private String validateTime() {
        int startTime = this.getTimeInMinutes(true);
        int endTime = this.getTimeInMinutes(false);
        if (startTime >= endTime)
            return "    - The start time must be less than the end time.\n";
        else if (endTime < startTime + Card.MINIMUM_CARD_DURATION)
            return "    - The duration must be at least " + Card.MINIMUM_CARD_DURATION + " minutes.\n";
        else if (!WeekManager.checkIfCardCanBeAdded(this.getDate(), startTime, endTime, this.ORIG_CARD))
            return "    - Card collision detected on " + TimeTracker.DATE_FORMAT.format(this.getDate().getTime()) + " between " + Time.formatTimeInMinutes(startTime) + " and " + Time.formatTimeInMinutes(endTime) + ".\n";
        return "";
    }

    /**
     * Makes sure the input entered into the popup is
     * valid. If not, an error message is shown.
     */
    private void validateInput() {
        if (!this.validateDate()) return;
        String errors = this.validateTicket() + this.validateDescription() + this.validateTime();
        if (!errors.equals("")) {
            JOptionPane.showMessageDialog(Popup.MAIN_WINDOW, "Invalid input:\n" + errors, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.successful = true;
        this.dispose();
    }

    /** Returns the date selected. */
    public Calendar getDate() {
        Date dateSelected = (Date) this.datePicker.getModel().getValue();
        if (dateSelected == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateSelected);
        return cal;
    }

    /** Returns the ticket selected. */
    public Ticket getTicket() {
        return (Ticket) this.ticket.getSelectedItem();
    }

    /** Returns the description entered. */
    public String getDescription() {
        return this.description.getText();
    }

    /** Returns the time selected. */
    public int getTimeInMinutes(boolean startTime) {
        Date whichTime = (Date) (startTime ? this.startTime : this.endTime).getValue();
        int timeInMinutes = Time.getTimeInMinutesFromDate(whichTime);
        if (!startTime && timeInMinutes == 0) timeInMinutes = 24 * 60;
        return timeInMinutes;
    }

    /** Returns whether the card creation action was successful. */
    public boolean isSuccessful() {
        return this.successful;
    }
}

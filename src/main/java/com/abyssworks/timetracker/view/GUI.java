package com.abyssworks.timetracker.view;

import com.abyssworks.timetracker.model.week.Card;
import com.abyssworks.timetracker.model.week.WeekManager;
import com.abyssworks.timetracker.util.Time;
import com.abyssworks.timetracker.view.popup.Popup;
import com.abyssworks.timetracker.view.week.WeekScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The GUI class handles the handles all the visual aspects
 * of the program.
 */
public class GUI extends JFrame {
    public static final int MINIMUM_WIDTH = 960;
    public static final int MINIMUM_HEIGHT = 540;
    private static int pixPerMin = 2;

    private final Header header;
    private final WeekScrollPane scrollPane;

    /**
     * Creates an instance of the GUI.
     */
    public GUI() {
        super("Time Planner");
        this.setLayout(new GridBagLayout());
        ImageIcon logo = new ImageIcon(Time.getResourcePath("logo.png"));
        this.setIconImage(logo.getImage());
        this.setMinimumSize(new Dimension(GUI.MINIMUM_WIDTH, GUI.MINIMUM_HEIGHT));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.scrollPane = new WeekScrollPane();
        this.header = new Header(this.scrollPane);
        this.displayWeek();
        this.displayWeekHeader();
        Popup.MAIN_WINDOW = this;

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             *
             * @param e The WindowEvent object
             */
            @Override
            public void windowClosing(WindowEvent e) {
                if (!WeekManager.changesMadeToWorklogs()) System.exit(1);
                Object[] choices = {"Save", "Don't Save", "Cancel"};
                int option = JOptionPane.showOptionDialog(Popup.MAIN_WINDOW,
                        "Would you like to save your data before quitting?",
                        "Save Data",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        choices,
                        choices[2]);
                if (option == 2 || option == -1) return;
                if (option == 0) WeekManager.saveWorklogs();
                System.exit(1);
            }
        });

        this.setVisible(true);
    }

    /** Displays the week changing header. */
    private void displayWeekHeader() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.PAGE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(this.header, c);
    }

    /** Displays the week. */
    private void displayWeek() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.PAGE_END;
        c.fill = GridBagConstraints.BOTH;
        this.add(this.scrollPane, c);
    }

    /** Returns the pixels per minute property. */
    public static int getPixPerMin() {
        return GUI.pixPerMin;
    }

    /** Sets the pixels per minute property. */
    public void setPixPerMin(int pixPerMin) {
        int oldPixPerMin = GUI.pixPerMin;
        GUI.pixPerMin = pixPerMin;
        Card.DRAGGABLE_CARD_MIN_DURATION = (pixPerMin == 1) ? 10 : 5;
        this.scrollPane.updateWeekView(oldPixPerMin);
    }
}

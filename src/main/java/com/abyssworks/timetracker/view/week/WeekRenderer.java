package com.abyssworks.timetracker.view.week;

import com.abyssworks.timetracker.TimeTracker;
import com.abyssworks.timetracker.model.week.Card;
import com.abyssworks.timetracker.model.week.Day;
import com.abyssworks.timetracker.model.week.Week;
import com.abyssworks.timetracker.model.week.WeekManager;
import com.abyssworks.timetracker.util.Pair;
import com.abyssworks.timetracker.util.Time;
import com.abyssworks.timetracker.view.ColourTheme;
import com.abyssworks.timetracker.view.GUI;
import com.abyssworks.timetracker.util.DisplayString;
import com.abyssworks.timetracker.view.popup.CardPopup;
import com.abyssworks.timetracker.view.popup.Popup;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.plaf.synth.ColorType;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * The WeekRenderer class handles drawing the current
 * week and the cards assigned to that week.
 *
 * @author Dysterio
 */
public class WeekRenderer extends JPanel implements ActionListener {
    public static final int TIME_BAR_WIDTH = 50;
    public static final int CARD_HORIZONTAL_BUFFER = 10;

    private static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 10);
    private static final Font BIG_FONT = new Font("Arial", Font.PLAIN, 15);

    private Week currWeek;
    private final Set<Card> cardsToHighlight = new HashSet<>();
    private final Pair<Integer, Integer> timeBarToHighlight = new Pair<>(-1, -1);
    private int highlightLength = 0;
    private Timer repaintTimer;

    /**
     * Creates a new week renderer and displays the
     * week passed.
     *
     * @param currWeek The week to draw.
     */
    public WeekRenderer(Week currWeek) {
        super();
        this.setOpaque(false);
//        this.setPreferredSize(new Dimension(0, 60 * 24 * GUI.getPixPerMin() + WeekHeader.HEADER_HEIGHT));

        this.currWeek = currWeek;
        this.initializeTimer();
    }

    /** Initializes the timer to render the screen every minute */
    private void initializeTimer() {
        Calendar calendar = Calendar.getInstance();
        int secondsPassed = calendar.get(Calendar.SECOND);
        int millisecondsPassed = calendar.get(Calendar.MILLISECOND);
        new Timer((1000 * (61 - secondsPassed)) + (1001 - millisecondsPassed), this).start();
    }

    /**
     * Updates the week currently being rendered.
     *
     * @param newWeek The new week to render.
     */
    public void updateDisplayedWeek(Week newWeek) {
        this.currWeek = newWeek;
    }

    /**
     * Renders the current week.
     *
     * @param graphics The graphics object.
     */
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        ColourTheme theme = TimeTracker.getColorTheme();
        Graphics2D g = (Graphics2D) graphics;

        this.drawBackground(g, theme);
        this.paintTimeBar(g, theme);
        this.drawDayLines(g, theme);
        this.drawTimeLines(g, theme);
        this.drawCards(g, theme);
        this.drawPastTint(g, theme);
        this.highlightTimeLine(g, theme);
    }

    /**
     * Draws in the panel's background.
     *
     * @param g The graphics object.
     * @param theme The color theme
     */
    private void drawBackground(Graphics2D g, ColourTheme theme) {
        g.setColor(theme.WeekRenderer_BG);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    /**
     * Draws a time bar on the left of the window to
     * show the user the time.
     *
     * @param g The graphics object.
     * @param theme The color theme.
     */
    private void paintTimeBar(Graphics2D g, ColourTheme theme) {
        g.setColor(theme.WeekRenderer_BORDER);
        g.drawRect(0, 0, WeekRenderer.TIME_BAR_WIDTH, this.getHeight());
        g.setFont(new Font("Arial", Font.PLAIN, 15));
        for (int i = 0; i < 24; i++) {
            int yPos = WeekHeader.HEADER_HEIGHT + i * 60 * GUI.getPixPerMin();
            DisplayString time = DisplayString.getStringDimensions(g, Time.formatTimeInMinutes(i * 60));
            g.drawString(time.TEXT, WeekRenderer.TIME_BAR_WIDTH - 10 - time.WIDTH, yPos + (time.HEIGHT/2));
        }
    }

    /**
     * Draws the cards assigned to the current week.
     *
     * @param g The graphics object.
     * @param theme The color theme
     */
    private void drawCards(Graphics2D g, ColourTheme theme) {
        if (this.currWeek.isEmpty()) return;
        int cardWidth = this.calculateCardWidth();
        for (int i = 0; i < 7; i++) {
            Day day = this.currWeek.getDayAtIndex(i);
            int startXPos = this.calculateCardStartXPos(i);
            for (Card c : day.getCards()) {
                int startYPos = this.calculateCardStartYPos(c);
                int cardHeight = this.calculateCardHeight(c);

                if (this.cardsToHighlight.contains(c)) g.setColor(theme.WeekRenderer_CARD_HIGHLIGHT_BG);
                else {
                    if (c.isDraggable()) g.setColor(theme.WeekRenderer_CARD_BG);
                    else g.setColor(theme.WeekRenderer_UNDRAGGABLE_CARD_BG);
                }

                g.fillRect(startXPos, startYPos, cardWidth, cardHeight);
                g.setColor(theme.WeekRenderer_CARD_BORDER);
                g.drawRect(startXPos, startYPos, cardWidth, cardHeight);

                this.displayCardComponents(g, theme, c, startXPos, startYPos, cardWidth, cardHeight);
            }

        }

        this.highlightCards(g, theme, cardWidth);
    }

    /**
     * Displays a card's components like its title, description, etc..
     *
     * @param g The graphics object.
     * @param theme The color theme
     * @param card The card to display information about.
     * @param startXPos The card's left x coordinate.
     * @param startYPos The card's top y coordinate.
     * @param cardWidth The card's width.
     * @param cardHeight The card's height.
     */
    private void displayCardComponents(Graphics2D g, ColourTheme theme, Card card, int startXPos, int startYPos, int cardWidth, int cardHeight) {
        int pixelsUsed = 0;
        int titleHeight = 0;
        Map<Card.Components, Card.FontSize> components = card.getComponentsToDisplay();
        if (components.isEmpty()) return;
        g.setColor(theme.WeekRenderer_CARD_TEXT);
        if (components.containsKey(Card.Components.Title)) {
            Font titleFont = this.getFont(components.get(Card.Components.Title));
            titleFont = new Font(titleFont.getFontName(), Font.BOLD, titleFont.getSize());

            g.setFont(titleFont);
            DisplayString title = DisplayString.truncateStringToFitWidth(g, card.getTicket().getTitle(), cardWidth, true);
            g.drawString(title.TEXT, (int) (startXPos + (cardWidth - title.WIDTH)/2.0), startYPos + title.HEIGHT);
            pixelsUsed += title.HEIGHT;
            titleHeight = title.HEIGHT;
        }
        if (components.containsKey(Card.Components.TicketAndDuration)) {
            g.setFont(this.getFont(components.get(Card.Components.TicketAndDuration)));
            DisplayString duration = DisplayString.truncateStringToFitWidth(g, Time.formatDuration(card.getDurationInMinutes()), cardWidth, true);
            DisplayString ticketTag = DisplayString.truncateStringToFitWidth(g, card.getTicket().toString(), cardWidth - duration.WIDTH, true);

            g.drawString(ticketTag.TEXT, startXPos, startYPos + cardHeight);
            g.drawString(duration.TEXT, startXPos + cardWidth - duration.WIDTH, startYPos + cardHeight);
            pixelsUsed += Math.max(ticketTag.HEIGHT, duration.HEIGHT);
        }
        if (components.containsKey(Card.Components.Description)) {
            g.setFont(this.getFont(components.get(Card.Components.Description)));
            List<DisplayString> descLines = DisplayString.truncateStringToFitWidthAndHeight(g, card.getDescription(), cardWidth, cardHeight - pixelsUsed);
            int yPos = startYPos + titleHeight;
            for (DisplayString line : descLines) {
                yPos += line.HEIGHT + 5;
                g.drawString(line.TEXT, startXPos, yPos);
            }
        }
    }

    /** Returns a Font object based on the enum passed. */
    private Font getFont(Card.FontSize fontSize) {
        return fontSize == Card.FontSize.Big ? WeekRenderer.BIG_FONT : WeekRenderer.SMALL_FONT;
    }

    /**
     * Highlights the cards by drawing them in a different color.
     *
     * @param g The graphics object.
     * @param theme The color theme
     * @param cardWidth The card's width.
     */
    private void highlightCards(Graphics2D g, ColourTheme theme, int cardWidth) {
        if (this.cardsToHighlight.isEmpty()) return;
        for (Card cardToHighlight : this.cardsToHighlight) {
            int startXPos = this.calculateCardStartXPos(cardToHighlight);
            int startYPos = this.calculateCardStartYPos(cardToHighlight);
            int height = this.calculateCardHeight(cardToHighlight);

            g.setColor(theme.WeekRenderer_CARD_HIGHLIGHT_BORDER);
            g.drawRect(startXPos - 1, startYPos - 1, cardWidth + 2, height + 2);
        }
    }

    /** Calculates the card's left side x coordinate. */
    private int calculateCardStartXPos(Card c) {
        return this.calculateCardStartXPos(c.getDate().get(Calendar.DAY_OF_WEEK) - 1);
    }
    private int calculateCardStartXPos(int dayIndex) {
        double dayWidth = (this.getWidth() - WeekRenderer.TIME_BAR_WIDTH) / 7.0;
        return WeekRenderer.TIME_BAR_WIDTH + (int) Math.round(dayWidth * dayIndex) + WeekRenderer.CARD_HORIZONTAL_BUFFER;
    }

    /** Calculates the card's top side y coordinate. */
    private int calculateCardStartYPos(Card c) {
        return WeekHeader.HEADER_HEIGHT + c.getStartTimeInMinutes() * GUI.getPixPerMin();
    }

    /** Calculates the card's width. */
    private int calculateCardWidth() {
        double dayWidth = (this.getWidth() - WeekRenderer.TIME_BAR_WIDTH) / 7.0;
        return (int) (dayWidth - (WeekRenderer.CARD_HORIZONTAL_BUFFER * 2));
    }

    /** Calculates the card's height in pixels. */
    private int calculateCardHeight(Card c) {
        return c.getDurationInMinutes() * GUI.getPixPerMin();
    }

    /**
     * Draws a tint over past days.
     *
     * @param g The graphics object.
     * @param theme The color theme
     */
    private void drawPastTint(Graphics2D g, ColourTheme theme) {
        int when = this.currWeek.weekRelationshipToPresentWeek();
        if (when < 0) {
            g.setColor(theme.WeekRenderer_PAST_BG);
            g.fillRect(WeekRenderer.TIME_BAR_WIDTH, WeekHeader.HEADER_HEIGHT, this.getWidth() - WeekRenderer.TIME_BAR_WIDTH, this.getHeight() - WeekHeader.HEADER_HEIGHT);
        } else if (when == 0) {
            int currDayIndex = WeekManager.getCurrDayIndex();
            double dayWidth = (this.getWidth() - WeekRenderer.TIME_BAR_WIDTH)/7.0;
            this.applyTintToPastDaysOfCurrWeek(g, theme, currDayIndex, dayWidth);
            this.highlightPresentDay(g, theme, currDayIndex, dayWidth);
        }
    }

    /**
     * Applies a tint to all the past days of the current week.
     *
     * @param g The graphics object.
     * @param theme The color theme
     * @param currDayIndex The present day's index.
     * @param dayWidth The day column's width.
     */
    private void applyTintToPastDaysOfCurrWeek(Graphics2D g, ColourTheme theme, int currDayIndex, double dayWidth) {
        int width = (int) Math.round(currDayIndex * dayWidth);
        int height = this.getHeight() - WeekHeader.HEADER_HEIGHT;
        g.setColor(theme.WeekRenderer_PAST_BG);
        g.fillRect(WeekRenderer.TIME_BAR_WIDTH, WeekHeader.HEADER_HEIGHT, width, height);
    }

    /**
     * Highlights the current day and time.
     *
     * @param g The graphics object.
     * @param theme The color theme
     * @param currDayIndex The current day's index.
     * @param dayWidth The day column's width.
     */
    private void highlightPresentDay(Graphics2D g, ColourTheme theme, int currDayIndex, double dayWidth) {
        double startXPos = Math.round(WeekRenderer.TIME_BAR_WIDTH + currDayIndex * dayWidth);
        double endXPos = WeekRenderer.TIME_BAR_WIDTH + (currDayIndex + 1) * ((this.getWidth() - WeekRenderer.TIME_BAR_WIDTH)/7.0);
        int height = Time.getTimeOfDayInMinutes() * GUI.getPixPerMin();

        g.setColor(theme.WeekRenderer_PAST_BG);
        g.fillRect((int) Math.round(startXPos), WeekHeader.HEADER_HEIGHT, (int) Math.round(dayWidth), height);
        g.setColor(theme.WeekRenderer_PRESENT_TIME_INDICATOR);
        g.drawRect((int) Math.round(startXPos), WeekHeader.HEADER_HEIGHT, (int) Math.round(endXPos - startXPos), 24 * 60 * GUI.getPixPerMin());
        g.drawLine((int) Math.round(startXPos), WeekHeader.HEADER_HEIGHT + height, (int) Math.round(startXPos + dayWidth), WeekHeader.HEADER_HEIGHT + height);
    }

    /**
     * Draws vertical lines to separate the individual
     * days i.e. Sunday, Monday, ...
     *
     * @param g The graphics object.
     * @param theme The color theme
     */
    private void drawDayLines(Graphics2D g, ColourTheme theme) {
        g.setColor(theme.WeekRenderer_VERTICAL_LINES);
        for (int i = 0; i < 7; i++) {
            int xPos = (int) Math.round(WeekRenderer.TIME_BAR_WIDTH + (i + 1) * ((this.getWidth() - WeekRenderer.TIME_BAR_WIDTH)/7.0));
            g.drawLine(xPos, WeekHeader.HEADER_HEIGHT, xPos, this.getHeight());
        }
    }

    /**
     * Draws horizontal lines throughout the week
     * to represent the minute gap set by the user.
     *
     * @param g The graphics object.
     * @param theme The color theme
     */
    private void drawTimeLines(Graphics2D g, ColourTheme theme) {
        double dayWidth = (this.getWidth() - WeekRenderer.TIME_BAR_WIDTH)/7.0;
        for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
            int startXPos = (int) Math.round(WeekRenderer.TIME_BAR_WIDTH + dayIndex * dayWidth);
            for (int timeIndex = 0; timeIndex < 24 * 60 / TimeTracker.getMinuteGap(); timeIndex++) {
                int timeInMinutes = timeIndex * TimeTracker.getMinuteGap();
                int yPos = WeekHeader.HEADER_HEIGHT + timeInMinutes * GUI.getPixPerMin();

                if (timeInMinutes % 60 == 0) g.setColor(theme.WeekRenderer_HOUR_INDICATOR);
                else g.setColor(theme.WeekRenderer_HORIZONTAL_LINES);

                g.drawLine(startXPos, yPos, (int) Math.round(startXPos + dayWidth), yPos);
            }
        }
    }

    /**
     * Highlights the time line that the mouse is over.
     *
     * @param g The graphics object.
     * @param theme The color theme
     */
    private void highlightTimeLine(Graphics2D g, ColourTheme theme) {
        if (this.timeBarToHighlight.getFirstItem() == -1) return;

        double dayWidth = (this.getWidth() - WeekRenderer.TIME_BAR_WIDTH)/7.0;
        int startXPos = (int) Math.round(WeekRenderer.TIME_BAR_WIDTH + this.timeBarToHighlight.getFirstItem() * dayWidth);
        int yPos = WeekHeader.HEADER_HEIGHT + this.timeBarToHighlight.getSecondItem() * TimeTracker.getMinuteGap() * GUI.getPixPerMin();

        if (this.highlightLength != 0) {
            startXPos += WeekRenderer.CARD_HORIZONTAL_BUFFER;
            int height = this.highlightLength * TimeTracker.getMinuteGap() * GUI.getPixPerMin();
            g.setColor(theme.WeekRenderer_CARD_CREATION_BG);
            g.fillRect(startXPos, yPos, (int) Math.round(dayWidth - 20), height);
            g.setColor(theme.WeekRenderer_CARD_BORDER);
            g.drawRect(startXPos, yPos, (int) Math.round(dayWidth - 20), height);
        } else {
            int timeInMinutes = this.timeBarToHighlight.getSecondItem() * TimeTracker.getMinuteGap();
            DisplayString timeHighlighted = DisplayString.getStringDimensions(g, Time.formatTimeInMinutes(timeInMinutes));

            int textStartXPosInDay = (int) Math.round(dayWidth/2.0 - timeHighlighted.WIDTH/2.0);
            int textBGStartXPos = startXPos + textStartXPosInDay - WeekRenderer.CARD_HORIZONTAL_BUFFER;
            int textBGEndXPos = startXPos + textStartXPosInDay + timeHighlighted.WIDTH + WeekRenderer.CARD_HORIZONTAL_BUFFER;
            g.setColor(theme.WeekRenderer_BG);
            g.drawLine(textBGStartXPos, yPos, textBGEndXPos, yPos);
            int currWeekPosition = this.currWeek.weekRelationshipToPresentWeek();
            boolean pastWeek = currWeekPosition < 0;
            boolean presentWeek = currWeekPosition == 0;
            boolean pastDay = this.timeBarToHighlight.getFirstItem() < WeekManager.getCurrDayIndex();
            boolean presentDay = this.timeBarToHighlight.getFirstItem() == WeekManager.getCurrDayIndex();
            boolean pastTime = Time.getTimeOfDayInMinutes() > timeInMinutes;
            if (pastWeek || (presentWeek && pastDay) || (presentWeek && presentDay && pastTime)) {
                g.setColor(theme.WeekRenderer_PAST_BG);
                g.drawLine(textBGStartXPos, yPos, textBGEndXPos, yPos);
            }

            g.setFont(WeekRenderer.BIG_FONT);
            g.setColor(theme.WeekRenderer_TIME_HIGHLIGHTED);

            int line1StartXPos = startXPos + WeekRenderer.CARD_HORIZONTAL_BUFFER;
            int line1EndXPos = startXPos + textStartXPosInDay - WeekRenderer.CARD_HORIZONTAL_BUFFER;
            int line2StartXPos = startXPos + textStartXPosInDay + timeHighlighted.WIDTH + WeekRenderer.CARD_HORIZONTAL_BUFFER;
            int line2EndXPos = (int) (startXPos + dayWidth - WeekRenderer.CARD_HORIZONTAL_BUFFER);

            g.drawLine(line1StartXPos, yPos, line1EndXPos, yPos);
            g.drawLine(line2StartXPos, yPos, line2EndXPos, yPos);
            g.drawString(timeHighlighted.TEXT, (int) (startXPos + dayWidth/2.0 - timeHighlighted.WIDTH/2.0), (int) (yPos + timeHighlighted.HEIGHT/2.0));
        }
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e Timer firing event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != this.repaintTimer) {
            this.repaintTimer = new Timer(60000, this);
            this.repaintTimer.start();
            ((Timer) e.getSource()).stop();
        }
        this.repaint();
    }

    /** Returns the current week being displayed. */
    public Week getCurrWeek() {
        return this.currWeek;
    }

    /**
     * Calculates the timeslot start and end times in minutes
     * based on the highlighted timebar.
     *
     * @param timeIndex The starting time bar's index.
     * @param highlightLength The timeslot's length.
     * @return The start and end time in an array.
     */
    private int[] getTimeInMinutes(int timeIndex, int highlightLength) {
        int startTime = timeIndex * TimeTracker.getMinuteGap();
        int endTime = (timeIndex + highlightLength) * TimeTracker.getMinuteGap();
        return new int[]{startTime, endTime};
    }

    /**
     * Returns the card on the day and time provided.
     *
     * @param dayIndex The index of the day to check for the card.
     * @param timeInMinutes The time in minutes at which to check for a card.
     * @return The card object at that time and day.
     */
    public Card getCardAtDayAndTime(int dayIndex, int timeInMinutes) {
        return this.currWeek.getCardAtDayAndTime(dayIndex, timeInMinutes);
    }

    /**
     * Adds a card to be highlighted.
     *
     * @param c The card to be highlighted.
     */
    public void addCardToHighlight(Card c) {
        this.cardsToHighlight.add(c);
    }

    /**
     * Un-highlights a card.
     *
     * @param c The card to unhighlight.
     */
    public void removeCardToHighlight(Card c) {
        this.cardsToHighlight.remove(c);
    }

    /** Un-highlights all highlighted cards. */
    public void clearCardsHighlighted() {
        this.cardsToHighlight.clear();
    }

    /**
     * Changes the time bar that is highlighted.
     *
     * @param dayIndex The new day index.
     * @param timeIndex The new time index.
     * @param highlightLength The new highlighted time bar's length
     */
    public void updateTimeBarHighlighted(int dayIndex, int timeIndex, int highlightLength) {
        if (dayIndex != -1) {
            int[] timeInMinutes = this.getTimeInMinutes(timeIndex, Math.max(highlightLength, 1));
            if (!this.currWeek.canAddCard(dayIndex, timeInMinutes[0], timeInMinutes[1])) {
                if (highlightLength == 0) this.updateTimeBarHighlighted(-1, -1, 0);
                return;
            }
        }
        this.timeBarToHighlight.setFirstItem(dayIndex);
        this.timeBarToHighlight.setSecondItem(timeIndex);
        this.highlightLength = highlightLength;
        this.repaint();
    }

    /**
     * Updates the card's properties if it can be placed at the
     * area specified without colliding with another card.
     *
     * @param card The card to update.
     * @param newDayIndex The new day index for the card.
     * @param timeIndexDiff The difference in time for the card.
     */
    public boolean moveCard(Card card, int newDayIndex, int timeIndexDiff, Card ignoreCollision) {
        int dayDiff = card.getDayIndex() - newDayIndex;
        int minuteDiff = timeIndexDiff * TimeTracker.getMinuteGap();
        if (card.getStartTimeInMinutes() + minuteDiff < 0 ||
            card.getEndTimeInMinutes() + minuteDiff > 24 * 60) {
            minuteDiff = 0;
        }
        int newStartTimeInMinutes = card.getStartTimeInMinutes() + minuteDiff;
        int newEndTimeInMinutes = card.getEndTimeInMinutes() + minuteDiff;

        boolean canAddCardAtNewPos = this.currWeek.canAddCard(newDayIndex, newStartTimeInMinutes, newEndTimeInMinutes, card, ignoreCollision);
        boolean canRepositionCardOnOldDay = this.currWeek.canAddCard(card.getDayIndex(), newStartTimeInMinutes, newEndTimeInMinutes, card, ignoreCollision);
        boolean canRepositionCardOnOldTime = this.currWeek.canAddCard(newDayIndex, card.getStartTimeInMinutes(), card.getEndTimeInMinutes(), card, ignoreCollision);
        if (canRepositionCardOnOldDay && minuteDiff != 0) {
            newDayIndex = card.getDayIndex();
        } else if (canRepositionCardOnOldTime && dayDiff != 0) {
            newStartTimeInMinutes = card.getStartTimeInMinutes();
            newEndTimeInMinutes = card.getEndTimeInMinutes();
            minuteDiff = 0;
        } else if (!canAddCardAtNewPos) return false;

        this.removeIfExists(card);
        card.getDate().set(Calendar.DAY_OF_WEEK, newDayIndex + 1);
        card.setStartTimeInMinutes(newStartTimeInMinutes);
        card.setEndTimeInMinutes(newEndTimeInMinutes);
        this.currWeek.addCard(card);

        return minuteDiff != 0;
    }

    /**
     * Resizes the card passed by incrementing or decrementing
     * its end time.
     *
     * @param card The card to resize.
     * @param timeIndexDiff The time change.
     * @return The actual timeIndexDiff
     */
    public int resizeCard(Card card, int timeIndexDiff) {
        int minuteDiff = timeIndexDiff * TimeTracker.getMinuteGap();
        while (card.getDurationInMinutes() + minuteDiff < Card.DRAGGABLE_CARD_MIN_DURATION) {
            minuteDiff += TimeTracker.getMinuteGap();
            timeIndexDiff++;
        }

        int dayIndex = card.getDate().get(Calendar.DAY_OF_WEEK) - 1;
        int startTime = card.getStartTimeInMinutes();
        int newEndTime = card.getEndTimeInMinutes() + minuteDiff;
        while (!this.currWeek.canAddCard(dayIndex, startTime, newEndTime, card)) {
            newEndTime -= TimeTracker.getMinuteGap();
            timeIndexDiff--;
        }

        if (!this.currWeek.canAddCard(dayIndex, startTime, newEndTime, card)) return 0;
        card.setEndTimeInMinutes(newEndTime);
        this.repaint();
        return timeIndexDiff;
    }

    /** Returns the time index of the highlighted time bar. */
    public int getTimeBarHighlightedTimeIndex() {
        return this.timeBarToHighlight.getSecondItem();
    }

    /**
     * Updates the highlighted time bar's length.
     *
     * @param highlightLength The new length.
     */
    public void updateTimeBarHighlightLength(int highlightLength) {
        this.updateTimeBarHighlighted(this.timeBarToHighlight.getFirstItem(), this.timeBarToHighlight.getSecondItem(), highlightLength);
    }

    /** Displays a popup to confirm the card details and then
     * renders it. */
    public void createCard() {
        if (this.timeBarToHighlight.getFirstItem() == -1) return;
        int timeIndex = this.timeBarToHighlight.getSecondItem();
        Calendar dayDate = this.currWeek.getStartDate();
        dayDate.add(Calendar.DATE, this.timeBarToHighlight.getFirstItem());
        int startTimeInMin = timeIndex * TimeTracker.getMinuteGap();
        int endTimeInMin = (timeIndex + this.highlightLength) * TimeTracker.getMinuteGap();

        CardPopup cp = new CardPopup(dayDate, startTimeInMin, endTimeInMin);
        if (!cp.isSuccessful()) return;

        WeekManager.addCard(new Card(cp.getDate(), cp.getTicket(), cp.getDescription(),
                        cp.getTimeInMinutes(true), cp.getTimeInMinutes(false)));

        this.updateTimeBarHighlighted(-1, -1, 0);
    }

    /** Displays a popup to allow the user to edit the
     * card's detials. */
    public void editCard(Card card) {
        CardPopup cp = new CardPopup(card);
        if (!cp.isSuccessful()) return;

        WeekManager.updateCard(card, cp);
        this.repaint();
    }

    /**
     * Adds a card to the current week.
     *
     * @param c The card to add.
     */
    public void addCard(Card c) {
        this.currWeek.addCard(c);
    }

    /**
     * Removes a card from the current week.
     *
     * @param c The card to remove.
     */
    public void removeCard(Card c) {
        this.currWeek.removeCard(c);
    }

    /**
     * Removes the card from this week if it is present in
     * this week.
     *
     * @param c The card to remove.
     */
    public void removeIfExists(Card c) {
        if (this.checkIfCardIsInWeek(c))
            this.removeCard(c);
    }

    /**
     * Adds a card to this week if it does not already exist.
     *
     * @param c The card to add.
     */
    public void addIfDoesntExist(Card c) {
        if (!this.checkIfCardIsInWeek(c)) {
            this.addCard(c);
        }
    }

    /**
     * Checks if the card passed exists in the current week.
     *
     * @param c The card to check.
     * @return True if the card exists in the current week.
     */
    public boolean checkIfCardIsInWeek(Card c) {
        return this.currWeek.checkForCard(c);
    }

    /** Displays a popup error message. */
    public void displayErrorPopup(String msg) {
        JOptionPane.showMessageDialog(Popup.MAIN_WINDOW, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

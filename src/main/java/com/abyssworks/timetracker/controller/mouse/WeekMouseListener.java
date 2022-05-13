package com.abyssworks.timetracker.controller.mouse;

import com.abyssworks.timetracker.TimeTracker;
import com.abyssworks.timetracker.model.week.Card;
import com.abyssworks.timetracker.view.GUI;
import com.abyssworks.timetracker.view.popup.CardContextMenu;
import com.abyssworks.timetracker.view.week.WeekHeader;
import com.abyssworks.timetracker.view.week.WeekRenderer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * This class listens for and handles mouse events on the
 * week display.
 *
 * @author Dysterio
 */
public class WeekMouseListener extends MouseActivityListener {
    private enum DragType { Move, Resize }

    private final WeekRenderer weekRenderer;

    private DragType dragType;
    private Card cardSelected = null;
    private Integer timeIndexAtMouse = null;
    private Card copyOfCardSelected = null;

    private Card contextMenuCard = null;

    private boolean lastEscStatus = false;
    private boolean mouseInWindow = false;
    private boolean mousePressed = false;
    private MouseEvent lastMotionEvent = null;

    private boolean forceAlt = false;
    private boolean disableListener = false;

    /**
     * Instantiate and assign the listener.
     *
     * @param weekHeader The component to attach the listener to.
     */
    public WeekMouseListener(WeekHeader weekHeader) {
        super(weekHeader);
        this.weekRenderer = weekHeader.getView();
    }

    /**
     * Checks if the mouse position is within the week view.
     *
     * @param e The mouse event.
     * @return True if the mouse's position is valid.
     */
    private boolean isMousePosInvalid(MouseEvent e) {
        if (!this.mouseInWindow) return true;
        return e.getX() <= WeekRenderer.TIME_BAR_WIDTH || e.getY() <= WeekHeader.HEADER_HEIGHT;
    }

    /**
     * Calculate the mouse's true coordinates by taking the scrolled
     * area into consideration.
     *
     * @param e The mouse event.
     * @return The mouse's true coordinates.
     */
    private Point getMousePos(MouseEvent e) {
        int xPos = e.getX() - WeekRenderer.TIME_BAR_WIDTH;
        int yPos = e.getY() + ((WeekHeader)e.getSource()).getViewPosition().y - WeekHeader.HEADER_HEIGHT;
        return new Point(xPos, yPos);
    }

    /**
     * Calculates the day's index based on the mouse's horizontal
     * position.
     *
     * @param xPos The mouse's x coordinate.
     * @return The day's index.
     */
    private int getDayIndexFromMouseXPos(int xPos) {
        double dayWidth = (this.weekRenderer.getWidth() - WeekRenderer.TIME_BAR_WIDTH) / 7.0;
        int dayIndex = (int) (xPos / dayWidth);
        dayIndex = Math.max(dayIndex, 0);
        dayIndex = Math.min(dayIndex, 6);
        return dayIndex;
    }

    /**
     * Calculates the time bar's index based on the mouse's
     * vertical position.
     *
     * @param yPos The mouse's y coordinate.
     * @return The time bar's index.
     */
    private int getTimeIndexFromMouseYPos(int yPos) {
        double timeIndex = (double) yPos / (GUI.getPixPerMin() * TimeTracker.getMinuteGap());
        return (int) Math.floor(timeIndex + 0.3);
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     *
     * @param e The mouse event.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        if (this.disableListener) {
            this.lastMotionEvent = e;
            return;
        }
        if (this.mousePressed) {
            this.mouseDragged(e);
            return;
        }
        // Reset highlights
        this.weekRenderer.updateTimeBarHighlighted(-1, -1, 0);
        this.weekRenderer.clearCardsHighlighted();
        if (this.isMousePosInvalid(e)) return;
        // Get card under mouse
        Point mousePos = this.getMousePos(e);
        double dayWidth = (this.weekRenderer.getWidth() - WeekRenderer.TIME_BAR_WIDTH) / 7.0;
        int xPosInDayColumn = (int) Math.round(mousePos.x % dayWidth);
        int dayIndex = this.getDayIndexFromMouseXPos(mousePos.x);
        int timeInMinutes = mousePos.y / GUI.getPixPerMin();
        Card cardAtMousePos = this.weekRenderer.getCardAtDayAndTime(dayIndex, timeInMinutes);
        // Check if mouse is inside the card
        if (cardAtMousePos != null &&
            xPosInDayColumn >= WeekRenderer.CARD_HORIZONTAL_BUFFER &&
            xPosInDayColumn <= dayWidth - (WeekRenderer.CARD_HORIZONTAL_BUFFER * 2)) {
            // Highlight card
            this.weekRenderer.addCardToHighlight(cardAtMousePos);
        } else {
            // Highlight time index
            int timeIndex = this.getTimeIndexFromMouseYPos(mousePos.y);
            this.weekRenderer.updateTimeBarHighlighted(dayIndex, timeIndex, 0);
        }
        this.updateCursor(mousePos, cardAtMousePos);

        this.lastMotionEvent = e;
    }

    /**
     * Checks if the mouse is outside the edge of the card.
     *
     * @param mouseXPos The mouse's x coordinate.
     * @return True if the mouse cursor is outside the card.
     */
    private boolean outsideCardBounds(int mouseXPos) {
        double dayWidth = (this.weekRenderer.getWidth() - WeekRenderer.TIME_BAR_WIDTH) / 7.0;
        int xPosOnDay = (int) Math.round(mouseXPos % dayWidth);
        return xPosOnDay < WeekRenderer.CARD_HORIZONTAL_BUFFER ||
                xPosOnDay > dayWidth - (WeekRenderer.CARD_HORIZONTAL_BUFFER * 2);
    }

    /**
     * Updates the mouse cursor's appearance depending on its location
     * on the card.
     *
     * @param mousePos The mouse cursor's coordinates.
     * @param card The card the mouse is over.
     */
    private void updateCursor(Point mousePos, Card card) {
        DragType dragType = this.getDragType(mousePos, card);
        this.updateCursor(dragType);
    }

    /**
     * Updates the mouse cursor's appearance to match the drag
     * type.
     *
     * @param dragType The drag type.
     */
    private void updateCursor(DragType dragType) {
        if (dragType == null) {
            this.weekRenderer.setCursor(Cursor.getDefaultCursor());
        } else if (dragType == DragType.Move) {
            this.weekRenderer.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        } else if (dragType == DragType.Resize) {
            this.weekRenderer.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
        }
    }

    /**
     * Calculates the drag event type based on the mouse's
     * vertical position on the card passed.
     *
     * @param mousePos The mouse cursor's coordinate.
     * @param card The card the mouse cursor is over.
     * @return The drag type as the DragType enum.
     */
    private DragType getDragType(Point mousePos, Card card) {
        if (card == null || !card.isDraggable() || this.outsideCardBounds(mousePos.x)) return null;
        int cardEndYPixel = card.getEndTimeInMinutes() * GUI.getPixPerMin();
        if (mousePos.y >= cardEndYPixel - (Card.DRAGGABLE_CARD_MIN_DURATION * GUI.getPixPerMin() / 2)) {
            return DragType.Resize;
        }
        return DragType.Move;
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e The mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // Error Check
        if (this.isMousePosInvalid(e)) return;
        if (this.mousePressed) return;
        // Check for card under mouse
        Point mousePos = this.getMousePos(e);
        int dayIndex = this.getDayIndexFromMouseXPos(mousePos.x);
        int timeInMinutes = mousePos.y / GUI.getPixPerMin();
        Card cardPressed = this.weekRenderer.getCardAtDayAndTime(dayIndex, timeInMinutes);
        if (this.outsideCardBounds(mousePos.x)) cardPressed = null;
        if (SwingUtilities.isRightMouseButton(e)) {
            this.contextMenuCard = cardPressed;
        } else {
            this.mousePressed = true;
            this.cardSelected = cardPressed;
            if (this.cardSelected == null) { // Create new card
                this.weekRenderer.updateTimeBarHighlightLength(1);
            } else { // Edit existing card
                this.dragType = this.getDragType(mousePos, this.cardSelected);
                this.copyOfCardSelected = this.cardSelected.clone();
                this.timeIndexAtMouse = this.getTimeIndexFromMouseYPos(mousePos.y);
            }
        }

    }

    /**
     * Invoked when a mouse button is pressed on a component and then
     * dragged.  <code>MOUSE_DRAGGED</code> events will continue to be
     * delivered to the component where the drag originated until the
     * mouse button is released (regardless of whether the mouse position
     * is within the bounds of the component).
     * <p>
     * Due to platform-dependent Drag&amp;Drop implementations,
     * <code>MOUSE_DRAGGED</code> events may not be delivered during a native
     * Drag&amp;Drop operation.
     *
     * @param e The mouse event.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.disableListener) {
            this.lastMotionEvent = e;
            return;
        }
        if (!this.mousePressed) {
            this.mouseMoved(e);
            return;
        }

        if (this.cardSelected == null) this.dragTimeBar(e);
        else this.dragCard(e);

        this.lastMotionEvent = e;
    }

    /**
     * Updates the length of the card being created based
     * on the mouse's position.
     *
     * @param e The mouse event.
     */
    private void dragTimeBar(MouseEvent e) {
        Point mousePos = this.getMousePos(e);
        int startTimeIndex = this.weekRenderer.getTimeBarHighlightedTimeIndex();
        int currTimeIndex = this.getTimeIndexFromMouseYPos(mousePos.y);
        this.weekRenderer.updateTimeBarHighlightLength(Math.max(currTimeIndex - startTimeIndex, 1));
    }

    /**
     * Handles drag events associated with the card such as
     * drag-move, drag-resize, and drag-duplicate.
     *
     * @param e The mouse event.
     */
    private void dragCard(MouseEvent e) {
        // Check for undraggable card
        if (!this.cardSelected.isDraggable()) {
            this.weekRenderer.displayErrorPopup("Can not drag cards smaller than 4 minutes.");
            this.mouseReleased(e);
            return;
        }

        if (this.dragType == DragType.Move) this.dragMove(e);
        else this.dragResize(e);
        this.weekRenderer.repaint();
    }

    /**
     * Handles the drag-move event. This allows the user to
     * drag the entire card across different days and times.
     *
     * @param e The mouse event.
     */
    private void dragMove(MouseEvent e) {
        Point mousePos = this.getMousePos(e);

        this.moveSelectedCard(mousePos);
        if (e.isAltDown() || this.forceAlt) { // Drag-Duplicated card
            if (this.copyOfCardSelected.checkForCollision(this.cardSelected)) { // Check if the original and duplicate cards are intersecting
                // Display single card at original position
                this.weekRenderer.addIfDoesntExist(this.copyOfCardSelected);
                this.weekRenderer.removeIfExists(this.cardSelected);
                this.weekRenderer.removeCardToHighlight(this.cardSelected);
                this.weekRenderer.addCardToHighlight(this.copyOfCardSelected);
            } else {
                // Display two cards, one at original position and one at mouse's position
                this.weekRenderer.addIfDoesntExist(this.cardSelected);
                this.weekRenderer.addIfDoesntExist(this.copyOfCardSelected);
                this.weekRenderer.addCardToHighlight(this.cardSelected);
                this.weekRenderer.addCardToHighlight(this.copyOfCardSelected);
            }
        } else { // Drag-Move card
            this.weekRenderer.removeIfExists(this.copyOfCardSelected);
            this.weekRenderer.addIfDoesntExist(this.cardSelected);
            this.weekRenderer.addCardToHighlight(this.cardSelected);
            this.weekRenderer.removeCardToHighlight(this.copyOfCardSelected);
        }
    }

    /**
     * Moves the card currently selected to the mouse's
     * location.
     *
     * @param mousePos The mouse's location.
     */
    private void moveSelectedCard(Point mousePos) {
        int dayIndex = this.getDayIndexFromMouseXPos(mousePos.x);
        int newTimeIndex = this.getTimeIndexFromMouseYPos(mousePos.y);
        int timeIndexDiff = newTimeIndex - this.timeIndexAtMouse;

        if (this.weekRenderer.moveCard(this.cardSelected, dayIndex, timeIndexDiff, this.copyOfCardSelected)) {
            this.timeIndexAtMouse += timeIndexDiff;
        }
    }

    /**
     * Handles the drag-resize event. This allows the user to
     * adjust the card's end time by increasing or decreasing it.
     *
     * @param e The mouse event.
     */
    private void dragResize(MouseEvent e) {
        int newTimeIndex = this.getTimeIndexFromMouseYPos(this.getMousePos(e).y);
        int timeIndexDiff = newTimeIndex - this.timeIndexAtMouse;

        int trueTimeIndexDiff = this.weekRenderer.resizeCard(this.cardSelected, timeIndexDiff);
        this.timeIndexAtMouse += trueTimeIndexDiff;
    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e The mouse event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            Point mousePos = this.getMousePos(e);
            int dayIndex = this.getDayIndexFromMouseXPos(mousePos.x);
            int timeInMinutes = mousePos.y / GUI.getPixPerMin();
            Card cardAtMousePos = this.weekRenderer.getCardAtDayAndTime(dayIndex, timeInMinutes);
            if (cardAtMousePos == null) return;
            if (this.outsideCardBounds(mousePos.x)) return;
            if (!cardAtMousePos.equals(this.contextMenuCard)) return;

            this.weekRenderer.setCursor(Cursor.getDefaultCursor());
            CardContextMenu cardContextMenu = new CardContextMenu(this, cardAtMousePos);
            cardContextMenu.show(this.weekRenderer, mousePos.x + WeekRenderer.TIME_BAR_WIDTH, mousePos.y + WeekHeader.HEADER_HEIGHT);
            this.contextMenuCard = null;
            this.disableListener = true;
            if (!this.mousePressed) return;
        }

        this.forceAlt = false;
        this.mousePressed = false;
        this.timeIndexAtMouse = null;
        this.cardSelected = null;
        this.copyOfCardSelected = null;

        this.weekRenderer.createCard();
    }

    /**
     * Sets up the listener to respond to the action the user
     * has selected from the card context menu.
     *
     * @param action The action the user selected
     * @param card The card the user selected
     * @param yOffset The y offset.
     */
    public void setupAction(CardContextMenu.CardActions action, Card card, int yOffset) {
        if (action == CardContextMenu.CardActions.Edit) {
            this.weekRenderer.editCard(card);
        } else if (action == CardContextMenu.CardActions.Delete) {
            this.weekRenderer.removeCard(card);
            this.weekRenderer.removeCardToHighlight(card);
        } else {
            this.mousePressed = true;
            this.dragType = DragType.Move;
            this.cardSelected = card;
            this.copyOfCardSelected = this.cardSelected.clone();
            this.timeIndexAtMouse = this.getTimeIndexFromMouseYPos(this.getMousePos(this.lastMotionEvent).y);
            if (action == CardContextMenu.CardActions.Duplicate) {
                this.forceAlt = true;
            } else if (action == CardContextMenu.CardActions.Resize) {
                this.dragType = DragType.Resize;
                this.timeIndexAtMouse = card.getEndTimeInMinutes() / TimeTracker.getMinuteGap();
                this.lastMotionEvent = new MouseEvent(this.lastMotionEvent.getComponent(),
                        this.lastMotionEvent.getID(),
                        this.lastMotionEvent.getWhen(),
                        this.lastMotionEvent.getModifiers(),
                        this.lastMotionEvent.getX(),
                        this.lastMotionEvent.getY() + yOffset,
                        this.lastMotionEvent.getClickCount(),
                        this.lastMotionEvent.isPopupTrigger(),
                        this.lastMotionEvent.getButton());
            }
        }
        this.updateCursor(this.dragType);
        this.mouseMoved(this.lastMotionEvent);
        this.weekRenderer.repaint();
    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e The mouse event.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        this.mouseInWindow = true;
    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e The mouse event.
     */
    @Override
    public void mouseExited(MouseEvent e) {
        if (this.mousePressed) return;
        this.timeIndexAtMouse = null;
        this.cardSelected = null;
        this.copyOfCardSelected = null;

        this.mouseInWindow = false;
        this.weekRenderer.updateTimeBarHighlighted(-1, -1, 0);
    }

    /**
     * Registers a change in the state of the
     * alt key and calls the necessary functions
     * to handle this change.
     */
    public void altKeyListener(boolean pressed) {
        if (this.cardSelected == null) return;
        this.lastMotionEvent = new MouseEvent(this.lastMotionEvent.getComponent(),
                this.lastMotionEvent.getID(),
                this.lastMotionEvent.getWhen(),
                (pressed ? KeyEvent.ALT_MASK : 0),
                this.lastMotionEvent.getX(),
                this.lastMotionEvent.getY(),
                this.lastMotionEvent.getClickCount(),
                this.lastMotionEvent.isPopupTrigger(),
                this.lastMotionEvent.getButton());
        this.mouseDragged(this.lastMotionEvent);
    }

    /**
     * If the user is in the middle of a drag action
     * this function cancels the action when the esc
     * key is registered.
     */
    public void escKeyListener(boolean pressed) {
        // Error Check
        if (this.lastEscStatus == pressed) return;
        this.lastEscStatus = pressed;
        if (!pressed) return;
        // Reset values
        this.mousePressed = false;
        this.timeIndexAtMouse = null;
        // Reset cards
        if (this.cardSelected != null) {
            this.weekRenderer.removeIfExists(this.cardSelected);
            this.weekRenderer.addIfDoesntExist(this.copyOfCardSelected);
            this.cardSelected = null;
            this.copyOfCardSelected = null;
        }
        this.mouseMoved(this.lastMotionEvent);
    }

    /**
     * Registers a scroll event and calls the necessary mouse
     * events to update the mouse's position based on the
     * amount scrolled.
     */
    public void scrollEvent(ChangeEvent e) {
        if (this.lastMotionEvent == null) return;
        WeekHeader wh = ((WeekHeader) e.getSource());
        Point mousePos = wh.getMousePosition();
        if (mousePos == null) mousePos = (Point) this.lastMotionEvent.getPoint().clone();

        MouseEvent newMouseEvent = new MouseEvent(this.lastMotionEvent.getComponent(),
                this.lastMotionEvent.getID(),
                this.lastMotionEvent.getWhen(),
                this.lastMotionEvent.getModifiers(),
                (int) mousePos.getX(),
                (int) mousePos.getY(),
                this.lastMotionEvent.getClickCount(),
                this.lastMotionEvent.isPopupTrigger(),
                this.lastMotionEvent.getButton());
        if (this.mousePressed) this.mouseDragged(newMouseEvent);
        else this.mouseMoved(newMouseEvent);
        this.weekRenderer.updateTimeBarHighlighted(-1, -1, 0);
    }

    /**
     * Turns on the mouse listener.
     */
    public void enableListener() {
        this.disableListener = false;
    }
}

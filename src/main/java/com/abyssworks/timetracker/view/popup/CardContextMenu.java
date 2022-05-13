package com.abyssworks.timetracker.view.popup;

import com.abyssworks.timetracker.controller.mouse.WeekMouseListener;
import com.abyssworks.timetracker.model.week.Card;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * The following class handles displaying and handling the
 * context menu actions for when the user right clicks on
 * a card.
 *
 * @author Dysterio
 */
public class CardContextMenu extends JPopupMenu implements ActionListener {
    public final Card CARD;
    public enum CardActions { Edit, Move, Resize, Duplicate, Delete };

    private WeekMouseListener weekMouseListener;

    /**
     * Display a card context menu.
     *
     * @param weekMouseListener The week mouse listener assigned to the grid.
     * @param card The card clicked on
     */
    public CardContextMenu(WeekMouseListener weekMouseListener, Card card) {
        this.CARD = card;
        boolean cardDraggable = this.CARD.isDraggable();
        this.weekMouseListener = weekMouseListener;
        for (CardActions contextAction : CardActions.values()) {
            JMenuItem menuItem = new JMenuItem(contextAction.name());
            menuItem.addActionListener(this);
            if (!cardDraggable) {
                if (contextAction == CardActions.Move ||
                    contextAction == CardActions.Resize ||
                    contextAction == CardActions.Duplicate) {
                    menuItem.setEnabled(false);
                }
            }
            this.add(menuItem);
        }

        this.addPopupMenuListener(new PopupMenuListener() {
            /**
             * This method is called before the popup menu becomes visible
             *
             * @param e
             */
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            /**
             * This method is called before the popup menu becomes invisible
             * Note that a JPopupMenu can become invisible any time
             *
             * @param e
             */
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                weekMouseListener.enableListener();
            }

            /**
             * This method is called when the popup menu is canceled
             *
             * @param e
             */
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem a = (JMenuItem) e.getSource();
        CardActions action = CardActions.valueOf(e.getActionCommand());
        int yOffset = (Arrays.asList(CardActions.values()).indexOf(action) + 1) * a.getHeight();
        this.weekMouseListener.setupAction(action, this.CARD, yOffset);
    }
}

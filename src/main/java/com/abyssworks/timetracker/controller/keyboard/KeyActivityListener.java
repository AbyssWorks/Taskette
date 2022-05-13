package com.abyssworks.timetracker.controller.keyboard;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * The following class listens for and handles keyboard events
 * with the use of KeyBindings.
 *
 * @author Dysterio
 */
public class KeyActivityListener {
    private final JComponent component;

    /**
     * Creates an instance of the KeyActivityListener from the component
     * it's assigned to.
     *
     * @param component The JComponent the listener is assigned to.
     */
    public KeyActivityListener(JComponent component) {
        this.component = component;
    }

    /**
     * Binds an action to a key on the user's keyboard.
     *
     * @param keyCode The key to bind.
     * @param keyName The key's name.
     * @param keyResponse The action to perform.
     */
    public void bindKey(int keyCode, String keyName, KeyResponse keyResponse) {
        this.bindKey(keyCode, keyName, 0, keyResponse, keyResponse);
    }

    /**
     * Binds an action to a key on the user's keyboard.
     *
     * @param keyCode The key to bind.
     * @param keyName The key's name.
     * @param keyPressed The action to perform when the
     *                   key is pressed.
     * @param keyReleased The action to perform when the
     *                    key is released.
     */
    public void bindKey(int keyCode, String keyName, int modifier, KeyResponse keyPressed, KeyResponse keyReleased) {
        this.bindKeyPressed(keyCode, keyName + "Pressed", modifier, keyPressed);
        this.bindKeyReleased(keyCode, keyName + "Released", keyReleased);
    }

    /**
     * Binds an action to a key being pressed.
     *
     * @param keyCode The key to bind.
     * @param actionName The action's name.
     * @param keyResponse The action to perform when the
     *                    key is pressed.
     */
    private void bindKeyPressed(int keyCode, String actionName, int modifier, KeyResponse keyResponse) {
        this.component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(keyCode, modifier, false), actionName);
        this.component.getActionMap().put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyResponse.keyAction();
            }
        });
    }

    /**
     * Binds an action to a key being released.
     *
     * @param keyCode The key to bind.
     * @param actionName The action's name.
     * @param keyResponse The action to perform when the
     *                    key is released.
     */
    private void bindKeyReleased(int keyCode, String actionName, KeyResponse keyResponse) {
        this.component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(keyCode, 0, true), actionName);
        this.component.getActionMap().put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyResponse.keyAction();
            }
        });
    }
}

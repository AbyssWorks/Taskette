package com.abyssworks.timetracker.view.popup;

import com.abyssworks.timetracker.controller.keyboard.KeyActivityListener;
import com.abyssworks.timetracker.controller.keyboard.KeyResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The abstract Popup class defines a super-type which contains
 * helpful functions. All popups fall under this class.
 */
public abstract class Popup extends JDialog {
    public static JFrame MAIN_WINDOW;
    protected boolean successful = false;

    /**
     * Creates a new popup.
     */
    public Popup(KeyResponse cancelAction) {
        this.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);

        KeyActivityListener keyActivityListener = new KeyActivityListener(this.getRootPane());
        keyActivityListener.bindKey(KeyEvent.VK_ESCAPE, "esc", () -> {
            cancelAction.keyAction();
            this.dispose();
        });

        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             *
             * @param e
             */
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                cancelAction.keyAction();
            }
        });
    }
    public Popup() {
        this(() -> {});
    }
}

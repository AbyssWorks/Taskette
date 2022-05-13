package com.abyssworks.timetracker.util;

import com.abyssworks.timetracker.TimeTracker;
import com.abyssworks.timetracker.controller.mouse.MouseActivityListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * The following class creates a toggle switch button.
 */
public class JSwitchButton extends JToggleButton {
    public static final double SWITCH_SLIDER_BUFFER = 0.05;
    public static final int FRAME_INTERVAL = 1;
    public static final double DELTA = 0.2;

    private Timer animationTimer;
    private double currDelta = JSwitchButton.DELTA;
    private double switchRatioOnSlider;
    private int horizontalMargin;


    /**
     * Creates a JSwitchButton with its initial state set from
     * the argument passed.
     *
     * @param initialState The switch's initial state.
     */
    public JSwitchButton(boolean initialState) {
        super();
        this.setFocusable(false);
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setSelected(initialState);
        this.setPreferredSize(new Dimension(36, 18));

        this.switchRatioOnSlider = initialState ? 1 : 0;

        new MouseActivityListener(this) {
            @Override
            public void mouseClicked(MouseEvent e) {
                setSelected(!isSelected());
                currDelta = (isSelected()) ? JSwitchButton.DELTA : -JSwitchButton.DELTA;
                restartAnimation();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                double mouseRatioOnSlider = (e.getX() - horizontalMargin) / (getWidth() - (horizontalMargin * 2.0));
                if (mouseRatioOnSlider < 0 || mouseRatioOnSlider > 1) return;
                switchRatioOnSlider = mouseRatioOnSlider;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isSelected()) {
                    if (switchRatioOnSlider <= 0.5) {
                        setSelected(false);
                        currDelta = -JSwitchButton.DELTA;
                    } else currDelta = JSwitchButton.DELTA;
                } else {
                    if (switchRatioOnSlider >= 0.5) {
                        setSelected(true);
                        currDelta = JSwitchButton.DELTA;
                    } else currDelta = -JSwitchButton.DELTA;
                }
                restartAnimation();
            }
        };
    }

    /**
     * Stops any animation in progress, and restarts it based
     * on the switch's current state.
     */
    private void restartAnimation() {
        if (this.animationTimer != null) this.animationTimer.stop();
        this.animationTimer = new Timer(JSwitchButton.FRAME_INTERVAL, timer -> {
            if (this.switchRatioOnSlider <= 0 && this.currDelta < 0 ||
                    this.switchRatioOnSlider >= 1 && this.currDelta > 0) {
                this.switchRatioOnSlider = Math.round(this.switchRatioOnSlider);
                this.animationTimer.stop();
                return;
            }
            this.switchRatioOnSlider += this.currDelta;
            if (this.switchRatioOnSlider < 0) this.switchRatioOnSlider = 0;
            if (this.switchRatioOnSlider > 1) this.switchRatioOnSlider = 1;
            repaint();
        });
        this.animationTimer.setRepeats(true);
        this.animationTimer.start();
    }

    /**
     * Paints the switch.
     *
     * @param graphics The graphics object.
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int red = (int) Math.abs((this.switchRatioOnSlider * TimeTracker.getColorTheme().SettingsPopup_TOGGLE_ON_BG.getRed()) + ((1 - this.switchRatioOnSlider) * TimeTracker.getColorTheme().SettingsPopup_TOGGLE_OFF_BG.getRed()));
        int green = (int) Math.abs((this.switchRatioOnSlider * TimeTracker.getColorTheme().SettingsPopup_TOGGLE_ON_BG.getGreen()) + ((1 - this.switchRatioOnSlider) * TimeTracker.getColorTheme().SettingsPopup_TOGGLE_OFF_BG.getGreen()));
        int blue = (int) Math.abs((this.switchRatioOnSlider * TimeTracker.getColorTheme().SettingsPopup_TOGGLE_ON_BG.getBlue()) + ((1 - this.switchRatioOnSlider) * TimeTracker.getColorTheme().SettingsPopup_TOGGLE_OFF_BG.getBlue()));
        g.setColor(new Color(red, green, blue));
        g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), this.getHeight(), this.getHeight());

        g.setColor(TimeTracker.getColorTheme().SettingsPopup_TOGGLE_SWITCH_BG);
        int switchDiameter = (int) Math.round(this.getHeight() * (1 - JSwitchButton.SWITCH_SLIDER_BUFFER));
        this.horizontalMargin = (int) Math.round(this.getHeight() * JSwitchButton.SWITCH_SLIDER_BUFFER/2.0 + switchDiameter/2.0);
        int xPos = (int) (this.horizontalMargin + ((this.getWidth() - (this.horizontalMargin * 2)) * this.switchRatioOnSlider));
        g.fillOval(xPos - switchDiameter/2, (int) Math.round(this.getHeight() * JSwitchButton.SWITCH_SLIDER_BUFFER/2.0), switchDiameter, switchDiameter);
    }
}

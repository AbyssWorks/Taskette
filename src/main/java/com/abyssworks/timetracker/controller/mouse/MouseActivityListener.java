package com.abyssworks.timetracker.controller.mouse;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * The following abstract class listens for mouse events and
 * executes the corresponding action.
 *
 * @author Dysterio
 */
public abstract class MouseActivityListener implements MouseListener, MouseMotionListener {
    /**
     * Creates an instance of a MouseActivityListener and assigns
     * it to the component passed.
     *
     * @param component The component to assign the listener to.
     */
    public MouseActivityListener(JComponent component) {
        this.assignMouseActivityListener(component);
    }

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     *
     * @param e The mouse event.
     */
    @Override
    public void mouseClicked(MouseEvent e) {}

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e The mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {}

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e The mouse event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {}

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e The mouse event.
     */
    @Override
    public void mouseEntered(MouseEvent e) {}

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e The mouse event.
     */
    @Override
    public void mouseExited(MouseEvent e) {}

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
    public void mouseDragged(MouseEvent e) {}

    /**
     * Invoked when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     *
     * @param e The mouse event.
     */
    @Override
    public void mouseMoved(MouseEvent e) {}

    /**
     * Assigns the MouseActivityListener to the component passed.
     *
     * @param component The component to listen on.
     */
    public void assignMouseActivityListener(JComponent component) {
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
    }

    /**
     * Removes the MouseActivityListener to the component passed.
     *
     * @param component The component to listen on.
     */
    public void removeMouseActivityListener(JComponent component) {
        component.removeMouseListener(this);
        component.removeMouseMotionListener(this);
    }
}

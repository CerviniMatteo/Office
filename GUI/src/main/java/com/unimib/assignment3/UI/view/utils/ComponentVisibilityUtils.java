package com.unimib.assignment3.UI.view.utils;

import javafx.scene.Node;

public final class ComponentVisibilityUtils {

    private ComponentVisibilityUtils() { /* utility */ }

    /**
     * Sets the visibility and managed state of a JavaFX Node. When a node is not visible, it is also not managed,
     * meaning it will not take up space in the layout.
     * @param visible true to make the node visible and managed, false to make it invisible and not managed
     * @param node the JavaFX Node to update
     */
    public static void setVisible(boolean visible, Node node) {
        if (node == null) return;
        node.setVisible(visible);
        node.setManaged(visible);
    }

    /**
     * Make the node invisible and not managed (it won't take up space in the layout)
     * @param node the JavaFX Node to hide
     */
    public static void setDisabled(Node node) {
        if (node == null) return;
        node.setVisible(true);
        node.setDisable(true);
    }

    /**
     * Make the node visible and interactive (not disabled)
     * @param node the JavaFX Node to enable
     */
    public static void setEnabled(Node node) {
        if (node == null) return;
        node.setVisible(true);
        node.setDisable(false);
    }

    /**
     * Make the node visible but not interactive (mouse events will pass through it)
     * @param node the JavaFX Node to set as read-only
     */
    public static void setReadOnly(Node node) {
        if (node == null) return;
        node.setVisible(true);
        node.setDisable(false);
        node.setMouseTransparent(true);
        node.setFocusTraversable(false);
    }

    /**
     * Make the node visible and interactive (mouse events will be processed by it)
     * @param node the JavaFX Node to set as interactive
     */
    public static void setInteractive(Node node) {
        if (node == null) return;
        node.setVisible(true);
        node.setMouseTransparent(false);
        node.setDisable(false);
    }

    /**
     * Toggle the visibility and managed state of the node. If the node is currently visible, it will be hidden and not managed; if it is currently hidden, it will be made visible and managed.
     * @param node the JavaFX Node to toggle
     */
    public static void toggle(Node node) {
        if (node == null) return;
        boolean newState = !node.isVisible();
        node.setVisible(newState);
        node.setManaged(newState);
    }
}
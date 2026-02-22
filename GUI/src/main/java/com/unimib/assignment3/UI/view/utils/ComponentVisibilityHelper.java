package com.unimib.assignment3.UI.view.utils;

import javafx.scene.Node;

public final class ComponentVisibilityHelper {
    private ComponentVisibilityHelper() { /* utility */ }

    public static void setVisible(boolean visible, Node node) {
        if (node == null) return;
        node.setVisible(visible);
        node.setManaged(visible);
    }

    public static void setDisabled(Node node) {
        if (node == null) return;
        node.setVisible(true);
        node.setDisable(true);
    }

    public static void setEnabled(Node node) {
        if (node == null) return;
        node.setVisible(true);
        node.setDisable(false);
    }
}

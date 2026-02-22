package com.unimib.assignment3.UI.view.utils;

import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;

/**
 * Utility to apply/remove a blur effect to siblings of an overlay in a StackPane.
 */
public final class BlurHelper {

    private BlurHelper() {
        // utility class
    }

    public static void applyBlur(Node node, double radius) {
        if (node == null) return;
        node.setEffect(new GaussianBlur(radius));
    }

    public static void removeBlur(Node node) {
        if (node == null) return;
        node.setEffect(null);
    }
}


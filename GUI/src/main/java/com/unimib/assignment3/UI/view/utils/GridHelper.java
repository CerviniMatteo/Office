package com.unimib.assignment3.UI.view.utils;

import com.unimib.assignment3.UI.utils.ImageHelper;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 * Small utility helpers to manage GridPane layouts in the view layer.
 * Provides a consistent way to add nodes/images into a grid with automatic row/col placement
 * and visibility/managed toggles.
 */
public final class GridHelper {

    private GridHelper() { /* utility */ }

    public static void clearGrid(GridPane grid) {
        if (grid == null) return;
        grid.getChildren().clear();
        grid.setVisible(false);
        grid.setManaged(false);
    }

    public static void ensureVisible(GridPane grid, boolean visible) {
        if (grid == null) return;
        grid.setVisible(visible);
        grid.setManaged(visible);
    }

    public static void addNode(GridPane grid, Node node, int cols) {
        if (grid == null || node == null || cols <= 0) return;
        ensureVisible(grid, true);
        int size = grid.getChildren().size();
        int col = size % cols;
        int row = size / cols;
        grid.add(node, col, row);
    }

    public static void addImageBase64(GridPane grid, String base64Image, ImageHelper imageHelper, int size, int cols) {
        if (grid == null || base64Image == null) return;
        ImageHelper helper = imageHelper == null ? new ImageHelper() : imageHelper;
        ImageView imageView = helper.createCircularImageView(helper.createImageFromBase64(base64Image), size);
        addNode(grid, imageView, cols);
    }
}


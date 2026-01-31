package com.unimib.assignment3.UI.utils;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

public class SVGHelper {
    public Node createIcon(String path, double iconeSize, String styleClass) {
        SVGPath svg = new SVGPath();
        svg.setContent(path);
        svg.getStyleClass().add(styleClass);

        StackPane wrapper = new StackPane(svg);
        wrapper.setPrefSize(iconeSize, iconeSize);
        wrapper.setMinSize(iconeSize, iconeSize);
        wrapper.setMaxSize(iconeSize, iconeSize);

        svg.setScaleX(iconeSize);
        svg.setScaleY(iconeSize);

        return wrapper;
    }

}

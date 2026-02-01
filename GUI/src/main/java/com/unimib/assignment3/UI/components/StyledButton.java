package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.utils.ImageHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class StyledButton extends Button {
    private HBox content;
    private final static double ICON_SIZE = 0.03;

    public StyledButton(){
        VBox.setMargin(this, new Insets(10));
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        getStyleClass().add("styled-btn");
        setFocusTraversable(false);
        setPickOnBounds(true);
    }

    public void createDashboardStyledButtonContent(String text, String svgPath, int iconSize) {
        ImageHelper imageHelper = new ImageHelper();
        Node icon = imageHelper.createIcon(svgPath, ICON_SIZE,"dashboard");

        StackPane iconWrapper = new StackPane(icon);
        iconWrapper.setPrefSize(iconSize, iconSize);
        iconWrapper.setMinSize(iconSize, iconSize);
        iconWrapper.setMaxSize(iconSize, iconSize);

        Label label = new Label(text);
        label.getStyleClass().add("dashboard");
        Region spacing = new Region();
        HBox.setHgrow(spacing, Priority.ALWAYS);
        HBox content = new HBox(8, iconWrapper, label, spacing);
        content.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(label, Priority.ALWAYS);
        this.content = content;
        setGraphic(content);
    }

    public HBox getContent() {
        return content;
    }
}


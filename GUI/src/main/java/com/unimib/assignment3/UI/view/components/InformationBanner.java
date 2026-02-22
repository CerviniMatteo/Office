package com.unimib.assignment3.UI.view.components;

import com.unimib.assignment3.UI.model.enums.BannerType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

public class InformationBanner extends HBox {

    public static final int timeInSeconds = 8;

    public InformationBanner(BannerType type, String message) {
        super();
        StackPane.setAlignment(this, Pos.TOP_RIGHT);

        StackPane.setMargin(this, new Insets(20, 0, 0, 0));
        setAlignment(Pos.CENTER);

        setMinWidth(240);
        setPrefWidth(240);
        setMaxWidth(240);

        setMinHeight(60);
        setPrefHeight(60);
        setMaxHeight(60);

        getStyleClass().add("information-banner");

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        HBox.setHgrow(messageLabel, Priority.ALWAYS);

        switch (type) {
            case FAILURE -> getStyleClass().add("failure-banner");
            case SUCCESS -> getStyleClass().add("success-banner");
        }

        getChildren().add(messageLabel);

        HBox.setHgrow(this, Priority.NEVER);
    }
}
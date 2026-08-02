package com.unimib.GUI.UI.view.components.impl.custom;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.Locale;

public class AlertDialogBox extends VBox {
    public AlertDialogBox(String title, String message, Runnable onClose) {
        super(12);

        String normalizedTitle = title == null ? "" : title.toLowerCase(Locale.ROOT);

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add(
                normalizedTitle.contains("success") ? "dialog-title-success" : "dialog-title-failure"
        );

        Label messageLbl = new Label(message);
        messageLbl.setWrapText(true);
        messageLbl.getStyleClass().add("dialog-message");

        Button okBtn = new Button("OK");
        okBtn.getStyleClass().add("styled-btn");
        Runnable closeAction = onClose == null ? () -> {} : onClose;
        okBtn.setOnAction(e -> closeAction.run());

        getChildren().addAll(titleLbl, messageLbl, okBtn);

        // removed dead "alert-dialog-box" class; semi-transparent-bg already supplies the visuals
        getStyleClass().add("semi-transparent-bg");
        setAlignment(Pos.CENTER);
        setFillWidth(false);
        setPadding(new Insets(22));
        setMinWidth(Region.USE_PREF_SIZE);
        setPrefWidth(420);
        setMaxWidth(420);
        setMinHeight(Region.USE_PREF_SIZE);
        setPrefHeight(Region.USE_COMPUTED_SIZE);
        setMaxHeight(Region.USE_PREF_SIZE);
        setOnMouseClicked(javafx.event.Event::consume);
    }
}
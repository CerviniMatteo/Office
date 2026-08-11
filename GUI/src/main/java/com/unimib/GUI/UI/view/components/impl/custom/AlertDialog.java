package com.unimib.GUI.UI.view.components.impl.custom;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Locale;

public class AlertDialog {

    public static Node createAlert(String title, String message, Runnable onClose) {

        VBox dialogBox = new VBox(12);

        String normalizedTitle =
                title == null
                        ? ""
                        : title.toLowerCase(Locale.ROOT);

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add(
                normalizedTitle.contains("success")
                        ? "dialog-title-success"
                        : "dialog-title-failure"
        );

        Label messageLbl = new Label(message);
        messageLbl.setWrapText(true);
        messageLbl.getStyleClass().add("dialog-message");

        Button okBtn = new Button("OK");
        okBtn.getStyleClass().add("styled-btn");

        Runnable closeAction =
                onClose == null ? () -> {} : onClose;

        okBtn.setOnAction(e -> closeAction.run());

        dialogBox.getChildren().addAll(
                titleLbl,
                messageLbl,
                okBtn
        );

        dialogBox.getStyleClass().add("semi-transparent-bg");

        dialogBox.setAlignment(Pos.CENTER);
        dialogBox.setFillWidth(false);
        dialogBox.setPadding(new Insets(22));

        dialogBox.setMinWidth(Region.USE_PREF_SIZE);
        dialogBox.setPrefWidth(420);
        dialogBox.setMaxWidth(420);

        dialogBox.setMinHeight(Region.USE_PREF_SIZE);
        dialogBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        dialogBox.setMaxHeight(Region.USE_PREF_SIZE);

        dialogBox.setOnMouseClicked(
                javafx.event.Event::consume
        );

        return dialogBox;
    }
}

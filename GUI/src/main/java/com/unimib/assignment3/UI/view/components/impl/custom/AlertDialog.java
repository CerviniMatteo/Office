package com.unimib.assignment3.UI.view.components.impl.custom;

import com.unimib.assignment3.UI.FxApplication;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.Objects;

public class AlertDialog{
    public static void showAlert(String title, String message) {
        Runnable showDialog = () -> {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle(title);

            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getStylesheets().add(
                    Objects.requireNonNull(
                            FxApplication.class.getResource("/styles/app.css")
                    ).toExternalForm()
            );

            dialogPane.getStyleClass().add("dialog-pane");

            Label titleLbl = new Label(title);
            titleLbl.getStyleClass().addAll("dialog-title");

            Label messageLbl = new Label(message);
            messageLbl.setWrapText(true);
            messageLbl.getStyleClass().addAll("dialog-message");

            VBox content = new VBox(titleLbl, messageLbl);
            dialogPane.setContent(content);

            ButtonType okType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialogPane.getButtonTypes().add(okType);

            Button okBtn = (Button) dialogPane.lookupButton(okType);
            if (okBtn != null) {
                okBtn.getStyleClass().add("styled-btn");
            }

            dialog.showAndWait();
        };
        if (javafx.application.Platform.isFxApplicationThread()) {
            showDialog.run();
        } else {
            javafx.application.Platform.runLater(showDialog);
        }
    }
}
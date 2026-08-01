package com.unimib.GUI.UI.view.components.impl.custom;

import com.unimib.GUI.FxApplication;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class AlertDialog {

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

            // classe CSS corretta
            dialogPane.getStyleClass().add("custom-dialog");


            Label titleLbl = new Label(title);

            if (title.toLowerCase().contains("success")) {
                titleLbl.getStyleClass().add("dialog-title-success");
            } else {
                titleLbl.getStyleClass().add("dialog-title-failure");
            }


            Label messageLbl = new Label(message);
            messageLbl.setWrapText(true);
            messageLbl.getStyleClass().add("dialog-message");


            VBox content = new VBox(
                    10,
                    titleLbl,
                    messageLbl
            );

            content.getStyleClass().add("dialog-content");

            dialogPane.setContent(content);


            ButtonType okType =
                    new ButtonType(
                            "OK",
                            ButtonBar.ButtonData.OK_DONE
                    );

            dialogPane.getButtonTypes().add(okType);


            Button okBtn =
                    (Button) dialogPane.lookupButton(okType);

            if (okBtn != null) {
                okBtn.getStyleClass().add("styled-btn");
            }


            dialog.showAndWait();
        };


        if (Platform.isFxApplicationThread()) {
            showDialog.run();
        } else {
            Platform.runLater(showDialog);
        }
    }
}
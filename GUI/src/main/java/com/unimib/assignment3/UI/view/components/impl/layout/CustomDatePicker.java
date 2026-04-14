package com.unimib.assignment3.UI.view.components.impl.layout;

import com.unimib.assignment3.UI.view.controller.abstr.DefaultController;
import com.unimib.assignment3.UI.view.controller.impl.layout.CustomDatePickerController;
import com.unimib.assignment3.UI.view.utils.FXMLUtilLoader;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Objects;

public class CustomDatePicker extends VBox {

    private final DefaultController controller;

    public CustomDatePicker() {
        controller = new CustomDatePickerController();

        FXMLUtilLoader.load(
                this,
                controller,
                "/components/DatePicker.fxml",
                null
        );

        getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/styles/app.css")
                ).toExternalForm()
        );
    }

    public LocalDate getSelectedDateTime() {
        if (controller instanceof CustomDatePickerController dateController) {
            return dateController.getSelectedDateTime();
        }
        return null;
    }
}
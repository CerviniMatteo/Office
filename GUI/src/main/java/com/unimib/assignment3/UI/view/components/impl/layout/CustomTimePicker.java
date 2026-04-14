package com.unimib.assignment3.UI.view.components.impl.layout;

import com.unimib.assignment3.UI.model.enums.TimeFormat;
import com.unimib.assignment3.UI.view.controller.impl.layout.CustomTimePickerController;
import com.unimib.assignment3.UI.view.utils.FXMLUtilLoader;
import javafx.scene.layout.HBox;

import java.time.LocalTime;
import java.util.Objects;

public class CustomTimePicker extends HBox {

    private final CustomTimePickerController controller;

    public CustomTimePicker() {
        controller = new CustomTimePickerController();

        FXMLUtilLoader.load(
                this,
                controller,
                "/components/TimePicker.fxml",
                null
        );

        getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/styles/app.css")
                ).toExternalForm()
        );
    }

    public LocalTime getSelectedTime() {
        return controller.getSelectedTime();
    }

    public void setTimeFormat(TimeFormat format) {
        controller.setTimeFormat(format);
    }

    public TimeFormat getTimeFormat() {
        return controller.getTimeFormat();
    }
}
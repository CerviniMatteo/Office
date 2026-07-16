package com.unimib.GUI.UI.view.components.impl.layout;

import com.unimib.GUI.model.enums.TimeFormat;
import com.unimib.GUI.UI.view.controller.impl.layout.custom_date_time.CustomTimePickerController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
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

    /**
     * Resets this time picker to its default state (current system time,
     * clamped to the allowed range). Time format selection is preserved.
     */
    public void clear() {
        controller.clear();
    }
}
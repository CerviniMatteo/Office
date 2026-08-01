package com.unimib.GUI.UI.view.components.impl.layout;

import com.unimib.GUI.UI.view.controller.impl.layout.custom_date_time.CustomDatePickerController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class CustomDatePicker extends VBox {

    private final CustomDatePickerController controller;

    public CustomDatePicker() {
        controller = new CustomDatePickerController();

        FXMLUtilLoader.load(
                this,
                controller,
                "/components/DatePicker.fxml",
                "app.css"
        );
    }

    public LocalDate getSelectedDateTime() {
        return controller.getSelectedDateTime();
    }

    /**
     * Resets this date picker to its default state (today's date),
     * closing the calendar popup if it was open.
     */
    public void clear() {
        controller.clear();
    }
}
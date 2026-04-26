package com.unimib.assignment3.UI.view.components.impl.layout;

import com.unimib.assignment3.UI.view.controller.impl.layout.GanttCalendarController;
import com.unimib.assignment3.UI.view.utils.FXMLUtilLoader;
import javafx.scene.layout.BorderPane;

import java.util.Objects;

public class GanttCalendar extends BorderPane {

    private final GanttCalendarController controller;

    public GanttCalendar() {
        super();
        controller = new GanttCalendarController();
        FXMLUtilLoader.load(this, controller, "/components/GanttCalendar.fxml", null);
        getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/styles/app.css")).toExternalForm()
        );
    }

    public GanttCalendarController getController() {
        return controller;
    }
}

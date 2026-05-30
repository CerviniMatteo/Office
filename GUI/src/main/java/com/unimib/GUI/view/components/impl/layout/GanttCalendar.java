package com.unimib.GUI.view.components.impl.layout;

import com.unimib.GUI.view.controller.impl.layout.GanttCalendarController;
import com.unimib.GUI.view.utils.FXMLUtilLoader;
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

package com.unimib.assignment3.UI.view.components.impl.layout;

import com.unimib.assignment3.UI.view.components.impl.custom.AlertDialog;
import com.unimib.assignment3.UI.view.controller.impl.layout.GanttCalendarController;
import com.unimib.assignment3.UI.view.utils.FXMLUtilLoader;
import com.unimib.assignment3.UI.web_socket_client.TaskWebSocketClientApp;
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
        TaskWebSocketClientApp webSocketClientApp = new TaskWebSocketClientApp(this);
        try {
            webSocketClientApp.start();
        } catch (Exception e) {
            System.out.println("Could not connect to GanttCalendar");
            AlertDialog.showAlert("Error", "Could not connect to GanttCalendar: " + e.getMessage());
        }
    }

    public GanttCalendarController getController() {
        return controller;
    }
}

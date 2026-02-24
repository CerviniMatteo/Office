package com.unimib.assignment3.UI.view.components.impl.container;

import com.unimib.assignment3.UI.FxApplication;
import com.unimib.assignment3.UI.view.components.impl.layout.TaskCreationForm;
import com.unimib.assignment3.UI.view.components.impl.layout.TaskLayout;
import com.unimib.assignment3.UI.view.state.ApplicationStateManager;
import com.unimib.assignment3.UI.web_socket_client.TaskWebSocketClientApp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

import static com.unimib.assignment3.UI.view.components.impl.custom.AlertDialog.showAlert;

public class Home extends StackPane {

    private final TaskLayout tasksLayout;
    private final Button addTaskButton;

    private final ApplicationStateManager applicationStateManager;

    public Home(FxApplication fxApplication) {
        this.applicationStateManager = ApplicationStateManager.getInstance(fxApplication);

        HBox.setHgrow(this, Priority.ALWAYS);
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        tasksLayout = new TaskLayout();

        // Create Add Task button
        addTaskButton = new Button();
        addTaskButton.getStyleClass().add("add-task-btn");

        // Create the SVG icon
        String svgPathContent = "M440-440H200v-80h240v-240h80v240h240v80H520v240h-80v-240Z"; // Your SVG path
        SVGPath addIcon = new SVGPath();
        addIcon.setContent(svgPathContent);
        addIcon.getStyleClass().add("add-icon");

        // Wrap the icon in a StackPane for alignment
        StackPane iconWrapper = new StackPane(addIcon);
        iconWrapper.setPrefSize(24, 24); // Size of the SVG icon
        addTaskButton.setGraphic(iconWrapper);

        // Add the main content and the add button to this pane
        this.getChildren().addAll(tasksLayout, addTaskButton);

        // Ensure initial layout and register this view with the state manager
        setUpDefaultHomeWindow();

        // Initialize WebSocket client
        TaskWebSocketClientApp webSocketClientApp = new TaskWebSocketClientApp(tasksLayout);

        try {
            webSocketClientApp.start();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
        addTaskButton.setOnAction(e ->{
            TaskCreationForm taskCreationForm = new TaskCreationForm();
            // show the task creation form as an overlay on top of current content
            applicationStateManager.replaceWindow(taskCreationForm);

             // Register callback to restore the tasks layout after successful creation
             taskCreationForm.setOnSuccess(() -> {
                 System.out.println("[Home] onSuccess invoked - restoring tasks layout");
                 // Switch back to the tasks layout on the JavaFX Application Thread
                 javafx.application.Platform.runLater(this::setUpDefaultHomeWindow);
             });
         });


    }

    public void setUpDefaultHomeWindow() {
        StackPane.setAlignment(addTaskButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(addTaskButton, new Insets(16));
        applicationStateManager.replaceWindow(this);
    }
}

package com.unimib.assignment3.UI.view.components.abstr;

import com.unimib.assignment3.UI.view.controller.abstr.TaskCardBaseController;
import com.unimib.assignment3.UI.view.state.ApplicationStateManager;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

/**
 * Base class for task cards. Subclasses load a specific FXML layout but
 * share the same controller behavior (TaskCardController).
 */
public abstract class TaskCardBase extends BorderPane {

    protected final TaskCardBaseController controller;

    // Max popup dimensions
    private static final double MAX_WIDTH = 1000;
    private static final double MAX_HEIGHT = 400;

    protected TaskCardBase(String fxmlResource, TaskCardBaseController taskCardBaseController) {
        controller = taskCardBaseController;
        com.unimib.assignment3.UI.view.utils.FXMLUtilLoader.load(this, taskCardBaseController, fxmlResource, "task-card");
    }

    public TaskCardBaseController getController() {
        return controller;
    }

    /**
     * Shows this card in a modal popup using ApplicationStateManager overlay.
     *
     * @param parentNode the parent node to inherit styles from (e.g., detailedWeekView)
     */
    public void showTaskPopup(Parent parentNode) {
        // Set maximum width/height
        this.setMaxWidth(MAX_WIDTH);
        this.setMaxHeight(MAX_HEIGHT);

        // Optional: set preferred size to match max size
        this.setPrefWidth(MAX_WIDTH);
        this.setPrefHeight(MAX_HEIGHT);

        // Copy stylesheets from parent node's scene
        if (parentNode.getScene() != null) {
            this.getStylesheets().addAll(parentNode.getScene().getStylesheets());
        }

        ApplicationStateManager.getInstance().addPopUp(this);

        // Handle close button
        controller.getCloseButton().setOnAction(e -> removeTaskPopup());
    }

    /**
     * Removes the card popup from overlay.
     */
    public void removeTaskPopup() {
        ApplicationStateManager appState = ApplicationStateManager.getInstance();
        appState.removeWindow(this);
    }
}
package com.unimib.GUI.view.controller.impl.layout;

import com.unimib.GUI.model.controller.TaskRestController;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.model.enums.TaskState;
import com.unimib.GUI.model.enums.TimeFormat;
import com.unimib.GUI.view.components.impl.custom.AlertDialog;
import com.unimib.GUI.view.components.impl.layout.CustomDatePicker;
import com.unimib.GUI.view.components.impl.layout.CustomTimePicker;
import com.unimib.GUI.view.controller.abstr.DefaultController;
import com.unimib.GUI.view.utils.ComponentVisibilityUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.time.LocalDateTime;

import static com.unimib.GUI.view.components.impl.custom.AlertDialog.showAlert;

public class TaskCreationFormController implements DefaultController {

    // ================= UI COMPONENTS =================
    @FXML private TextField descriptionField;

    @FXML private HBox header;

    @FXML private Button submitButton;

    @FXML private Pane startPickerContainer;
    @FXML private Pane startTimeBox;
    @FXML private Pane endPickerContainer;
    @FXML private Pane endTimeBox;

    @FXML private StackPane shrinkContainer;

    // ================= INTERNAL COMPONENTS =================
    private CustomDatePicker startDatePicker;
    private CustomDatePicker endDatePicker;
    private CustomTimePicker startTimePicker;
    private CustomTimePicker endTimePicker;

    private ToggleButton timeFormatToggle;
    private Circle toggleThumb;
    private Label ampmLabel;
    private Label h24Label;

    private TaskRestController restController;

    // ================= INITIALIZATION =================
    @FXML
    private void initialize() {
        initPickers();
        initTimeFormatToggle();
        initActions();

        restController = new TaskRestController();
    }

    /**
     * Creates the date/time picker components and injects them into their containers.
     */
    private void initPickers() {
        startDatePicker = new CustomDatePicker();
        endDatePicker = new CustomDatePicker();

        startTimePicker = new CustomTimePicker();
        endTimePicker = new CustomTimePicker();

        startPickerContainer.getChildren().add(startDatePicker);
        startTimeBox.getChildren().add(startTimePicker);
        endPickerContainer.getChildren().add(endDatePicker);
        endTimeBox.getChildren().add(endTimePicker);
    }

    /**
     * Builds the AM/PM <-> 24h toggle switch and wires it into the header.
     */
    private void initTimeFormatToggle() {
        timeFormatToggle = new ToggleButton();
        timeFormatToggle.getStyleClass().add("toggle-switch");

        toggleThumb = new Circle(13);
        toggleThumb.setTranslateX(-15);
        toggleThumb.setStyle("-fx-fill: white;");
        timeFormatToggle.setGraphic(toggleThumb);

        ampmLabel = new Label("AM/PM");
        ampmLabel.getStyleClass().add("insert-text-lbl");

        h24Label = new Label("24h");
        h24Label.getStyleClass().add("insert-text-lbl");
        ComponentVisibilityUtils.setDisabled(h24Label);

        Region leftSpacer = new Region();
        leftSpacer.setPrefWidth(20);
        Region rightSpacer = new Region();
        rightSpacer.setPrefWidth(20);

        timeFormatToggle.selectedProperty().addListener((_, _, isSelected) -> applyTimeFormat(isSelected));

        header.getChildren().add(0, ampmLabel);
        header.getChildren().add(1, leftSpacer);
        header.getChildren().add(2, timeFormatToggle);
        header.getChildren().add(3, rightSpacer);
        header.getChildren().add(4, h24Label);
    }

    /**
     * Applies the given time format to both time pickers and updates the toggle visuals/labels.
     */
    private void applyTimeFormat(boolean is24h) {
        TimeFormat format;

        if (is24h) {
            toggleThumb.setTranslateX(15);
            ComponentVisibilityUtils.setDisabled(ampmLabel);
            ComponentVisibilityUtils.setEnabled(h24Label);
            format = TimeFormat.H24;
        } else {
            toggleThumb.setTranslateX(-15);
            ComponentVisibilityUtils.setEnabled(ampmLabel);
            ComponentVisibilityUtils.setDisabled(h24Label);
            format = TimeFormat.AMPM;
        }

        startTimePicker.setTimeFormat(format);
        endTimePicker.setTimeFormat(format);
    }

    /**
     * Wires up button actions.
     */
    private void initActions() {
        submitButton.setOnAction(_ -> handleSubmit());
    }

    // ================= PUBLIC API =================

    /**
     * Resets the form to its default state:
     * - clears the description field
     * - clears both date pickers
     * - clears both time pickers
     * - resets the time format toggle to AM/PM (default)
     *
     * Safe to call multiple times (e.g. after a successful submit, or when the
     * creation form is reopened) without re-instantiating any component.
     */
    public void clear() {
        descriptionField.clear();

        clearDatePicker(startDatePicker);
        clearDatePicker(endDatePicker);
        clearTimePicker(startTimePicker);
        clearTimePicker(endTimePicker);

        resetTimeFormatToggle();
    }

    private void clearDatePicker(CustomDatePicker picker) {
        if (picker == null) {
            return;
        }
        picker.clear();
    }

    private void clearTimePicker(CustomTimePicker picker) {
        if (picker == null) {
            return;
        }
        picker.clear();
    }

    /**
     * Brings the AM/PM <-> 24h toggle back to its default (AM/PM) state without
     * re-triggering side effects beyond what selectedProperty's listener already does.
     */
    private void resetTimeFormatToggle() {
        if (timeFormatToggle.isSelected()) {
            timeFormatToggle.setSelected(false);
        } else {
            // Toggle already at default; still make sure pickers/labels are in sync.
            applyTimeFormat(false);
        }
    }

    // ================= SUBMIT HANDLING =================
    private void handleSubmit() {

        if (descriptionField.getText() == null || descriptionField.getText().isBlank()) {
            AlertDialog.showAlert("Error", "Description cannot be empty");
            return;
        }

        final TaskDTO taskDTO;
        try {
            taskDTO = buildTaskDTO();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            return;
        }

        Task<String> createTaskTask = restController.createTask(taskDTO);
        createTaskTask.setOnSucceeded(_ -> {
            showAlert("Success", "Task created successfully");
            clear();
        });
        createTaskTask.setOnFailed(_ -> {
            showAlert("Error", "Failed to create task: " + createTaskTask.getException().getMessage());
        });

        new Thread(createTaskTask).start();
    }

    // ================= DTO =================
    private TaskDTO buildTaskDTO() {

        LocalDateTime start = buildStartDateTime();
        LocalDateTime end = buildEndDateTime();

        if (end.isBefore(start)) {
            throw new RuntimeException("End date must be after start date");
        }

        return new TaskDTO(
                null,
                descriptionField.getText(),
                TaskState.TO_BE_STARTED,
                start,
                end,
                null
        );
    }

    private LocalDateTime buildStartDateTime() {
        return LocalDateTime.of(
                startDatePicker.getSelectedDateTime(),
                startTimePicker.getSelectedTime()
        );
    }

    private LocalDateTime buildEndDateTime() {
        return LocalDateTime.of(
                endDatePicker.getSelectedDateTime(),
                endTimePicker.getSelectedTime()
        );
    }
}
package com.unimib.GUI.UI.view.controller.impl.layout;

import com.unimib.GUI.UI.view.components.impl.layout.CustomDatePicker;
import com.unimib.GUI.UI.view.components.impl.layout.CustomTimePicker;
import com.unimib.GUI.UI.view.controller.abstr.DefaultController;
import com.unimib.GUI.UI.view.utils.ComponentVisibilityUtils;
import com.unimib.GUI.UI.viewmodel.impl.TaskViewModel;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.model.enums.TaskState;
import com.unimib.GUI.model.enums.TimeFormat;
import com.unimib.GUI.utils.UserSession;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;
import java.time.LocalDateTime;

public class TaskCreationFormController extends DefaultController {
    @FXML
    private TextField descriptionField;

    @FXML
    private HBox header;

    @FXML
    private Button submitButton;

    @FXML
    private Pane startPickerContainer;

    @FXML
    private Pane startTimeBox;

    @FXML
    private Pane endPickerContainer;

    @FXML
    private Pane endTimeBox;

    @FXML
    private javafx.scene.layout.StackPane shrinkContainer;


    // INTERNAL COMPONENTS

    private CustomDatePicker startDatePicker;
    private CustomDatePicker endDatePicker;

    private CustomTimePicker startTimePicker;
    private CustomTimePicker endTimePicker;

    private ToggleButton timeFormatToggle;

    private Circle toggleThumb;

    private Label ampmLabel;
    private Label h24Label;

    private TaskViewModel viewModel;

    public TaskCreationFormController() {
        super(null);
    }

    @FXML
    public void initialize() {

        viewModel = new TaskViewModel();

        initPickers();
        initTimeFormatToggle();
        initActions();
        observeCreateTask();
    }


    private void observeCreateTask() {

        observeState(
                viewModel.getCreateTaskStateProperty(),
                null,
                task -> {
                    showSuccess("Task created successfully");
                    clear();
                },
                this::showError
        );
    }


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

    private void initTimeFormatToggle() {

        timeFormatToggle = new ToggleButton();
        timeFormatToggle.getStyleClass().add("toggle-switch");

        toggleThumb = new Circle(13);
        toggleThumb.setTranslateX(-15);
        toggleThumb.setStyle("-fx-fill: white;");

        timeFormatToggle.setGraphic(toggleThumb);

        ampmLabel = new Label("AM/PM");
        ampmLabel.getStyleClass().add("form-section-label-big");

        h24Label = new Label("24h");
        h24Label.getStyleClass().add("form-section-label-big");

        ComponentVisibilityUtils.setDisabled(h24Label);

        timeFormatToggle.selectedProperty().addListener(
                (_, _, selected) -> applyTimeFormat(selected)
        );

        HBox switchContainer = new HBox(
                12,
                ampmLabel,
                timeFormatToggle,
                h24Label
        );

        switchContainer.setAlignment(Pos.CENTER);
        HBox.setHgrow(
                switchContainer,
                Priority.ALWAYS
        );

        header.getChildren().addFirst(
                switchContainer
        );
    }


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


    private void initActions() {

        submitButton.setOnAction(
                event -> handleSubmit()
        );
    }


    // CLEAR


    public void clear() {

        descriptionField.clear();

        clearDatePicker(startDatePicker);
        clearDatePicker(endDatePicker);

        clearTimePicker(startTimePicker);
        clearTimePicker(endTimePicker);

        resetTimeFormatToggle();
    }


    private void clearDatePicker(CustomDatePicker picker) {

        if (picker != null) {
            picker.clear();
        }
    }


    private void clearTimePicker(CustomTimePicker picker) {

        if (picker != null) {
            picker.clear();
        }
    }


    private void resetTimeFormatToggle() {

        if (timeFormatToggle.isSelected()) {
            timeFormatToggle.setSelected(false);
        } else {
            applyTimeFormat(false);
        }
    }


    // SUBMIT


    private void handleSubmit() {

        if (!validate(
                descriptionField,
                "Description cannot be empty"
        )) {
            return;
        }

        try {

            TaskDTO taskDTO = buildTaskDTO();

            viewModel.createTask(taskDTO);

        } catch (IllegalArgumentException e) {
            showError(
                    e.getMessage()
            );
        }
    }

    // DTO BUILDER

    private TaskDTO buildTaskDTO() {

        LocalDateTime start = buildStartDateTime();
        LocalDateTime end = buildEndDateTime();

        if (end.isBefore(start)) {

            throw new IllegalArgumentException(
                    "End date must be after start date"
            );
        }

        return new TaskDTO(
                null,
                descriptionField.getText().trim(),
                TaskState.TO_BE_STARTED,
                start,
                end,
                null
        );
    }


    private LocalDateTime buildStartDateTime() {

        if (startDatePicker.getSelectedDateTime() == null) {

            throw new IllegalArgumentException(
                    "Start date required"
            );
        }

        if (startTimePicker.getSelectedTime() == null) {

            throw new IllegalArgumentException(
                    "Start time required"
            );
        }

        return LocalDateTime.of(
                startDatePicker.getSelectedDateTime(),
                startTimePicker.getSelectedTime()
        );
    }


    private LocalDateTime buildEndDateTime() {

        if (endDatePicker.getSelectedDateTime() == null) {

            throw new IllegalArgumentException(
                    "End date required"
            );
        }

        if (endTimePicker.getSelectedTime() == null) {

            throw new IllegalArgumentException(
                    "End time required"
            );
        }

        return LocalDateTime.of(
                endDatePicker.getSelectedDateTime(),
                endTimePicker.getSelectedTime()
        );
    }
}
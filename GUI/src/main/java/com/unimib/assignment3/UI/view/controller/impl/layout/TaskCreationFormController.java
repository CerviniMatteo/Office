package com.unimib.assignment3.UI.view.controller.impl.layout;

import com.unimib.assignment3.UI.model.dto.TaskDTO;
import com.unimib.assignment3.UI.model.enums.TaskState;
import com.unimib.assignment3.UI.model.enums.TimeFormat;
import com.unimib.assignment3.UI.view.components.impl.custom.AlertDialog;
import com.unimib.assignment3.UI.view.components.impl.layout.CustomDatePicker;
import com.unimib.assignment3.UI.view.components.impl.layout.CustomTimePicker;
import com.unimib.assignment3.UI.view.controller.abstr.DefaultController;
import com.unimib.assignment3.UI.view.utils.ComponentVisibilityUtils;
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
import javafx.scene.shape.Rectangle;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static com.unimib.assignment3.UI.view.components.impl.custom.AlertDialog.showAlert;

public class TaskCreationFormController implements DefaultController {

    // ================= UI COMPONENTS =================
    @FXML private TextField descriptionField;

    @FXML private HBox header;

    @FXML private Button submitButton;
    @FXML private Button closeButton;

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

    // ================= CALLBACKS =================
    private Consumer<TaskDTO> onSuccess;
    private Runnable onClose;

    // ================= INITIALIZATION =================
    @FXML
    private void initialize() {

        // Create components
        startDatePicker = new CustomDatePicker();
        endDatePicker = new CustomDatePicker();

        startTimePicker = new CustomTimePicker();
        endTimePicker = new CustomTimePicker();

        // Inject components
        startPickerContainer.getChildren().add(startDatePicker);
        startTimeBox.getChildren().add(startTimePicker);
        endPickerContainer.getChildren().add(endDatePicker);
        endTimeBox.getChildren().add(endTimePicker);

        // Actions
        submitButton.setOnAction(event -> handleSubmit());

        if (closeButton != null) {
            closeButton.setOnAction(event -> {
                if (onClose != null) onClose.run();
            });
        }

        createToggleButton();
    }

    private void createToggleButton(){
        ToggleButton toggle = new ToggleButton();
        toggle.getStyleClass().add("toggle-switch");

        Circle circle = new Circle(13);
        circle.setTranslateX(-15);
        circle.setStyle("-fx-fill: white;");
        toggle.setGraphic(circle);
        Label AMPM  = new Label("AM/PM");
        AMPM.getStyleClass().add("insert-text-lbl");
        Label twenty24h = new Label("24h");
        twenty24h.getStyleClass().add("insert-text-lbl");
        ComponentVisibilityUtils.setDisabled(twenty24h);
        Region region1 = new Region();
        region1.setPrefWidth(20);
        Region region2 = new Region();
        region2.setPrefWidth(20);

        toggle.selectedProperty().addListener((obs, oldVal, isSelected) -> {
            TimeFormat format;

            if (isSelected) {
                circle.setTranslateX(15);
                ComponentVisibilityUtils.setDisabled(AMPM);
                ComponentVisibilityUtils.setEnabled(twenty24h);
                format = TimeFormat.H24;
            } else {
                circle.setTranslateX(-15);
                ComponentVisibilityUtils.setEnabled(AMPM);
                ComponentVisibilityUtils.setDisabled(twenty24h);
                format = TimeFormat.AMPM;
            }
            startTimePicker.setTimeFormat(format);
            endTimePicker.setTimeFormat(format);
        });

        header.getChildren().add(0, AMPM);
        header.getChildren().add(1, region1);
        header.getChildren().add(2, toggle);
        header.getChildren().add(3, region2);
        header.getChildren().add(4, twenty24h);
    }

    private void handleSubmit() {
        try {
            TaskDTO task = buildTaskDTO();
            if(task.description().isEmpty()){
                AlertDialog.showAlert("Error", "Description cannot be empty");
            }
            else if (onSuccess != null) onSuccess.accept(task);
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
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

    // ================= CALLBACKS =================
    public void setOnSuccess(Consumer<TaskDTO> onSuccess) {
        this.onSuccess = onSuccess;
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public Button getCloseButton() {
        return closeButton;
    }
}
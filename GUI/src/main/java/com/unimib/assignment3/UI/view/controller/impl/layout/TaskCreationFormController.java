package com.unimib.assignment3.UI.view.controller.impl.layout;

import com.unimib.assignment3.UI.model.dto.TaskDTO;
import com.unimib.assignment3.UI.model.enums.TaskState;
import com.unimib.assignment3.UI.view.controller.abstr.DefaultController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

import static com.unimib.assignment3.UI.view.components.impl.custom.AlertDialog.showAlert;

public class TaskCreationFormController implements DefaultController {

    // Formatter for displaying dates in dd/MM/yyyy format
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Allowed time range
    private static final LocalTime MIN_TIME = LocalTime.of(7, 0);
    private static final LocalTime MAX_TIME = LocalTime.of(19, 0);

    // ================= UI COMPONENTS =================
    @FXML private Rectangle designRectangle;
    @FXML private TextField descriptionField;

    @FXML private HBox startDateContainer;
    @FXML private Pane startPickerContainer;
    @FXML private TextField startDateField;
    @FXML private TextField startHourField;
    @FXML private TextField startMinuteField;
    @FXML private VBox startTimeBox;

    @FXML private HBox endDateContainer;
    @FXML private Pane endPickerContainer;
    @FXML private Button startCalendarButton;
    @FXML private Button endCalendarButton;
    @FXML private TextField endDateField;
    @FXML private TextField endHourField;
    @FXML private TextField endMinuteField;
    @FXML private HBox endDateBox;

    @FXML private Button submitButton;
    @FXML private Button closeButton;

    // ================= INTERNAL VARIABLES =================
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Node startPickerNode;
    private Node endPickerNode;

    private Consumer<TaskDTO> onSuccess;
    private Runnable onClose;


    // ================= INITIALIZATION =================
    @FXML
    private void initialize() {
        initDatePickers();
        initStartCalendarButton();
        initEndCalendarButton();
        initDefaultDateTime();

        submitButton.setOnAction(event -> handleSubmit());
    }

    // ================= DATE PICKERS =================
    private void initDatePickers() {
        startDatePicker = new DatePicker(LocalDate.now());
        endDatePicker = new DatePicker(LocalDate.now());

        startDatePicker.setOnAction(ev ->
                startDateField.setText(DATE_FORMAT.format(startDatePicker.getValue()))
        );

        endDatePicker.setOnAction(ev ->
                endDateField.setText(DATE_FORMAT.format(endDatePicker.getValue()))
        );
    }

    // Initialize the start calendar button to show/hide start date picker
    private void initStartCalendarButton()
        { startCalendarButton.setOnAction(ev -> { ensureStartPickerNode(); if (isEndPickerVisible()) hideEndPicker(); toggleStartPicker(); }); }
    // Initialize the end calendar button to show/hide end date picker
    private void initEndCalendarButton() { endCalendarButton.setOnAction(ev -> { ensureEndPickerNode(); if (isStartPickerVisible()) hideStartPicker(); toggleEndPicker(); }); }

    // ================= DEFAULT DATE/TIME =================
    /**
     * Initializes default values and formats based on system (AM/PM or 24h).
     */
    private void initDefaultDateTime() {
        LocalDateTime now = LocalDateTime.now();

        startDateField.setText(now.toLocalDate().format(DATE_FORMAT));
        endDateField.setText(now.toLocalDate().format(DATE_FORMAT));

        LocalTime start = clampTime(now.toLocalTime());
        LocalTime end = clampTime(now.toLocalTime());

        if (isSystemUsingAmPm()) {
            startHourField.setText(formatHourAmPm(start));
            endHourField.setText(formatHourAmPm(end));
        } else {
            startHourField.setText(String.format("%02d", start.getHour()));
            endHourField.setText(String.format("%02d", end.getHour()));
        }

        startMinuteField.setText(String.format("%02d", start.getMinute()));
        endMinuteField.setText(String.format("%02d", end.getMinute()));
    }

    /**
     * Formats hour in AM/PM format (e.g., 03PM)
     */
    private String formatHourAmPm(LocalTime time) {
        int hour = time.getHour();
        String amPm = hour >= 12 ? "PM" : "AM";

        hour = hour % 12;
        if (hour == 0) hour = 12;

        return String.format("%02d%s", hour, amPm);
    }

    /**
     * Clamps a LocalTime between MIN_TIME and MAX_TIME
     */
    private LocalTime clampTime(LocalTime time) {
        if (time.isBefore(MIN_TIME)) return MIN_TIME;
        if (time.isAfter(MAX_TIME)) return MAX_TIME;
        return time;
    }

    // ================= SUBMIT =================
    private void handleSubmit() {
        try {
            TaskDTO task = buildTaskDTO();
            if (onSuccess != null) onSuccess.accept(task);
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
        return LocalDateTime.of(startDatePicker.getValue(),
                buildTime(startHourField, startMinuteField));
    }

    private LocalDateTime buildEndDateTime() {
        return LocalDateTime.of(endDatePicker.getValue(),
                buildTime(endHourField, endMinuteField));
    }

    /**
     * Builds LocalTime from UI fields.
     * - Supports AM/PM or 24h input
     * - Converts everything to 24h for backend
     * - Applies clamping at the end
     */
    private LocalTime buildTime(TextField hourField, TextField minuteField) {
        try {
            String hourText = hourField.getText().trim().toLowerCase();
            int minute = Integer.parseInt(minuteField.getText().trim());

            if (minute < 0 || minute > 59) {
                throw new IllegalArgumentException("Invalid minutes");
            }

            int hour;

            if (isSystemUsingAmPm()) {
                boolean isPM = hourText.contains("pm");
                boolean isAM = hourText.contains("am");

                hourText = hourText.replaceAll("[^0-9]", "");
                hour = Integer.parseInt(hourText);

                if (hour < 1 || hour > 12) {
                    throw new IllegalArgumentException("Invalid AM/PM hour");
                }

                // Convert to 24h
                if (isPM && hour != 12) hour += 12;
                if (isAM && hour == 12) hour = 0;

            } else {
                hour = Integer.parseInt(hourText);

                if (hour < 0 || hour > 23) {
                    throw new IllegalArgumentException("Invalid hour");
                }
            }

            // Apply clamping AFTER full time is built
            return clampTime(LocalTime.of(hour, minute));

        } catch (NumberFormatException e) {
            throw new RuntimeException("Hour or minutes not numeric");
        }
    }

    /**
     * Detects if system uses AM/PM format
     */
    public boolean isSystemUsingAmPm() {
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        if (df instanceof SimpleDateFormat) {
            return ((SimpleDateFormat) df).toPattern().contains("a");
        }
        return false;
    }

    // ================= DATE PICKER LOGIC =================
    private void ensureStartPickerNode() {
        if (startPickerNode == null) {
            startPickerNode = new DatePickerSkin(startDatePicker).getPopupContent();
        }
    }

    private void ensureEndPickerNode() {
        if (endPickerNode == null) {
            endPickerNode = new DatePickerSkin(endDatePicker).getPopupContent();
        }
    }

    private boolean isStartPickerVisible() {
        return startPickerContainer.getChildren().contains(startPickerNode);
    }

    private boolean isEndPickerVisible() {
        return endPickerContainer.getChildren().contains(endPickerNode);
    }

    private void toggleStartPicker() {
        if (isStartPickerVisible()) hideStartPicker();
        else showStartPicker();
    }

    private void toggleEndPicker() {
        if (isEndPickerVisible()) hideEndPicker();
        else showEndPicker();
    }

    private void showStartPicker() {
        startPickerContainer.getChildren().add(startPickerNode);
    }

    private void hideStartPicker() {
        startPickerContainer.getChildren().remove(startPickerNode);
    }

    private void showEndPicker() {
        endPickerContainer.getChildren().add(endPickerNode);
    }

    private void hideEndPicker() {
        endPickerContainer.getChildren().remove(endPickerNode);
    }

    // ================= GETTERS =================
    public void setOnSuccess(Consumer<TaskDTO> onSuccess) {
        this.onSuccess = onSuccess;
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    @FXML
    private void handleClose() {
        if (onClose != null) {
            onClose.run();
        }
        // Hide/remove the popup from UI
        closeButton.getScene().getWindow().hide();
    }

    public Button getCloseButton() {
        return closeButton;
    }
}
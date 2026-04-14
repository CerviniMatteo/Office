package com.unimib.assignment3.UI.view.controller.impl.layout;

import com.unimib.assignment3.UI.model.enums.TimeFormat;
import com.unimib.assignment3.UI.view.controller.abstr.DefaultController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

public class CustomTimePickerController implements DefaultController {

    // Allowed time range
    private static final LocalTime MIN_TIME = LocalTime.of(7, 0);
    private static final LocalTime MAX_TIME = LocalTime.of(19, 0);

    // Property representing the current time format (AM/PM or 24h)
    private final ObjectProperty<TimeFormat> timeFormat =
            new SimpleObjectProperty<>();

    // Internal state of the selected time
    private LocalTime currentTime;

    @FXML private TextField hourField;
    @FXML private TextField minuteField;
    @FXML private VBox timeBox;

    @FXML
    private void initialize() {
        // Detect system format and initialize accordingly
        timeFormat.set(isSystemUsingAmPm() ? TimeFormat.AMPM : TimeFormat.H24);

        initDefaultDateTime();
        initListeners();
    }

    /**
     * Initializes the default time using the current system time,
     * clamped within the allowed range.
     */
    private void initDefaultDateTime() {
        LocalDateTime now = LocalDateTime.now();
        currentTime = clampTime(now.toLocalTime());

        updateUI();
    }

    /**
     * Initializes listeners for UI interactions and format changes.
     */
    private void initListeners() {

        // Update UI when time format changes (does NOT re-parse fields)
        timeFormat.addListener((obs, oldVal, newVal) -> updateUI());

        // Update internal state when user finishes editing hour field
        hourField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) updateCurrentTimeFromFields();
        });

        // Update internal state when user finishes editing minute field
        minuteField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) updateCurrentTimeFromFields();
        });
    }

    /**
     * Updates the UI fields based on the internal time state.
     */
    private void updateUI() {
        if (timeFormat.get() == TimeFormat.AMPM) {
            hourField.setText(formatHourAmPm(currentTime));
        } else {
            hourField.setText(String.format("%02d", currentTime.getHour()));
        }

        minuteField.setText(String.format("%02d", currentTime.getMinute()));
    }

    /**
     * Safely updates the internal time from user input fields.
     * If parsing fails, restores previous valid UI state.
     */
    private void updateCurrentTimeFromFields() {
        try {
            currentTime = buildTime(hourField, minuteField);
        } catch (Exception e) {
            updateUI();
        }
    }

    /**
     * Formats hour in AM/PM format (e.g., 02PM).
     */
    private String formatHourAmPm(LocalTime time) {
        int hour = time.getHour();
        String amPm = hour >= 12 ? "PM" : "AM";

        hour = hour % 12;
        if (hour == 0) hour = 12;

        return String.format("%02d%s", hour, amPm);
    }

    /**
     * Ensures the given time is within the allowed range.
     */
    private LocalTime clampTime(LocalTime time) {
        if (time.isBefore(MIN_TIME)) return MIN_TIME;
        if (time.isAfter(MAX_TIME)) return MAX_TIME;
        return time;
    }

    /**
     * Detects whether the system uses AM/PM format.
     */
    public boolean isSystemUsingAmPm() {
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        if (df instanceof SimpleDateFormat) {
            return ((SimpleDateFormat) df).toPattern().contains("a");
        }
        return false;
    }

    /**
     * Robust parsing of user input fields into a LocalTime object.
     */
    private LocalTime buildTime(TextField hourField, TextField minuteField) {
        String hourText = hourField.getText().trim().toLowerCase();
        int minute = Integer.parseInt(minuteField.getText().trim());

        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Invalid minutes");
        }

        int hour;

        if (timeFormat.get() == TimeFormat.AMPM) {

            boolean isPM = hourText.contains("pm");
            boolean isAM = hourText.contains("am");

            // Remove non-numeric characters (e.g., "PM", spaces, etc.)
            hourText = hourText.replaceAll("[^0-9]", "");

            if (hourText.isEmpty()) {
                throw new IllegalArgumentException("Hour empty");
            }

            hour = Integer.parseInt(hourText);

            if (hour < 1 || hour > 12) {
                throw new IllegalArgumentException("Invalid AM/PM hour");
            }

            // Default to AM if no suffix is provided
            if (!isAM && !isPM) isAM = true;

            if (isPM && hour != 12) hour += 12;
            if (isAM && hour == 12) hour = 0;

        } else {
            hour = Integer.parseInt(hourText);

            if (hour < 0 || hour > 23) {
                throw new IllegalArgumentException("Invalid hour");
            }
        }

        return clampTime(LocalTime.of(hour, minute));
    }

    /**
     * Returns the currently selected time.
     */
    public LocalTime getSelectedTime() {
        return currentTime;
    }

    /**
     * Sets the time format (AM/PM or 24h).
     */
    public void setTimeFormat(TimeFormat format) {
        this.timeFormat.set(format);
    }

    /**
     * Returns the current time format.
     */
    public TimeFormat getTimeFormat() {
        return timeFormat.get();
    }

    /**
     * Returns the observable property for time format.
     */
    public ObjectProperty<TimeFormat> timeFormatProperty() {
        return timeFormat;
    }
}
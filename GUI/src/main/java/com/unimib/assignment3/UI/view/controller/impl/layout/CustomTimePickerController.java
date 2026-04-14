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

import static com.unimib.assignment3.UI.model.enums.TimeFormat.AMPM;

public class CustomTimePickerController implements DefaultController {

    private static final LocalTime MIN_TIME = LocalTime.of(7, 0);
    private static final LocalTime MAX_TIME = LocalTime.of(19, 0);

    private final ObjectProperty<TimeFormat> timeFormat =
            new SimpleObjectProperty<>();

    private LocalTime currentTime;

    @FXML private TextField hourField;
    @FXML private TextField minuteField;
    @FXML private VBox timeBox;

    @FXML
    private void initialize() {
        timeFormat.set(isSystemUsingAmPm() ? AMPM : TimeFormat.H24);

        initDefaultDateTime();
        initListeners();
    }

    private void initDefaultDateTime() {
        LocalDateTime now = LocalDateTime.now();
        currentTime = clampTime(now.toLocalTime());
        updateUI();
    }

    private void initListeners() {

        timeFormat.addListener((obs, oldVal, newVal) -> updateUI());

        hourField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) updateCurrentTimeFromFields();
        });

        minuteField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) updateCurrentTimeFromFields();
        });
    }

    private void updateUI() {
        if (timeFormat.get() == AMPM) {
            hourField.setText(formatHourAmPm(currentTime));
        } else {
            hourField.setText(String.format("%02d", currentTime.getHour()));
        }

        minuteField.setText(String.format("%02d", currentTime.getMinute()));
    }

    private void updateCurrentTimeFromFields() {
        try {
            normalizeHourField(); // 🔥 ensure consistent format before parsing
            currentTime = buildTime(hourField, minuteField);
        } catch (Exception e) {
            updateUI();
        }
    }

    /**
     * Normalizes the hour field (e.g. "10pm" → "10 PM")
     */
    private void normalizeHourField() {
        if (timeFormat.get() != AMPM) return;

        String text = hourField.getText();
        if (text == null || text.isEmpty()) return;

        text = text.trim().toUpperCase();

        // remove all spaces/tabs
        text = text.replaceAll("\\s+", "");

        // reinsert proper format if AM/PM exists
        if (text.matches("\\d{1,2}(AM|PM)")) {
            text = text.replaceAll("(\\d{1,2})(AM|PM)", "$1 $2");
        }

        hourField.setText(text);
    }

    private String formatHourAmPm(LocalTime time) {
        int hour = time.getHour();
        String amPm = hour >= 12 ? " PM" : " AM";

        hour = hour % 12;
        if (hour == 0) hour = 12;

        return String.format("%02d%s", hour, amPm);
    }

    private LocalTime clampTime(LocalTime time) {
        if (time.isBefore(MIN_TIME)) return MIN_TIME;
        if (time.isAfter(MAX_TIME)) return MAX_TIME;
        return time;
    }

    public boolean isSystemUsingAmPm() {
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        if (df instanceof SimpleDateFormat) {
            return ((SimpleDateFormat) df).toPattern().contains("a");
        }
        return false;
    }

    private LocalTime buildTime(TextField hourField, TextField minuteField) {
        String hourText = hourField.getText().trim().toLowerCase();
        int minute = Integer.parseInt(minuteField.getText().trim());

        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Invalid minutes");
        }

        int hour;

        if (timeFormat.get() == AMPM) {

            boolean isPM = hourText.contains("pm");
            boolean isAM = hourText.contains("am");

            hourText = hourText.replace(" ", "").replaceAll("[^0-9]", "");

            if (hourText.isEmpty()) {
                throw new IllegalArgumentException("Hour empty");
            }

            hour = Integer.parseInt(hourText);

            if (hour < 1 || hour > 12) {
                throw new IllegalArgumentException("Invalid AM/PM hour");
            }

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

    public LocalTime getSelectedTime() {
        return currentTime;
    }

    public void setTimeFormat(TimeFormat format) {
        this.timeFormat.set(format);
    }

    public TimeFormat getTimeFormat() {
        return timeFormat.get();
    }

    public ObjectProperty<TimeFormat> timeFormatProperty() {
        return timeFormat;
    }
}
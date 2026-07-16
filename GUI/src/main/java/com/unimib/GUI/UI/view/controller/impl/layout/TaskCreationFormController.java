package com.unimib.GUI.UI.view.controller.impl.layout;

import com.unimib.GUI.UI.viewmodel.impl.TaskViewModel;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.model.enums.TaskState;
import com.unimib.GUI.model.enums.TimeFormat;
import com.unimib.GUI.UI.view.components.impl.custom.AlertDialog;
import com.unimib.GUI.UI.view.components.impl.layout.CustomDatePicker;
import com.unimib.GUI.UI.view.components.impl.layout.CustomTimePicker;
import com.unimib.GUI.UI.view.controller.abstr.DefaultController;
import com.unimib.GUI.UI.view.utils.ComponentVisibilityUtils;

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


import static com.unimib.GUI.UI.view.components.impl.custom.AlertDialog.showAlert;


public class TaskCreationFormController implements DefaultController {


    // ================= UI COMPONENTS =================

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
    private StackPane shrinkContainer;



    // ================= INTERNAL COMPONENTS =================

    private CustomDatePicker startDatePicker;
    private CustomDatePicker endDatePicker;

    private CustomTimePicker startTimePicker;
    private CustomTimePicker endTimePicker;


    private ToggleButton timeFormatToggle;

    private Circle toggleThumb;

    private Label ampmLabel;
    private Label h24Label;



    // ================= VIEW MODEL =================

    private TaskViewModel viewModel;



    // ================= INITIALIZATION =================

    @FXML
    public void initialize() {


        viewModel =
                new TaskViewModel();


        initPickers();

        initTimeFormatToggle();

        initActions();

        observeCreateTask();
    }



    private void observeCreateTask() {

        observeState(
                viewModel.createTaskStateProperty(),

                null,

                taskDTO -> {

                    showSuccess(
                            "Task created successfully"
                    );

                    clear();
                },


                this::showError
        );
    }



    /**
     * Creates date/time picker components.
     */
    private void initPickers() {


        startDatePicker =
                new CustomDatePicker();


        endDatePicker =
                new CustomDatePicker();



        startTimePicker =
                new CustomTimePicker();


        endTimePicker =
                new CustomTimePicker();



        startPickerContainer
                .getChildren()
                .add(startDatePicker);


        startTimeBox
                .getChildren()
                .add(startTimePicker);



        endPickerContainer
                .getChildren()
                .add(endDatePicker);


        endTimeBox
                .getChildren()
                .add(endTimePicker);
    }



    /**
     * Creates AM/PM - 24h toggle.
     */
    private void initTimeFormatToggle() {


        timeFormatToggle =
                new ToggleButton();


        timeFormatToggle
                .getStyleClass()
                .add(
                        "toggle-switch"
                );



        toggleThumb =
                new Circle(13);


        toggleThumb.setTranslateX(-15);

        toggleThumb.setStyle(
                "-fx-fill: white;"
        );


        timeFormatToggle
                .setGraphic(toggleThumb);



        ampmLabel =
                new Label("AM/PM");


        ampmLabel
                .getStyleClass()
                .add(
                        "insert-text-lbl"
                );



        h24Label =
                new Label("24h");


        h24Label
                .getStyleClass()
                .add(
                        "insert-text-lbl"
                );



        ComponentVisibilityUtils
                .setDisabled(h24Label);



        Region leftSpacer =
                new Region();

        leftSpacer.setPrefWidth(20);



        Region rightSpacer =
                new Region();

        rightSpacer.setPrefWidth(20);



        timeFormatToggle
                .selectedProperty()
                .addListener(
                        (_, _, selected) ->
                                applyTimeFormat(selected)
                );



        header.getChildren()
                .addAll(
                        0,
                        java.util.List.of(
                                ampmLabel,
                                leftSpacer,
                                timeFormatToggle,
                                rightSpacer,
                                h24Label
                        )
                );
    }



    private void applyTimeFormat(
            boolean is24h
    ) {


        TimeFormat format;


        if(is24h) {


            toggleThumb
                    .setTranslateX(15);


            ComponentVisibilityUtils
                    .setDisabled(ampmLabel);


            ComponentVisibilityUtils
                    .setEnabled(h24Label);



            format =
                    TimeFormat.H24;


        } else {


            toggleThumb
                    .setTranslateX(-15);


            ComponentVisibilityUtils
                    .setEnabled(ampmLabel);


            ComponentVisibilityUtils
                    .setDisabled(h24Label);



            format =
                    TimeFormat.AMPM;
        }



        startTimePicker
                .setTimeFormat(format);


        endTimePicker
                .setTimeFormat(format);
    }



    private void initActions() {


        submitButton
                .setOnAction(
                        _ -> handleSubmit()
                );
    }



    // ================= PUBLIC API =================


    public void clear() {


        descriptionField.clear();


        clearDatePicker(startDatePicker);

        clearDatePicker(endDatePicker);


        clearTimePicker(startTimePicker);

        clearTimePicker(endTimePicker);


        resetTimeFormatToggle();
    }



    private void clearDatePicker(
            CustomDatePicker picker
    ) {

        if(picker != null)
            picker.clear();
    }



    private void clearTimePicker(
            CustomTimePicker picker
    ) {

        if(picker != null)
            picker.clear();
    }



    private void resetTimeFormatToggle() {


        if(timeFormatToggle.isSelected()) {

            timeFormatToggle
                    .setSelected(false);

        } else {

            applyTimeFormat(false);
        }
    }



    // ================= SUBMIT =================


    private void handleSubmit() {


        if(descriptionField
                .getText()
                .isBlank()) {


            AlertDialog.showAlert(
                    "Error",
                    "Description cannot be empty"
            );

            return;
        }



        final TaskDTO taskDTO;


        try {

            taskDTO =
                    buildTaskDTO();


        } catch(Exception e) {


            showAlert(
                    "Error",
                    e.getMessage()
            );

            return;
        }



        viewModel
                .createTask(taskDTO);
    }



    // ================= DTO =================


    private TaskDTO buildTaskDTO() {


        LocalDateTime start =
                buildStartDateTime();



        LocalDateTime end =
                buildEndDateTime();



        if(end.isBefore(start)) {


            throw new RuntimeException(
                    "End date must be after start date"
            );
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


        if(startDatePicker
                .getSelectedDateTime() == null) {


            throw new RuntimeException(
                    "Start date required"
            );
        }



        if(startTimePicker
                .getSelectedTime() == null) {


            throw new RuntimeException(
                    "Start time required"
            );
        }



        return LocalDateTime.of(
                startDatePicker.getSelectedDateTime(),
                startTimePicker.getSelectedTime()
        );
    }



    private LocalDateTime buildEndDateTime() {


        if(endDatePicker
                .getSelectedDateTime() == null) {


            throw new RuntimeException(
                    "End date required"
            );
        }



        if(endTimePicker
                .getSelectedTime() == null) {


            throw new RuntimeException(
                    "End time required"
            );
        }



        return LocalDateTime.of(
                endDatePicker.getSelectedDateTime(),
                endTimePicker.getSelectedTime()
        );
    }
}
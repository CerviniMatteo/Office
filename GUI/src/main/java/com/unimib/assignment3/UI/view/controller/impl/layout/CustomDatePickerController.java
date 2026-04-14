package com.unimib.assignment3.UI.view.controller.impl.layout;

import com.unimib.assignment3.UI.view.controller.abstr.DefaultController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomDatePickerController implements DefaultController {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private HBox dateContainer;
    @FXML private Pane pickerContainer;
    @FXML private TextField dateField;

    @FXML private Button calendarButton;

    private DatePicker datePicker;
    private Node pickerNode;


    @FXML
    private void initialize() {
        datePicker = new DatePicker(LocalDate.now());
        dateField.setText(LocalDate.now().format(DATE_FORMAT));
        datePicker.setOnAction(ev ->
                dateField.setText(DATE_FORMAT.format(datePicker.getValue()))
        );
        pickerNode = new DatePickerSkin(datePicker).getPopupContent();
        calendarButton.setOnAction(ev -> togglePicker());
    }

    public boolean isPickerVisible() {
    return pickerContainer.getChildren().contains(pickerNode);
}

    private void togglePicker() {
        if (isPickerVisible()){
            hidePicker();
        }else{
            showPicker();
        }
    }

    private void showPicker() {
        pickerContainer.getChildren().add(pickerNode);
    }

    private void hidePicker() {
        pickerContainer.getChildren().remove(pickerNode);
    }

    public LocalDate getSelectedDateTime(){
        return datePicker.getValue();
    }

    public void popPicker() {
        if(isPickerVisible()){
            hidePicker();
        }
    }
}

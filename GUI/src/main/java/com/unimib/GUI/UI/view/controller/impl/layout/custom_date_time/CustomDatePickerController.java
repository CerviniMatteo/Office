package com.unimib.GUI.UI.view.controller.impl.layout.custom_date_time;

import com.unimib.GUI.UI.view.controller.abstr.Clearable;
import com.unimib.GUI.UI.view.controller.abstr.DefaultController;
import com.unimib.GUI.utils.UserSession;
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

public class CustomDatePickerController extends DefaultController implements Clearable {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private HBox dateContainer;
    @FXML private Pane pickerContainer;
    @FXML private TextField dateField;

    @FXML private Button calendarButton;

    private DatePicker datePicker;
    private Node pickerNode;

    public CustomDatePickerController() {
        super(null);
    }


    @FXML
    private void initialize() {
        datePicker = new DatePicker(LocalDate.now());
        dateField.setText(LocalDate.now().format(DATE_FORMAT));
        datePicker.setOnAction(_ ->
                dateField.setText(DATE_FORMAT.format(datePicker.getValue()))
        );
        pickerNode = new DatePickerSkin(datePicker).getPopupContent();
        calendarButton.setOnAction(_ -> togglePicker());
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

    /**
     * Resets the date picker to today's date, updates the visible text
     * field accordingly, and closes the calendar popup if it was open.
     */
    @Override
    public void clear() {
        popPicker();

        LocalDate today = LocalDate.now();
        datePicker.setValue(today);
        dateField.setText(today.format(DATE_FORMAT));
    }
}
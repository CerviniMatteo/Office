package com.unimib.GUI.UI.view.controller.impl.base;

import com.unimib.GUI.UI.view.controller.abstr.DefaultController;
import com.unimib.GUI.model.dto.WorkerInfoDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.function.Consumer;

public class UnmatchedEmployeeBarController implements DefaultController {

    @FXML
    private Label employeeDetailsLabel;

    @FXML
    private Button createButton;

    private WorkerInfoDTO userInfo;
    private Consumer<Long> onCreateClickListener;

    @FXML
    public void initialize() {
        createButton.setOnAction(event -> {
            if (userInfo != null && onCreateClickListener != null) {
                onCreateClickListener.accept(userInfo.userId());
            }
        });
    }

    public void setUserData(WorkerInfoDTO userInfo) {
        this.userInfo = userInfo;
        if (employeeDetailsLabel != null && userInfo != null) {
            employeeDetailsLabel.setText(userInfo.personalDetails());
        }
    }

    public void setOnCreateClick(Consumer<Long> listener) {
        this.onCreateClickListener = listener;
    }

    public WorkerInfoDTO getUserInfo() {
        return userInfo;
    }
}
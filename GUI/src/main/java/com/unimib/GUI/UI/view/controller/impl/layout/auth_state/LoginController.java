package com.unimib.GUI.UI.view.controller.impl.layout.auth_state;

import com.unimib.GUI.UI.view.controller.abstr.AuthController;
import com.unimib.GUI.UI.viewmodel.impl.AuthViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController extends AuthController {

    @FXML
    private Label insertEmailLabel;

    @FXML
    private TextField inputForm;

    @FXML
    private Button submitButton;

    private AuthViewModel viewModel;


    @FXML
    private void initialize() {

        viewModel = new AuthViewModel();

        observeState(
                viewModel.getLoginState(),
                employeeId -> {

                    saveEmployeeId(employeeId);

                    showSuccess("Login successful");

                    goToTaskContainer();
                },
                this::showError
        );


        if (inputForm != null) {
            inputForm.setText("matteo.cervini@example.com");
        }

        if (submitButton != null) {
            submitButton.setOnAction(
                    _ -> handleSubmit(inputForm)
            );
        }
    }


    private void handleSubmit(TextField input) {

        if (!validate(input, "Input mail cannot be empty")) {
            return;
        }

        viewModel.login(input.getText());
    }
}
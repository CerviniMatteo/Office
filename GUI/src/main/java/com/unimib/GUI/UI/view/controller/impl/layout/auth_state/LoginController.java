package com.unimib.GUI.UI.view.controller.impl.layout.auth_state;

import com.unimib.GUI.UI.view.controller.abstr.AuthController;
import com.unimib.GUI.UI.viewmodel.impl.AuthViewModel;
import com.unimib.GUI.utils.UserSession;
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

    @FXML
    private Button goToRegisterButton;

    private AuthViewModel viewModel;

    private Runnable switchToRegistration;

    public LoginController(UserSession userSession) {
        super(userSession);
    }

    public void setSwitchToRegistration(Runnable switchToRegistration) {
        this.switchToRegistration = switchToRegistration;
    }

    @FXML
    private void initialize() {
        viewModel = new AuthViewModel();

        observeState(
                viewModel.getLoginState(),
                employeeId -> {

                    saveEmployeeId(employeeId);
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

        goToRegisterButton.setOnAction(
                _ -> {
                    if (switchToRegistration != null) {
                        switchToRegistration.run();
                    }
                }
        );
    }

    private void handleSubmit(TextField input) {

        if (!validate(input, "Input mail cannot be empty")) {
            return;
        }

        viewModel.login(input.getText());
    }
}
package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.FxApplication;
import com.unimib.assignment3.UI.controller.rest.LoginController;
import com.unimib.assignment3.UI.utils.SessionManagerSingleton;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import static com.unimib.assignment3.UI.components.AlertDialog.showAlert;

public class Login extends VBox {

    FxApplication fxApplication;
    private final TextArea inputForm;
    private final StyledButton submitButton;

    public Login(FxApplication fxApplication) {
        super(8);
        this.fxApplication = fxApplication;
        Label instertEmailLabel = new Label("Insert your email to login");
        instertEmailLabel.getStyleClass().add("insert-email-lbl");
        instertEmailLabel.setMaxWidth(Double.MAX_VALUE);
        instertEmailLabel.setAlignment(Pos.CENTER);
        inputForm = new TextArea();
        inputForm.getStyleClass().add("input-form");
        submitButton = new StyledButton();
        submitButton.setText("SUBMIT");
        setupLoginAction();
        inputForm.setText("matteo.cervini@example.com");

        getChildren().addAll(instertEmailLabel, inputForm, submitButton);
        setAlignment(Pos.CENTER);
    }

    private void setupLoginAction() {
        submitButton.setOnAction(event -> {
            String email = inputForm.getText();
            try {
                Task<String> loginTask = LoginController.login(email);
                loginTask.setOnSucceeded(ev->{
                    Long response = Long.parseLong(loginTask.getValue().replaceAll("\n", "").replaceAll(" ", "").trim());
                    SessionManagerSingleton.getInstance().setAttribute("employeeId", response);
                    fxApplication.afterLogin("Login successful");
                });
                loginTask.setOnFailed(ev-> fxApplication.failedLogin("Login failed\nEmail not found"));

                new Thread(loginTask).start();
            }catch (Exception e){
                showAlert("Error", e.getMessage());
            }
        });
    }
}
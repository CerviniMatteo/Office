package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.FxApplication;
import com.unimib.assignment3.UI.rest.LoginRest;
import com.unimib.assignment3.UI.utils.SessionManagerSingleton;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import static com.unimib.assignment3.UI.utils.AlertDialog.showAlert;

public class Login extends VBox {

    FxApplication fxApplication;
    private final TextArea inputForm;
    private final StyledButton submitButton;

    public Login(FxApplication fxApplication) {
        super(8);
        this.fxApplication = fxApplication;
        Label instertEmailLabel = new Label("Insert your email to login:");
        instertEmailLabel.getStyleClass().add("insert-email-lbl");
        inputForm = new TextArea();
        inputForm.getStyleClass().add("input-form");
        submitButton = new StyledButton();
        submitButton.setText("SUBMIT");
        setupLoginAction();

        getChildren().addAll(instertEmailLabel, inputForm, submitButton);
        setAlignment(Pos.CENTER);
    }

    private void setupLoginAction() {
        submitButton.setOnAction(event -> {
            String email = inputForm.getText();
            try {
                Task<String> loginTask = LoginRest.login(email);
                loginTask.setOnSucceeded(ev->{
                    String response = loginTask.getValue();
                    SessionManagerSingleton.getInstance().setAttribute("worker", response);
                    showAlert("Success", "Login successful! Your Worker ID is: " + response);
                    fxApplication.afterLogin();
                });
                loginTask.setOnFailed(ev-> showAlert("Error", "Login failed due to an unexpected error."));

                new Thread(loginTask).start();
            }catch (Exception e){
                showAlert("Error", e.getMessage());
            }
        });
    }
}
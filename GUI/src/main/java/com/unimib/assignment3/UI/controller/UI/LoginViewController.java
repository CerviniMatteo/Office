package com.unimib.assignment3.UI.controller.UI;

import com.unimib.assignment3.UI.FxApplication;
import com.unimib.assignment3.UI.controller.rest.LoginController;
import com.unimib.assignment3.UI.utils.SessionManagerSingleton;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import static com.unimib.assignment3.UI.components.AlertDialog.showAlert;

public class LoginViewController {

    @FXML
    private Label insertEmailLabel;

    @FXML
    private TextField inputForm;

    @FXML
    private Button submitButton;

    private FxApplication fxApplication;

    public void setFxApplication(FxApplication fxApplication){
        this.fxApplication = fxApplication;
    }

    @FXML
    private void initialize(){
        if(inputForm != null) inputForm.setText("matteo.cervini@example.com");

        if(submitButton != null) submitButton.setOnAction(event -> {
            String email = inputForm.getText();
            try {
                Task<String> loginTask = LoginController.login(email);
                loginTask.setOnSucceeded(ev->{
                    try{
                        Long response = Long.parseLong(loginTask.getValue().replaceAll("\\n", "").replaceAll(" ", "").trim());
                        SessionManagerSingleton.getInstance().setAttribute("employeeId", response);
                        if(fxApplication != null) fxApplication.afterLogin("Login successful");
                    }catch (Exception ex){
                        showAlert("Error", ex.getMessage());
                    }
                });
                loginTask.setOnFailed(ev-> {
                    if(fxApplication != null) fxApplication.failedLogin("Login failed\nEmail not found");
                });

                new Thread(loginTask).start();
            }catch (Exception e){
                showAlert("Error", e.getMessage());
            }
        });
    }
}

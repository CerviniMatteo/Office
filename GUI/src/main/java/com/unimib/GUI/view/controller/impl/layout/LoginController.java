package com.unimib.GUI.view.controller.impl.layout;

import com.unimib.GUI.view.components.impl.custom.InformationBanner;
import com.unimib.GUI.model.controller.LoginRestController;
import com.unimib.GUI.view.components.impl.layout.TaskContainer;
import com.unimib.GUI.view.controller.abstr.DefaultController;
import com.unimib.GUI.view.state.ApplicationStateManager;
import com.unimib.GUI.utils.SessionManagerSingleton;
import com.unimib.GUI.model.enums.BannerType;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import static com.unimib.GUI.utils.StringHelper.replaceSpaces;
import static com.unimib.GUI.view.components.impl.custom.AlertDialog.showAlert;
import static com.unimib.GUI.view.components.impl.custom.InformationBanner.timeInSeconds;
import static com.unimib.GUI.view.utils.StringHelper.hashString;

/**
 * Controller for the Login view. Handles UI initialization, layout clipping and login submission.
 */
public class LoginController implements DefaultController {

    @FXML
    private Label insertEmailLabel;

    @FXML
    private TextField inputForm;

    @FXML
    private Button submitButton;

    /**
     * Initialize UI components and event handlers.
     */
    @FXML
    private void initialize() {
        if(inputForm != null)
            inputForm.setText("matteo.cervini@example.com");

        if(submitButton != null)
            submitButton.setOnAction(_ -> handleSubmit(inputForm));
    }

    /**
     * Handle the login submission flow: run login task and show banners/dialogs on result.
     * @param input the TextField containing the email to submit
     */
    private void handleSubmit(TextField input) {
        if (input == null) {
            showAlert("Error", "Input field not found");
            return;
        }

        String email = hashString(input.getText());

        try {
            LoginRestController restController = new LoginRestController();
            Task<String> loginTask = restController.login(email);
            ApplicationStateManager stateManager = ApplicationStateManager.getInstance();
            loginTask.setOnSucceeded(_ -> {
                try {
                    Long response = Long.parseLong(replaceSpaces(loginTask.getValue()));
                    SessionManagerSingleton.getInstance().setAttribute("employeeId", response);
                    stateManager.replaceWindow(new TaskContainer());

                    showBanner(BannerType.SUCCESS, "Login successful");
                } catch (Exception ex){
                    showAlert("Error", ex.getMessage());
                }
            });

            loginTask.setOnFailed(_ -> {
                    showBanner(BannerType.FAILURE, "Login failed\nEmail not found");
            });

            new Thread(loginTask).start();
        } catch (Exception e){
            showAlert("Error", e.getMessage());
        }
    }

    private void showBanner(BannerType type, String message) {
        InformationBanner banner = new InformationBanner(type, message);
        ApplicationStateManager stateManager = ApplicationStateManager.getInstance();
        stateManager.addWindow(banner);
        PauseTransition pause = new PauseTransition(Duration.seconds(timeInSeconds));
        pause.setOnFinished(p -> stateManager.removeWindow(banner));
        pause.play();
    }
}
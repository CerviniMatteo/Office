package com.unimib.assignment3.UI.view.controller;

import com.unimib.assignment3.UI.FxApplication;
import com.unimib.assignment3.UI.view.components.Home;
import com.unimib.assignment3.UI.view.components.InformationBanner;
import com.unimib.assignment3.UI.model.controller.LoginController;
import com.unimib.assignment3.UI.view.state.ApplicationStateManager;
import com.unimib.assignment3.UI.utils.SessionManagerSingleton;
import com.unimib.assignment3.UI.model.enums.BannerType;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import static com.unimib.assignment3.UI.utils.StringHelper.replaceSpaces;
import static com.unimib.assignment3.UI.view.components.AlertDialog.showAlert;
import static com.unimib.assignment3.UI.view.components.InformationBanner.timeInSeconds;

/**
 * Controller for the Login view. Handles UI initialization, layout clipping and login submission.
 */
public class LoginViewController {

    @FXML
    private Label insertEmailLabel;

    @FXML
    private TextField inputForm;

    @FXML
    private Button submitButton;

    private FxApplication application;

    private ApplicationStateManager stateManager;

    /**
     * Sets the application instance for the controller.
     * @param fxApplication the main application instance
     */
    public void setFxApplication(FxApplication fxApplication){
        this.application = fxApplication;
    }

    /**
     * Initialize UI components and event handlers.
     */
    @FXML
    private void initialize() {
        if(inputForm != null)
            inputForm.setText("matteo.cervini@example.com");

        if(submitButton != null)
            submitButton.setOnAction(event -> handleSubmit(inputForm));

        stateManager = ApplicationStateManager.getInstance(application);
    }

    /**
     * Attach handlers to controls looked up from a root node.
     * @param root root node where controls will be looked up
     */
    public void attachHandlers(Parent root) {
        try {
            final TextField input = (TextField) root.lookup("#inputForm");
            final Button submit = (Button) root.lookup("#submitButton");

            if (submit != null) submit.setOnAction(event -> handleSubmit(input));
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
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

        String email = input.getText();
        try {
            Task<String> loginTask = LoginController.login(email);

            loginTask.setOnSucceeded(ev -> {
                try {
                    Long response = Long.parseLong(replaceSpaces(loginTask.getValue()));
                    SessionManagerSingleton.getInstance().setAttribute("employeeId", response);

                    if(application != null) {
                        stateManager.replaceWindow(new Home(application));

                        showBanner(BannerType.SUCCESS, "Login successful");

                    }
                } catch (Exception ex){
                    showAlert("Error", ex.getMessage());
                }
            });

            loginTask.setOnFailed(ev -> {
                if(application != null) {
                    showBanner(BannerType.FAILURE, "Login failed\nEmail not found");
                }
            });

            new Thread(loginTask).start();
        } catch (Exception e){
            showAlert("Error", e.getMessage());
        }
    }

    private void showBanner(BannerType type, String message) {
        InformationBanner banner = new InformationBanner(type, message);

        stateManager.addWindow(banner);
        PauseTransition pause = new PauseTransition(Duration.seconds(timeInSeconds));
        pause.setOnFinished(p -> stateManager.removeWindow(banner));
        pause.play();
    }
}
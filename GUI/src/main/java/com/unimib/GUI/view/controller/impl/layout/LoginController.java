package com.unimib.GUI.view.controller.impl.layout;

import com.unimib.GUI.model.enums.BannerType;
import com.unimib.GUI.view.components.impl.custom.InformationBanner;
import com.unimib.GUI.view.components.impl.layout.TaskContainer;
import com.unimib.GUI.view.controller.abstr.DefaultController;
import com.unimib.GUI.view.state.ApplicationStateManager;
import com.unimib.GUI.utils.SessionManagerSingleton;
import com.unimib.GUI.view.viewmodel.LoginViewModel;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import static com.unimib.GUI.utils.StringHelper.replaceSpaces;
import static com.unimib.GUI.view.components.impl.custom.AlertDialog.showAlert;

/**
 * Controller for the Login view.
 *
 * <p>This controller is responsible only for managing the user interface.
 * It handles user interactions, delegates the authentication workflow to the
 * {@link LoginViewModel}, and updates the UI according to the operation result.</p>
 *
 * <p>Business logic, networking and data retrieval are intentionally delegated
 * to the ViewModel and Repository layers.</p>
 */
public class LoginController implements DefaultController {

    /**
     * Label displayed above the email input field.
     */
    @FXML
    private Label insertEmailLabel;

    /**
     * Text field used to insert the user's email.
     */
    @FXML
    private TextField inputForm;

    /**
     * Button used to submit the login request.
     */
    @FXML
    private Button submitButton;

    /**
     * ViewModel responsible for the login workflow.
     */
    private LoginViewModel viewModel;

    /**
     * Initializes the view components and registers the event handlers.
     *
     * <p>This method is automatically invoked by the {@code FXMLLoader}
     * after the FXML elements have been injected.</p>
     */
    @FXML
    private void initialize() {

        viewModel = new LoginViewModel();

        if (inputForm != null) {
            inputForm.setText("matteo.cervini@example.com");
        }

        if (submitButton != null) {
            submitButton.setOnAction(_ -> handleSubmit(inputForm));
        }
    }

    /**
     * Handles the login button action.
     *
     * <p>The entered email is forwarded to the {@link LoginViewModel},
     * which performs the authentication process asynchronously.
     * According to the outcome, the UI is updated by displaying feedback,
     * storing the authenticated user identifier and navigating to the next view.</p>
     *
     * @param input the text field containing the user's email.
     */
    private void handleSubmit(TextField input) {

        if (input == null) {
            showAlert("Error", "Input field not found");
            return;
        }

        Task<String> loginTask = viewModel.login(input.getText());

        ApplicationStateManager stateManager =
                ApplicationStateManager.getInstance();

        loginTask.setOnSucceeded(_ -> {

            try {

                Long employeeId =
                        Long.parseLong(replaceSpaces(loginTask.getValue()));

                SessionManagerSingleton.getInstance()
                        .setAttribute("employeeId", employeeId);

                stateManager.replaceWindow(new TaskContainer());

                showBanner(BannerType.SUCCESS, "Login successful");

            } catch (Exception ex) {

                showAlert("Error", ex.getMessage());

            }

        });

        loginTask.setOnFailed(_ ->
                showBanner(
                        BannerType.FAILURE,
                        "Login failed\nEmail not found"
                )
        );

        new Thread(loginTask).start();
    }

    /**
     * Displays a temporary information banner.
     *
     * <p>The banner is automatically removed after a predefined timeout.</p>
     *
     * @param type    the banner type.
     * @param message the message to display.
     */
    private void showBanner(BannerType type, String message) {

        InformationBanner banner =
                new InformationBanner(type, message);

        ApplicationStateManager stateManager =
                ApplicationStateManager.getInstance();

        stateManager.addWindow(banner);

        PauseTransition pause =
                new PauseTransition(Duration.seconds(InformationBanner.timeInSeconds));

        pause.setOnFinished(_ -> stateManager.removeWindow(banner));

        pause.play();
    }
}
package com.unimib.assignment3.UI.controller.UI;

import com.unimib.assignment3.UI.FxApplication;
import com.unimib.assignment3.UI.components.Home;
import com.unimib.assignment3.UI.components.InformationBanner;
import com.unimib.assignment3.UI.controller.rest.LoginController;
import com.unimib.assignment3.UI.state.ApplicationStateManager;
import com.unimib.assignment3.UI.utils.SessionManagerSingleton;
import com.unimib.assignment3.UI.enums.BannerType;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.layout.StackPane;

import static com.unimib.assignment3.UI.components.AlertDialog.showAlert;
import static com.unimib.assignment3.UI.components.InformationBanner.timeInSeconds;

public class LoginViewController {

    @FXML
    private Label insertEmailLabel;

    @FXML
    private TextField inputForm;

    @FXML
    private Button submitButton;

    private FxApplication application;

    public void setFxApplication(FxApplication fxApplication){
        this.application = fxApplication;
    }

    /**
     * FXML initialize: will run when FXMLLoader injects fields.
     * If injection fails for some reason, call attachHandlers(root) from the caller.
     */
    @FXML
    private void initialize(){
        if(inputForm != null) inputForm.setText("matteo.cervini@example.com");

        if(submitButton != null) submitButton.setOnAction(event -> handleSubmit(inputForm));
    }

    /**
     * Attach handlers using lookup on the provided root node. This guarantees the visible
     * controls receive handlers even if field injection didn't happen.
     */
    public void attachHandlers(Parent root) {
        try {
            TextField input = (TextField) root.lookup("#inputForm");
            Button submit = (Button) root.lookup("#submitButton");

            if (input != null) input.setText("matteo.cervini@example.com");
            if (submit != null) submit.setOnAction(event -> handleSubmit(input));
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    /**
     * Shared submit logic extracted so we can call it from either initialize() or attachHandlers().
     */
    private void handleSubmit(TextField input) {
        if (input == null) {
            showAlert("Error", "Input field not found");
            return;
        }

        String email = input.getText();
        try {
            Task<String> loginTask = LoginController.login(email);
            loginTask.setOnSucceeded(ev->{
                try{
                    Long response = Long.parseLong(loginTask.getValue().replaceAll("\\n", "").replaceAll(" ", "").trim());
                    SessionManagerSingleton.getInstance().setAttribute("employeeId", response);

                    // Post-login UI changes: use ApplicationStateManager to swap to Home and show banner
                    if(application != null) {
                        ApplicationStateManager stateManager = ApplicationStateManager.getInstance(application);

                        // Construct Home with the application so it can interact with state manager
                        Home home = new Home(application);
                        // Ensure the content is displayed
                        stateManager.replaceWindow(home);

                        // create banner (compact container)
                        InformationBanner banner = new InformationBanner(BannerType.SUCCESS, "Login successful");
                        banner.setPrefHeight(60);
                        banner.setPrefWidth(240);
                        banner.setAlignment(Pos.CENTER);

                        HBox bannerBox = new HBox(banner);
                        bannerBox.setPickOnBounds(false);

                        // anchor banner flush to the right edge of the overlay, vertically centered
                        StackPane.setAlignment(bannerBox, Pos.CENTER_RIGHT);
                        StackPane.setMargin(bannerBox, new Insets(0, 0, 0, 0));
                        stateManager.addWindow(bannerBox);

                        PauseTransition pause = new PauseTransition(Duration.seconds(timeInSeconds));
                        pause.setOnFinished(p -> stateManager.removeWindow(bannerBox));
                        pause.play();
                    }
                }catch (Exception ex){
                    showAlert("Error", ex.getMessage());
                }
            });
            loginTask.setOnFailed(ev-> {
                // show failure banner via state manager
                if(application != null) {
                    ApplicationStateManager stateManager = ApplicationStateManager.getInstance(application);

                    InformationBanner banner = new InformationBanner(BannerType.FAILURE, "Login failed\nEmail not found");
                    banner.setPrefHeight(60);
                    banner.setPrefWidth(240);
                    banner.setAlignment(Pos.CENTER);

                    HBox bannerBox = new HBox(banner);
                    bannerBox.setPickOnBounds(false);

                    StackPane.setAlignment(bannerBox, Pos.CENTER_RIGHT);
                    StackPane.setMargin(bannerBox, new Insets(0, 0, 0, 0));
                    stateManager.addWindow(bannerBox);

                    PauseTransition pause = new PauseTransition(Duration.seconds(timeInSeconds));
                    pause.setOnFinished(p -> stateManager.removeWindow(bannerBox));
                    pause.play();
                }
            });

            new Thread(loginTask).start();
        }catch (Exception e){
            showAlert("Error", e.getMessage());
        }
    }
}

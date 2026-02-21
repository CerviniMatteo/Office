package com.unimib.assignment3.UI.view.controller;

import com.unimib.assignment3.UI.FxApplication;
import com.unimib.assignment3.UI.view.components.Home;
import com.unimib.assignment3.UI.view.components.InformationBanner;
import com.unimib.assignment3.UI.model.controller.LoginController;
import com.unimib.assignment3.UI.state.ApplicationStateManager;
import com.unimib.assignment3.UI.utils.SessionManagerSingleton;
import com.unimib.assignment3.UI.model.enums.BannerType;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Ellipse;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.application.Platform;

import static com.unimib.assignment3.UI.view.components.AlertDialog.showAlert;
import static com.unimib.assignment3.UI.view.components.InformationBanner.timeInSeconds;

public class LoginViewController {

    @FXML
    private Label insertEmailLabel;

    @FXML
    private TextField inputForm;

    @FXML
    private Button submitButton;

    @FXML
    private StackPane rootStack;

    @FXML
    private Rectangle designRectangle;

    @FXML
    private Ellipse designOval;

    private FxApplication application;

    public void setFxApplication(FxApplication fxApplication){
        this.application = fxApplication;
    }

    @FXML
    private void initialize() {
        // Default email text
        if(inputForm != null)
            inputForm.setText("matteo.cervini@example.com");

        if(submitButton != null)
            submitButton.setOnAction(event -> handleSubmit(inputForm));

        // Clip solo l'ovale, non tutto lo StackPane
        applyClipToOval();
    }

    private void applyClipToOval() {
        if (designRectangle == null || designOval == null) return;

        // create a clip rectangle and attach it to the oval
        final Rectangle clip = new Rectangle();
        clip.setArcWidth(designRectangle.getArcWidth());
        clip.setArcHeight(designRectangle.getArcHeight());
        designOval.setClip(clip);

        // updater computes the rectangle bounds (in parent's coordinates) and maps them to the oval's local coordinates
        final Runnable updateClip = () -> {
            try {
                // bounds of the rectangle in the parent (StackPane) coordinate space
                Bounds rectBounds = designRectangle.getBoundsInParent();

                // convert rectangle corners into the oval's local coordinate space
                Point2D topLeft = designOval.parentToLocal(rectBounds.getMinX(), rectBounds.getMinY());
                Point2D bottomRight = designOval.parentToLocal(rectBounds.getMaxX(), rectBounds.getMaxY());

                final double x = topLeft.getX();
                final double y = topLeft.getY();
                final double w = bottomRight.getX() - x;
                final double h = bottomRight.getY() - y;
                final double wClamped = w < 0 ? 0 : w;
                final double hClamped = h < 0 ? 0 : h;

                // update clip on JavaFX application thread
                Platform.runLater(() -> {
                    clip.setX(x);
                    clip.setY(y);
                    clip.setWidth(wClamped);
                    clip.setHeight(hClamped);
                    clip.setArcWidth(designRectangle.getArcWidth());
                    clip.setArcHeight(designRectangle.getArcHeight());
                });
            } catch (Exception ignored) {
                // best-effort; ignore if layout not ready yet
            }
        };

        // listen for layout changes so clip stays correct when window resizes or nodes move
        designRectangle.boundsInParentProperty().addListener((obs, oldV, newV) -> updateClip.run());
        designOval.boundsInParentProperty().addListener((obs, oldV, newV) -> updateClip.run());
        designOval.sceneProperty().addListener((obs, oldV, newV) -> Platform.runLater(updateClip));

        // initial run after layout pass
        Platform.runLater(updateClip);
    }

    public void attachHandlers(Parent root) {
        try {
            final TextField input = (TextField) root.lookup("#inputForm");
            final Button submit = (Button) root.lookup("#submitButton");

            if (input != null) input.setText("matteo.cervini@example.com");
            if (submit != null) submit.setOnAction(event -> handleSubmit(input));
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

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
                    Long response = Long.parseLong(loginTask.getValue().replaceAll("\\n", "").replaceAll(" ", "").trim());
                    SessionManagerSingleton.getInstance().setAttribute("employeeId", response);

                    if(application != null) {
                        ApplicationStateManager stateManager = ApplicationStateManager.getInstance(application);
                        Home home = new Home(application);
                        stateManager.replaceWindow(home);

                        InformationBanner banner = new InformationBanner(BannerType.SUCCESS, "Login successful");
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
                } catch (Exception ex){
                    showAlert("Error", ex.getMessage());
                }
            });

            loginTask.setOnFailed(ev -> {
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
        } catch (Exception e){
            showAlert("Error", e.getMessage());
        }
    }
}
package com.unimib.GUI.UI.view.controller.impl.layout.auth_state;

import com.jpro.webapi.WebAPI;
import com.unimib.GUI.UI.view.controller.abstr.AuthController;
import com.unimib.GUI.UI.viewmodel.impl.AuthViewModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Window;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.ResourceBundle;

import static com.unimib.GUI.UI.view.utils.ComponentVisibilityUtils.setVisible;

public class RegistrationController extends AuthController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField imageUrlField;

    @FXML
    private Button chooseImageButton;

    @FXML
    private Button submitButton;

    @FXML
    private Label selectedImageLabel;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Button goToLoginButton;

    private AuthViewModel viewModel;
    private Runnable switchToLogin;
    private String selectedImageBase64;

    public void setSwitchToLogin(Runnable switchToLogin) {
        this.switchToLogin = switchToLogin;
    }

    @FXML
    private void initialize() {

        profileImageView.setFitWidth(120);
        profileImageView.setFitHeight(120);
        profileImageView.setPreserveRatio(true);

        setVisible(false, profileImageView);

        viewModel = new AuthViewModel();

        observeState(
                viewModel.getRegistrationState(),
                workerDTO -> {
                    if (workerDTO != null) {
                        showSuccess(
                                "Registration successful!" +
                                        "\nSave the email written below" +
                                        "\nEmail: " + workerDTO.email()
                        );
                        saveEmployeeId(workerDTO.workerId());
                        goToTaskContainer();
                    } else {
                        showError("User information are empty, registration failed");
                    }
                },
                this::showError
        );

        setupImageUpload();

        submitButton.setOnAction(event -> handleSubmit());

        goToLoginButton.setOnAction(_ -> {
            if (switchToLogin != null) {
                switchToLogin.run();
            }
        });
    }

    private void setupImageUpload() {
        waitForShowingWindow(chooseImageButton, this::initFileUploader);
    }

    private void waitForShowingWindow(Node node, Runnable onReady) {

        Scene scene = node.getScene();

        if (scene == null) {
            node.sceneProperty().addListener(new ChangeListener<Scene>() {
                @Override
                public void changed(ObservableValue<? extends Scene> obs, Scene oldScene, Scene newScene) {
                    if (newScene != null) {
                        node.sceneProperty().removeListener(this);
                        waitForShowingWindow(node, onReady);
                    }
                }
            });
            return;
        }

        Window window = scene.getWindow();

        if (window == null) {
            scene.windowProperty().addListener(new ChangeListener<Window>() {
                @Override
                public void changed(ObservableValue<? extends Window> obs, Window oldWindow, Window newWindow) {
                    if (newWindow != null) {
                        scene.windowProperty().removeListener(this);
                        waitForShowingWindow(node, onReady);
                    }
                }
            });
            return;
        }

        if (window.isShowing()) {
            onReady.run();
        } else {
            window.showingProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> obs, Boolean oldVal, Boolean newVal) {
                    if (newVal) {
                        window.showingProperty().removeListener(this);
                        onReady.run();
                    }
                }
            });
        }
    }

    private void initFileUploader() {
        WebAPI webAPI = WebAPI.getWebAPI(chooseImageButton.getScene().getWindow());
        WebAPI.FileUploader uploader = webAPI.makeFileUploadNode(chooseImageButton);

        uploader.selectFileOnClickProperty().set(true);

        // 1. Log file selection step
        uploader.selectedFileProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("[upload] Selected: " + newVal);
            if (newVal != null) {
                Platform.runLater(() -> selectedImageLabel.setText("Uploading file..."));
            }
        });

        uploader.progressProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("[upload] Progress: " + newVal);
        });

        // 2. Main upload completion handler
        uploader.uploadedFileProperty().addListener((obs, oldFile, newFile) -> {
            System.out.println("[upload] Upload complete. Server file: " + newFile);

            if (newFile == null || !newFile.exists()) {
                return;
            }

            try {
                // Read binary data directly from local server storage
                byte[] data = Files.readAllBytes(newFile.toPath());

                // Convert to Base64 for submission
                selectedImageBase64 = Base64.getEncoder().encodeToString(data);

                // Create FX Image from stream
                Image image = new Image(new ByteArrayInputStream(data));

                // Update UI components on JavaFX App Thread
                Platform.runLater(() -> {
                    profileImageView.setImage(image);

                    // Explicitly reveal node
                    profileImageView.setVisible(true);
                    profileImageView.setManaged(true);

                    if (selectedImageLabel != null) {
                        selectedImageLabel.setText("Image uploaded successfully!");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                selectedImageBase64 = null;
                Platform.runLater(() -> showError("Failed to read uploaded file: " + e.getMessage()));
            }
        });
    }

    private void handleSubmit() {
        if (!validate(nameField, "Name is required")) {
            return;
        }

        if (!validate(surnameField, "Surname is required")) {
            return;
        }

        if (!validate(selectedImageBase64, "Please select a profile picture")) {
            return;
        }

        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();

        viewModel.signup(name, surname, selectedImageBase64);
    }
}
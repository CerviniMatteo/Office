package com.unimib.GUI.UI.view.controller.impl.layout.auth_state;

import com.unimib.GUI.UI.view.controller.abstr.AuthController;
import com.unimib.GUI.UI.viewmodel.impl.AuthViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

import static com.unimib.GUI.UI.view.utils.ComponentVisibilityUtils.setVisible;
import static com.unimib.GUI.UI.view.utils.FileUtils.setUpFileChooser;
import static com.unimib.GUI.UI.view.utils.WorkerImageUtils.encodeFileAsBase64;

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

        observeState(viewModel.getRegistrationState(),
                workerDTO -> {
                    if (workerDTO != null) {
                        showSuccess("Registration successful!" +
                                "\nSave the email written below" +
                                "\nEmail: " + workerDTO.email());

                        saveEmployeeId(workerDTO.workerId());
                        goToTaskContainer();
                    } else {
                        showError("User information are empty, registration failed");
                    }
                },
                this::showError);

        chooseImageButton.setOnAction(event -> handleChooseImage());
        submitButton.setOnAction(event -> handleSubmit());

        goToLoginButton.setOnAction(_ -> {
            if (switchToLogin != null) {
                switchToLogin.run();
            }
        });
    }

    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");

        setUpFileChooser(fileChooser);

        try {
            selectedImageBase64 = encodeFileAsBase64(fileChooser.showOpenDialog(
                    chooseImageButton.getScene().getWindow()));

            Image image = new Image(fileChooser.showOpenDialog(
                    chooseImageButton.getScene().getWindow()).toURI().toString());

            profileImageView.setImage(image);
            setVisible(true, profileImageView);
            selectedImageLabel.setText("Image selected");

        } catch (RuntimeException e) {
            selectedImageBase64 = null;
            showError(e.getMessage());
        }
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
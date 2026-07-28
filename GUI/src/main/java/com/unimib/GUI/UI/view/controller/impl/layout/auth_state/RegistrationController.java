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
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import static com.unimib.GUI.UI.view.utils.ComponentVisibilityUtils.setVisible;

public class RegistrationController extends AuthController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private Button chooseImageButton;

    @FXML
    private Button submitButton;

    @FXML
    private Label selectedImageLabel;

    @FXML
    private ImageView profileImageView;

    private File selectedImage;

    private AuthViewModel viewModel;

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
    }

    private void handleChooseImage() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png",
                        "*.jpg",
                        "*.jpeg",
                        "*.webp"
                )
        );

        File file = fileChooser.showOpenDialog(
                chooseImageButton.getScene().getWindow()
        );

        if (file == null) {
            return;
        }

        selectedImage = file;

        Image image = new Image(selectedImage.toURI().toString());

        if (image.isError()) {
            showError("Error occurred while loading the image");
            return;
        }

        profileImageView.setImage(image);
        setVisible(true, profileImageView);
        selectedImageLabel.setText("Image has been selected");
        selectedImageLabel.getStyleClass().add("section-title");
    }

    private void handleSubmit() {

        if (!validate(nameField, "Name is required")) {
            return;
        }
        if (!validate(surnameField, "Surname is required")) {
            return;
        }
        if (!validate(selectedImage, "Please select a profile picture")) {
            return;
        }

        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();

        try {
            byte[] fileBytes = Files.readAllBytes(selectedImage.toPath());
            String encodedImage = Base64.getEncoder().encodeToString(fileBytes);
            viewModel.signup(name, surname, encodedImage);
        } catch (IOException e) {
            showError("Failed to read selected image file");
        }
    }
}
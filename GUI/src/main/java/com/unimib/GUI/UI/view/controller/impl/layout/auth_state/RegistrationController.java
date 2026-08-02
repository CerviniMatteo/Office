package com.unimib.GUI.UI.view.controller.impl.layout.auth_state;

import com.unimib.GUI.UI.view.controller.abstr.AuthController;
import com.unimib.GUI.UI.viewmodel.impl.AuthViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import one.jpro.platform.file.ExtensionFilter;
import one.jpro.platform.file.FileSource;
import one.jpro.platform.file.picker.FileOpenPicker;

import static com.unimib.GUI.UI.view.utils.ComponentVisibilityUtils.setVisible;
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

        ExtensionFilter imageFilter = ExtensionFilter.of(
                "Images",
                ".png",
                ".jpg",
                ".jpeg"
        );

        FileOpenPicker picker = FileOpenPicker.create(chooseImageButton);

        picker.getExtensionFilters().add(imageFilter);
        picker.setSelectedExtensionFilter(imageFilter);
        picker.setSelectionMode(SelectionMode.SINGLE);

        picker.setOnFilesSelected(fileSources -> {

            if (fileSources == null || fileSources.isEmpty()) {
                return;
            }

            FileSource source = fileSources.getFirst();

            source.uploadFileAsync().thenAccept(uploadedFile -> {
                if (uploadedFile == null) {
                    Platform.runLater(() -> showError("Failed to read image: no uploaded file available"));
                    return;
                }

                try {
                    selectedImageBase64 = encodeFileAsBase64(uploadedFile);

                    Image image = new Image(uploadedFile.toURI().toString());

                    Platform.runLater(() -> {

                        profileImageView.setImage(image);

                        profileImageView.setVisible(true);
                        profileImageView.setManaged(true);

                        selectedImageLabel.setText(
                                source.getName()
                        );

                    });

                } catch (Exception ex) {

                    ex.printStackTrace();

                    Platform.runLater(() ->
                            showError(
                                    "Failed to read image: "
                                            + ex.getMessage()
                            )
                    );
                }
            }).exceptionally(ex -> {
                Platform.runLater(() ->
                        showError(
                                "Failed to upload image: "
                                        + ex.getMessage()
                        )
                );
                return null;
            });

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
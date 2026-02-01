package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.dto.WorkerDTO;
import com.unimib.assignment3.UI.rest.WorkerRest;
import com.unimib.assignment3.UI.utils.SessionManagerSingleton;
import jakarta.annotation.Nonnull;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import static com.unimib.assignment3.UI.utils.AlertDialog.showAlert;

public class ProfileDashboardButton extends StyledButton {

    private final HBox topContent; // container for icon + name+surname
    private final VBox mainLayout;  // container for topContent + email

    public ProfileDashboardButton() {
        super();

        // --- Top StyledButton with icon (reused) ---
        StyledButton iconButton = getStyledButton();

        // --- Container for top content: icon + name/surname ---
        topContent = new HBox(8);
        topContent.setAlignment(Pos.CENTER_LEFT);
        topContent.getChildren().add(iconButton.getContent());

        // --- Main layout: topContent + email ---
        mainLayout = new VBox(4);
        mainLayout.getChildren().add(topContent);

        // Set mainLayout as the button's graphic
        setGraphic(mainLayout);

        // Fetch profile information asynchronously
        fetchProfileInformation();
    }

    @Nonnull
    private static StyledButton getStyledButton() {
        StyledButton iconButton = new StyledButton();
        iconButton.createDashboardStyledButtonContent(
                "Profile",
                "M200-246q54-53 125.5-83.5T480-360q83 0 154.5 30.5T760-246v-514H200v514Zm280-194q58 0 99-41t41-99q0-58-41-99t-99-41q-58 0-99 41t-41 99q0 58 41 99t99 41ZM200-120q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h560q33 0 56.5 23.5T840-760v560q0 33-23.5 56.5T760-120H200Zm69-80h422q-44-39-99.5-59.5T480-280q-56 0-112.5 20.5T269-200Zm211-320q-25 0-42.5-17.5T420-580q0-25 17.5-42.5T480-640q25 0 42.5 17.5T540-580q0 25-17.5 42.5T480-520Zm0 17Z"
        );
        return iconButton;
    }

    private void fetchProfileInformation() {
        SessionManagerSingleton session = SessionManagerSingleton.getInstance();
        Long workerId = (Long) session.getAttribute("workerId");

        if (workerId == null) {
            showAlert("Error", "No worker is logged in.");
            return;
        }

        try {
            Task<WorkerDTO> workerDTOTask = getWorkerDTOTask(workerId);
            new Thread(workerDTOTask).start();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private Task<WorkerDTO> getWorkerDTOTask(Long workerId) {
        Task<WorkerDTO> workerDTOTask = WorkerRest.fetchWorker(workerId);

        workerDTOTask.setOnSucceeded(ev -> {
            WorkerDTO workerDTO = workerDTOTask.getValue();
            this.setId(String.valueOf(workerDTO.getWorkerId()));

            // Name + Surname label inside topContent
            Region spacer1 = new Region();
            HBox.setHgrow(spacer1, Priority.ALWAYS);
            Region spacer2 = new Region();
            spacer2.setPrefWidth(8);
            Label profileInfoLabel = new Label(workerDTO.getName() + "\n" + workerDTO.getSurname());
            profileInfoLabel.getStyleClass().add("dashboard");
            topContent.getChildren().addAll(spacer1,profileInfoLabel, spacer2);

            Label emailLabel = new Label(workerDTO.getEmail());
            emailLabel.getStyleClass().add("dashboard");

            VBox.setMargin(emailLabel, new Insets(2, 0, 0, 0));
            emailLabel.setAlignment(Pos.CENTER);
            emailLabel.setMaxWidth(Double.MAX_VALUE);

            Separator separator = new Separator();
            mainLayout.getChildren().addAll(separator, emailLabel);
            mainLayout.setAlignment(Pos.CENTER);
            separator.setPrefWidth(emailLabel.getPrefWidth());
        });

        workerDTOTask.setOnFailed(ev -> {
            Throwable e = workerDTOTask.getException();
            showAlert("Error", e != null && e.getMessage() != null ? e.getMessage() : "Unknown error");
        });

        return workerDTOTask;
    }


}

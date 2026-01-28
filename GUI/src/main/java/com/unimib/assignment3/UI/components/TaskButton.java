package com.unimib.assignment3.UI.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unimib.assignment3.UI.dto.ChangeTaskStateRequestDTO;
import com.unimib.assignment3.UI.dto.TaskDTO;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TaskButton extends Button {
    private static final String BASE_URL = "http://localhost:8080/api/tasks";
    private Label descriptionLabel;
    private Label stateLabel;
    private Label startDateLabel;
    private Label endDateLabel;
    private StyledButton changeStateButton;

    public TaskButton(TaskDTO taskDTO){
        super();

        descriptionLabel = new Label(taskDTO.getDescription());
        stateLabel = new Label();
        startDateLabel = new Label();
        endDateLabel = new Label();
        changeStateButton = new StyledButton();

        setUpTaskLabels(taskDTO);

        HBox topDates = new HBox();
        topDates.setPadding(new Insets(2));
        topDates.setAlignment(Pos.TOP_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topDates.getChildren().addAll(startDateLabel, spacer, endDateLabel);


        VBox centerBox = new VBox(2);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().addAll(descriptionLabel, stateLabel);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topDates);
        borderPane.setCenter(centerBox);

        setGraphic(borderPane);
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);

        GridPane.setHgrow(this, Priority.ALWAYS);
        GridPane.setVgrow(this, Priority.ALWAYS);

        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(10));
        bottomBox.setAlignment(Pos.BOTTOM_RIGHT);
        bottomBox.getChildren().add(changeStateButton);
        borderPane.setBottom(bottomBox);

        setUpChangeStateButtonAction();
    }

    private void setUpTaskLabels(TaskDTO taskDTO){
        setId("" + taskDTO.getTaskId());
        descriptionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 24px;");

        stateLabel.setText(taskDTO.getTaskState().replace("_", " "));
        stateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 20px;");


        String startDateStr = "START DATE\n";
        startDateStr += taskDTO.getStartDate() != null
                ? taskDTO.getStartDate().toString()
                : "N/A";
        startDateLabel.setText(startDateStr);
        startDateLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 18px;");
        startDateLabel.setPadding(new Insets(10));

        String endDateStr = "END DATE\n";
        endDateStr+=  taskDTO.getEndDate() != null
                ? taskDTO.getEndDate().toString()
                : "N/A";
        endDateLabel.setText(endDateStr);
        endDateLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 18px;");
        endDateLabel.setPadding(new Insets(10));

        changeStateButton.setText(changeStateButton(taskDTO.getTaskState().replace("_", " ")));

        boolean isDone = "DONE".equals(taskDTO.getTaskState());
        changeStateButton.setDisable(isDone);


        setButtonColor(this, stateLabel.getText(), "#F8E2D4");
    }

    private String changeStateButton(String taskState) {
        return switch (taskState) {
            case "TO BE STARTED" -> "START TASK";
            case "STARTED"      -> "COMPLETE TASK";
            case "DONE"         -> "TASK COMPLETED";
            default             -> "UNKNOWN STATE";
        };
    }

    private void setButtonColor(Button button, String taskState, String borderColor) {
        String color;
        switch (taskState) {
            case "TO BE STARTED" -> color = "#2F2139";
            case "STARTED"      -> color = "#6A2E3D";
            case "DONE"         -> color = "#086466";
            default             -> color = "#444";
        }

        button.setStyle("""
        -fx-background-color: %s;
        -fx-background-radius: 30;
        -fx-border-radius: 30;
        -fx-border-width: 2;
        -fx-border-color: %s;
    """.formatted(color, borderColor));
    }

    private void setUpChangeStateButtonAction() {
        changeStateButton.setOnAction(e -> {

            try {
                ChangeTaskStateRequestDTO payload =
                        new ChangeTaskStateRequestDTO(
                                Long.valueOf(getId()),
                                mapTaskState(stateLabel.getText())
                        );

                Task<TaskDTO> task = new Task<>() {
                    @Override
                    protected TaskDTO call() {

                        HttpResponse<String> response = postChangeTaskState(payload);

                        if (response == null) {
                            throw new RuntimeException("No response from server");
                        }

                        if (response.statusCode() != 200) {
                            throw new RuntimeException(
                                    "HTTP error " + response.statusCode()
                            );
                        }

                        TaskDTO taskDTO = fetchTaskFromBackend(Long.valueOf(getId()));

                        if (taskDTO == null) {
                            throw new RuntimeException("Failed to fetch updated task");
                        }

                        return taskDTO;
                    }
                };

                task.setOnSucceeded(ev -> {
                    setUpTaskLabels(task.getValue());
                });


                task.setOnFailed(ev -> {
                    showAlert("Error", task.getException().getMessage());
                });

                new Thread(task).start();

            } catch (Exception ex) {
                showAlert("Error", "Failed to create request payload");
            }
        });
    }


    private String mapTaskState(String taskState) {
        return switch (taskState) {
            case "TO BE STARTED" -> "STARTED";
            case "STARTED" -> "DONE";
            default -> "UNKNOWN_STATE";
        };
    }


    private TaskDTO fetchTaskFromBackend(Long taskId) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/tasks/" + taskId))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            return mapper.readValue(response.body(), TaskDTO.class);

        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            return null;
        }
    }
    private HttpResponse<String> postChangeTaskState(ChangeTaskStateRequestDTO requestObj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String payload = mapper.writeValueAsString(requestObj);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/tasks/changeState"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            return null;
        }
    }



    // Show an alert dialog in JavaFX
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


}

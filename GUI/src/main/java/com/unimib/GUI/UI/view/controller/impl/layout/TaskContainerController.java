package com.unimib.GUI.UI.view.controller.impl.layout;

import com.unimib.GUI.UI.viewmodel.impl.TaskCardViewModel;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.utils.SessionManagerSingleton;
import com.unimib.GUI.UI.view.components.abstr.TaskCardBase;
import com.unimib.GUI.UI.view.components.impl.custom.AlertDialog;
import com.unimib.GUI.UI.view.components.impl.layout.Chat;
import com.unimib.GUI.UI.view.components.impl.layout.TaskCreationForm;
import com.unimib.GUI.UI.view.controller.abstr.DefaultController;
import com.unimib.GUI.UI.view.factory.TaskCardFactory;
import com.unimib.GUI.UI.state.ApplicationStateManager;
import com.unimib.GUI.web_socket_client.ChatWebSocketClientApp;
import com.unimib.GUI.web_socket_client.TaskWebSocketClientApp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;


public class TaskContainerController implements DefaultController {


    @FXML
    private VBox notStartedTaskBox;

    @FXML
    private VBox startedTaskBox;

    @FXML
    private VBox doneTaskBox;

    @FXML
    private HBox centerContainer;

    @FXML
    private VBox activeTaskContainer;


    @FXML
    private Button createTaskButton;

    @FXML
    private Button chatButton;

    @FXML
    private Button logOutButton;


    private final Map<Long, TaskCardBase> tasks =
            new HashMap<>();


    private TaskCardViewModel viewModel;

    private Chat chat;



    @FXML
    public void initialize() {

        centerContainer.setFillHeight(true);


        viewModel = new TaskCardViewModel();


        observeTasks();

        observeTask();


        viewModel.fetchTasks();



        TaskWebSocketClientApp webSocketClient =
                new TaskWebSocketClientApp();


        try {

            webSocketClient.start();

        } catch (Exception e) {

            AlertDialog.showAlert(
                    "Error",
                    "Could not connect to TaskContainer: "
                            + e.getMessage()
            );
        }



        webSocketClient
                .getProperty()
                .addListener((_, _, message) -> {

                    if(message != null && !message.isEmpty()) {

                        handleTaskChange(message);

                    }
                });



        setupCenterComponents();

        setupButtons(webSocketClient);
    }



    private void observeTasks() {

        observeState(
                viewModel.getTasksStateProperty(),

                // loading
                () -> {
                },


                // success
                fetchedTasks -> {

                    if(fetchedTasks == null)
                        return;


                    fetchedTasks.forEach(task -> {

                        TaskCardBase card =
                                TaskCardFactory.create(task);


                        if(card != null) {

                            tasks.put(
                                    task.taskId(),
                                    card
                            );


                            addTask(
                                    task,
                                    card
                            );
                        }
                    });
                },


                this::showError
        );
    }



    private void observeTask() {

        observeState(
                viewModel.getTaskStateProperty(),

                // loading
                () -> {
                },


                // success
                updatedTask -> {

                    if(updatedTask == null)
                        return;


                    removeTask(
                            updatedTask.taskId()
                    );


                    TaskCardBase card =
                            TaskCardFactory.create(updatedTask);


                    if(card != null) {

                        tasks.put(
                                updatedTask.taskId(),
                                card
                        );


                        addTask(
                                updatedTask,
                                card
                        );
                    }
                },


                this::showError
        );
    }



    private void setupCenterComponents() {


        TaskCreationForm form =
                new TaskCreationForm(
                        new TaskCreationFormController()
                );


        centerContainer
                .getChildren()
                .add(form);



        createTaskButton.setOnAction(_ -> {


            centerContainer
                    .getChildren()
                    .remove(chat);



            if(centerContainer
                    .getChildren()
                    .contains(form)) {


                centerContainer
                        .getChildren()
                        .remove(form);

            } else {


                form.clear();

                centerContainer
                        .getChildren()
                        .add(form);
            }

        });



        chatButton.setOnAction(_ -> {


            if(chat == null) {

                chat = new Chat();

            }


            if(centerContainer
                    .getChildren()
                    .contains(chat)) {


                chatButton.setText(
                        "OPEN CHAT"
                );


                centerContainer
                        .getChildren()
                        .remove(chat);


            } else {


                chatButton.setText(
                        "CLOSE CHAT"
                );


                centerContainer
                        .getChildren()
                        .remove(form);


                centerContainer
                        .getChildren()
                        .add(chat);
            }

        });
    }



    private void setupButtons(
            TaskWebSocketClientApp webSocketClient
    ) {


        logOutButton.setOnAction(_ -> {


            webSocketClient.stop();


            ChatWebSocketClientApp.resetInstance();



            SessionManagerSingleton session =
                    SessionManagerSingleton.getInstance();


            session.removeAttribute(
                    "employeeId"
            );


            ApplicationStateManager
                    .getInstance()
                    .goBack();

        });
    }



    private void handleTaskChange(String message) {


        if(message.startsWith("FETCH_TASK:")) {


            Long taskId =
                    Long.valueOf(
                            message.substring(
                                    message.indexOf(":") + 1
                            )
                    );


            Platform.runLater(
                    () -> updateTaskCard(taskId)
            );

        }


        if(message.startsWith("DELETE_TASK:")) {


            Long taskId =
                    Long.valueOf(
                            message.substring(
                                    message.indexOf(":") + 1
                            )
                    );


            Platform.runLater(
                    () -> deleteEntry(taskId)
            );
        }
    }



    private void updateTaskCard(Long taskId) {

        viewModel.fetchTask(taskId);

    }



    private void deleteEntry(Long taskId) {


        TaskCardBase card =
                tasks.remove(taskId);


        if(card == null)
            return;


        removeTask(taskId);
    }



    private void removeTask(Long taskId) {


        notStartedTaskBox
                .getChildren()
                .removeIf(node ->
                        taskId.toString()
                                .equals(node.getId())
                );


        startedTaskBox
                .getChildren()
                .removeIf(node ->
                        taskId.toString()
                                .equals(node.getId())
                );


        doneTaskBox
                .getChildren()
                .removeIf(node ->
                        taskId.toString()
                                .equals(node.getId())
                );


        activeTaskContainer
                .getChildren()
                .removeIf(node ->
                        taskId.toString()
                                .equals(node.getId())
                );
    }



    private void addTask(
            TaskDTO taskDTO,
            TaskCardBase taskCard
    ) {


        taskCard.setId(
                taskDTO.taskId().toString()
        );


        taskCard
                .getStyleClass()
                .add("task-card-compact");



        switch(taskDTO.taskState()) {


            case TO_BE_STARTED -> {

                notStartedTaskBox
                        .getChildren()
                        .add(taskCard);
            }


            case STARTED -> {


                startedTaskBox
                        .getChildren()
                        .add(taskCard);



                Label label =
                        new Label(
                                taskDTO.description()
                        );


                label.setId(
                        taskDTO.taskId().toString()
                );


                label.getStyleClass()
                        .add(
                                "active-task-entry-lbl"
                        );


                label.setMaxWidth(
                        Double.MAX_VALUE
                );


                label.setWrapText(true);



                activeTaskContainer
                        .getChildren()
                        .add(label);
            }


            case DONE -> {

                doneTaskBox
                        .getChildren()
                        .add(taskCard);

            }
        }
    }
}
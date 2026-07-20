package com.unimib.GUI.UI.view.controller.impl.layout;

import com.unimib.GUI.UI.view.components.impl.task.TaskCardSkeleton;
import com.unimib.GUI.UI.viewmodel.impl.TaskViewModel;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.model.enums.TaskState;
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

import java.util.Collections;
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

    private final Map<Long, TaskCardSkeleton> skeletonTasks =
            new HashMap<>();


    private TaskViewModel viewModel;

    private Chat chat;



    @FXML
    public void initialize() {
        centerContainer.setFillHeight(true);

        viewModel = new TaskViewModel();

        observeTasks();
        observeTask();

        viewModel.fetchTasks();

        TaskWebSocketClientApp webSocketClient = new TaskWebSocketClientApp();

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

    private void showInitialSkeleton(){

        clearContainers();

        for(int i = 0; i < 3; i++){
            notStartedTaskBox.getChildren().add(newSkeletonPlaceholder(-1L));

            startedTaskBox.getChildren()
                    .add(newSkeletonPlaceholder(-1L));

            doneTaskBox.getChildren()
                    .add(newSkeletonPlaceholder(-1L));
        }
    }

    private TaskCardSkeleton newSkeletonPlaceholder(Long taskId) {
        TaskDTO placeholder = new TaskDTO(
                taskId,
                "",
                TaskState.TO_BE_STARTED,
                null,
                null,
                Collections.emptyMap()
        );

        return new TaskCardSkeleton(placeholder);
    }

    private void removeInitialSkeleton(){

        notStartedTaskBox.getChildren()
                .removeIf(
                        node -> node instanceof TaskCardSkeleton
                );

        startedTaskBox.getChildren()
                .removeIf(
                        node -> node instanceof TaskCardSkeleton
                );

        doneTaskBox.getChildren()
                .removeIf(
                        node -> node instanceof TaskCardSkeleton
                );
    }

    private void showTaskSkeleton(Long taskId){

        removeSkeleton(taskId);

        TaskCardSkeleton skeleton =
                newSkeletonPlaceholder(taskId);

        skeleton.setId(
                "skeleton-" + taskId
        );

        skeletonTasks.put(
                taskId,
                skeleton
        );

        doneTaskBox.getChildren()
                .add(skeleton);
    }

    private void removeSkeleton(Long taskId){

        TaskCardSkeleton skeleton =
                skeletonTasks.remove(taskId);


        if(skeleton == null)
            return;


        notStartedTaskBox.getChildren()
                .remove(skeleton);


        startedTaskBox.getChildren()
                .remove(skeleton);


        doneTaskBox.getChildren()
                .remove(skeleton);
    }

    private void observeTasks() {

        showInitialSkeleton();

        observeState(
                viewModel.getTasksStateProperty(),

                () -> {
                },


                fetchedTasks -> {

                    removeInitialSkeleton();

                    if(fetchedTasks == null)
                        return;

                    clearContainers();

                    tasks.clear();


                    for(TaskDTO taskDTO : fetchedTasks) {

                        TaskCardBase card =
                                TaskCardFactory.create(taskDTO);


                        if(card != null) {

                            tasks.put(
                                    taskDTO.taskId(),
                                    card
                            );


                            addTask(
                                    taskDTO,
                                    card
                            );
                        }
                    }
                },


                error -> {

                    removeInitialSkeleton();

                    this.showError(error);
                }
        );
    }

    private void clearContainers() {

        notStartedTaskBox.getChildren().clear();

        startedTaskBox.getChildren().clear();

        doneTaskBox.getChildren().clear();

        activeTaskContainer.getChildren().clear();
    }


    private void observeTask() {

        observeState(
                viewModel.getTaskStateProperty(),

                () -> {
                },


                updatedTask -> {

                    if(updatedTask == null)
                        return;


                    removeSkeleton(
                            updatedTask.taskId()
                    );


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

            if(chatButton.getText().equals("CLOSE CHAT")) {
                chatButton.setText("OPEN CHAT");
                centerContainer.getChildren().remove(chat);
            } else {
                chatButton.setText("CLOSE CHAT");
                centerContainer.getChildren().remove(form);
                centerContainer.getChildren().add(chat);
            }

        });
    }

    private void setupButtons(
            TaskWebSocketClientApp webSocketClient
    ) {
        logOutButton.setOnAction(_ -> {
            webSocketClient.stop();
            ChatWebSocketClientApp.getInstance().stop();
            SessionManagerSingleton session =
                    SessionManagerSingleton.getInstance();
            session.removeAttribute("employeeId");

            ApplicationStateManager
                    .getInstance()
                    .goBack();

        });
    }

    private void handleTaskChange(String message) {

        if(message.startsWith("FETCH_TASK:")) {

            Long taskId = Long.valueOf(
                    message.substring("FETCH_TASK:".length())
            );

            Platform.runLater(
                    () -> updateTaskCard(taskId)
            );

        } else if(message.startsWith("DELETE_TASK:")) {

            Long taskId = Long.valueOf(
                    message.substring("DELETE_TASK:".length())
            );

            Platform.runLater(
                    () -> deleteEntry(taskId)
            );
        }
    }

    private void updateTaskCard(Long taskId){

        showTaskSkeleton(taskId);

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
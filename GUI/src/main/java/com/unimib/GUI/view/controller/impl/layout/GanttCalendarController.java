package com.unimib.GUI.view.controller.impl.layout;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.DayViewBase;
import com.calendarfx.view.DetailedWeekView;
import com.unimib.GUI.model.controller.TaskRestController;
import com.unimib.GUI.model.custom_entity.CalendarEntry;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.model.enums.TaskState;
import com.unimib.GUI.utils.SessionManagerSingleton;
import com.unimib.GUI.view.components.abstr.TaskCardBase;
import com.unimib.GUI.view.components.impl.custom.AlertDialog;
import com.unimib.GUI.view.components.impl.layout.Chat;
import com.unimib.GUI.view.components.impl.layout.TaskCreationForm;
import com.unimib.GUI.view.controller.abstr.DefaultController;
import com.unimib.GUI.view.factory.CalendarEntryStylingFactory;
import com.unimib.GUI.view.factory.TaskCardFactory;
import com.unimib.GUI.view.state.ApplicationStateManager;
import com.unimib.GUI.web_socket_client.TaskWebSocketClientApp;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.unimib.GUI.model.enums.TaskState.*;

public class GanttCalendarController implements DefaultController {

    @FXML
    private DetailedWeekView detailedWeekView;
    @FXML
    private HBox centerContainer;

    @FXML
    private VBox activeTaskContainer;

    private Map<Long, Pair<CalendarEntry<TaskDTO>, TaskCardBase>> entries;
    private TaskRestController taskRestController;

    // 1. Define a separate Calendar for each state
    private Calendar<TaskDTO> toStartCalendar;
    private Calendar<TaskDTO> startedCalendar;
    private Calendar<TaskDTO> doneCalendar;

    @FXML
    private Button chatButton;

    @FXML
    private Button logOutButton;

    private Chat chat;

    @FXML
    public void initialize() {
        detailedWeekView.setNumberOfDays(5);
        detailedWeekView.setAdjustToFirstDayOfWeek(false);
        detailedWeekView.setShowTimeScaleView(true);

        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        detailedWeekView.setDate(monday);

        detailedWeekView.getWeekView().setStartTime(LocalTime.of(7, 0));
        detailedWeekView.getWeekView().setEndTime(LocalTime.of(19, 30));
        detailedWeekView.getWeekView().setEarlyLateHoursStrategy(DayViewBase.EarlyLateHoursStrategy.HIDE);

        detailedWeekView.setShowToday(true);
        detailedWeekView.setShowWeekDayHeaderView(true);
        detailedWeekView.setShowAllDayView(false);
        detailedWeekView.setShowScrollBar(false);

        detailedWeekView.getWeekView().layoutBoundsProperty().addListener((_, _, newVal) -> {
            Rectangle clip = new Rectangle(newVal.getWidth(), newVal.getHeight());
            detailedWeekView.getWeekView().setClip(clip);
        });

        detailedWeekView.setEntryFactory(param -> {

            TaskCreationForm form = new TaskCreationForm(new TaskCreationFormController());
            LocalDateTime clickedTime = param.getZonedDateTime().toLocalDateTime();

            Entry<TaskDTO> tempEntry = new Entry<>("");
            tempEntry.setUserObject(null);
            tempEntry.setInterval(clickedTime, clickedTime.plusHours(3));
            param.getCalendar().setStyle(CalendarEntryStylingFactory.styleCalendarEntry(TO_BE_STARTED));

            form.setOnClose(() -> tempEntry.getCalendar().removeEntry(tempEntry));
            // Show the form asynchronously
            form.setOnSuccess(newTask -> {
                    Task<String> task = taskRestController.createTask(newTask);
                    task.setOnSucceeded(_ ->{
                            AlertDialog.showAlert("Success", "Task correctly create");
                            form.removeTaskPopup();
                            tempEntry.getCalendar().removeEntry(tempEntry);
                    });
                    task.setOnFailed(_ ->
                            AlertDialog.showAlert("Error", task.getException().getMessage()));

                    new Thread(task).start();
            });

            form.showTaskPopup();

            // Return the temporary entry to avoid NPE
            return tempEntry;
        });

        entries = new HashMap<>();
        taskRestController = new TaskRestController();

        detailedWeekView.setEntryDetailsCallback(param -> {
            Entry<?> entry = param.getEntry();
            if (entry != null && entry.getUserObject() instanceof TaskDTO taskDTO) {
                Pair<CalendarEntry<TaskDTO>, TaskCardBase> pair = entries.get(taskDTO.taskId());
                TaskCardBase taskCard = (pair != null && pair.getValue() != null) ? pair.getValue() : TaskCardFactory.create(taskDTO);
                taskCard.showTaskPopup();
            }
            return null;
        });

        toStartCalendar = new Calendar<>("To Be Started");
        toStartCalendar.setStyle(CalendarEntryStylingFactory.styleCalendarEntry(TO_BE_STARTED));
        startedCalendar = new Calendar<>("Started");
        startedCalendar.setStyle(CalendarEntryStylingFactory.styleCalendarEntry(STARTED));
        doneCalendar = new Calendar<>("Done");
        doneCalendar.setStyle(CalendarEntryStylingFactory.styleCalendarEntry(DONE));

        CalendarSource calendarSource = new CalendarSource("Tasks Source");
        calendarSource.getCalendars().addAll(toStartCalendar, startedCalendar, doneCalendar);
        detailedWeekView.getCalendarSources().add(calendarSource);

        List<TaskDTO> tasks = taskRestController.fetchTasks();
        if (tasks != null) {
            tasks.forEach(taskDTO -> {
                addTaskToCalendar(taskDTO);
                if(taskDTO.taskState() == STARTED){
                    addActiveTaskToDashboard(taskDTO);
                }
            });
        }

        TaskWebSocketClientApp webSocketClientApp = new TaskWebSocketClientApp();
        try {
            webSocketClientApp.start();
        } catch (Exception e) {
            System.out.println("Could not connect to GanttCalendar");
            e.printStackTrace();
            AlertDialog.showAlert("Error", "Could not connect to GanttCalendar: " + e.getMessage());
        }

        webSocketClientApp.getProperty().addListener((_, _, newVal) -> {;
            if (newVal != null && !newVal.isEmpty()) {
                handleTaskChange(newVal);
            }

        });

         chatButton.setOnAction(_ -> {
             if(chat == null){
                 chat = new Chat();
             }
            if(centerContainer.getChildren().contains(chat)){
                centerContainer.getChildren().remove(chat);
            } else {
                centerContainer.getChildren().add(chat);
            }
        });

        logOutButton.setOnAction(_ -> {
            SessionManagerSingleton sessionManagerSingleton = SessionManagerSingleton.getInstance();
            sessionManagerSingleton.removeAttribute("employeeId");
            ApplicationStateManager stateManager = ApplicationStateManager.getInstance();
            stateManager.goBack();
        });
    }

    private void handleTaskChange(String message){
        System.out.println("GanttCalendarController received WS message: " + message);
        if (message.contains("FETCH_TASK:")) {
            String substring = message.substring(message.indexOf(":") + 1);
            Long taskId = Long.valueOf(substring);
            System.out.println(taskId);
            Platform.runLater(() -> updateEntry(taskId));
        }
        if (message.contains("DELETE_TASK:")) {
            String substring = message.substring(message.indexOf(":") + 1);
            Long taskId = Long.valueOf(substring);
            System.out.println(taskId);
            Platform.runLater(() -> deleteEntry(taskId));
        }
    }

    // Helper method to route entries to the correct visual calendar
    private Calendar<TaskDTO> getCalendarForState(TaskState state) {
        if (state == null) return toStartCalendar;
        return switch (state) {
            case TO_BE_STARTED -> toStartCalendar;
            case STARTED -> startedCalendar;
            case DONE -> doneCalendar;
        };
    }

    private void addTaskToCalendar(TaskDTO taskDTO) {

        CalendarEntry<TaskDTO> entry = new CalendarEntry<>(taskDTO, taskDTO.description(), taskDTO.taskId().toString());
        entry.setInterval(taskDTO.startDate(), taskDTO.endDate()    );

        getCalendarForState(taskDTO.taskState()).addEntry(entry);

        entries.put(taskDTO.taskId(), new Pair<>(entry, TaskCardFactory.create(taskDTO)));

    }

    private void removeTaskFromCalendar(Long taskId) {
        Pair<CalendarEntry<TaskDTO>, TaskCardBase> pair = entries.get(taskId);
        CalendarEntry<TaskDTO> entry = pair.getKey();
        entry.getCalendar().removeEntry(entry);
    }

    public void updateEntry(Long taskId) {
        Task<TaskDTO> task = new Task<>() {
            @Override protected TaskDTO call() { return taskRestController.fetchTask(taskId); }
        };

        task.setOnSucceeded(_ -> {
            TaskDTO updatedTask = task.getValue();
            if(!entries.containsKey(taskId)){
                addTaskToCalendar(updatedTask);
            }else{
                entries.get(taskId).getValue().removeTaskPopup();
                removeTaskFromCalendar(updatedTask.taskId());
                if(updatedTask.taskState() == DONE){
                    deleteActiveTaskFromDashboard(taskId);
                }
                addTaskToCalendar(updatedTask);
                addActiveTaskToDashboard(updatedTask);

                entries.get(taskId).getValue().showTaskPopup();
            }
        });

        new Thread(task).start();
    }

    public void deleteEntry(Long taskId) {
        Pair<CalendarEntry<TaskDTO>, TaskCardBase> task = entries.get(taskId);
        removeTaskFromCalendar(task.getKey().getUserObject().taskId());
        if(task.getKey().getUserObject().taskState() == STARTED){
            deleteActiveTaskFromDashboard(taskId);
        }
        task.getValue().removeTaskPopup();
    }

    public void deleteActiveTaskFromDashboard(Long taskId) {
        activeTaskContainer.getChildren().removeIf(node -> node.getId().equals(taskId.toString()));
    }

    private void addActiveTaskToDashboard(TaskDTO taskDTO) {
        if (taskDTO.taskState() == STARTED) {
            Label taskLabel = new Label(taskDTO.description());
            taskLabel.setId(taskDTO.taskId().toString());
            taskLabel.getStyleClass().add("active-task-entry-lbl");
            taskLabel.setMaxWidth(Double.MAX_VALUE);
            taskLabel.setWrapText(true);
            taskLabel.onMouseClickedProperty().set(_ -> {
                Pair<CalendarEntry<TaskDTO>, TaskCardBase> pair = entries.get(taskDTO.taskId());
                if (pair != null && pair.getValue() != null) {
                    pair.getValue().showTaskPopup();
                }
            });
            activeTaskContainer.getChildren().add(taskLabel);
        }
    }
}
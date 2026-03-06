package com.unimib.assignment3.UI.view.controller.impl.layout;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.DayViewBase;
import com.calendarfx.view.DetailedWeekView;
import com.unimib.assignment3.UI.model.controller.TaskRestController;
import com.unimib.assignment3.UI.model.custom_entity.CalendarEntry;
import com.unimib.assignment3.UI.model.dto.TaskDTO;
import com.unimib.assignment3.UI.model.enums.TaskState;
import com.unimib.assignment3.UI.view.components.abstr.TaskCardBase;
import com.unimib.assignment3.UI.view.controller.abstr.DefaultController;
import com.unimib.assignment3.UI.view.factory.CalendarEntryStylingFactory;
import com.unimib.assignment3.UI.view.factory.TaskCardFactory;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

public class GanttCalendarController implements DefaultController {

    @FXML
    private DetailedWeekView detailedWeekView;

    @FXML
    private VBox activeTaskContainer;

    private Map<Long, Pair<CalendarEntry<TaskDTO>, TaskCardBase>> entries;
    private TaskRestController taskRestController;

    // 1. Define a separate Calendar for each state
    private Calendar<TaskDTO> toStartCalendar;
    private Calendar<TaskDTO> startedCalendar;
    private Calendar<TaskDTO> doneCalendar;

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

        detailedWeekView.getWeekView().layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            Rectangle clip = new Rectangle(newVal.getWidth(), newVal.getHeight());
            detailedWeekView.getWeekView().setClip(clip);
        });

        entries = new HashMap<>();
        taskRestController = new TaskRestController();

        detailedWeekView.setEntryDetailsCallback(param -> {
            Entry<?> entry = param.getEntry();
            if (entry != null && entry.getUserObject() instanceof TaskDTO taskDTO) {
                Pair<CalendarEntry<TaskDTO>, TaskCardBase> pair = entries.get(taskDTO.taskId());
                TaskCardBase taskCard = (pair != null && pair.getValue() != null) ? pair.getValue() : TaskCardFactory.create(taskDTO);
                taskCard.showTaskPopup(detailedWeekView);
            }
            return null;
        });

        // 2. Initialize Calendars with Base Styles
        toStartCalendar = new Calendar<>("To Start");
        toStartCalendar.setStyle(CalendarEntryStylingFactory.styleCalendarEntry(TaskState.TO_BE_STARTED));

        startedCalendar = new Calendar<>("Started");
        startedCalendar.setStyle(CalendarEntryStylingFactory.styleCalendarEntry(TaskState.STARTED));

        doneCalendar = new Calendar<>("Done");
        doneCalendar.setStyle(CalendarEntryStylingFactory.styleCalendarEntry(TaskState.DONE));

        CalendarSource calendarSource = new CalendarSource("Tasks Source");
        calendarSource.getCalendars().addAll(toStartCalendar, startedCalendar, doneCalendar);
        detailedWeekView.getCalendarSources().add(calendarSource);

        List<TaskDTO> tasks = taskRestController.fetchTasks();
        if (tasks != null) {
            tasks.forEach(this::addTaskToComponents);
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

    private void addTaskToComponents(TaskDTO taskDTO) {
        addTaskToCalendar(taskDTO);
        addActiveTaskToDashboard(taskDTO);
    }

    private void addTaskToCalendar(TaskDTO taskDTO) {
        LocalDateTime start = taskDTO.startDate() != null ? taskDTO.startDate() : LocalDateTime.now();
        LocalDateTime end = taskDTO.endDate() != null ? taskDTO.endDate() : start.plusHours(1);

        CalendarEntry<TaskDTO> entry = new CalendarEntry<>(taskDTO, taskDTO.description(), taskDTO.taskId().toString());
        entry.setInterval(start, end);

        // 3. Add to the specific calendar based on state
        getCalendarForState(taskDTO.taskState()).addEntry(entry);

        entries.put(taskDTO.taskId(), new Pair<>(entry, TaskCardFactory.create(taskDTO)));
    }

    public void updateEntry(Long taskId) {
        Task<TaskDTO> task = new Task<>() {
            @Override protected TaskDTO call() { return taskRestController.fetchTask(taskId); }
        };

        task.setOnSucceeded(event -> {
            TaskDTO updatedTask = task.getValue();
            activeTaskContainer.getChildren().add(new Label(updatedTask.description()));
        });

        new Thread(task).start();
    }

    private void addActiveTaskToDashboard(TaskDTO taskDTO) {
        if (taskDTO.taskState() == TaskState.STARTED) {
            Label taskLabel = new Label(taskDTO.description());
            taskLabel.getStyleClass().add("active-task-entry-lbl");
            taskLabel.setMaxWidth(Double.MAX_VALUE); // Make it fill the width

            activeTaskContainer.getChildren().add(taskLabel);
        }
    }

}
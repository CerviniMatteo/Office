package com.unimib.assignment3.UI.view.controller.impl.layout;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.DayViewBase;
import com.calendarfx.view.DetailedWeekView;
import com.unimib.assignment3.UI.model.controller.TaskRestController;
import com.unimib.assignment3.UI.model.custom_entity.CalendarEntry;
import com.unimib.assignment3.UI.model.dto.TaskDTO;
import com.unimib.assignment3.UI.view.components.abstr.TaskCardBase;
import com.unimib.assignment3.UI.view.controller.abstr.DefaultController;
import com.unimib.assignment3.UI.view.factory.CalendarEntryStylingFactory;
import com.unimib.assignment3.UI.view.factory.TaskCardFactory;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
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

    // taskId -> (CalendarEntry, TaskCard)
    private Map<Long, Pair<CalendarEntry<TaskDTO>, TaskCardBase>> entries;

    private TaskRestController taskRestController;
    private Calendar<TaskDTO> taskCalendar;

    @FXML
    public void initialize() {

        // ----------------------------------
        // Configure week view
        // ----------------------------------

        detailedWeekView.setNumberOfDays(5);
        detailedWeekView.setAdjustToFirstDayOfWeek(false);
        detailedWeekView.setShowTimeScaleView(true);

        LocalDate monday = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        detailedWeekView.setDate(monday);

        detailedWeekView.getWeekView().setStartTime(LocalTime.of(7, 0));
        detailedWeekView.getWeekView().setEndTime(LocalTime.of(19, 0));
        detailedWeekView.getWeekView()
                .setEarlyLateHoursStrategy(DayViewBase.EarlyLateHoursStrategy.HIDE);

        detailedWeekView.setShowToday(true);
        detailedWeekView.setShowWeekDayHeaderView(true);
        detailedWeekView.setShowAllDayView(false);
        detailedWeekView.setShowScrollBar(false);

        // ----------------------------------
        // Clip week view to remove footer
        // ----------------------------------

        detailedWeekView.getWeekView().layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            Rectangle clip = new Rectangle(newVal.getWidth(), newVal.getHeight());
            detailedWeekView.getWeekView().setClip(clip);
        });

        // ----------------------------------
        // Initialize structures
        // ----------------------------------

        entries = new HashMap<>();
        taskRestController = new TaskRestController();

        // ----------------------------------
        // Custom entry popup
        // ----------------------------------

        detailedWeekView.setEntryDetailsCallback(param -> {
            Entry<?> entry = param.getEntry();

            if (entry != null && entry.getUserObject() instanceof TaskDTO taskDTO) {
                Pair<CalendarEntry<TaskDTO>, TaskCardBase> pair =
                        getEntryPairByTaskId(taskDTO.taskId());

                TaskCardBase taskCard;
                if (pair != null && pair.getValue() != null) {
                    taskCard = pair.getValue();
                } else {
                    taskCard = TaskCardFactory.create(taskDTO);
                    if (pair == null) {
                        entries.put(taskDTO.taskId(), new Pair<>(null, taskCard));
                    }
                }
                taskCard.showTaskPopup(detailedWeekView);
            }
            return null;
        });

        // ----------------------------------
        // Create calendar
        // ----------------------------------

        taskCalendar = new Calendar<>("Tasks");

        // Disable CalendarFX's own built-in coloring so our CSS takes full control
        taskCalendar.setStyle(Calendar.Style.STYLE1);

        CalendarSource calendarSource = new CalendarSource("Source");
        calendarSource.getCalendars().add(taskCalendar);
        detailedWeekView.getCalendarSources().add(calendarSource);

        // ----------------------------------
        // Hook scene-graph listeners BEFORE loading tasks so every entry
        // node CalendarFX creates gets our CSS class applied immediately.
        //
        // Why this is necessary:
        //   entry.getStyleClass() lives on the *model* object.
        //   The rendered pane CalendarFX produces is a completely separate
        //   Node in the scene graph.  The only way to reach it is to wait
        //   for CalendarFX to add it as a child of a WeekDayView, then
        //   read the "entry" key from node.getProperties() — which is the
        //   internal handle CalendarFX itself stores there — and add our
        //   CSS class to that node's own styleClass list.
        // ----------------------------------

        detailedWeekView.getWeekView().getWeekDayViews()
                .forEach(this::hookEntryStylesOnDayView);

        // Also handle day views that might be added later
        detailedWeekView.getWeekView().getWeekDayViews().addListener(
                (ListChangeListener<? super Node>) change -> {
                    while (change.next()) {
                        if (change.wasAdded()) {
                            change.getAddedSubList().forEach(n ->
                                    hookEntryStylesOnDayView((javafx.scene.control.Control) n));
                        }
                    }
                }
        );

        // ----------------------------------
        // Load tasks
        // ----------------------------------

        List<TaskDTO> tasks = taskRestController.fetchTasks();

        if (tasks != null && !tasks.isEmpty()) {
            tasks.forEach(taskDTO -> {

                // Guarantee non-null interval so CalendarFX can render the entry
                TaskDTO resolved = (taskDTO.startDate() == null || taskDTO.endDate() == null)
                        ? new TaskDTO(
                        taskDTO.taskId(),
                        taskDTO.description(),
                        taskDTO.taskState(),
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(3),
                        taskDTO.assignedWorkers())
                        : taskDTO;

                addTaskToCalendar(resolved);
            });
        }

        // Re-hook + re-apply after the first layout pass (CalendarFX may
        // rebuild entry panes once the view has a real size)
        Platform.runLater(this::applyLayoutAdjustments);
    }

    // ----------------------------------------------------------------
    // Attach a ListChangeListener to a WeekDayView's children list.
    // Fires for every node CalendarFX adds (initial render + scrolling).
    // ----------------------------------------------------------------

    private void hookEntryStylesOnDayView(javafx.scene.control.Control dayView) {

        // Style any nodes already present
        dayView.getChildrenUnmodifiable().forEach(this::applyStyleClassToEntryNode);

        // Style nodes added in the future
        dayView.getChildrenUnmodifiable().addListener(
                (ListChangeListener<Node>) change -> {
                    while (change.next()) {
                        if (change.wasAdded()) {
                            change.getAddedSubList()
                                    .forEach(this::applyStyleClassToEntryNode);
                        }
                    }
                }
        );
    }

    // ----------------------------------------------------------------
    // Read the Entry stored by CalendarFX in node.getProperties(),
    // resolve the CSS class from CalendarEntryStylingFactory, and apply
    // it directly to the rendered node's styleClass list.
    // ----------------------------------------------------------------

    private void applyStyleClassToEntryNode(Node node) {

        Object raw = node.getProperties().get("entry");
        if (!(raw instanceof Entry<?> entry)) return;

        if (!(entry.getUserObject() instanceof TaskDTO taskDTO)) return;
        if (taskDTO.taskState() == null) return;

        String cssClass = CalendarEntryStylingFactory.styleCalendarEntry(taskDTO.taskState());

        // Remove any previously applied entry-task-* class, then add the correct one
        node.getStyleClass().removeIf(c -> c.startsWith("entry-task-"));
        if (!node.getStyleClass().contains(cssClass)) {
            node.getStyleClass().add(cssClass);
        }
    }

    // ----------------------------------------------------------------
    // After layout: re-apply time range settings and re-hook listeners
    // since CalendarFX may have rebuilt panes during the layout pass.
    // ----------------------------------------------------------------

    private void applyLayoutAdjustments() {
        detailedWeekView.getWeekView().getWeekDayViews().forEach(dayView -> {
            dayView.setStartTime(LocalTime.of(7, 0));
            dayView.setEndTime(LocalTime.of(19, 0));
            dayView.setEarlyLateHoursStrategy(DayViewBase.EarlyLateHoursStrategy.HIDE);
            hookEntryStylesOnDayView(dayView);
        });
    }

    // ----------------------------------
    // Add entry to calendar
    // ----------------------------------

    private void addTaskToCalendar(TaskDTO taskDTO) {

        CalendarEntry<TaskDTO> entry = new CalendarEntry<>(
                taskDTO,
                taskDTO.description(),
                taskDTO.taskId().toString()
        );

        entry.setInterval(taskDTO.startDate(), taskDTO.endDate());

        taskCalendar.addEntry(entry);

        entries.put(taskDTO.taskId(),
                new Pair<>(entry, TaskCardFactory.create(taskDTO)));
    }

    // ----------------------------------
    // Entry map accessors
    // ----------------------------------

    public Map<Long, Pair<CalendarEntry<TaskDTO>, TaskCardBase>> getEntries() {
        return entries;
    }

    private Pair<CalendarEntry<TaskDTO>, TaskCardBase> getEntryPairByTaskId(Long taskId) {
        return entries.get(taskId);
    }

    // ----------------------------------
    // Remove task
    // ----------------------------------

    private void removeTask(Long taskId) {
        Pair<CalendarEntry<TaskDTO>, TaskCardBase> pair = entries.remove(taskId);
        if (pair != null && pair.getKey() != null) {
            taskCalendar.removeEntry(pair.getKey());
        }
    }

    // ----------------------------------
    // Update entry
    // ----------------------------------

    public void updateEntry(Long taskId) {

        Task<TaskDTO> task = new Task<>() {
            @Override
            protected TaskDTO call() {
                return taskRestController.fetchTask(taskId);
            }
        };

        task.setOnSucceeded(event -> {

            TaskDTO updatedTask = task.getValue();

            Pair<CalendarEntry<TaskDTO>, TaskCardBase> entryPair = getEntryPairByTaskId(taskId);
            if (entryPair == null) return;

            CalendarEntry<TaskDTO> entry = entryPair.getKey();
            TaskCardBase card = entryPair.getValue();

            if (entry != null && updatedTask != null) {

                entry.setUserObject(updatedTask);
                entry.setTitle(updatedTask.description());
                entry.setInterval(
                        updatedTask.startDate() != null ? updatedTask.startDate() : LocalDateTime.now(),
                        updatedTask.endDate()   != null ? updatedTask.endDate()   : LocalDateTime.now().plusHours(3)
                );

                // Re-style the rendered node on the FX thread after the
                // update so state changes (e.g. STARTED -> DONE) are reflected
                Platform.runLater(() -> reapplyStyleForEntry(entry, updatedTask));

                card.removeTaskPopup();

                TaskCardBase newCard = TaskCardFactory.create(updatedTask);
                entries.put(taskId, new Pair<>(entry, newCard));
                newCard.showTaskPopup(detailedWeekView);
            }
        });

        new Thread(task).start();
    }

    // ----------------------------------------------------------------
    // Walk all day-view children to find every rendered pane that
    // belongs to this entry (identity check) and swap its CSS class.
    // ----------------------------------------------------------------

    private void reapplyStyleForEntry(CalendarEntry<TaskDTO> entry, TaskDTO updatedTask) {
        if (updatedTask.taskState() == null) return;

        String newClass = CalendarEntryStylingFactory.styleCalendarEntry(updatedTask.taskState());

        detailedWeekView.getWeekView().getWeekDayViews().forEach(dayView ->
                dayView.getChildrenUnmodifiable().forEach(node -> {
                    if (node.getProperties().get("entry") == entry) {
                        node.getStyleClass().removeIf(c -> c.startsWith("entry-task-"));
                        if (!node.getStyleClass().contains(newClass)) {
                            node.getStyleClass().add(newClass);
                        }
                    }
                })
        );
    }
}
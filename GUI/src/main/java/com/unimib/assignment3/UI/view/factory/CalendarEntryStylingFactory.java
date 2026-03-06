package com.unimib.assignment3.UI.view.factory;
import com.unimib.assignment3.UI.model.enums.TaskState;

public class CalendarEntryStylingFactory {
    public static String styleCalendarEntry(TaskState state) {
        return switch (state) {
            case TO_BE_STARTED -> "entry-task-to-start";
            case STARTED -> "entry-task-started";
            case DONE -> "entry-task-done";
        };
    }
}

package com.unimib.assignment3.UI.view.factory;
import com.calendarfx.model.Calendar;
import com.unimib.assignment3.UI.model.enums.TaskState;

public class CalendarEntryStylingFactory {
    public static Calendar.Style styleCalendarEntry(TaskState state) {
        return switch (state) {
            case TO_BE_STARTED -> Calendar.Style.STYLE5;
            case STARTED -> Calendar.Style.STYLE2;
            case DONE -> Calendar.Style.STYLE4;
        };
    }
}

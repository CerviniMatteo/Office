package com.unimib.assignment3.UI.model.custom_entity;

import com.calendarfx.model.Entry;

public class CalendarEntry <T> extends Entry<T> {
    public CalendarEntry(T child, String description, String id) {
        super(description, id);
        setUserObject(child);
    }
}

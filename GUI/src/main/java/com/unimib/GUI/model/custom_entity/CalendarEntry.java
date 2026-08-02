package com.unimib.GUI.model.custom_entity;

public class CalendarEntry<T> {

    private final T child;
    private final String description;
    private final String id;

    public CalendarEntry(T child, String description, String id) {
        this.child = child;
        this.description = description;
        this.id = id;
    }

    public T getChild() {
        return child;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }
}

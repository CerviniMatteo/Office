package com.unimib.GUI.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class SessionManager {

    private final ConcurrentMap<String, Object> sessionAttributes =
            new ConcurrentHashMap<>();

    public void setAttribute(String key, Object value) {
        sessionAttributes.put(key, value);
    }

    public void removeAttribute(String key) {
        sessionAttributes.remove(key);
    }

    public Object getAttribute(String key) {
        return sessionAttributes.get(key);
    }

    public <T> T getAttribute(String key, Class<T> type) {
        Object value = sessionAttributes.get(key);

        if (value == null) {
            return null;
        }

        if (!type.isInstance(value)) {
            throw new IllegalStateException(
                    "Session attribute '" + key +
                            "' is not a " + type.getSimpleName()
            );
        }

        return type.cast(value);
    }

    public void clear() {
        sessionAttributes.clear();
    }
}
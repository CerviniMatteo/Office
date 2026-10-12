package com.unimib.GUI.utils;

import java.util.HashMap;
import java.util.Map;

public final class SessionManager {

    private final Map<String, Object> sessionAttributes =
            new HashMap<>();

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
package com.unimib.assignment3.UI.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class SessionManagerSingleton {

    private static volatile SessionManagerSingleton INSTANCE;
    private static final ConcurrentMap <String, Object> sessionAttributes = new ConcurrentHashMap<>();

    private SessionManagerSingleton() {}

    public static SessionManagerSingleton getInstance() {
        if (INSTANCE == null) {
            synchronized (SessionManagerSingleton.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SessionManagerSingleton();
                }
            }
        }
        return INSTANCE;
    }

    public void setAttribute(String key, Object value) {
        sessionAttributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return sessionAttributes.get(key);
    }
}

package com.unimib.assignment3.web_socket_client;

import jakarta.annotation.Nonnull;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;

/**
 * WebSocket client that subscribes to task-related topics.
 */
public class TaskWebSocketClientApp extends AbstractWebSocketClientApp {

    private final StringProperty property = new SimpleStringProperty();

    @Override
    protected StompSessionHandler createSessionHandler() {
        return new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(@NonNull StompSession session, @Nonnull StompHeaders connectedHeaders) {
                System.out.println("Connected to WebSocket server");
                session.subscribe("/topic/task", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(@NonNull StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(@Nonnull StompHeaders headers, Object payload) {
                        String message = (String) payload;
                        System.out.println("Received message: " + message);
                        property.set(message);
                    }
                });
            }
        };
    }

    public ObservableValue<String> getProperty() {
        return property;
    }
}
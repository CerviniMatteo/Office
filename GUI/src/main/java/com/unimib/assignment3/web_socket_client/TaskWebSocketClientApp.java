package com.unimib.assignment3.web_socket_client;

import jakarta.annotation.Nonnull;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import java.lang.reflect.Type;

/**
 * WebSocket client helper that subscribes to task-related topics and updates the TaskLayout.
 */
public class TaskWebSocketClientApp {

    private final StringProperty property = new SimpleStringProperty();

    /**
     * Start the WebSocket STOMP client and subscribe to task updates.
     * @throws Exception if the connection fails
     */
    public void start() throws Exception {
        WebSocketStompClient stompClient =
                new WebSocketStompClient(new StandardWebSocketClient());

        stompClient.setMessageConverter(new StringMessageConverter());


        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, @Nonnull StompHeaders connectedHeaders) {
                System.out.println("Connected to: WebSocketStompClient server");

                session.subscribe("/topic/task", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(@Nonnull StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(@Nonnull StompHeaders headers, Object payload) {
                        String message = (String) payload;
                        System.out.println("Received message: " + message);
                        Platform.runLater(() -> {
                            try {
                                property.set(null);
                            } catch (Exception ignored) {}
                            property.set(message);
                        });
                    }
                });
            }
        };

        stompClient.connectAsync("ws://localhost:8080/ws", sessionHandler).get();
    }


    public ObservableValue<String> getProperty() {
        return property;
    }
}
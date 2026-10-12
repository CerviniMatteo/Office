package com.unimib.GUI.web_socket_client;

import jakarta.annotation.Nonnull;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

import static com.unimib.GUI.constants.Rest.WS_ENDPOINT;

/**
 * WebSocket client helper that subscribes to task-related topics and updates the TaskLayout.
 */
public class TaskWebSocketClientApp {

    private final StringProperty property = new SimpleStringProperty();
    private final WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
    private StompSession session;
    private CompletableFuture<StompSession> connectionFuture;

    /**
     * Start the WebSocket STOMP client and subscribe to task updates.
     * Connection is asynchronous to avoid blocking the JavaFX thread.
     */
    public synchronized void start() {
        if (session != null && session.isConnected()) {
            return;
        }

        if (connectionFuture != null && !connectionFuture.isDone()) {
            return;
        }

        stompClient.setMessageConverter(new StringMessageConverter());

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, @Nonnull StompHeaders connectedHeaders) {
                System.out.println("Connected to: WebSocketStompClient server");
                TaskWebSocketClientApp.this.session = session;

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
                            } catch (Exception ignored) {
                            }
                            property.set(message);
                        });
                    }
                });
            }

            @Override
            public void handleException(StompSession session, StompCommand command,
                                        StompHeaders headers, byte[] payload, Throwable exception) {
                System.err.println("STOMP error: " + exception.getMessage());
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.err.println("Transport error: " + exception.getMessage());
            }
        };

        connectionFuture = stompClient.connectAsync(WS_ENDPOINT, sessionHandler);
        connectionFuture.whenComplete((connectedSession, throwable) -> {
            if (throwable != null) {
                System.err.println("Could not connect to TaskContainer WebSocket: " + throwable.getMessage());
                return;
            }

            synchronized (TaskWebSocketClientApp.this) {
                session = connectedSession;
            }
        });
    }

    public synchronized void stop() {
        if (connectionFuture != null && !connectionFuture.isDone()) {
            connectionFuture.cancel(true);
        }
        connectionFuture = null;

        if (session != null) {
            try {
                if (session.isConnected()) {
                    session.disconnect();
                }
            } finally {
                session = null;
            }
        }
        stompClient.stop();
    }


    public ObservableValue<String> getProperty() {
        return property;
    }
}
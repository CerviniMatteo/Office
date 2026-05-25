package com.unimib.assignment3.web_socket_client;

import jakarta.annotation.Nonnull;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;

/**
 * WebSocket client that subscribes to chat topics and supports sending messages.
 */
public class ChatWebSocketClientApp extends AbstractWebSocketClientApp {

    private StompSession session;
    private final StringProperty receivedMessage = new SimpleStringProperty();

    @Override
    protected StompSessionHandler createSessionHandler() {
        return new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(@NonNull StompSession s, @Nonnull StompHeaders connectedHeaders) {
                System.out.println("Connected to WebSocket server");
                ChatWebSocketClientApp.this.session = s;

                s.subscribe("/topic/chat", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(@Nonnull StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(@Nonnull StompHeaders headers, Object payload) {
                        String message = (String) payload;
                        System.out.println("Received message: " + message);
                        Platform.runLater(() -> receivedMessage.set(message));
                    }
                });
            }

            @Override
            public void handleException(@NonNull StompSession s, StompCommand command,
                                        @NonNull StompHeaders headers, @NonNull byte[] payload,
                                        @NonNull Throwable exception) {
                System.err.println("STOMP error: " + exception.getMessage());
            }

            @Override
            public void handleTransportError(@NonNull StompSession s, @NonNull Throwable exception) {
                System.err.println("Transport error: " + exception.getMessage());
            }
        };
    }

    @Override
    protected void connect(WebSocketStompClient stompClient, String wsUrl) throws Exception {
        this.session = stompClient.connectAsync(wsUrl, createSessionHandler()).get();
    }

    public ObservableValue<String> receiveMessage() {
        return receivedMessage;
    }

    public void sendMessage(String message) {
        if (session != null && session.isConnected()) {
            session.send("/app/chat", message);
            System.out.println("Sent message: " + message);
        } else {
            System.err.println("WebSocket not connected — cannot send message");
        }
    }
}
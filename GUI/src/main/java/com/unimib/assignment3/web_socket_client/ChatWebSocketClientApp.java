package com.unimib.assignment3.web_socket_client;

import jakarta.annotation.Nonnull;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import org.springframework.lang.NonNull;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;

/**
 * WebSocket client helper that subscribes to chat-related topics
 * and allows sending messages to the server.
 */
public class ChatWebSocketClientApp {

    // Singleton instance to avoid multiple connections from different controllers
    private static final ChatWebSocketClientApp INSTANCE = new ChatWebSocketClientApp();

    private StompSession session;
    private final StringProperty receivedMessage = new SimpleStringProperty();
    /**
     * Start the WebSocket STOMP client and subscribe to messages.
     * @throws Exception if the connection fails
     */
    private ChatWebSocketClientApp() {
        // private constructor for singleton
    }

    public static ChatWebSocketClientApp getInstance() {
        return INSTANCE;
    }

    public synchronized void start() throws Exception {
        // If already connected, do nothing
        if (session != null && session.isConnected()) {
            return;
        }

        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new StringMessageConverter());

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, @Nonnull StompHeaders connectedHeaders) {
                System.out.println("Connected to: ChatWebSocketClientApp server");
                ChatWebSocketClientApp.this.session = session;

                session.subscribe("/topic/chat", new StompFrameHandler() {
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
            public void handleException(@NonNull StompSession session, StompCommand command,
                                        @NonNull StompHeaders headers, @NonNull byte[] payload, Throwable exception) {
                System.err.println("STOMP error: " + exception.getMessage());
            }

            @Override
            public void handleTransportError(@NonNull StompSession session, Throwable exception) {
                System.err.println("Transport error: " + exception.getMessage());
            }
        };

        // connect and wait for session (throws if fails)
        this.session = stompClient.connectAsync("ws://localhost:8080/ws", sessionHandler).get();
    }

    public ObservableValue<String> receiveMessage() {
        return receivedMessage;
    }

    /**
     * Send a message to the server.
     *
     * @param message     contenuto del messaggio
     */
    public void sendMessage(String message) {
        if (session != null && session.isConnected()) {
            session.send("/app/send", message);
            System.out.println("Sent message: " + message);
        } else {
            System.out.println("WebSocket not connected");
        }
    }
}
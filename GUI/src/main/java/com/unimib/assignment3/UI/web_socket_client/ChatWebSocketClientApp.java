package com.unimib.assignment3.UI.web_socket_client;

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
 * WebSocket client helper that subscribes to chat-related topics
 * and allows sending messages to the server.
 */
public class ChatWebSocketClientApp {

    private StompSession session;
    private final StringProperty receivedMessage = new SimpleStringProperty();
    /**
     * Start the WebSocket STOMP client and subscribe to messages.
     * @throws Exception if the connection fails
     */
    public void start() throws Exception {
        WebSocketStompClient stompClient =
                new WebSocketStompClient(new StandardWebSocketClient());

        stompClient.setMessageConverter(new StringMessageConverter());

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {

            @Override
            public void afterConnected(StompSession session, @Nonnull StompHeaders connectedHeaders) {
                System.out.println("Connected to WebSocket server");

                // salva la sessione
                ChatWebSocketClientApp.this.session = session;

                // subscribe al topic
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
            public void handleException(StompSession session, StompCommand command,
                                        StompHeaders headers, byte[] payload, Throwable exception) {
                System.err.println("STOMP error: " + exception.getMessage());
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.err.println("Transport error: " + exception.getMessage());
            }
        };

        // connessione
        this.session = stompClient
                .connectAsync("ws://localhost:8080/ws", sessionHandler)
                .get();
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
            session.send("/app/chat", message);
            System.out.println("Sent message: " + message);
        } else {
            System.out.println("WebSocket not connected");
        }
    }
}
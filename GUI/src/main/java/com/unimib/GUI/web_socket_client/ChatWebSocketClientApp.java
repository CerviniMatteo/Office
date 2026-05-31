package com.unimib.GUI.web_socket_client;

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
 *
 * Singleton so that baseInitialize() can be called on every controller
 * construction without opening a new connection each time — start() is
 * a no-op if already connected.
 * Call resetInstance() on logout AFTER stop() to get a clean slate on the next login.
 */
public class ChatWebSocketClientApp {

    private static volatile ChatWebSocketClientApp INSTANCE;

    private StompSession session;
    private WebSocketStompClient stompClient;
    private final StringProperty receivedMessage = new SimpleStringProperty();
    // Keep track of listeners so we can remove them if stop() / reset is called
    private final java.util.List<javafx.beans.value.ChangeListener<String>> listeners = new java.util.ArrayList<>();

    private ChatWebSocketClientApp() {}

    public static ChatWebSocketClientApp getInstance() {
        if (INSTANCE == null) {
            synchronized (ChatWebSocketClientApp.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ChatWebSocketClientApp();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Null the singleton so the next login gets a clean instance.
     * Call on logout AFTER stop().
     */
    public static synchronized void resetInstance() {
        INSTANCE = null;
    }

    /**
     * Start the WebSocket STOMP client and subscribe to messages.
     * No-op if already connected.
     *
     * @throws Exception if the connection fails
     */
    public synchronized void start() throws Exception {
        if (session != null && session.isConnected()) {
            return;
        }

        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new StringMessageConverter());

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, @Nonnull StompHeaders connectedHeaders) {
                System.out.println("Connected to ChatWebSocketClientApp server");
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
                        // Force-null first so listeners fire even if the JSON string
                        // is identical to the previous one (JavaFX skips no-change sets)
                        Platform.runLater(() -> {
                            receivedMessage.set(null);
                            receivedMessage.set(message);
                        });
                    }
                });
            }

            @Override
            public void handleException(@NonNull StompSession session, StompCommand command,
                                        @NonNull StompHeaders headers, @NonNull byte[] payload,
                                        Throwable exception) {
                System.err.println("STOMP error: " + exception.getMessage());
            }

            @Override
            public void handleTransportError(@NonNull StompSession session, Throwable exception) {
                System.err.println("Transport error: " + exception.getMessage());
            }
        };

        this.session = stompClient.connectAsync("ws://localhost:8080/ws", sessionHandler).get();
    }

    /**
     * Disconnect the STOMP session and shut down the client.
     * Safe to call even if not connected.
     */
    public synchronized void stop() {
        try {
            if (session != null) {
                try {
                    if (session.isConnected()) {
                        session.disconnect();
                    }
                } catch (Exception e) {
                    System.err.println("Error disconnecting session: " + e.getMessage());
                } finally {
                    session = null;
                }
            }
        } finally {
            if (stompClient != null) {
                try {
                    stompClient.stop();
                } catch (Exception e) {
                    System.err.println("Error stopping stompClient: " + e.getMessage());
                } finally {
                    stompClient = null;
                }
            }
            // Defensive cleanup: remove any listeners that controllers may have left behind
            removeAllListeners();
        }
    }

    /**
     * Observable property for incoming messages.
     * Callers MUST remove their listener before calling stop().
     */
    public ObservableValue<String> receiveMessage() {
        return receivedMessage;
    }

    /**
     * Add a ChangeListener and track it so it can be removed later (stop()/reset).
     */
    public synchronized void addReceiveListener(javafx.beans.value.ChangeListener<String> listener) {
        if (listener == null) return;
        receivedMessage.addListener(listener);
        listeners.add(listener);
    }

    /**
     * Remove a previously added ChangeListener.
     */
    public synchronized void removeReceiveListener(javafx.beans.value.ChangeListener<String> listener) {
        if (listener == null) return;
        receivedMessage.removeListener(listener);
        listeners.remove(listener);
    }

    /**
     * Remove and clear all tracked listeners. Defensive — controllers should remove their
     * own listeners, but this prevents duplicated handlers if they fail to do so.
     */
    private synchronized void removeAllListeners() {
        for (javafx.beans.value.ChangeListener<String> l : new java.util.ArrayList<>(listeners)) {
            try {
                receivedMessage.removeListener(l);
            } catch (Exception e) {
                // ignore
            }
        }
        listeners.clear();
    }

    /**
     * Send a message to the server.
     *
     * @param message message payload
     */
    public void sendMessage(String message) {
        if (session != null && session.isConnected()) {
            session.send("/app/send", message);
            System.out.println("Sent message: " + message);
        } else {
            System.err.println("WebSocket not connected — message dropped: " + message);
        }
    }
}
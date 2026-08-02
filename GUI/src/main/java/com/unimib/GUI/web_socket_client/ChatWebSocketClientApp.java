package com.unimib.GUI.web_socket_client;

import jakarta.annotation.Nonnull;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static com.unimib.GUI.constants.Rest.WS_ENDPOINT;

public class ChatWebSocketClientApp {

    private static volatile ChatWebSocketClientApp INSTANCE;

    private StompSession session;
    private WebSocketStompClient stompClient;

    private final List<Consumer<String>> listeners = new CopyOnWriteArrayList<>();

    private ChatWebSocketClientApp() {}

    public static ChatWebSocketClientApp getInstance() {
        if (INSTANCE == null) {
            synchronized (ChatWebSocketClientApp.class) {
                if (INSTANCE == null)
                    INSTANCE = new ChatWebSocketClientApp();
            }
        }
        return INSTANCE;
    }


    public synchronized void start() throws Exception {
        if (session != null && session.isConnected())
            return;

        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new StringMessageConverter());

        StompSessionHandler handler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, @Nonnull StompHeaders headers) {
                ChatWebSocketClientApp.this.session = session;

                session.subscribe("/topic/chat",
                        new StompFrameHandler() {
                            @Override
                            public java.lang.reflect.Type getPayloadType(@Nonnull StompHeaders headers) {
                                return String.class;
                            }

                            @Override
                            public void handleFrame(@Nonnull StompHeaders headers, Object payload) {
                                String message = (String) payload;
                                listeners.forEach(listener -> listener.accept(message));
                            }
                        });
            }
        };

        session = stompClient.connectAsync(WS_ENDPOINT, handler) .get();
    }


    public void sendMessage(String message) {
        if(session != null && session.isConnected())
            session.send("/app/send", message);
    }



    public synchronized void stop() {
        if(session != null && session.isConnected())
            session.disconnect();

        if(stompClient != null)
            stompClient.stop();

        session = null;
        stompClient = null;

        listeners.clear();
    }



    public void addMessageListener(Consumer<String> listener) {
        listeners.add(listener);
    }


    public void removeMessageListener(Consumer<String> listener) {
        listeners.remove(listener);
    }
}
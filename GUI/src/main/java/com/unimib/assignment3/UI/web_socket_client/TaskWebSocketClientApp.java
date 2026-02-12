package com.unimib.assignment3.UI.web_socket_client;

import com.unimib.assignment3.UI.components.TaskLayout;
import jakarta.annotation.Nonnull;
import javafx.application.Platform;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import java.lang.reflect.Type;

public class TaskWebSocketClientApp {

    private final TaskLayout taskLayout;

    public TaskWebSocketClientApp(TaskLayout taskLayout) {
        this.taskLayout = taskLayout;
    }

    public void start() throws Exception {
        WebSocketStompClient stompClient =
                new WebSocketStompClient(new StandardWebSocketClient());

        stompClient.setMessageConverter(new StringMessageConverter());


        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, @Nonnull StompHeaders connectedHeaders) {
                System.out.println("Connesso al WebSocket server!");

                session.subscribe("/topic/task", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(@Nonnull StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(@Nonnull StompHeaders headers, Object payload) {
                        String message = (String) payload;
                        System.out.println("Messaggio ricevuto: " + message);

                        if (message.contains("FETCH_TASKS:")) {
                            String substring = message.substring(message.indexOf(":") + 1);
                            Long taskId = Long.valueOf(substring);

                            Platform.runLater(() -> taskLayout.updateTaskDetails(taskId));
                        }
                    }
                });
            }
        };

        StompSession session = stompClient.connectAsync("ws://localhost:8080/ws", sessionHandler).get();
        System.out.println("Session ID: " + session.getSessionId() + "\nLayout ID:" + System.identityHashCode(taskLayout));
    }
}

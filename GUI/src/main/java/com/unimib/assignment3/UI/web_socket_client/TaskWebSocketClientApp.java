package com.unimib.assignment3.UI.web_socket_client;

import com.unimib.assignment3.UI.components.TaskLayout;
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
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                System.out.println("Connesso al WebSocket server!");

                session.subscribe("/topic/tasks", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        String message = (String) payload;
                        System.out.println("Messaggio ricevuto: " + message);

                        if ("FETCH_TASKS".equals(message)) {
                            Platform.runLater(taskLayout::loadTasks);
                        }
                    }
                });
            }
        };

        StompSession session = stompClient.connect("ws://localhost:8080/ws", sessionHandler).get();
        System.out.println("Session ID: " + session.getSessionId() + "\nLayout ID:" + System.identityHashCode(taskLayout));
    }
}

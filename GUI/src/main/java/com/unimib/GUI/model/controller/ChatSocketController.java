package com.unimib.GUI.model.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimib.GUI.model.dto.MessageDTO;
import com.unimib.GUI.web_socket_client.ChatWebSocketClientApp;
import javafx.concurrent.Task;

import java.util.function.Consumer;

public class ChatSocketController {

    private final ChatWebSocketClientApp client;
    private final ObjectMapper mapper;


    public ChatSocketController() {
        client = ChatWebSocketClientApp.getInstance();
        mapper = new ObjectMapper();
    }


    public Task<Void> connect() {
        return new Task<>() {
            @Override
            protected Void call() {
                try {
                    client.start();
                } catch(Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
    }



    public Task<Void> sendMessage(MessageDTO message) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                client.sendMessage(mapper.writeValueAsString(message));
                return null;
            }
        };
    }

    public Task<Void> disconnect() {
        return new Task<>() {
            @Override
            protected Void call() {
                client.stop();
                return null;
            }
        };
    }

    public void addReceiveListener(Consumer<String> listener) {
        client.addMessageListener(listener);
    }

    public void removeReceiveListener(Consumer<String> listener) {
        client.removeMessageListener(listener);
    }
}
package com.unimib.assignment3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimib.assignment3.notifier.ChatObserverNotifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChatStompController {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ChatObserverNotifier notifier;

    public ChatStompController(ChatObserverNotifier notifier) {
        this.notifier = notifier;
    }

    @MessageMapping("/send")
    public void handleMessage(String message){
        System.out.println("Message received via STOMP: " + message);
        notifier.notifyOnFetchAllChatSubscribers(message);
    }
}
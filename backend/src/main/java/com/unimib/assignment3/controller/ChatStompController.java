package com.unimib.assignment3.controller;

import com.unimib.assignment3.notifier.ChatObserverNotifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChatStompController {

    private final ChatObserverNotifier notifier;

    public ChatStompController(ChatObserverNotifier notifier) {
        this.notifier = notifier;
    }

    @MessageMapping("/chat")
    public void handleMessage(String message) {
        System.out.println("Message received via STOMP: " + message);
        notifier.notifyOnFetchAllChatSubscribers(message);
    }
}
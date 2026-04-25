package com.unimib.assignment3.notifier;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatObserverNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatObserverNotifier(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyOnFetchAllChatSubscribers(String message) {
        System.out.println("notifyAllObservers called");
        messagingTemplate.convertAndSend("/topic/chat", message);
    }
}

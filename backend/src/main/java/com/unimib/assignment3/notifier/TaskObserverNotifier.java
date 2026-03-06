package com.unimib.assignment3.notifier;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskObserverNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    public TaskObserverNotifier(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyAllTaskObservers(Long taskId) {
        System.out.println("notifyAllObservers called");
        String message = "FETCH_TASK:" + taskId;
        messagingTemplate.convertAndSend("/topic/task", message);
    }
}

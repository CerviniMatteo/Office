package com.unimib.assignment3.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.unimib.assignment3.DTO.MessageDTO;
import com.unimib.assignment3.DTO.ReadReceiptDTO;
import com.unimib.assignment3.facade.Facade;
import com.unimib.assignment3.notifier.ChatObserverNotifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChatStompController {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ChatObserverNotifier notifier;
    private final Facade facade;

    public ChatStompController(ChatObserverNotifier notifier, Facade facade) {
        this.notifier = notifier;
        this.facade = facade;
    }

    @MessageMapping("/send")
    public void handleMessage(String message) throws JsonProcessingException {
        System.out.println("Message received via STOMP: " + message);
        MessageDTO messageDTO = mapper.readValue(message, MessageDTO.class);

        facade.appendUnreadMessage(messageDTO.chatId(), messageDTO.message());

        System.out.println(messageDTO.message());
        notifier.notifyOnFetchAllChatSubscribers(message);
    }

    @MessageMapping("/remove")
    public void removeUnreadMessage(String message) throws JsonProcessingException {
        ReadReceiptDTO messageObj = mapper.readValue(message, ReadReceiptDTO.class);
        System.out.println("Message received via STOMP: " + message);
        facade.removeUnreadMessage(messageObj.chatId(), messageObj.message());
    }
}
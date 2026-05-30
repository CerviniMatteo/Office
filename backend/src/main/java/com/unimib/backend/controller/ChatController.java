package com.unimib.backend.controller;

import com.unimib.backend.facade.Facade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
public class ChatController {
    @Autowired
    Facade facade;

    @GetMapping("/chatIds/{employeeId}")
    public List<Long> getChats(@PathVariable Long employeeId) {
        System.out.println("fetch chats by employeeId: " + employeeId);
        return facade.findChatRoomIdByUserId(employeeId);
    }
}

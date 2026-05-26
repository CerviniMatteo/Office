package com.unimib.assignment3.controller;

import com.unimib.assignment3.DTO.MessageDTO;
import com.unimib.assignment3.facade.Facade;
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

package com.unimib.assignment3.controller;

import com.unimib.assignment3.DTO.Message;
import com.unimib.assignment3.repository.UserChatMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
public class ChatController {
    @Autowired
    UserChatMappingRepository userChatMappingRepository;

    @GetMapping("/{employeeId}")
    public List<Long> getChats(@PathVariable Long employeeId) {
        System.out.println("fetch chats by employeeId: " + employeeId);

        return userChatMappingRepository.findRoomIdsByUserId(employeeId);
    }
}

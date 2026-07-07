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

    @GetMapping("employeeIds/{employeeId}")
    public List<Long> getUnMatchedEmployeeIds(@PathVariable Long employeeId) {
        System.out.println("fetch un matched employeeIds, by employee: " + employeeId);
        return facade.findUnMatchedEmployeeIds(employeeId);
    }

    @GetMapping("create/{employeeId1}/{employeeId2}")
    public void createChat(@PathVariable Long employeeId1, @PathVariable Long employeeId2) {
        System.out.println("chat created by employeeId1" + employeeId1 +  " and by employeeId2:" + employeeId2);
        facade.createChat(employeeId1);
    }
}

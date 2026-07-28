package com.unimib.backend.controller;

import com.unimib.backend.DTO.ChatEmployeePair;
import com.unimib.backend.DTO.ChatInfoDTO;
import com.unimib.backend.DTO.UserInfoDTO;
import com.unimib.backend.facade.ChatFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
public class ChatController {
    @Autowired
    ChatFacade facade;

    @GetMapping("/chatIds/{employeeId}")
    public List<ChatInfoDTO> getChats(@PathVariable Long employeeId) {
        System.out.println("fetch chats by employeeId: " + employeeId);
        return facade.findChatInfoByUserId(employeeId);
    }

    @GetMapping("/unmatchedEmployeeInfos/{employeeId}")
    public List<UserInfoDTO> getUnMatchedEmployeeIds(@PathVariable Long employeeId) {
        System.out.println("fetch unmatched employeeIds, by employee: " + employeeId);
        return facade.findUnMatchedEmployeeIds(employeeId);
    }

    @PostMapping("/create")
    public void createChat(@RequestBody ChatEmployeePair chatEmployeePair) {
        System.out.println("chat created by employeeId1" + chatEmployeePair.employeeId1() +  " and by employeeId2:" + chatEmployeePair.employeeId2());
        facade.bindChat(chatEmployeePair.employeeId1(), chatEmployeePair.employeeId2());
    }
}

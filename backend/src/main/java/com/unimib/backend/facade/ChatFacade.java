package com.unimib.backend.facade;

import com.unimib.backend.DTO.ChatInfoDTO;
import com.unimib.backend.DTO.UserInfoDTO;
import com.unimib.backend.POJO.WorkerChatMapping;
import com.unimib.backend.service.UserChatMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatFacade {

    @Autowired
    private UserChatMappingService userChatMappingService;

    public WorkerChatMapping createChat(Long employeeId) {
        return userChatMappingService.createChat(employeeId);
    }

    public void bindChat(Long employeeId1, Long employeeId2) {
        userChatMappingService.bindChat(employeeId1, employeeId2);
    }

    public WorkerChatMapping saveChat(WorkerChatMapping workerChatMapping) {
        return userChatMappingService.saveChat(workerChatMapping);
    }

    public List<ChatInfoDTO> findChatInfoByUserId(Long employeeId) {
        return userChatMappingService.findChatInfoByUserId(employeeId);
    }

    public List<UserInfoDTO> findUnMatchedEmployeeIds(Long employeeId) {
        return userChatMappingService.findUnMatchedEmployeeIds(employeeId);
    }
}
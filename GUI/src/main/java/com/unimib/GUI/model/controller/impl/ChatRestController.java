package com.unimib.GUI.model.controller.impl;

import com.unimib.GUI.model.controller.BaseRestController;
import com.unimib.GUI.model.dto.ChatEmployeePair;
import com.unimib.GUI.model.dto.ChatInfoDTO;
import com.unimib.GUI.model.dto.UserInfoDTO;
import javafx.concurrent.Task;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

import static com.unimib.GUI.constants.Rest.*;

public class ChatRestController extends BaseRestController {


    public List<ChatInfoDTO> getChats(Long employeeId) {
        return getMany(
                CHATS_ENDPOINT + "/" + employeeId,
                new ParameterizedTypeReference<List<ChatInfoDTO>>() {}
        );
    }

    public List<UserInfoDTO> getUnMatchedEmployeeInfos(Long employeeId) {
        return getMany(
                UNMATCHED_EMPLOYEE_INFOS_ENDPOINT + "/" + employeeId,
                new ParameterizedTypeReference<List<UserInfoDTO>>() {}
        );
    }

    public Task<Void> createChat(Long employeeId1, Long employeeId2) {
           return post(CREATE_CHATS_ENDPOINT,
                        new ChatEmployeePair(employeeId1, employeeId2),
                        Void.class
           );
    }
}
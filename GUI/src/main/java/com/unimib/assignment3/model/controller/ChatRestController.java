package com.unimib.assignment3.model.controller;

import com.unimib.assignment3.model.controller.base.BaseRestController;
import com.unimib.assignment3.model.dto.MessageDTO;
import javafx.concurrent.Task;
import org.springframework.core.ParameterizedTypeReference;


import java.util.List;
import static com.unimib.assignment3.constants.Rest.CHATS_ENDPOINT;
import static com.unimib.assignment3.constants.Rest.UNREAD_MESSAGES;

public class ChatRestController extends BaseRestController {

    public Task<List<Long>> getChats(Long employeeId) {
        return new Task<>() {
            @Override
            protected List<Long> call() {
                return getMany(CHATS_ENDPOINT + "/" + employeeId, new ParameterizedTypeReference<>() {});
            }
        };
    }

    public Task<List<MessageDTO>> getUnreadMessages(Long roomId, Long receiverId) {
        return new Task<>() {
            @Override
            protected List<MessageDTO> call() {
                return getMany(UNREAD_MESSAGES + "/" + roomId + "/" + receiverId, new ParameterizedTypeReference<>() {});
            }
        };
    }
}
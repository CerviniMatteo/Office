package com.unimib.GUI.model.controller.impl;

import com.unimib.GUI.model.controller.BaseRestController;
import javafx.concurrent.Task;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

import static com.unimib.GUI.constants.Rest.CHATS_ENDPOINT;

public class ChatRestController extends BaseRestController {


    public Task<List<Long>> getChats(Long employeeId) {
        return new Task<>() {
            @Override
            protected List<Long> call() {
                return getMany(CHATS_ENDPOINT + "/" + employeeId, new ParameterizedTypeReference<>() {}
                );}
        };
    }
}
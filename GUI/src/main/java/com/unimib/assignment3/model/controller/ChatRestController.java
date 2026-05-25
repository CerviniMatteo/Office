package com.unimib.assignment3.model.controller;

import com.unimib.assignment3.model.controller.base.BaseRestController;
import javafx.concurrent.Task;
import org.springframework.core.ParameterizedTypeReference;


import java.util.List;

import static com.unimib.assignment3.constants.Rest.BASE_CHAT_ENDPOINT;

public class ChatRestController extends BaseRestController {

    public Task<List<Long>> getChats(Long employeeId) {
        return new Task<>() {
            @Override
            protected List<Long> call() {
                return getMany(BASE_CHAT_ENDPOINT + "/" + employeeId, new ParameterizedTypeReference<>() {});
            }
        };
    }
}
package com.unimib.assignment3.UI.model.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unimib.assignment3.UI.utils.RestHelper;
import javafx.concurrent.Task;

import java.net.http.HttpResponse;
import java.util.List;

import static com.unimib.assignment3.UI.constants.Rest.BASE_CHAT_ENDPOINT;

public class ChatRestController {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static Task<List<Long>> getChats(Long employeeId) {
        return new Task<>() {
            @Override
            protected List<Long> call() {
                System.out.println("CHAT TASK STARTED");
                try {
                    String url = BASE_CHAT_ENDPOINT + "/" + employeeId;
                    System.out.println("URL: " + url);

                    HttpResponse<String> response =
                            RestHelper.createGetRequest(url);

                    if (response == null) {
                        System.out.println("Response is NULL ❌");
                        return List.of();
                    }

                    System.out.println("STATUS: " + response.statusCode());
                    System.out.println("BODY: " + response.body());

                    return mapper.readValue(
                            response.body(),
                            new TypeReference<List<Long>>() {}
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                    return List.of();
                }
            }
        };
    }
}
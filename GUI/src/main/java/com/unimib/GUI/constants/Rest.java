package com.unimib.GUI.constants;

public class Rest {

    private static final String DEFAULT_BASE_ENDPOINT = "http://localhost:8081";
    private static final String DEFAULT_WS_ENDPOINT = "ws://localhost:8081/ws";

    public static final String BASE_ENDPOINT =
            System.getenv().getOrDefault("BACKEND_URL", DEFAULT_BASE_ENDPOINT);

    public static final String WS_ENDPOINT =
            System.getenv().getOrDefault("BACKEND_WS_URL", DEFAULT_WS_ENDPOINT);

    public static final String BASE_TASK_ENDPOINT = BASE_ENDPOINT + "/task";
    public static final String BASE_AUTH_ENDPOINT = BASE_ENDPOINT + "/auth";
    public static final String BASE_LOGIN_ENDPOINT = BASE_AUTH_ENDPOINT + "/login";
    public static final String BASE_REGISTRATION_ENDPOINT = BASE_AUTH_ENDPOINT + "/register";

    public static final String BASE_EMPLOYEE_ENDPOINT = BASE_ENDPOINT + "/employee";
    public static final String BASE_CHAT_ENDPOINT = BASE_ENDPOINT + "/chats";
    public static final String CHATS_ENDPOINT = BASE_CHAT_ENDPOINT + "/chatIds";
    public static final String UNMATCHED_EMPLOYEE_INFOS_ENDPOINT = BASE_CHAT_ENDPOINT + "/unmatchedEmployeeInfos";
    public static final String CREATE_CHATS_ENDPOINT = BASE_CHAT_ENDPOINT + "/create";
}
package com.unimib.assignment3.interceptor;

import com.unimib.assignment3.notifier.ChatObserverNotifier;
import com.unimib.assignment3.notifier.TaskObserverNotifier;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class BrokerInterceptor implements HandlerInterceptor {

    @Autowired
    private final TaskObserverNotifier taskObserverNotifier;
    @Autowired
    private final ChatObserverNotifier  chatObserverNotifier;

    public BrokerInterceptor(TaskObserverNotifier taskObserverNotifier, ChatObserverNotifier chatObserverNotifier) {
        this.taskObserverNotifier = taskObserverNotifier;
        this.chatObserverNotifier = chatObserverNotifier;
    }

    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request,
                                @Nonnull HttpServletResponse response,
                                @Nonnull Object handler,
                                Exception ex) {
        if(request.getRequestURI().endsWith("/deleteTask")) {
            taskObserverNotifier.notifyOnDeleteAllTaskSubscribers((Long) request.getAttribute("taskId"));
        }
        else{
            taskObserverNotifier.notifyOnFetchAllTaskSubscribers((Long) request.getAttribute("taskId"));
        }
    }
}

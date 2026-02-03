package com.unimib.assignment3.DTO;

import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.request.TaskValidator;

public record ChangeTaskStateRequestDTO(
        Long taskId,
        TaskState taskState
) implements TaskValidator {
    @Override
    public void validate() throws IllegalArgumentException{
        if (taskId() == null || taskState() == null) {
            throw new IllegalArgumentException("Task ID and state cannot be null");
        }
    }
}

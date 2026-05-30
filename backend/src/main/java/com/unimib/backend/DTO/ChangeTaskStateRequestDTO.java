package com.unimib.backend.DTO;

import com.unimib.backend.enums.TaskState;
import com.unimib.backend.request.TaskValidator;

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

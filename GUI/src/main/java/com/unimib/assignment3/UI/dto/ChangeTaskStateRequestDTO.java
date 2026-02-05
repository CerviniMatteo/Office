package com.unimib.assignment3.UI.dto;

import com.unimib.assignment3.UI.enums.TaskState;
import com.unimib.assignment3.UI.validator.TaskValidator;

public record ChangeTaskStateRequestDTO(
        Long taskId,
        TaskState taskState
)implements TaskValidator {
    @Override
    public void validate() throws IllegalArgumentException{
        if (taskId() == null || taskState() == null) {
            throw new IllegalArgumentException("Task ID and Task State cannot be null");
        }
    }
}

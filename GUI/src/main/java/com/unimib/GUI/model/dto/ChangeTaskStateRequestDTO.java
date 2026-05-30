package com.unimib.GUI.model.dto;

import com.unimib.GUI.model.enums.TaskState;
import com.unimib.GUI.model.validator.TaskValidator;

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

package com.unimib.assignment3.UI.dto;

import com.unimib.assignment3.UI.request.TaskValidator;

public record AcceptTaskRequestDTO(
        Long taskId,
        Long employeeId
)implements TaskValidator {
    @Override
    public void validate() throws IllegalArgumentException{
        if (taskId() == null || employeeId() == null) {
            throw new IllegalArgumentException("Task ID and Employee ID cannot be null");
        }
    }
}

package com.unimib.assignment3.model.dto;

import com.unimib.assignment3.model.validator.TaskValidator;

public record StartTaskRequestDTO(
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

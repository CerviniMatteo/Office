package com.unimib.assignment3.DTO;
import com.unimib.assignment3.enums.TaskState;

public record ChangeTaskStateRequestDTO(
        Long taskId,
        TaskState taskState
){}
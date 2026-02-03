package com.unimib.assignment3.UI.dto;

import com.unimib.assignment3.UI.enums.TaskState;

public record ChangeTaskStateRequestDTO(
        Long taskId,
        TaskState taskState
){}

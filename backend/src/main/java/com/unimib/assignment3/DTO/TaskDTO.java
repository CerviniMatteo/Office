package com.unimib.assignment3.DTO;

import com.unimib.assignment3.enums.TaskState;

import java.time.LocalDate;
import java.util.Map;

public record TaskDTO(
        Long taskId,
        String description,
        TaskState taskState,
        LocalDate startDate,
        LocalDate endDate,
        Map<Long, String> assignedWorkers
) {}

package com.unimib.assignment3.UI.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unimib.assignment3.UI.enums.TaskState;

import java.time.LocalDate;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskDTO (
        Long taskId,
        String description,
        TaskState taskState,
        LocalDate startDate,
        LocalDate endDate,
        Map<Long, String> assignedWorkers
){}
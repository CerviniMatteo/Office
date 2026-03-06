package com.unimib.assignment3.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unimib.assignment3.enums.TaskState;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskDTO(
        Long taskId,
        String description,
        TaskState taskState,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Map<Long, String> assignedWorkers
) {}

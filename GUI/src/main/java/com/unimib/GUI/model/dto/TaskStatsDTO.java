package com.unimib.GUI.model.dto;

public record TaskStatsDTO(
        long workerId,
        int totalTasks,
        int completedTasks,
        int inProgressTasks
) {
}

package com.unimib.backend.DTO;

public record TaskStatsDTO(
    long workerId,
    int totalTasks,
    int completedTasks,
    int inProgressTasks
) {
}

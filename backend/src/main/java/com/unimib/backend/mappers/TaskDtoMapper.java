package com.unimib.backend.mappers;

import com.unimib.backend.DTO.TaskDTO;
import com.unimib.backend.POJO.Task;
import com.unimib.backend.POJO.Worker;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TaskDtoMapper {

    public TaskDTO mapToDto(Task task) {
        return new TaskDTO(
                task.getTaskId(),
                task.getDescription(),
                task.getTaskState(),
                task.getStartDate(),
                task.getEndDate(),
                task.getAssignedEmployees()
                        .stream()
                        .collect(Collectors.toMap(
                                Worker::getWorkerId,
                                Worker::getEncodedImage
                        ))
        );
    }
}

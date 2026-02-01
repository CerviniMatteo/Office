package com.unimib.assignment3.controller;

import com.unimib.assignment3.DTO.TaskDTO;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.facade.Facade;
import com.unimib.assignment3.mappers.TaskDtoMapper;
import com.unimib.assignment3.response.request.AcceptTaskRequest;
import com.unimib.assignment3.response.request.ChangeTaskStateRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpClient;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private final Facade facade;
    @Autowired
    private final TaskDtoMapper taskDtoMapper;

    public TaskController(Facade facade, TaskDtoMapper taskDtoMapper) {
        this.facade = facade;
        this.taskDtoMapper = taskDtoMapper;
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        System.out.println("Called taks/getAllTasks");
        return ResponseEntity.ok(facade.getAllTasks().stream()
                .map(taskDtoMapper::mapToDto)
                .toList());
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long taskId) {
        System.out.println("Called tasks/getTask");
        return ResponseEntity.ok(taskDtoMapper.mapToDto(facade.getTaskById(taskId)));
    }

    @PostMapping("/changeState")
    public ResponseEntity<String> changeTaskState(@RequestBody ChangeTaskStateRequest request, HttpServletRequest httpServletRequest) {
        Long taskId = request.getTaskId();
        TaskState taskState = request.getTaskState();
        System.out.println("Called tasks/changeState for taskId: " + taskId + " to state: " + taskState);
        try {
            facade.changeTaskState(taskId, taskState);
            httpServletRequest.setAttribute("taskId", taskId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resetState")
    public ResponseEntity<String> resetTaskState(@RequestBody Long taskId, HttpServletRequest httpServletRequest) {
        System.out.println("Called tasks/resetState for taskId: " + taskId);
        try {
            facade.resetTask(taskId);
            httpServletRequest.setAttribute("taskId", taskId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/acceptTask")
    public ResponseEntity<String> acceptTask(@RequestBody AcceptTaskRequest request, HttpServletRequest httpServletRequest) {
        Long taskId = request.getTaskId();
        Long employeeId = request.getEmployeeId();
        System.out.println("Called tasks/acceptTask for taskId: " + taskId + " and employeeId: " + employeeId);
        try {
            facade.assignEmployeeToTask(taskId, employeeId);
            httpServletRequest.setAttribute("taskId", taskId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}


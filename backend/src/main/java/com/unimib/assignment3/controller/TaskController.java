package com.unimib.assignment3.controller;

import com.unimib.assignment3.DTO.TaskDTO;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.facade.Facade;
import com.unimib.assignment3.mappers.TaskDtoMapper;
import com.unimib.assignment3.DTO.AcceptTaskRequestDTO;
import com.unimib.assignment3.DTO.StartTaskRequestDTO;
import com.unimib.assignment3.DTO.ChangeTaskStateRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private final Facade facade;
    @Autowired
    private final TaskDtoMapper taskDtoMapper;

    public TaskController(Facade facade, TaskDtoMapper taskDtoMapper) {
        this.facade = facade;
        this.taskDtoMapper = taskDtoMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        System.out.println("Called taks/getAllTasks");
        return ResponseEntity.ok(facade.getAllTasks().stream()
                .map(taskDtoMapper::mapToDto)
                .toList());
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long taskId) {
        if(taskId == null) {
            return ResponseEntity.badRequest().build();
        }
        System.out.println("Called tasks/getTask");
        return ResponseEntity.ok(taskDtoMapper.mapToDto(facade.getTaskById(taskId)));
    }

    @PostMapping("/createTask")
    public ResponseEntity<String> createTask(@RequestBody TaskDTO taskDTO, HttpServletRequest httpServletRequest) {
        if(taskDTO == null) {
            return ResponseEntity.badRequest().body("Task data cannot be null");
        }
        System.out.println("Called tasks/createTask for task: " + taskDTO);
        try {
            Task task = facade.saveTask(facade.createAndSaveTask(taskDTO));
            httpServletRequest.setAttribute("taskId", task.getTaskId());
            return ResponseEntity.created(URI.create(taskDTO.taskId() + "")).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/changeState")
    public ResponseEntity<String> changeTaskState(@RequestBody ChangeTaskStateRequestDTO request, HttpServletRequest httpServletRequest) throws IllegalArgumentException{
        try {
            request.validate();
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        Long taskId = request.taskId();
        TaskState taskState = request.taskState();
        System.out.println("Called tasks/changeState for taskId: " + taskId + " to state: " + taskState);
        try {
            facade.changeTaskState(taskId, taskState);
            httpServletRequest.setAttribute("taskId", taskId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/startTask")
    public ResponseEntity<String> startTask(@RequestBody StartTaskRequestDTO request, HttpServletRequest httpServletRequest) throws IllegalArgumentException{
            try {
                request.validate();
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
            Long taskId = request.taskId();
            Long employeeId = request.employeeId();
            System.out.println("Called tasks/startTask for taskId: " + taskId + " and employeeId: " + employeeId);
            try {
                facade.changeTaskState(taskId, TaskState.TO_BE_STARTED);
                facade.assignEmployeeToTask(taskId, employeeId);
                httpServletRequest.setAttribute("taskId", taskId);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
    }

    @PostMapping("/resetState")
    public ResponseEntity<String> resetTaskState(@RequestBody Long taskId, HttpServletRequest httpServletRequest) {
        if(taskId == null) {
            return ResponseEntity.badRequest().body("Task ID cannot be null");
        }
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
        public ResponseEntity<String> acceptTask(@RequestBody AcceptTaskRequestDTO request, HttpServletRequest httpServletRequest) {
            try {
                request.validate();
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
            Long taskId = request.taskId();
            Long employeeId = request.employeeId();
            System.out.println("Called tasks/acceptTask for taskId: " + taskId + " and employeeId: " + employeeId);
            try {
                facade.assignEmployeeToTask(taskId, employeeId);
                httpServletRequest.setAttribute("taskId", taskId);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

    @PostMapping("/deleteTask")
    public ResponseEntity<String> deleteTask(@RequestBody Long taskId, HttpServletRequest httpServletRequest) {
        System.out.println("Called tasks/deleteTask for taskId: " + taskId );
        try {
            facade.deleteTask(taskId);
            httpServletRequest.setAttribute("taskId", taskId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}


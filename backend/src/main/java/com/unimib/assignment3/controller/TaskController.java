package com.unimib.assignment3.controller;

import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.facade.Facade;
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

    public TaskController(Facade facade) {
        this.facade = facade;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        System.out.println("Called taks/getAllTasks");
        return ResponseEntity.ok(facade.getAllTasks());
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable Long taskId) {
        System.out.println("Called tasks/getTask");
        return ResponseEntity.ok(facade.getTaskById(taskId));
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
}


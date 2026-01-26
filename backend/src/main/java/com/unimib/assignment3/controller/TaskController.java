package com.unimib.assignment3.controller;

import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.facade.Facade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final Facade facade;

    public TaskController(Facade facade) {
        this.facade = facade;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(facade.getAllTasks());
    }
}


package com.unimib.assignment3.controller;
import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.facade.Facade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private final Facade facade;

    public LoginController(Facade facade) {
        this.facade = facade;
    }

    @GetMapping("/{email}")
    public ResponseEntity<Long> getEmployeeId(@PathVariable  String email) {
        System.out.println("login by email: " + email);
        Optional<Long> employeeId = facade.findEmployeeIdByEmail(email);
        return employeeId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

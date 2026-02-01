package com.unimib.assignment3.controller;
import com.unimib.assignment3.DTO.EmployeeDTO;
import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.facade.Facade;
import com.unimib.assignment3.mappers.EmployeeDtoMapper;
import com.unimib.assignment3.mappers.TaskDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private final Facade facade;
    @Autowired
    private final EmployeeDtoMapper employeeDtoMapper;

    public EmployeeController(Facade facade, EmployeeDtoMapper employeeDtoMapper) {
        this.facade = facade;
        this.employeeDtoMapper = employeeDtoMapper;
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long employeeId) {
        System.out.println("fetch employee by employeeId: " + employeeId);

        return facade.findEmployeeById(employeeId)
                .map(employeeDtoMapper::mapToDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

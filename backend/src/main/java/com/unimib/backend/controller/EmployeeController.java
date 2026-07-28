package com.unimib.backend.controller;
import com.unimib.backend.DTO.EmployeeDTO;
import com.unimib.backend.facade.EmployeeFacade;
import com.unimib.backend.mappers.EmployeeDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    EmployeeFacade facade;
    @Autowired
    EmployeeDtoMapper employeeDtoMapper;


    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long employeeId) {
        System.out.println("fetch employee by employeeId: " + employeeId);

        return facade.findEmployeeById(employeeId)
                .map(employeeDtoMapper::mapToDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

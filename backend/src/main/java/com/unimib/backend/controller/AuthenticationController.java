package com.unimib.backend.controller;
import com.unimib.backend.DTO.EmployeeDTO;
import com.unimib.backend.DTO.EmployeeRegistrationDTO;
import com.unimib.backend.POJO.Employee;
import com.unimib.backend.facade.EmployeeFacade;
import com.unimib.backend.mappers.EmployeeDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    EmployeeFacade facade;

    @GetMapping("/login/{email}")
    public ResponseEntity<Long> getEmployeeId(@PathVariable  String email) {
        System.out.println("login by email: " + email);
        Optional<Long> employeeId = facade.findEmployeeIdByEmail(email);
        return employeeId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/register")
    public ResponseEntity<EmployeeDTO> registerEmployeeId(@RequestBody EmployeeRegistrationDTO request) {
        Employee employee = facade.registerEmployee(request.name(), request.surname(), request.encodedImage());
        EmployeeDtoMapper mapper = new EmployeeDtoMapper();
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.mapToDto(employee));
    }
}

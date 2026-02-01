package com.unimib.assignment3.mappers;

import com.unimib.assignment3.DTO.EmployeeDTO;
import com.unimib.assignment3.DTO.TaskDTO;
import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Worker;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EmployeeDtoMapper {

    public EmployeeDTO mapToDto(Employee employee) {
        return new EmployeeDTO(
                employee.getWorkerId(),
                employee.getName(),
                employee.getSurname(),
                employee.getEmail(),
                employee.getEncodedImage()
        );
    }
}

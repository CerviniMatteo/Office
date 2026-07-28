package com.unimib.backend.mappers;

import com.unimib.backend.DTO.EmployeeDTO;
import com.unimib.backend.POJO.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeDtoMapper {

    public EmployeeDTO mapToDto(Employee employee) {
        return new EmployeeDTO(
                employee.getWorkerId(),
                employee.getName(),
                employee.getSurname(),
                employee.getPlainEmail(),
                employee.getEncodedImage()
        );
    }
}

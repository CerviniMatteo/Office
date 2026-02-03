package com.unimib.assignment3.DTO;

public record EmployeeDTO(
        Long workerId,
        String name,
        String surname,
        String email,
        String encodedImage
) {}

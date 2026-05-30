package com.unimib.backend.DTO;

public record EmployeeDTO(
        Long workerId,
        String name,
        String surname,
        String email,
        String encodedImage
) {}

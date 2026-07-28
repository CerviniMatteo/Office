package com.unimib.backend.DTO;

public record EmployeeRegistrationDTO(
        String name,
        String surname,
        String encodedImage
) {
}

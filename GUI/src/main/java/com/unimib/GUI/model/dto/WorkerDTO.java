package com.unimib.GUI.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkerDTO(
    Long workerId,
    String name,
    String surname,
    String email,
    String encodedImage){}
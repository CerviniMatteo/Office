package com.unimib.backend.DTO;

public record MessageDTO(
    Long chatId,
    Long senderId,
    String message) {
}
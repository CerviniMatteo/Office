package com.unimib.assignment3.DTO;

public record MessageDTO(
    Long chatId,
    Long senderId,
    String message) {
}
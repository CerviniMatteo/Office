package com.unimib.assignment3.model.dto;

public record MessageDTO(
    Long chatId,
    Long senderId,
    String message) {
}
    package com.unimib.assignment3.UI.model.dto;

public record MessageDTO(
    Long chatId,
    Long senderId,
    String message) {
}
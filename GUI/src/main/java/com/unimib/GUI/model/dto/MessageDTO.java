package com.unimib.GUI.model.dto;

public record MessageDTO(
    Long chatId,
    Long senderId,
    String message) {
}
package com.unimib.GUI.model.dto;

import java.util.UUID;

public record MessageDTO(
        String id,
        Long chatId,
        Long senderId,
        String message
) {

    public MessageDTO {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
    }

    public MessageDTO(Long chatId, Long senderId, String message) {
        this(UUID.randomUUID().toString(), chatId, senderId, message);
    }
}
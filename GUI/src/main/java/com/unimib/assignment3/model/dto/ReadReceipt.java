package com.unimib.assignment3.model.dto;

/**
 * DTO representing a read receipt sent from the client to the server
 */
public record ReadReceipt(
        Long chatId,
        Long senderId,
        Long readerId,
        String message) {
}


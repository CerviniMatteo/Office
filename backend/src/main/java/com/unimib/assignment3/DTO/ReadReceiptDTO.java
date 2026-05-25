package com.unimib.assignment3.DTO;

public record ReadReceiptDTO(
        Long chatId,
        Long senderId,
        Long readerId,
        String message) {
}


package com.unimib.assignment3.DTO;

public record Message(
    Long chatId,
    Long senderId,
    String message) {
}
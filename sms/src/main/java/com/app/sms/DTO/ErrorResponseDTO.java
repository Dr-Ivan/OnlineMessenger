package com.app.sms.DTO;


import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String error,
        String details,
        LocalDateTime errorTime
) {
}

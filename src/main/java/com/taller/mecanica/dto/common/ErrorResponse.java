package com.taller.mecanica.dto.common;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String path,
        Map<String, String> campos
) {

    public static ErrorResponse of(HttpStatus status, String mensaje, String path) {
        return new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensaje, path, null);
    }

    public static ErrorResponse of(HttpStatus status, String mensaje, String path, Map<String, String> campos) {
        return new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensaje, path, campos);
    }
}

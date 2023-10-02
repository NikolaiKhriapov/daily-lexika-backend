package my.project.notification.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApiErrorDTO(
        String path,
        int statusCode,
        HttpStatus statusMessage,
        LocalDateTime localDateTime,
        String message,
        String developerMessage
) {
}

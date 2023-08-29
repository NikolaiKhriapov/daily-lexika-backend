package my.project.vocabulary.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApiErrorWrapper(
        String path,
        String message,
        int statusCode,
        HttpStatus statusMessage,
        LocalDateTime localDateTime
) {
}

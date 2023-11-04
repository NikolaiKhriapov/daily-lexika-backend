package my.project.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;

@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleException(ResourceNotFoundException e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO(
                request.getRequestURI(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now(),
                e.getMessage(),
                Arrays.toString(e.getStackTrace())
        );

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorDTO> handleException(UserAlreadyExistsException e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO(
                request.getRequestURI(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT,
                LocalDateTime.now(),
                e.getMessage(),
                Arrays.toString(e.getStackTrace())
        );

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ReviewAlreadyExistsException.class)
    public ResponseEntity<ApiErrorDTO> handleException(ReviewAlreadyExistsException e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO(
                request.getRequestURI(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT,
                LocalDateTime.now(),
                e.getMessage(),
                Arrays.toString(e.getStackTrace())
        );

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleException(Exception e, HttpServletRequest request) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO(
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now(),
                e.getMessage(),
                Arrays.toString(e.getStackTrace())
        );

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

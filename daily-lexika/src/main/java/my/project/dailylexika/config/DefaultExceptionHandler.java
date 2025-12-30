package my.project.dailylexika.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import my.project.library.util.exception.ApiErrorDTO;
import my.project.library.util.exception.BadRequestException;
import my.project.library.util.exception.ResourceAlreadyExistsException;
import my.project.library.util.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidationExceptions(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        e.getBindingResult()
                .getAllErrors()
                .forEach((error) -> errors.add(error.getDefaultMessage()));
        log.warn("Validation failed for {} {}: {}", request.getMethod(), request.getRequestURI(), errors, e);

        ApiErrorDTO apiErrorDTO = new ApiErrorDTO(
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now(),
                errors.get(0),
                errors.toString()
        );

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleException(ResourceNotFoundException e, HttpServletRequest request) {
        log.warn("Resource not found for {} {}", request.getMethod(), request.getRequestURI(), e);
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

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiErrorDTO> handleException(ResourceAlreadyExistsException e, HttpServletRequest request) {
        log.warn("Resource already exists for {} {}", request.getMethod(), request.getRequestURI(), e);
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

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorDTO> handleException(BadCredentialsException e, HttpServletRequest request) {
        log.warn("Bad credentials for {} {}", request.getMethod(), request.getRequestURI(), e);
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO(
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now(),
                "Invalid email or password.",
                Arrays.toString(e.getStackTrace())
        );

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorDTO> handleException(BadRequestException e, HttpServletRequest request) {
        log.warn("Bad request for {} {}", request.getMethod(), request.getRequestURI(), e);
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO(
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now(),
                e.getMessage(),
                Arrays.toString(e.getStackTrace())
        );

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDTO> handleException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("Access denied for {} {}", request.getMethod(), request.getRequestURI(), e);
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO(
                request.getRequestURI(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN,
                LocalDateTime.now(),
                I18nUtil.getMessage("dailylexika-exceptions.security.forbidden"),
                Arrays.toString(e.getStackTrace())
        );

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorDTO> handleException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("Missing request parameter for {} {}", request.getMethod(), request.getRequestURI(), e);
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO(
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now(),
                e.getMessage(),
                Arrays.toString(e.getStackTrace())
        );

        return new ResponseEntity<>(apiErrorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleException(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception for {} {}", request.getMethod(), request.getRequestURI(), e);
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

package ca.tetervak.citydata.errors;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(content = @Content(mediaType = "application/json"))
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn(ex.getMessage(), ex);
        var bindingResult = ex.getBindingResult();
        String message = "Validation failed for " + bindingResult.getObjectName() + ". " +
                bindingResult.getFieldErrors().stream()
                        .map(fieldError -> "Field " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                        .collect(Collectors.joining("; "));
        return new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getClass().getSimpleName(),
                message
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(content = @Content(mediaType = "application/json"))
    public Object handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn(ex.getMessage(), ex);
        var violations = ex.getConstraintViolations();
        String message = "Validation failed for " +
                violations.stream()
                        .map(violation -> "Field " + violation.getPropertyPath() + ": " + violation.getMessage())
                        .collect(Collectors.joining("; "));
        return new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getClass().getSimpleName(),
                message
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(content = @Content(mediaType = "application/json"))
    public ApiError handleResponseStatusException(ResponseStatusException ex) {
        log.warn(ex.getMessage(), ex);
        return new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getClass().getSimpleName(),
                ex.getReason()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(content = @Content(mediaType = "application/json"))
    public ApiError handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn(ex.getMessage(), ex);
        return new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getClass().getSimpleName(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(content = @Content(mediaType = "application/json"))
    public ApiError handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn(ex.getMessage(), ex);
        return new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getClass().getSimpleName(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(content = @Content(mediaType = "application/json"))
    public ApiError handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn(ex.getMessage(), ex);
        return new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getClass().getSimpleName(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(content = @Content(mediaType = "application/json"))
    public ApiError handleAllUncaughtException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getClass().getSimpleName(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(content = @Content(mediaType = "application/json"))
    public ApiError handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn(ex.getMessage(), ex);
        return new ApiError(
                HttpStatus.NOT_FOUND.value(),
                ex.getClass().getSimpleName(),
                "No resource found at " + ex.getResourcePath()
        );
    }
}

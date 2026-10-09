package com.scrumapp.backend.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Unico punto de traduccion de excepciones a application/problem+json,
 * siguiendo el schema ProblemDetail de docs/api/components/schemas/common.yaml.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ProblemDetail> handleApiException(
            ApiException ex, HttpServletRequest request) {
        ProblemDetail body = buildProblemDetail(
                ex.getType(), ex.getTitle(), ex.getStatus(), ex.getMessage(), request);

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(ex.getStatus());
        if (ex.getRetryAfterSeconds() != null) {
            responseBuilder.header(HttpHeaders.RETRY_AFTER, String.valueOf(ex.getRetryAfterSeconds()));
        }
        return responseBuilder.body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Class<?> targetType = ex.getBindingResult().getTarget() == null
                ? null
                : ex.getBindingResult().getTarget().getClass();
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> toWireFieldMessage(fieldError, targetType))
                .collect(Collectors.joining("; "));

        ProblemDetail body = buildProblemDetail(
                ErrorTypes.VALIDATION_ERROR,
                "Solicitud invalida",
                HttpStatus.BAD_REQUEST,
                detail,
                request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        ProblemDetail body = buildProblemDetail(
                ErrorTypes.CONFLICT,
                "Conflicto con el estado actual del recurso",
                HttpStatus.CONFLICT,
                "La operacion viola una restriccion de integridad de datos.",
                request);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        ProblemDetail body = buildProblemDetail(
                ErrorTypes.BAD_REQUEST,
                "Solicitud invalida",
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud es invalido o esta mal formado.",
                request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ProblemDetail> handleNotFound(
            Exception ex, HttpServletRequest request) {
        ProblemDetail body = buildProblemDetail(
                ErrorTypes.NOT_FOUND,
                "Recurso no encontrado",
                HttpStatus.NOT_FOUND,
                "El recurso solicitado no existe.",
                request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        ProblemDetail body = buildProblemDetail(
                ErrorTypes.BAD_REQUEST,
                "Metodo HTTP no soportado",
                HttpStatus.METHOD_NOT_ALLOWED,
                ex.getMessage(),
                request);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    private String toWireFieldMessage(FieldError fieldError, Class<?> targetType) {
        String wireFieldName = resolveWireFieldName(fieldError, targetType);
        return wireFieldName + ": " + fieldError.getDefaultMessage();
    }

    private String resolveWireFieldName(FieldError fieldError, Class<?> targetType) {
        if (targetType != null) {
            try {
                var field = targetType.getDeclaredField(fieldError.getField());
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                if (jsonProperty != null && !jsonProperty.value().isBlank()) {
                    return jsonProperty.value();
                }
            } catch (NoSuchFieldException ignored) {
                // Si el DTO no declara el campo directamente, se usa el nombre tal cual.
            }
        }
        return fieldError.getField();
    }

    private ProblemDetail buildProblemDetail(
            String type, String title, HttpStatus status, String detail, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(type));
        problemDetail.setTitle(title);
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId != null && !requestId.isBlank()) {
            problemDetail.setProperty("request_id", requestId);
        }
        return problemDetail;
    }
}

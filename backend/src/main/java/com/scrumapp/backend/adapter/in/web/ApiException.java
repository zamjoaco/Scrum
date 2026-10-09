package com.scrumapp.backend.adapter.in.web;

import org.springframework.http.HttpStatus;

/**
 * Excepcion de capa web, pensada para ser lanzada desde adapters o
 * mapeada desde excepciones de dominio, y traducida a ProblemDetail
 * por el GlobalExceptionHandler.
 */
public class ApiException extends RuntimeException {

    private final String type;
    private final String title;
    private final HttpStatus status;
    private final Long retryAfterSeconds;

    public ApiException(String type, String title, HttpStatus status, String detail) {
        this(type, title, status, detail, null);
    }

    public ApiException(
            String type, String title, HttpStatus status, String detail, Long retryAfterSeconds) {
        super(detail);
        this.type = type;
        this.title = title;
        this.status = status;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}

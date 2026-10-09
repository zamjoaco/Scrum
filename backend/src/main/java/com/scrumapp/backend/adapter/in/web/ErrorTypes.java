package com.scrumapp.backend.adapter.in.web;

/**
 * URIs de tipo de error, espejo de los nombres de respuesta reutilizables
 * definidos en docs/api/components/responses/errors.yaml.
 */
public final class ErrorTypes {

    private static final String BASE = "https://api.scrumapp.dev/errors/";

    public static final String BAD_REQUEST = BASE + "bad-request";
    public static final String UNAUTHORIZED = BASE + "unauthorized";
    public static final String FORBIDDEN = BASE + "forbidden";
    public static final String NOT_FOUND = BASE + "not-found";
    public static final String CONFLICT = BASE + "conflict";
    public static final String VALIDATION_ERROR = BASE + "validation-error";

    private ErrorTypes() {
    }
}

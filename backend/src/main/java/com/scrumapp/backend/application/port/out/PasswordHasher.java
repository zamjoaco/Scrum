package com.scrumapp.backend.application.port.out;

/**
 * Puerto de salida para hashing y verificacion de contrasenas.
 */
public interface PasswordHasher {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}

package com.scrumapp.backend.application.port.in;

public interface ConfirmPasswordResetUseCase {

    void confirmReset(String token, String newRawPassword);
}

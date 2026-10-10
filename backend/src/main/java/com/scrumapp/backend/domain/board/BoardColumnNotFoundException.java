package com.scrumapp.backend.domain.board;

import com.scrumapp.backend.domain.user.DomainException;
import java.util.UUID;

public class BoardColumnNotFoundException extends DomainException {

    public BoardColumnNotFoundException(UUID boardColumnId) {
        super("No existe un board column con id " + boardColumnId);
    }
}

package com.scrumapp.backend.domain.board;

import com.scrumapp.backend.domain.user.DomainException;
import java.util.UUID;

public class BoardNotFoundException extends DomainException {

    public BoardNotFoundException(UUID boardId) {
        super("No existe un board con id " + boardId);
    }
}

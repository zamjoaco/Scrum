package com.scrumapp.backend.application.port.in;

import java.util.UUID;

/**
 * Caso de uso: obtener el Board de un project junto a sus columnas y, por
 * cada columna, los ids de las UserStory que estan en ella.
 */
public interface GetBoardUseCase {

    BoardView getBoard(UUID projectId, UUID requesterUserId);
}

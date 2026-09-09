package org.portfolio.benttchessgame.engine.board;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.GameState;

import java.util.Optional;

public record MoveResult(GameState newState, Move move, boolean check, boolean checkmate, Optional<BoardSquare> checkedBoardSquare) {
}

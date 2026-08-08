package org.portfolio.benttchessgame.engine.event;

import org.portfolio.benttchessgame.core.BoardSquare;

import java.util.List;

public record ActiveSquaresChangedEvent(List<BoardSquare> boardSquares) {
}

package org.portfolio.benttchessgame.engine.event;

import org.portfolio.benttchessgame.engine.BoardSquare;

import java.util.ArrayList;

public record ActiveSquaresChangedEvent(ArrayList<BoardSquare> boardSquares) {
}

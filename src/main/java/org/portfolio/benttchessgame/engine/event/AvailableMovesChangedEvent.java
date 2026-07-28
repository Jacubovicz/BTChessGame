package org.portfolio.benttchessgame.engine.event;

import org.portfolio.benttchessgame.engine.BoardSquare;

import java.util.ArrayList;

public record AvailableMovesChangedEvent(ArrayList<BoardSquare> avlMoves) { }

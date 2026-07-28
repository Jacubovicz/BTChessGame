package org.portfolio.benttchessgame.engine.event;

import org.portfolio.benttchessgame.engine.BoardSquare;

public record MoveHighlightChangedEvent(BoardSquare clickedOrMoved) { }

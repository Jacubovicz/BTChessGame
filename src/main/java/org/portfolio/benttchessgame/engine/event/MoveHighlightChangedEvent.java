package org.portfolio.benttchessgame.engine.event;

import org.portfolio.benttchessgame.core.BoardSquare;

public record MoveHighlightChangedEvent(BoardSquare clickedOrMoved) { }

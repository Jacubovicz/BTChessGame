package org.portfolio.benttchessgame.engine.event;

import org.portfolio.benttchessgame.piece.Piece;

public record BoardChangedEvent(Piece[][] board) { }

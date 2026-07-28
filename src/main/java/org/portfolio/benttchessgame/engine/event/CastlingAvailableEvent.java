package org.portfolio.benttchessgame.engine.event;

import org.portfolio.benttchessgame.engine.board.Move;

import java.util.ArrayList;

public record CastlingAvailableEvent(ArrayList<Move> castlingMovements) {
}

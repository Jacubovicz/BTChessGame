package org.portfolio.benttchessgame.chessengine.events;

import org.portfolio.benttchessgame.chessengine.movement.MoveOperation;

import java.util.ArrayList;

public record CastlingAvailableEvent(ArrayList<MoveOperation> castlingMovements) {
}

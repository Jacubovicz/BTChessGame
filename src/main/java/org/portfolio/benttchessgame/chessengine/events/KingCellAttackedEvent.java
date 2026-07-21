package org.portfolio.benttchessgame.chessengine.events;

import org.portfolio.benttchessgame.chessengine.Cell;

import java.util.ArrayList;

public record KingCellAttackedEvent(ArrayList<Cell> attackerAndAttackedKing) {
}

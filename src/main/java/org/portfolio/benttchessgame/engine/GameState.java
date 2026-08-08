package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.player.PlayerColor;

import java.util.List;



public record GameState(ChessBoard board, int moveCounter, PlayerColor nowMovingPlayer, List<Move> moveHistory) {
}

package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.player.PlayerColor;

import java.util.ArrayList;
import java.util.List;



public record GameState(ChessBoard board, int moveCounter, PlayerColor nowMovingPlayer, List<Move>moveHistory) {

    public GameState {
        moveHistory =  List.copyOf(moveHistory);
    }

    public int turn() {
        return moveCounter;
    }

    public GameState next(ChessBoard nextChessBoard, Move move) {
        List<Move> nextHistory = new ArrayList<>(moveHistory());
        nextHistory.add(move);

        return new GameState(nextChessBoard, moveCounter + 1, nowMovingPlayer.opposite(), nextHistory);
    }
}

package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.MoveResult;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.engine.player.Player;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.time.TimeSettings;

import java.util.List;

public interface ChessGameListener {

    void publishClickedMovedAndAvlMovesChanged(BoardSquare proposedBoardSquare, List<BoardSquare> availableMoves);

    void notifyBoardChangedListeners(ChessBoard chessBoard);

    void publishKingCellAttackedEvent(BoardSquare attackedKingCell);

    void flushMoveResultData(MoveResult moveResult, List<BoardSquare> availableMoves);

    void publishInitialState(GameConfiguration gameConfig,List<BoardSquare> activeSquares, GameState gameState) throws IllegalMoveException;

    void publishEndGame(String message, PlayerColor winningPlayer, int moveCounter, String winningPlayerName);

    void publishTimeRunOut(PlayerColor playerColor);

    void publishTimeEvent(PlayerColor playerColor, long timeLeft);
}

package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.MoveResult;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.engine.player.Player;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.replay.ReplayService;
import org.portfolio.benttchessgame.engine.time.TimeSettings;
import org.portfolio.benttchessgame.piece.Piece;

import java.util.List;

public class OnlineGameBackend implements GameBackend, ChessGameListener {


    @Override
    public void initialize(GameConfiguration gameConfig, GameEventPublisher eventPublisher) {

    }

    @Override
    public void proposeMove(int row, int col) {

    }

    @Override
    public void moveTheFigure(BoardSquare boardSquare) {

    }

    @Override
    public void resetTheGame() {

    }

    @Override
    public void requestInitialState() {

    }

    @Override
    public void requestFrame(int frameNumber) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void requestReplay() {

    }

    @Override
    public void publishClickedMovedAndAvlMovesChanged(BoardSquare proposedBoardSquare, List<BoardSquare> availableMoves) {

    }

    @Override
    public void notifyBoardChangedListeners(ChessBoard chessBoard) {

    }

    @Override
    public void publishKingCellAttackedEvent(BoardSquare attackedKingCell) {

    }

    @Override
    public void flushMoveResultData(MoveResult moveResult, List<BoardSquare> availableMoves) {

    }

    @Override
    public void publishInitialState(GameConfiguration gameConfig, List<BoardSquare> activeSquares, GameState gameState) throws IllegalMoveException {

    }

    @Override
    public void publishEndGame(String message, PlayerColor winningPlayer, int moveCounter, String winningPlayerName) {

    }

    @Override
    public void publishTimeRunOut(PlayerColor playerColor) {

    }

    @Override
    public void publishTimeEvent(PlayerColor playerColor, long timeLeft) {

    }
}

package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.MoveResult;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.event.StateEventPublisher;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.replay.ChessBoardFrame;
import org.portfolio.benttchessgame.engine.replay.ReplayFrameSender;
import org.portfolio.benttchessgame.engine.replay.ReplayService;
import org.portfolio.benttchessgame.engine.time.TimeEventPublisher;
import org.portfolio.benttchessgame.piece.PieceType;

import java.util.List;

public class LocalGameBackend implements GameBackend, ChessGameListener, ReplayService {

    private ChessGame localChessGame;
    private StateEventPublisher stateEventPublisher;
    private TimeEventPublisher timeEventPublisher;
    private ReplayFrameSender replayFrameSender;

    @Override
    public void initialize(GameConfiguration gameConfig, GameEventPublisher eventPublisher) {
        this.localChessGame = new ChessGame(gameConfig, this);
        this.stateEventPublisher = new StateEventPublisher(eventPublisher);
        this.timeEventPublisher = new TimeEventPublisher(eventPublisher);
        this.replayFrameSender = new ReplayFrameSender(eventPublisher);
    }

    @Override
    public void proposeMove(int row, int col) {
        if(localChessGame != null)
        {
            localChessGame.proposeMove(row, col);
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }

    @Override
    public void moveTheFigure(BoardSquare destBoardSquare) throws IllegalMoveException {
        if(localChessGame != null)
        {
            localChessGame.move(destBoardSquare);
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }

    @Override
    public void moveTheFigure(BoardSquare destBoardSquare, PieceType promotionType) throws IllegalMoveException {
        if(localChessGame != null)
        {
            localChessGame.move(destBoardSquare, promotionType);
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }

    @Override
    public void resetTheGame() throws IllegalMoveException {
        if(localChessGame != null)
        {
            localChessGame.resetTheGame();
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }


    @Override
    public void requestInitialState() throws IllegalMoveException {
        localChessGame.requestInitialState();
    }

    @Override
    public void requestFrame(int frameNumber) {
        if(localChessGame != null && localChessGame.gameEnded())
        {
            localChessGame.requestFrame(frameNumber);
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }

    @Override
    public void requestReplay(){
        if(localChessGame != null)
        {
            localChessGame.requestReplay(this);
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }

    @Override
    public void publishClickedMovedAndAvlMovesChanged(BoardSquare proposedBoardSquare, List<BoardSquare> availableMoves) {
        stateEventPublisher.publishClickedMovedChanged(proposedBoardSquare);
        stateEventPublisher.publishAvlMovesChanged(availableMoves);
    }

    @Override
    public void notifyBoardChangedListeners(ChessBoard chessBoard) {
        stateEventPublisher.notifyBoardChangedListeners(chessBoard);
    }

    @Override
    public void publishKingCellAttackedEvent(BoardSquare attackedKingCell) {
        stateEventPublisher.publishKingCellAttackedEvent(attackedKingCell);
    }

    @Override
    public void flushMoveResultData(MoveResult moveResult, List<BoardSquare> activeSquares) {
        stateEventPublisher.flushMoveResultData(moveResult);
        stateEventPublisher.flushActiveSquares(activeSquares);
    }

    @Override
    public void publishInitialState(GameConfiguration gameConfig, List<BoardSquare> activeSquares, GameState gameState) {
        stateEventPublisher.publishInitialState(gameConfig.whitePlayer(), gameConfig.blackPlayer(),gameState.nowMovingPlayer(), gameState.board(), gameConfig.timeSettings());
        stateEventPublisher.flushActiveSquares(activeSquares);
    }

    @Override
    public void publishEndGame(String message, PlayerColor winningPlayer, int moveCounter, String winningPlayerName) {
        stateEventPublisher.publishEndGame(message, winningPlayer, moveCounter, winningPlayerName);
    }

    @Override
    public void publishTimeRunOut(PlayerColor playerColor) {
        timeEventPublisher.publishTimeRunOut(playerColor);
    }

    @Override
    public void publishTimeEvent(PlayerColor playerColor, long timeLeft) {
        timeEventPublisher.publishTimeEvent(playerColor, timeLeft);
    }

    @Override
    public void sendFrameToUI(ChessBoardFrame frame) {
        replayFrameSender.sendFrameToUI(frame);
    }

    @Override
    public void notifyReplayReady(ChessBoard board, int numberOfFrames) {
        replayFrameSender.notifyReplayReady(board, numberOfFrames);
    }
}

package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.ai.AIEntity;
import org.portfolio.benttchessgame.engine.ai.MoveTracker;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.MoveResult;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.event.StateEventPublisher;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.engine.player.Player;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.replay.ChessBoardFrame;
import org.portfolio.benttchessgame.engine.replay.ReplayFrameSender;
import org.portfolio.benttchessgame.engine.replay.ReplayService;
import org.portfolio.benttchessgame.engine.time.TimeEventPublisher;
import org.portfolio.benttchessgame.piece.PieceType;

import java.util.List;

public class AIGameBackend implements GameBackend, ChessGameListener, ReplayService {

    private ChessGame chessGame;
    private AIEntity aiEntity;
    private StateEventPublisher stateEventPublisher;
    private TimeEventPublisher timeEventPublisher;
    private ReplayFrameSender replayFrameSender;
    private MoveTracker moveTracker;

    @Override
    public void initialize(GameConfiguration gameConfig, GameEventPublisher eventPublisher) {
        this.chessGame = new ChessGame(gameConfig, this);
        this.aiEntity = new AIEntity(this);

        this.stateEventPublisher = new StateEventPublisher(eventPublisher);
        this.timeEventPublisher = new TimeEventPublisher(eventPublisher);
        this.replayFrameSender = new ReplayFrameSender(eventPublisher);

        this.moveTracker = new MoveTracker(gameConfig.whitePlayer(), gameConfig.blackPlayer(), aiEntity); //to add parameters
    }

    @Override
    public void proposeMove(int row, int col) {
        if (chessGame != null) {
            chessGame.proposeMove(row, col);
        }
    }

    @Override
    public void moveTheFigure(BoardSquare destBoardSquare) throws IllegalMoveException {
        if(chessGame != null){
            moveTheFigure(destBoardSquare, null);
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");

    }

    @Override
    public void moveTheFigure(BoardSquare destBoardSquare, PieceType promotionType) throws IllegalMoveException {
        if(chessGame != null){
            chessGame.move(destBoardSquare, promotionType);
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }

    @Override
    public void resetTheGame() throws IllegalMoveException {
        if (chessGame != null) {
            chessGame.resetTheGame();
        }
    }

    @Override
    public void requestInitialState() throws IllegalMoveException {
        if (chessGame != null) {
            chessGame.requestInitialState();
        }
    }

    @Override
    public void requestFrame(int frameNumber) {

        if(chessGame != null && chessGame.gameEnded())
        {
            chessGame.requestFrame(frameNumber);
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }

    @Override
    public void requestReplay() {
        if(chessGame != null){
            chessGame.requestReplay(this);
        }
    }

    @Override
    public void publishClickedMovedAndAvlMovesChanged(BoardSquare proposedBoardSquare, List<BoardSquare> availableMoves) {
        stateEventPublisher.publishClickedMovedChanged(proposedBoardSquare);
        //stateEventPublisher.publishAvlMovesChanged(proposedBoardSquare, availableMoves);
        moveTracker.publishAvlMovesChanged(() -> stateEventPublisher.publishAvlMovesChanged(availableMoves));
    }

    @Override
    public void notifyBoardChangedListeners(ChessBoard chessBoard) {
            stateEventPublisher.notifyBoardChangedListeners(chessBoard);
            moveTracker.notifyBoardChangedListener(chessBoard); //here, the AIPlayer changes and should play
    }

    @Override
    public void publishKingCellAttackedEvent(BoardSquare attackedKingCell) {
            stateEventPublisher.publishKingCellAttackedEvent(attackedKingCell);
    }

    @Override
    public void flushMoveResultData(MoveResult moveResult, List<BoardSquare> availableMoves) {
        //the flushActiveSquare needs to be taken out from here
        //to be separated to different methods

            stateEventPublisher.flushMoveResultData(moveResult);
            aiEntity.updateChessBoard(moveResult.newState().board());
            moveTracker.setCurrentPlayer(moveResult.newState().nowMovingPlayer());
           // stateEventPublisher.flushActiveSquares(availableMoves);
            moveTracker.flushActiveSquares(() -> stateEventPublisher.flushActiveSquares(availableMoves));
            moveTracker.setCurrentActiveTurn(moveResult.newState().turn());

            //flushActiveSquares sends ActiveSquaresChangedEvent and is dependent on a player.
            //notifyActivateMoves is independent of a player but important for trace
    }


    //the publishInitialState should already be filtered by the MoveTracker
    @Override //this is coming out from the ChessGame object
    public void publishInitialState(GameConfiguration gameConfig, List<BoardSquare> activeSquares, GameState gameState) throws IllegalMoveException {

        PlayerColor nowMovingPlayer = gameState.nowMovingPlayer();
        Player nowMoving = nowMovingPlayer == PlayerColor.WHITE ? gameConfig.whitePlayer() : gameConfig.blackPlayer();

        aiEntity.updateChessBoard(gameState.board());
        moveTracker.setCurrentPlayer(nowMoving);
        moveTracker.setCurrentActiveTurn(gameState.turn());
        stateEventPublisher.publishInitialState(gameConfig.whitePlayer(), gameConfig.blackPlayer(), gameState.nowMovingPlayer(), gameState.board(), gameConfig.timeSettings());
        moveTracker.flushActiveSquares(() -> stateEventPublisher.flushActiveSquares(activeSquares));
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

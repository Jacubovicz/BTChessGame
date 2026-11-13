package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.board.MoveResult;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.BoardManager;
import org.portfolio.benttchessgame.engine.replay.ReplayCreator;
import org.portfolio.benttchessgame.engine.replay.ReplayService;
import org.portfolio.benttchessgame.engine.rule.ChessRules;
import org.portfolio.benttchessgame.engine.rule.MoveValidator;
import org.portfolio.benttchessgame.engine.time.TimeCounter;
import org.portfolio.benttchessgame.engine.time.TimerData;

import org.portfolio.benttchessgame.piece.PieceType;

import java.util.ArrayList;
import java.util.List;


public class ChessGame {

    private final BoardManager boardManager;
    private final GameConfiguration gameConfig;
    private final ChessGameListener gameListener;

    private GameState gameState;
    private BoardSquare initialSquare = null;

    TimeCounter timeCounter;
    private TimerData timerData;

    private boolean gameEnded;
    private boolean disposed;
    private ReplayCreator replayCreator;

    public ChessGame(GameConfiguration gameConfig, ChessGameListener gameListener) {
        //Player white, Player black, GameMode(online/ai/offline), TimeSettings(blitz/standard/etc)
        this.gameConfig = gameConfig;
        this.gameEnded = false;

        this.gameListener = gameListener;
        this.boardManager = new BoardManager();
        ChessBoard initialChessBoard = new ChessBoard();
        initialChessBoard.setupTheBoard();

        //ChessBoard actualBoard, int actualMoveCounter, PlayerColor currentPlayer, List<Move> moveHistory
        this.gameState = new GameState(initialChessBoard, 0, PlayerColor.WHITE, new ArrayList<>());

        this.timerData = new TimerData(gameState.nowMovingPlayer());
        this.timeCounter = new TimeCounter(gameConfig.timeSettings(), timerData, this::processAndSendToUI, this);
    }

    public synchronized void proposeMove(int row, int col) {
        if (gameEnded || disposed) return;
        BoardSquare proposedBoardSquare = new BoardSquare(row, col);

        if(boardManager.isEmptyFieldOrOppositePlayer(proposedBoardSquare, gameState))
        {
            initialSquare = null;
            gameListener.publishClickedMovedAndAvlMovesChanged(proposedBoardSquare, List.of());
            return;
        }
        initialSquare = proposedBoardSquare;
        List<BoardSquare> avlMovesFromSquare = boardManager.getValidMoves(proposedBoardSquare, gameState);
        gameListener.publishClickedMovedAndAvlMovesChanged(proposedBoardSquare, avlMovesFromSquare);
    }

    public void move(BoardSquare destBoardSquare) throws IllegalMoveException {
        move(destBoardSquare, null);
    }

    public synchronized void move(BoardSquare destBoardSquare, PieceType promotionType) throws IllegalMoveException {
        if (gameEnded || disposed) throw new IllegalMoveException("Game is not active.");
        if (initialSquare == null) {
            throw new IllegalMoveException("No piece selected for movement.");
        }

        Move operation = new Move(initialSquare, destBoardSquare, promotionType);
        MoveResult moveResult; //newState, Move move, boolean check, boolean checkmate, Optional<BoardSquare> checkedBoardSquare
        try {
            moveResult = boardManager.move(operation, gameState);
        } catch (IllegalArgumentException e) {
            throw new IllegalMoveException(e.getMessage());
        }
        gameListener.publishClickedMovedAndAvlMovesChanged(operation.getDestCell(), new ArrayList<>());
        initialSquare = null;

        this.gameState = moveResult.newState();
        if (moveResult.checkmate()) {
            timeCounter.stopTimer();
            gameListener.notifyBoardChangedListeners(gameState.board());
            if (moveResult.checkedBoardSquare().isPresent()) {
                gameListener.publishKingCellAttackedEvent(moveResult.checkedBoardSquare().get());
            }
            throwTheEndGame(EndGameReason.CHECKMATE, gameState.nowMovingPlayer().opposite());
            return;
        }

        boolean stalemate = ChessRules.isStalemate(gameState, gameState.nowMovingPlayer());
        if (stalemate) {
            timeCounter.stopTimer();
            gameListener.notifyBoardChangedListeners(gameState.board());
            throwTheEndGame(EndGameReason.STALEMATE, null);
            return;
        }

        timeCounter.changeTurn();
        flushMoveResultData(moveResult);
    }

    private void flushMoveResultData(MoveResult moveResult)
    {
        gameListener.flushMoveResultData(moveResult, activatePlayerSquares(gameState.nowMovingPlayer()));
    }

    public synchronized void requestInitialState() throws IllegalMoveException {
        if (gameEnded || disposed) return;
        gameListener.publishInitialState(gameConfig, activatePlayerSquares(gameState.nowMovingPlayer()), gameState);
        timeCounter.startGame();
    }

    private List<BoardSquare> activatePlayerSquares(PlayerColor playerColor) {

        if(boardManager != null)
        {
            ChessBoard snapshotBoard = gameState.board().getSnapshot();
            return MoveValidator.getActiveSquares(snapshotBoard, playerColor);
         //  return boardManager.getPositionSquaresOfThePlayer(snapshotBoard, playerColor);
        }
        return new ArrayList<>();
    }


    private synchronized void processAndSendToUI(){
        if(disposed || gameEnded || timerData == null){
            return;
        }
        if(timerData.timeRanOut()){
            timeCounter.stopTimer();
            throwTheEndGame(EndGameReason.TIME_RUN_OUT, timerData.currentPlayer().opposite()); //currentPlayer os the one lost
            if (!disposed) gameListener.publishTimeRunOut(timerData.currentPlayer());
            //Still needs implementation of game behavior
            return;
        }
        gameListener.publishTimeEvent(timerData.currentPlayer(), timerData.timeLeft());
    }

    public synchronized void throwTheEndGame(EndGameReason endGameReason, PlayerColor winningPlayer){
        if (gameEnded || disposed) return;
        gameEnded = true;
        initialSquare = null;
        timeCounter.dispose();
        String message = "";
        switch(endGameReason)
        {
            case TIME_RUN_OUT -> {
                message = "%s's has run out of time, \n\t%s's won!".formatted(winningPlayer.opposite(), winningPlayer);
                String winningPlayerName = gameConfig.whitePlayer().getColor() == winningPlayer ? gameConfig.whitePlayer().getName() : gameConfig.blackPlayer().getName();
                gameListener.publishEndGame(message, winningPlayer, gameState.moveCounter(), winningPlayerName);
            }
            case CHECKMATE -> {
                message = "%s's king has been checkmated, \n\t%s's won!".formatted(winningPlayer.opposite(), winningPlayer);
                String winningPlayerName = gameConfig.whitePlayer().getColor() == winningPlayer ? gameConfig.whitePlayer().getName() : gameConfig.blackPlayer().getName();
                gameListener.publishEndGame(message, winningPlayer, gameState.moveCounter(), winningPlayerName);
            }
            case STALEMATE -> {
                message = "Game drawn by stalemate!";
                gameListener.publishEndGame(message, null, gameState.moveCounter(), "None");
            }
            default -> throw new RuntimeException("Invalid end game reason");
        }
    }

    public synchronized void resetTheGame() throws IllegalMoveException {
        if (disposed) return;
        timeCounter.dispose();
        ChessBoard initialChessBoard = new ChessBoard();
        initialChessBoard.setupTheBoard();
        this.gameState = new GameState(initialChessBoard, 0, PlayerColor.WHITE, new ArrayList<>());
        this.initialSquare = null;
        this.gameEnded = false;
        this.replayCreator = null;
        this.timerData = new TimerData(PlayerColor.WHITE);
        this.timeCounter = new TimeCounter(gameConfig.timeSettings(), timerData, this::processAndSendToUI, this);
        requestInitialState();
    }

    public synchronized void dispose() {
        if (disposed) return;
        disposed = true;
        initialSquare = null;
        timeCounter.dispose();
    }

    public synchronized void applyAiMove(GameState expected, Move move) throws IllegalMoveException {
        if (disposed || gameEnded || gameState != expected) return;
        proposeMove(move.initBoardSquare().getRow(), move.initBoardSquare().getCol());
        move(move.destBoardSquare(), move.promotionType());
    }

    public GameState getGameState() {
        return gameState;
    }

    public PlayerColor getNowMovingPlayer() {
        return gameState.nowMovingPlayer();
    }

    public int getMoveCounter() {
        return gameState.moveCounter();
    }


    public synchronized boolean gameEnded(){
        return this.gameEnded;
    }

    public synchronized void requestReplay(ReplayService replayService){
        if(!disposed && gameEnded()){
            replayCreator = new ReplayCreator(gameState.moveHistory(), replayService);
        }
    }

    public synchronized void requestFrame(int frameNumber){
        if(!disposed && replayCreator != null && gameEnded()){
            replayCreator.requestFrame(frameNumber);
        }
    }
}

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
    private final TimerData timerData;

    private boolean gameEnded;
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
        this.timeCounter = new TimeCounter(gameConfig.timeSettings(), timerData, this::processAndSendToUI);
    }

    public void proposeMove(int row, int col) {
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

    public void move(BoardSquare destBoardSquare, PieceType promotionType) throws IllegalMoveException {
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

    public void requestInitialState() throws IllegalMoveException {
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


    private void processAndSendToUI(){
        if(timerData == null){
            return;
        }
        if(timerData.timeRanOut()){
            timeCounter.stopTimer();
            throwTheEndGame(EndGameReason.TIME_RUN_OUT, timerData.currentPlayer().opposite()); //currentPlayer os the one lost
            gameListener.publishTimeRunOut(timerData.currentPlayer());
            //Still needs implementation of game behavior
            return;
        }
        gameListener.publishTimeEvent(timerData.currentPlayer(), timerData.timeLeft());
    }

    public void throwTheEndGame(EndGameReason endGameReason, PlayerColor winningPlayer){
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
        setGameEnded();
    }

    public void resetTheGame() throws IllegalMoveException {
        timeCounter.stopTimer();
        ChessBoard initialChessBoard = new ChessBoard();
        initialChessBoard.setupTheBoard();
        this.gameState = new GameState(initialChessBoard, 0, PlayerColor.WHITE, new ArrayList<>());
        this.initialSquare = null;
        requestInitialState();
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

    private void setGameEnded(){
        this.gameEnded = true;
    }

    public boolean gameEnded(){
        return this.gameEnded;
    }

    public void requestReplay(ReplayService replayService){
        if(gameEnded()){
            replayCreator = new ReplayCreator(gameState.moveHistory(), replayService);
        }
    }

    public void requestFrame(int frameNumber){
        if(replayCreator != null && gameEnded()){
            replayCreator.requestFrame(frameNumber);
        }
    }
}

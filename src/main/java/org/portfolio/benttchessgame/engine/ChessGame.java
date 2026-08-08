package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.event.*;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.BoardManager;
import org.portfolio.benttchessgame.piece.Piece;

import java.util.ArrayList;
import java.util.List;


public class ChessGame {

    private final BoardManager boardManager;
    private final GameEventPublisher eventPublisher;
    private final GameConfiguration gameConfig;
    private GameState gameState;

    private BoardSquare initialSquare = null;

    public ChessGame(GameConfiguration gameConfig, GameEventPublisher eventPublisher) {

        this.gameConfig = gameConfig;
        this.eventPublisher = eventPublisher;
        this.boardManager = new BoardManager(new ChessBoard(), eventPublisher);

        gameState = new GameState(boardManager.getSnapshot(), 0, PlayerColor.WHITE, new ArrayList<>());
        boardManager.setState(gameState);

        activatePlayerSquares(gameState.nowMovingPlayer());
    }

    public void activatePlayerSquares(PlayerColor playerColor) {
        //find the fields and flush it to the UIEngine.
        if(boardManager != null)
        {
            List<BoardSquare> activeSquares = boardManager.getPositionSquaresOfThePlayer(playerColor);
            flushActiveSquares(activeSquares);
        }
    }

    public Piece[][] getSnapshot() {
        return boardManager.getSnapshot().getPiecesOnBoard();
    }

    /** **/
    public void fillTheStarterMoves() {
        eventPublisher.publishEvent(new PlayersChangedEvent(gameConfig.whitePlayer(), gameConfig.blackPlayer()));
        activatePlayerSquares(gameState.nowMovingPlayer());
        notifyActivateMoves(gameState.nowMovingPlayer());
        boardManager.notifyBoardChangedListeners();
    }

    /** **/
    public void proposeMove(int row, int col)
    {
        BoardSquare proposedBoardSquare = new BoardSquare(row, col);

        initialSquare = proposedBoardSquare;
        List<BoardSquare> avlMovesFromSquare = boardManager.proposeMoves(proposedBoardSquare, gameState); //adds all possible moves of this cell to an array.
        notifyPaintClickedMovedEvent(proposedBoardSquare);
        notifyAvlMovesChangedListeners(avlMovesFromSquare);
    }

    //The owner of MoveOperation should by assigned at JavaFX level or AI level(if playing with pc)
    public void move(BoardSquare destBoardSquare) throws IllegalMoveException
    {
        if (initialSquare == null) {
            throw new IllegalMoveException("No piece selected for movement.");
        }

        List<BoardSquare> avlMoves = boardManager.proposeMoves(initialSquare, gameState);

        if(avlMoves.contains(destBoardSquare))
        {
            Move operation = new Move(initialSquare, destBoardSquare);
            boardManager.move(operation, gameState);
            notifyPaintClickedMovedEvent(operation.getDestCell());
            notifyAvlMovesChangedListeners(new ArrayList<>()); //create an implementation in GUI
            initialSquare = null;

            // Update state

            PlayerColor nextTurn = gameState.nowMovingPlayer() == PlayerColor.WHITE ? PlayerColor.DARK : PlayerColor.WHITE;
            int nextCounter = gameState.moveCounter() + 1;
            ArrayList<Move> nextHistory = new ArrayList<>(gameState.moveHistory());
            nextHistory.add(operation);

            this.gameState = new GameState(boardManager.getSnapshot(), nextCounter, nextTurn, nextHistory);
            boardManager.setState(gameState);
        }
        else
            throw new IllegalMoveException("The outcome of the method is not expected. ");

        activatePlayerSquares(gameState.nowMovingPlayer());
        notifyActivateMoves(gameState.nowMovingPlayer()); //notifies the method responsible for updating the activate fields.
    }

    /** **/
    public void notifyAvlMovesChangedListeners(List<BoardSquare> avlMoves) {
        List<BoardSquare> copy = new ArrayList<>();

        for(BoardSquare boardSquare : avlMoves)
        {
            copy.add(new BoardSquare(boardSquare.getRow(), boardSquare.getCol(), boardSquare.isCastlingCell()));
        }
        eventPublisher.publishEvent(new AvailableMovesChangedEvent(copy));
    }

    //this one is to wake up the change of active fields on the game object,
    public void notifyActivateMoves(PlayerColor playerToBeActivated)
    {
        eventPublisher.publishEvent(new TurnChangedEvent(playerToBeActivated));
    }


    //sends info to GUI to reactivate opponent's fields and block the ones of current player.
    public void flushActiveSquares(List<BoardSquare> squares)
    {
        eventPublisher.publishEvent(new ActiveSquaresChangedEvent(squares));
    }

    //sends info to GUI that it needs to paint the cell as clicked or moved (both yellow color)
    public void notifyPaintClickedMovedEvent(BoardSquare boardSquare)
    {
        eventPublisher.publishEvent(new MoveHighlightChangedEvent(boardSquare));
    }

    public void resetTheGame(){}
}

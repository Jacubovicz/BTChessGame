package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.engine.event.*;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.BoardManager;
import org.portfolio.benttchessgame.engine.player.Player;
import org.portfolio.benttchessgame.piece.Piece;

import java.util.ArrayList;


public class ChessGame {

    private BoardManager boardManager = null;
    private GameSession currentGameSession = null; // Sessions will have data about time, special rules (en passant) available for players, move counter

    private Player p1, p2;


    //this array stores the fields(cells) which are currently active.
    private ArrayList<BoardSquare> activeFields = new ArrayList<>();

    //this has avlMoves in its array
    ArrayList<BoardSquare> avlMoves = new ArrayList<>();

    //this will store the complete move(will be filled by proposeMove and later by "move")
    Move operation = new Move();

    boolean turn;

    public ChessGame(GameSession gameSession) {
        this.p1 = gameSession.getPlayer1();
        this.p2 = gameSession.getPlayer2();

        this.currentGameSession = gameSession;
        this.boardManager = new BoardManager(new ChessBoard(), currentGameSession);
        this.turn = currentGameSession.getTurn();

        activatePlayerFieldsOf(currentGameSession.getTurn());

        GameEventBus.getInstance().subscribe(TurnChangedEvent.class, event -> activatePlayerFieldsOf(event.isWhitePlayer()));
    }

    public ChessGame(Player p1, Player p2) {
        this.p1 = p1;
        this.p2 = p2;

        this.currentGameSession = new GameSession(p1, p2);
        this.boardManager = new BoardManager(new ChessBoard(), currentGameSession);
        this.turn = currentGameSession.getTurn();
    }

    /** **/
    public void activatePlayerFieldsOf(boolean color) {
        //find the fields and flush it to the UIEngine.
        if(boardManager != null)
        {
            activeFields.clear();
            activeFields = boardManager.getPositionCellsOfThePlayer(color);
            notifyReactivateFieldsEvent(activeFields);
        }
    }

    public Piece[][] getSnapshot() {
        return boardManager.getSnapshot().getPiecesOnBoard();
    }

    /** **/
    public void fillTheStarterMoves() {
        activatePlayerFieldsOf(currentGameSession.getTurn());
    }

    /** **/
    public void proposeMove(int row, int col)
    {
        BoardSquare proposedBoardSquare = new BoardSquare(row, col);

        operation.setInitCell(proposedBoardSquare);
        avlMoves = boardManager.proposeMove(proposedBoardSquare, currentGameSession); //adds all possible moves of this cell to an array.
        notifyPaintClickedMovedEvent(proposedBoardSquare);
        notifyAvlMovesChangedListeners(avlMoves);
    }

    //The owner of MoveOperation should by assigned at JavaFX level or AI level(if playing with pc)
    public void move(BoardSquare destBoardSquare) throws IllegalMoveException
    {
        if(operation != null && avlMoves.contains(destBoardSquare))
        {
            operation.setDestCell(destBoardSquare);
            boardManager.move(operation, currentGameSession);
            notifyPaintClickedMovedEvent(operation.getDestCell());
            notifyAvlMovesChangedListeners(new ArrayList<>()); //create an implementation in GUI
        }
        else
            throw new IllegalMoveException("The outcome of the method is not expected. ");

        currentGameSession.switchTurn();
        notifyActivateMoves(currentGameSession.getTurn()); //notifies the method responsible for updating the activate fields.
    }

    /** **/
    public void notifyAvlMovesChangedListeners(ArrayList<BoardSquare> avlMoves) {
        ArrayList<BoardSquare> copy = new ArrayList<>();

        for(BoardSquare boardSquare : avlMoves)
        {
            copy.add(new BoardSquare(boardSquare.getRow(), boardSquare.getCol(), boardSquare.isCastlingCell()));
        }
        GameEventBus.getInstance().publishEvent(new AvailableMovesChangedEvent(copy));
    }

    //this one is to wake up the change of active fields on the game object,
    public void notifyActivateMoves(boolean playerToBeActivated)
    {
        GameEventBus.getInstance().publishEvent(new TurnChangedEvent(playerToBeActivated));
    }


    //sends info to GUI to reactivate opponent's fields and block the ones of current player.
    public void notifyReactivateFieldsEvent(ArrayList<BoardSquare> fields)
    {
        GameEventBus.getInstance().publishEvent(new ActiveSquaresChangedEvent(fields));
    }

    //sends info to GUI that it needs to paint the cell as clicked or moved (both yellow color)
    public void notifyPaintClickedMovedEvent(BoardSquare boardSquare)
    {
        GameEventBus.getInstance().publishEvent(new MoveHighlightChangedEvent(boardSquare));
    }

    public void resetTheGame(){}
}

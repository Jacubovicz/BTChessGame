package org.portfolio.benttchessgame.chessengine;

import UILogic.helperclasses.EnumColors;
import org.portfolio.benttchessgame.chessengine.events.*;
import org.portfolio.benttchessgame.chessengine.exceptions.IllegalMoveException;
import org.portfolio.benttchessgame.chessengine.helperclasses.GameEventBus;
import org.portfolio.benttchessgame.chessengine.movement.MoveOperation;
import org.portfolio.benttchessgame.chessengine.movement.PieceBoard;
import org.portfolio.benttchessgame.chessengine.movement.PieceBoardManager;
import org.portfolio.benttchessgame.chessengine.players.Player;
import org.portfolio.benttchessgame.figures.Figure;

import java.util.ArrayList;


public class Game {

    private PieceBoardManager pieceBoardManager = null;
    private Session currentSession = null; // Sessions will have data about time, special rules (en passant) available for players, move counter

    private Player p1, p2;


    //this array stores the fields(cells) which are currently active.
    private ArrayList<Cell> activeFields = new ArrayList<>();

    //this has avlMoves in its array
    ArrayList<Cell> avlMoves = new ArrayList<>();

    //this will store the complete move(will be filled by proposeMove and later by "move")
    MoveOperation operation = new MoveOperation();

    boolean turn;

    public Game(Session session) {
        this.p1 = session.getPlayer1();
        this.p2 = session.getPlayer2();

        this.currentSession = session;
        this.pieceBoardManager = new PieceBoardManager(new PieceBoard(), currentSession);
        this.turn = currentSession.getTurn();

        activatePlayerFieldsOf(currentSession.getTurn());

        GameEventBus.getInstance().subscribe(OnTurnChangedActivateFieldsEvent.class, event -> activatePlayerFieldsOf(event.isWhitePlayer()));
    }

    public Game(Player p1, Player p2) {
        this.p1 = p1;
        this.p2 = p2;

        this.currentSession = new Session(p1, p2);
        this.pieceBoardManager = new PieceBoardManager(new PieceBoard(), currentSession);
        this.turn = currentSession.getTurn();
    }

    /** **/
    public void activatePlayerFieldsOf(boolean color) {
        //find the fields and flush it to the UIEngine.
        if(pieceBoardManager != null)
        {
            activeFields.clear();
            activeFields = pieceBoardManager.getPositionCellsOfThePlayer(color);
            notifyReactivateFieldsEvent(activeFields);
        }
    }

    public Figure[][] getSnapshot() {
        return pieceBoardManager.getSnapshot().getPiecesOnBoard();
    }

    /** **/
    public void fillTheStarterMoves() {
        activatePlayerFieldsOf(currentSession.getTurn());
    }

    /** **/
    public void proposeMove(int row, int col)
    {
        Cell proposedCell = new Cell(row, col);

        operation.setInitCell(proposedCell);
        avlMoves = pieceBoardManager.proposeMove(proposedCell, currentSession); //adds all possible moves of this cell to an array.
        notifyPaintClickedMovedEvent(proposedCell);
        notifyAvlMovesChangedListeners(avlMoves);
    }

    //The owner of MoveOperation should by assigned at JavaFX level or AI level(if playing with pc)
    public void move(Cell destCell) throws IllegalMoveException
    {
        if(operation != null && avlMoves.contains(destCell))
        {
            operation.setDestCell(destCell);
            pieceBoardManager.move(operation, currentSession);
            notifyPaintClickedMovedEvent(operation.getDestCell());
            notifyAvlMovesChangedListeners(new ArrayList<>()); //create an implementation in GUI
        }
        else
            throw new IllegalMoveException("The outcome of the method is not expected. ");

        currentSession.switchTurn();
        notifyActivateMoves(currentSession.getTurn()); //notifies the method responsible for updating the activate fields.
    }

    /** **/
    public void notifyAvlMovesChangedListeners(ArrayList<Cell> avlMoves) {
        ArrayList<Cell> copy = new ArrayList<>();

        for(Cell cell : avlMoves)
        {
            copy.add(new Cell(cell.getRow(), cell.getCol(), cell.isCastlingCell()));
        }
        GameEventBus.getInstance().publishEvent(new AvlMovesChangedEvent(copy));
    }

    //this one is to wake up the change of active fields on the game object,
    public void notifyActivateMoves(boolean playerToBeActivated)
    {
        GameEventBus.getInstance().publishEvent(new OnTurnChangedActivateFieldsEvent(playerToBeActivated));
    }


    //sends info to GUI to reactivate opponent's fields and block the ones of current player.
    public void notifyReactivateFieldsEvent(ArrayList<Cell> fields)
    {
        GameEventBus.getInstance().publishEvent(new ReactivateFieldsEvent(fields));
    }

    //sends info to GUI that it needs to paint the cell as clicked or moved (both yellow color)
    public void notifyPaintClickedMovedEvent(Cell cell)
    {
        GameEventBus.getInstance().publishEvent(new PaintClickedMovedEvent(cell));
    }

    public void resetTheGame(){}
}

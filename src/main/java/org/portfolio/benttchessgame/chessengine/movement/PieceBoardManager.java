package org.portfolio.benttchessgame.chessengine.movement;

import org.portfolio.benttchessgame.chessengine.Cell;
import org.portfolio.benttchessgame.chessengine.Session;
import org.portfolio.benttchessgame.chessengine.events.*;
import org.portfolio.benttchessgame.chessengine.helperclasses.GameEventBus;
import org.portfolio.benttchessgame.figures.*;

import java.util.ArrayList;

public class PieceBoardManager {

    private PieceBoard pieceBoard;
    private final int GRID_SIZE;
    private final Session currentSession;

    public PieceBoardManager(PieceBoard pieceBoard, Session currentSession) {
        this.currentSession = currentSession;
        this.pieceBoard = pieceBoard;
        this.GRID_SIZE = pieceBoard.GRID_SIZE;

    }

    public void setPieceBoard(PieceBoard pieceBoard) {
        this.pieceBoard = pieceBoard;
    }

    public PieceBoardManager rawMove(MoveOperation operation) {

        Cell from = operation.getInitCell();
        Cell to = operation.getDestCell();

        Figure f = pieceBoard.piecesOnBoard[from.getRow()][from.getCol()];
        pieceBoard.piecesOnBoard[from.getRow()][from.getCol()] = null;
        pieceBoard.piecesOnBoard[to.getRow()][to.getCol()] = f;

        return null;
    }

    public PieceBoard getSnapshot() {
        return pieceBoard.getSnapshot();
    }

    //returns all the cells where said player has physical figures
    public ArrayList<Cell> getPositionCellsOfThePlayer(boolean isWhitePlayer)
    {
        ArrayList<Cell> positionCells = new ArrayList<>();

        for(int row = 0; row < GRID_SIZE; row++)
        {
            for(int col = 0; col < GRID_SIZE; col++)
            {
                if(pieceBoard.getPiecesOnBoard()[row][col] != null && pieceBoard.getPiecesOnBoard()[row][col].getColor() == isWhitePlayer)
                {
                    positionCells.add(new Cell(row,col));
                }
            }
        }
        return positionCells;
    }

    //eliminates the figure on said position
    public void kill(Cell cell) {
        pieceBoard.getPiecesOnBoard()[cell.getRow()][cell.getCol()] = null;
        notifyBoardChangedListeners();
    }


    // returns legal moves of said figure
    public ArrayList<Cell> proposeMove(Cell cell, Session currentSession) {

        Figure piece = pieceBoard.piecesOnBoard[cell.getRow()][cell.getCol()];

        if (piece == null) {
            return new ArrayList<>();
        }
        return Validator.getLegalMovesOf(cell.getRow(), cell.getCol(), currentSession, this.getSnapshot());
    }


    public void move(MoveOperation operation, Session currentSession)
    {
        Cell from = operation.getInitCell();
        Cell to = operation.getDestCell();

        Figure[][] board = pieceBoard.piecesOnBoard; //this has exact copies so it works on original array

        if(board[from.getRow()][from.getCol()] != null)
        {
            Figure f= board[from.getRow()][from.getCol()];
            board[from.getRow()][from.getCol()] = null;
            board[to.getRow()][to.getCol()] = f;

            if(f instanceof Pawn p)
            {
               handleThePawn(from,to,board,p);
            }
            if(f instanceof Rook r)
            {
                r.setDidMove();
            }

            //here we are getting castling and applying it to the game.
            if(f instanceof  King k)
            {
                handleTheKing(k, operation);
            }

            //checkmate straight away after movement, before switching the player colors. Looks only for complete lack of movements
            //no possible escape left:
            boolean checkmate = RuleApplicator.isCheckmate(getSnapshot().getPiecesOnBoard(), !currentSession.getTurn());
            if(checkmate)
            {
                //if checkmate(no possible moves for the next player) then the gameOverEvent is being sent.
                notifyBoardChangedListeners();
                notifyGameOverListener(currentSession.getTurn());
                System.err.print("CHECKMATE");
                return;
            }
            //if no checkmate, then the program executes further and checks if kingCell of opposite player is attacked.
            boolean kingCellAttacked = RuleApplicator.isKingCellAttackedBy(getSnapshot().getPiecesOnBoard(), Validator.getTheKingCell(getSnapshot().getPiecesOnBoard(), !currentSession.getTurn()), currentSession.getTurn());

            //if the opposite player's king is attacked, then the adequate color is being added to the list of colors
            if(kingCellAttacked)
            {
                ArrayList<Cell> attackerAndTheKing = new ArrayList<>();
                //add the king field to be highlighted as DARK RED
                attackerAndTheKing.add(Validator.getTheKingCell(getSnapshot().piecesOnBoard, !currentSession.getTurn()));
                attackerAndTheKing.add(operation.getDestCell());

                //NEED TO FIND THE ATTACKER STILL
                notifyBoardChangedListeners();
                notifyKingCellAttacked(attackerAndTheKing);
            }
            //end

        }

        //notify the Board listeners that it has changed
        notifyBoardChangedListeners();

    }

    private void handleTheKing(King k, MoveOperation kingOperation)
    {
        if(!movesSideways(kingOperation)) return;
        if(k.isCastlingPossible())
        {
            MoveOperation castlingOperation = k.getCastling(kingOperation);
            if(castlingOperation != null)
            {
                rawMove(castlingOperation);
            }
        }
        k.setDidMove();
    }

    private boolean movesSideways(MoveOperation operation)
    {
        return operation.getInitCell().getRow() == operation.getDestCell().getRow();
    }

    private void handleThePawn(Cell from, Cell to, Figure[][] board, Pawn p)
    {
        int diff = from.getCol() > to.getCol() ? -1 : 1;
        Figure enpassant = null;
        if((from.getCol() + diff) < GRID_SIZE && from.getCol() + diff >= 0)
        {
            enpassant = board[from.getRow()][from.getCol() + diff];
        }
        if(enpassant != null && board[from.getRow()][from.getCol() + diff] instanceof Pawn capturedPawn && capturedPawn.getColor() != p.getColor())
        {
            Cell cell = new Cell(from.getRow(), from.getCol() + diff);
            if(capturedPawn.isEnpassantPossible(currentSession)) this.kill(cell);
        }

        p.incTheRank();
        if(Math.abs(from.getRow() - to.getRow()) == 2)
        {
            p.incTheRank();
            p.setEnpassantPossible(currentSession);
        }
        p.disableFirstMove(); //This is special case for the Pawn.
        if(p.getRank() == 7)
        {
            promotePawn(new Queen(p.getColor()), to.getRow(), to.getCol());
        }
    }

    private void promotePawn(Figure fig, int row, int col)
    {
        this.pieceBoard.piecesOnBoard[row][col] = fig;
    }

    public void notifyBoardChangedListeners()
    {
        GameEventBus.getInstance().publishEvent(new BoardChangedEvent(pieceBoard.piecesOnBoard));
    }

    public void notifyGameOverListener(boolean winningPlayer)
    {
        GameEventBus.getInstance().publishEvent(new GameOverEvent(winningPlayer));
    }

    public void notifyKingCellAttacked(ArrayList<Cell> attackerAndTheKing)
    {
        GameEventBus.getInstance().publishEvent(new KingCellAttackedEvent(attackerAndTheKing));
    }

}

package org.portfolio.benttchessgame.chessengine;

import UILogic.helperclasses.EnumColors;
import org.portfolio.benttchessgame.chessengine.events.AvlMovesChangedEvent;
import org.portfolio.benttchessgame.chessengine.events.BoardChangedEvent;
import org.portfolio.benttchessgame.chessengine.helperclasses.GameEventBus;
import org.portfolio.benttchessgame.chessengine.listeners.AvailableMovesChangeListener;
import org.portfolio.benttchessgame.chessengine.listeners.BoardChangedListener;
import org.portfolio.benttchessgame.figures.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PieceBoardManager {

    HashMap<EnumColors, ArrayList<Cell>> paletteMap = new HashMap<>();

    public final boolean WHITE = true;
    public final boolean BLACK = false;
    public final int INVERSE = -1;

    private final PieceBoard pieceBoard;
    private final int GRID_SIZE;
    private final Figure[][] piecesOnBoard;
    private final Session currentSession;

    public PieceBoardManager(PieceBoard pieceBoard, Session currentSession)
    {
        this.currentSession = currentSession;
        this.pieceBoard = pieceBoard;
        this.GRID_SIZE = pieceBoard.GRID_SIZE;
        this.piecesOnBoard = pieceBoard.getPiecesOnBoard();

        setToStartingPositions();
        fillTheStarterMoves(WHITE);
    }

    public PieceBoard getSnapshot()
    {
        return pieceBoard.getSnapshot();
    }

    private void setToStartingPositions()
    {
        for(int i = 0; i < GRID_SIZE; i++)
        {
            for(int j = 0; j < GRID_SIZE; j++)
            {
                if(i == 0)
                {
                    if(j==0 || j == 7)
                    {
                        piecesOnBoard[i][j] = new Rook(WHITE);
                        piecesOnBoard[GRID_SIZE-1][j] = new Rook(BLACK);
                    }
                    else if(j==1 || j == 6)
                    {
                        piecesOnBoard[i][j] = new Knight(WHITE);
                        piecesOnBoard[GRID_SIZE - 1][j] = new Knight(BLACK);
                    }
                    else if(j==2 || j== 5)
                    {
                        piecesOnBoard[i][j] = new Bishop(WHITE);
                        piecesOnBoard[GRID_SIZE - 1][j] = new Bishop(BLACK);
                    }
                    else if(j == 3)
                    {
                        piecesOnBoard[i][j] = new Queen(WHITE);
                        piecesOnBoard[GRID_SIZE - 1][j] = new Queen(BLACK);
                    }
                    else if(j == 4)
                    {
                        piecesOnBoard[i][j] = new King(WHITE);
                        piecesOnBoard[GRID_SIZE - 1][j] = new King(BLACK);
                    }
                }
                else if( i == 1)
                {
                    piecesOnBoard[i][j] = new Pawn(WHITE);
                    piecesOnBoard[GRID_SIZE-2][j] = new Pawn(BLACK, INVERSE);
                }


            }
        }
    }

public ArrayList<Cell> fillTheStarterMoves(boolean color) {
    ArrayList<Cell> blankField = new ArrayList<>();

    for (int row = 0; row < GRID_SIZE; row++) {
        for (int col = 0; col < GRID_SIZE; col++) {
            Figure f = piecesOnBoard[row][col];
            if (f != null && f.getColor() == color) {
                blankField.add(new Cell(row, col));
            }
        }
    }

    paletteMap.clear();
    paletteMap.put(EnumColors.BLANK, blankField);
    notifyAvlMovesChangedListeners();

    return blankField;
}

    public void kill(Cell cell)
    {
        pieceBoard.getPiecesOnBoard()[cell.getRow()][cell.getCol()] = null;
        notifyBoardChangedListeners();
    }


    public void proposeMove(int row, int col, Session currentSession) {
        Figure piece = piecesOnBoard[row][col];

        if (piece == null || piece.getColor() != currentSession.getTurn()) {
            fillTheStarterMoves(currentSession.getTurn());
            return;
        }

        paletteMap.clear();
        paletteMap = Validator.checkMoves(row, col, this.getSnapshot(), this, currentSession, paletteMap);
        notifyAvlMovesChangedListeners();
    }

    public void move(MoveOperation operation, Session currentSession, boolean accessedByConsumer)
    {
        if(!accessedByConsumer)
        {
            Queue<MoveOperation> moveOperationList = new ConcurrentLinkedQueue<>();
            if(this.getSnapshot().getPiecesOnBoard()[operation.getInitCell().getRow()][operation.getInitCell().getCol()] instanceof King k)
            {
                if(k.getCastling() != null)
                {
                    moveOperationList.add(k.getCastling());
                    k.setCastling(null);
                }
            }
            moveOperationList.add(operation);

            currentSession.publishMoves(moveOperationList);

            return;
        }

        Cell from = operation.getInitCell();
        Cell to = operation.getDestCell();

        Figure[][] board = piecesOnBoard;

        if(board[from.getRow()][from.getCol()] != null)
        {
            Figure f= board[from.getRow()][from.getCol()];
            board[from.getRow()][from.getCol()] = null;
            board[to.getRow()][to.getCol()] = f;

            if(f instanceof Pawn p)
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

            boolean val = RuleApplicator.checkTheGameFinished(piecesOnBoard);
        }

        notifyBoardChangedListeners();
    }

    private void promotePawn(Figure fig, int row, int col)
    {
        this.piecesOnBoard[row][col] = fig;
    }

    public void notifyAvlMovesChangedListeners() {
        HashMap<EnumColors, ArrayList<Cell>> copy = new HashMap<>();

        for (var entry : paletteMap.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        GameEventBus.getInstance().publishEvent(new AvlMovesChangedEvent(copy));
    }

    public void notifyBoardChangedListeners()
    {
        GameEventBus.getInstance().publishEvent(new BoardChangedEvent(piecesOnBoard));
    }

}

package org.portfolio.benttchessgame.engine.board;

import org.portfolio.benttchessgame.engine.BoardSquare;
import org.portfolio.benttchessgame.engine.GameSession;
import org.portfolio.benttchessgame.engine.event.*;
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;
import org.portfolio.benttchessgame.engine.rule.ChessRules;
import org.portfolio.benttchessgame.engine.rule.MoveValidator;
import org.portfolio.benttchessgame.piece.*;

import java.util.ArrayList;

public class BoardManager {

    private ChessBoard chessBoard;
    private final int GRID_SIZE;
    private final GameSession currentGameSession;

    public BoardManager(ChessBoard chessBoard, GameSession currentGameSession) {
        this.currentGameSession = currentGameSession;
        this.chessBoard = chessBoard;
        this.GRID_SIZE = chessBoard.GRID_SIZE;

    }

    public void setPieceBoard(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    public BoardManager rawMove(Move operation) {

        BoardSquare from = operation.getInitCell();
        BoardSquare to = operation.getDestCell();

        Piece f = chessBoard.piecesOnBoard[from.getRow()][from.getCol()];
        chessBoard.piecesOnBoard[from.getRow()][from.getCol()] = null;
        chessBoard.piecesOnBoard[to.getRow()][to.getCol()] = f;

        return null;
    }

    public ChessBoard getSnapshot() {
        return chessBoard.getSnapshot();
    }

    //returns all the cells where said player has physical figures
    public ArrayList<BoardSquare> getPositionCellsOfThePlayer(boolean isWhitePlayer)
    {
        ArrayList<BoardSquare> positionBoardSquares = new ArrayList<>();

        for(int row = 0; row < GRID_SIZE; row++)
        {
            for(int col = 0; col < GRID_SIZE; col++)
            {
                if(chessBoard.getPiecesOnBoard()[row][col] != null && chessBoard.getPiecesOnBoard()[row][col].getColor() == isWhitePlayer)
                {
                    positionBoardSquares.add(new BoardSquare(row,col));
                }
            }
        }
        return positionBoardSquares;
    }

    //eliminates the figure on said position
    public void kill(BoardSquare boardSquare) {
        chessBoard.getPiecesOnBoard()[boardSquare.getRow()][boardSquare.getCol()] = null;
        notifyBoardChangedListeners();
    }


    // returns legal moves of said figure
    public ArrayList<BoardSquare> proposeMove(BoardSquare boardSquare, GameSession currentGameSession) {

        Piece piece = chessBoard.piecesOnBoard[boardSquare.getRow()][boardSquare.getCol()];

        if (piece == null) {
            return new ArrayList<>();
        }
        return MoveValidator.getLegalMovesOf(boardSquare.getRow(), boardSquare.getCol(), currentGameSession, this.getSnapshot());
    }


    public void move(Move operation, GameSession currentGameSession)
    {
        BoardSquare from = operation.getInitCell();
        BoardSquare to = operation.getDestCell();

        Piece[][] board = chessBoard.piecesOnBoard; //this has exact copies so it works on original array

        if(board[from.getRow()][from.getCol()] != null)
        {
            Piece f= board[from.getRow()][from.getCol()];
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
            boolean checkmate = ChessRules.isCheckmate(getSnapshot().getPiecesOnBoard(), !currentGameSession.getTurn());
            if(checkmate)
            {
                //if checkmate(no possible moves for the next player) then the gameOverEvent is being sent.
                notifyBoardChangedListeners();
                notifyGameOverListener(currentGameSession.getTurn());
                System.err.print("CHECKMATE");
                return;
            }
            //if no checkmate, then the program executes further and checks if kingCell of opposite player is attacked.
            boolean kingCellAttacked = ChessRules.isKingCellAttackedBy(getSnapshot().getPiecesOnBoard(), MoveValidator.getTheKingCell(getSnapshot().getPiecesOnBoard(), !currentGameSession.getTurn()), currentGameSession.getTurn());

            //if the opposite player's king is attacked, then the adequate color is being added to the list of colors
            if(kingCellAttacked)
            {
                ArrayList<BoardSquare> attackerAndTheKing = new ArrayList<>();
                //add the king field to be highlighted as DARK RED
                attackerAndTheKing.add(MoveValidator.getTheKingCell(getSnapshot().piecesOnBoard, !currentGameSession.getTurn()));
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

    private void handleTheKing(King k, Move kingOperation)
    {
        if(!movesSideways(kingOperation)) return;
        if(k.isCastlingPossible())
        {
            Move castlingOperation = k.getCastling(kingOperation);
            if(castlingOperation != null)
            {
                rawMove(castlingOperation);
            }
        }
        k.setDidMove();
    }

    private boolean movesSideways(Move operation)
    {
        return operation.getInitCell().getRow() == operation.getDestCell().getRow();
    }

    private void handleThePawn(BoardSquare from, BoardSquare to, Piece[][] board, Pawn p)
    {
        int diff = from.getCol() > to.getCol() ? -1 : 1;
        Piece enpassant = null;
        if((from.getCol() + diff) < GRID_SIZE && from.getCol() + diff >= 0)
        {
            enpassant = board[from.getRow()][from.getCol() + diff];
        }
        if(enpassant != null && board[from.getRow()][from.getCol() + diff] instanceof Pawn capturedPawn && capturedPawn.getColor() != p.getColor())
        {
            BoardSquare boardSquare = new BoardSquare(from.getRow(), from.getCol() + diff);
            if(capturedPawn.isEnpassantPossible(currentGameSession)) this.kill(boardSquare);
        }

        p.incTheRank();
        if(Math.abs(from.getRow() - to.getRow()) == 2)
        {
            p.incTheRank();
            p.setEnpassantPossible(currentGameSession);
        }
        p.disableFirstMove(); //This is special case for the Pawn.
        if(p.getRank() == 7)
        {
            promotePawn(new Queen(p.getColor()), to.getRow(), to.getCol());
        }
    }

    private void promotePawn(Piece fig, int row, int col)
    {
        this.chessBoard.piecesOnBoard[row][col] = fig;
    }

    public void notifyBoardChangedListeners()
    {
        GameEventBus.getInstance().publishEvent(new BoardChangedEvent(chessBoard.piecesOnBoard));
    }

    public void notifyGameOverListener(boolean winningPlayer)
    {
        GameEventBus.getInstance().publishEvent(new GameEndedEvent(winningPlayer));
    }

    public void notifyKingCellAttacked(ArrayList<BoardSquare> attackerAndTheKing)
    {
        GameEventBus.getInstance().publishEvent(new KingInCheckEvent(attackerAndTheKing));
    }

}

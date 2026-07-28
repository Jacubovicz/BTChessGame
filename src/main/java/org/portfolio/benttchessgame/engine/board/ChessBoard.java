package org.portfolio.benttchessgame.engine.board;

import org.portfolio.benttchessgame.engine.rule.MoveValidator;
import org.portfolio.benttchessgame.piece.*;

public class ChessBoard {

    public final boolean WHITE = true;
    public final boolean BLACK = false;
    public final int INVERSE = -1;

    public Piece[][] piecesOnBoard;

    public final int GRID_SIZE = 8;

    //default constructor, creates the board of chess with figures
    public ChessBoard()
    {
        piecesOnBoard = new Piece[GRID_SIZE][GRID_SIZE];
        resetToStartingPositions();
    }

    //constructor needed for the snapshot
    private ChessBoard(ChessBoard chessBoard)
    {
        this.piecesOnBoard = MoveValidator.copy(chessBoard.getPiecesOnBoard());
    }

    public void resetToStartingPositions()
    {
        cleanupTheBoard();
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
                    else {
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

    //removes all the pieces from board, refills with nulls
    private void cleanupTheBoard()
    {
        for(int i = 0; i < GRID_SIZE; i++)
        {
            for(int j = 0; j < GRID_SIZE; j++)
            {
                piecesOnBoard[i][j] = null;
            }
        }
    }

    //returns Figure table
    public Piece[][] getPiecesOnBoard()
    {
        return this.piecesOnBoard;
    }

    //returns the immutable copy of PieceBoard
    public ChessBoard getSnapshot()
    {
        return new ChessBoard(this);
    }
}


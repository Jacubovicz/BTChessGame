package org.portfolio.benttchessgame.engine.board;

import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.rule.MoveValidator;
import org.portfolio.benttchessgame.engine.tools.UtilityMethods;
import org.portfolio.benttchessgame.piece.*;

public class ChessBoard {

    public final int INVERSE = -1;
    private final Piece[][] piecesOnBoard;
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
        this.piecesOnBoard = UtilityMethods.copyPieceArray(chessBoard.getPiecesOnBoard());
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
                        piecesOnBoard[i][j] = new Rook(PlayerColor.WHITE);
                        piecesOnBoard[GRID_SIZE-1][j] = new Rook(PlayerColor.DARK);
                    }
                    else if(j==1 || j == 6)
                    {
                        piecesOnBoard[i][j] = new Knight(PlayerColor.WHITE);
                        piecesOnBoard[GRID_SIZE - 1][j] = new Knight(PlayerColor.DARK);
                    }
                    else if(j==2 || j== 5)
                    {
                        piecesOnBoard[i][j] = new Bishop(PlayerColor.WHITE);
                        piecesOnBoard[GRID_SIZE - 1][j] = new Bishop(PlayerColor.DARK);
                    }
                    else if(j == 3)
                    {
                        piecesOnBoard[i][j] = new Queen(PlayerColor.WHITE);
                        piecesOnBoard[GRID_SIZE - 1][j] = new Queen(PlayerColor.DARK);
                    }
                    else {
                        piecesOnBoard[i][j] = new King(PlayerColor.WHITE);
                        piecesOnBoard[GRID_SIZE - 1][j] = new King(PlayerColor.DARK);
                    }
                }
                else if( i == 1)
                {
                    piecesOnBoard[i][j] = new Pawn(PlayerColor.WHITE);
                    piecesOnBoard[GRID_SIZE-2][j] = new Pawn(PlayerColor.DARK, INVERSE);
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

    public Piece getPieceAt(int row, int col) {
        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE) {
            return null;
        }
        return piecesOnBoard[row][col];
    }

    public void setPieceAt(int row, int col, Piece piece) {
        if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
            piecesOnBoard[row][col] = piece;
        }
    }

    //returns the immutable copy of PieceBoard
    public ChessBoard getSnapshot()
    {
        return new ChessBoard(this);
    }
}


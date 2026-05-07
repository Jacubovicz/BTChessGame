package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.figures.*;

public class PieceBoardManager {

    private final PieceBoard pieceBoard;
    private final int GRID_SIZE;
    private final Figure[][] piecesOnBoard;
    public final boolean WHITE = true;
    public final boolean BLACK = false;
    public final int INVERSE = -1;

    public PieceBoardManager(PieceBoard pieceBoard)
    {
        this.pieceBoard = pieceBoard;
        this.GRID_SIZE = pieceBoard.GRID_SIZE;
        this.piecesOnBoard = pieceBoard.getPiecesOnBoard();

        setToStartingPositions();
        fillTheStarterMoves(WHITE);
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

    public void fillTheStarterMoves(boolean color)
    {
        Figure[][] grid = piecesOnBoard;
        pieceBoard.starterMovesAvailable = new boolean[GRID_SIZE][GRID_SIZE];
        for(int row = 0; row < GRID_SIZE; row++)
        {
            for(int col = 0; col < GRID_SIZE; col++)
            {
                if(grid[row][col] != null && grid[row][col].getColor() == color)
                {
                    pieceBoard.starterMovesAvailable[row][col] = true;
                }
            }
        }
    }

    public void move(MoveOperation moveOperation, boolean turn)
    {
        Cell from = moveOperation.getInitCell();
        Cell to = moveOperation.getDestCell();

        Figure[][] board = piecesOnBoard;

        //As of now, this only performs hardcoded movement and overrides whatever there is in position.
        if(board[from.getRow()][from.getCol()] != null)
        {
            Figure f= board[from.getRow()][from.getCol()];
            if(f instanceof Pawn p) p.disableFirstMove(); //This is special case for the Pawn.
            board[from.getRow()][from.getCol()] = null;
            board[to.getRow()][to.getCol()] = f;

            fillTheStarterMoves(!turn);

        }
    }

}

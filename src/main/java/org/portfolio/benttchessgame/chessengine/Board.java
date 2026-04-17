package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.figures.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//represents Board and pieces on it.
public class Board {

    Logger log = LoggerFactory.getLogger(getClass().getName());

    /*The grid representing pieces on board and grid itself.
    * if there is a piece, it contains polymorphic Figure
    * if not, contains null;
    */
    Figure[][] gridWithPieces;
    boolean[][] starterMovesAvailable;

    private final boolean WHITE = true;
    private final boolean BLACK = false;
    private final int INVERSE = -1;

    //values representing colors of the pieces.
    private final boolean color1, color2;

    //size of the grid row/col
    public final int ROWS = 8;
    public final int COLUMNS = 8;


    /*
    * The board is initialized with Player's colors from Game object
    * and this is what's going to determine which figures(colors) will be filled from top to bottom
    * it also starts the method with filling the board up (fillTheBoard())
     */
    public Board(boolean color1, boolean color2)
    {
        gridWithPieces = new Figure[ROWS][COLUMNS];
       // starterMovesAvailable = new boolean[ROWS][COLUMNS];
        this.color1 = color1;
        this.color2 = color2;
        fillTheBoard();
        fillTheStarterMoves(WHITE);
    }

    public void fillTheStarterMoves(boolean color)
    {
        Figure[][] grid = gridWithPieces;
        starterMovesAvailable = new boolean[ROWS][COLUMNS];
        for(int row = 0; row < ROWS; row++)
        {
            for(int col = 0; col < COLUMNS; col++)
            {
                if(grid[row][col] != null && grid[row][col].getColor() == color)
                {
                    this.starterMovesAvailable[row][col] = true;
                }
            }
        }
    }

    public boolean[][] getStarterMovesAvailable()
    {
        return this.starterMovesAvailable;
    }

    //returning the original grid with pieces
    public Figure[][] getBoard()
    {
        return this.gridWithPieces;
    }

    /*
    * Fills the physical board up with the figures according to pattern
    * starts with player1 color
    * ends with player2 color
     */
    private void fillTheBoard()
    {
        for(int i = 0; i < ROWS; i++)
        {
            for(int j = 0; j < COLUMNS; j++)
            {
                if(i == 0)
                {
                    if(j==0 || j == 7)
                    {
                        gridWithPieces[i][j] = new Rook(color1);
                        gridWithPieces[ROWS - 1][j] = new Rook(color2);
                    }
                    else if(j==1 || j == 6)
                    {
                        gridWithPieces[i][j] = new Knight(color1);
                        gridWithPieces[ROWS - 1][j] = new Knight(color2);
                    }
                    else if(j==2 || j== 5)
                    {
                        gridWithPieces[i][j] = new Bishop(color1);
                        gridWithPieces[ROWS - 1][j] = new Bishop(color2);
                    }
                    else if(j == 3)
                    {
                        gridWithPieces[i][j] = new Queen(color1);
                        gridWithPieces[ROWS - 1][j] = new Queen(color2);
                    }
                    else if(j == 4)
                    {
                        gridWithPieces[i][j] = new King(color1);
                        gridWithPieces[ROWS - 1][j] = new King(color2);
                    }
                }
                else if( i == 1)
                {
                    gridWithPieces[i][j] = new Pawn(color1);
                    gridWithPieces[ROWS-2][j] = new Pawn(color2, INVERSE);
                }


            }
        }
    }

    /*
    * returns the snapshot which is specifically needed for JavaFX rendering
    * but also for debugging
    * it is meant to return the read-only version of grid with pieces
     */
    public Figure[][] getSnapshot()
    {
       return gridWithPieces.clone();
    }


}


package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.figures.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PieceBoard {

    Logger log = LoggerFactory.getLogger(getClass().getName());

    Figure[][] piecesOnBoard;
    boolean[][] starterMovesAvailable;

    public final int GRID_SIZE = 8;

    public PieceBoard()
    {
        piecesOnBoard = new Figure[GRID_SIZE][GRID_SIZE];
    }

    public boolean[][] getStarterMovesAvailable()
    {
        return this.starterMovesAvailable;
    }

    public Figure[][] getPiecesOnBoard()
    {
        return this.piecesOnBoard;
    }

    public Figure[][] getSnapshot()
    {

       Figure[][] snapshot = new Figure[piecesOnBoard.length][piecesOnBoard.length];

       for(int row = 0; row < piecesOnBoard.length; row++)
       {
           snapshot[row] = piecesOnBoard[row].clone();
       }

       return snapshot;
    }



}


package org.portfolio.benttchessgame.chessengine;

public class Cell {

    //final row and column of this specific cell.
    private final int row,col;

    public Cell(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    public int getRow()
    {
        return this.row;
    }

    public int getCol()
    {
        return col;
    }
}

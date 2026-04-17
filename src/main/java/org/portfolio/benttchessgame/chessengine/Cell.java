package org.portfolio.benttchessgame.chessengine;

public class Cell {

    //final row and column of this specific cell.
    private final int row,col;

    /*
    * This is the representation of specific cell positions where Figure exists
    * it consists of parameter row representing rows on physical board
    * and of parameter col representing columns on the same board
     */
    public Cell(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    /*
    * returns tow of the cell
     */
    public int getRow()
    {
        return this.row;
    }

    /*
    * returns column of the cell
     */
    public int getCol()
    {
        return col;
    }
}

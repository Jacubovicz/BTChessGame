package org.portfolio.benttchessgame.chessengine;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;

        if(o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;
        return row == cell.getRow() && col == cell.getCol();

    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getRow(), getCol());
    }
}

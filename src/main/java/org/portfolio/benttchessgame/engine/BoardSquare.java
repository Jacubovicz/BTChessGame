package org.portfolio.benttchessgame.engine;

import java.util.Objects;

public class BoardSquare {

    //final row and column of this specific cell.
    private final int row,col;
    private boolean isCastlingCell = false;

    public BoardSquare(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    public BoardSquare(int row, int col, boolean isCastlingCell)
    {
        this.row = row;
        this.col = col;
        this.isCastlingCell = isCastlingCell;
    }

    public boolean isCastlingCell()
    {
        return isCastlingCell;
    }

    public void setCastlingCell()
    {
        this.isCastlingCell = true;
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

        BoardSquare boardSquare = (BoardSquare) o;
        return row == boardSquare.getRow() && col == boardSquare.getCol();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getRow(), getCol());
    }
}

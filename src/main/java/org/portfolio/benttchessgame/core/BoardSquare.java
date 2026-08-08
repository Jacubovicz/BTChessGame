package org.portfolio.benttchessgame.core;

import java.util.Objects;

public record BoardSquare(int row, int col, boolean isCastlingCell) {

    public BoardSquare(int row, int col) {
        this(row, col, false);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardSquare that = (BoardSquare) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}

package org.portfolio.benttchessgame.piece;

public class Rook extends Piece {

    private boolean moved = false;

    private int[][] offsets = {{0,1}, {0,-1}, {-1,0}, {1,0}};

    public Rook(boolean color) {
        super(color);
        super.setName("rook");
    }

    @Override
    public int getBaseScore() {
        return 50;
    }

    @Override
    public boolean doesSlide()
    {
        return true;
    }

    @Override
    public int[][] getOffset() {
        return offsets;
    }

    public void setDidMove()
    {
        this.moved = true;
    }

    public boolean didMove()
    {
        return moved;
    }

}

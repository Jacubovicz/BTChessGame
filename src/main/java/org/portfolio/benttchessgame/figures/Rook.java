package org.portfolio.benttchessgame.figures;

public class Rook extends Figure{

    private boolean moved = false;

    private int[][] offsets = {{0,1}, {0,-1}, {-1,0}, {1,0}};

    public Rook(boolean color) {
        super(color);
        super.setName("rook");
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

    public void setMoved()
    {
        this.moved = true;
    }

    public boolean getMoved()
    {
        return moved;
    }

}

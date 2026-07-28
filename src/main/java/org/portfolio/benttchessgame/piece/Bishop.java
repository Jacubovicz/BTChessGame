package org.portfolio.benttchessgame.piece;

public class Bishop extends Piece {


    private final int[][] offsets = {{1,1}, {1,-1}, {-1, 1}, {-1,-1}};

    public Bishop(boolean color) {

        super(color);
        super.setName("bishop");
    }

    @Override
    public int getBaseScore() {
        return 30;
    }


    @Override
    public boolean doesSlide() {
        return true;
    }


    @Override
    public int[][] getOffset()
    {
        return offsets;
    }
}

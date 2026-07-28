package org.portfolio.benttchessgame.piece;

public class Queen extends Piece {


    private final int[][] offsets = {{-1,-1}, {-1,0}, {-1,1},{0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};

    public Queen(boolean color) {
        super(color);
        super.setName("queen");;
    }

    @Override
    public int getBaseScore() {
        return 90;
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

}

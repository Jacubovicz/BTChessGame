package org.portfolio.benttchessgame.figures;

public class Queen extends Figure{


    private final int[][] offsets = {{-1,-1}, {-1,0}, {-1,1},{0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};

    public Queen(boolean color) {
        super(color);
        super.setName("queen");;
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

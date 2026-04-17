package org.portfolio.benttchessgame.figures;

public class Bishop extends Figure{


    private final int[][] offsets = {{1,1}, {1,-1}, {-1, 1}, {-1,-1}};


    public Bishop(boolean color) {

        super(color);
        super.setName("bishop");
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

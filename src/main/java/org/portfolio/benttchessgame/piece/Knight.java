package org.portfolio.benttchessgame.piece;

public class Knight extends Piece {



    private int[][] offsets = {{-1,-2}, {-2,-1}, {-2, 1}, {-1,2},
            {1,-2}, {2,-1}, {2,1}, {1,2}};


    public Knight(boolean color) {
        super(color);
        super.setName("knight");
    }

    @Override
    public int getBaseScore() {
        return 30;
    }


    @Override
    public int[][] getOffset() {
        return offsets;
    }

}

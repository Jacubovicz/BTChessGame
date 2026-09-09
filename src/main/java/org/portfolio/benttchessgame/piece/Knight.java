package org.portfolio.benttchessgame.piece;

import org.portfolio.benttchessgame.engine.player.PlayerColor;

public final class Knight extends Piece {



    private int[][] offsets = {{-1,-2}, {-2,-1}, {-2, 1}, {-1,2},
            {1,-2}, {2,-1}, {2,1}, {1,2}};


    public Knight(PlayerColor color) {
        super(color);
        super.setName(PieceType.KNIGHT);
    }

    @Override
    public int getBaseScore() {
        return 30;
    }


    @Override
    public int[][] getOffset() {
        return offsets;
    }

    @Override
    public Piece copy() {
        return new Knight(this.color);
    }

}

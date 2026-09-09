package org.portfolio.benttchessgame.piece;

import org.portfolio.benttchessgame.engine.player.PlayerColor;

public final class Bishop extends Piece {


    private final int[][] offsets = {{1,1}, {1,-1}, {-1, 1}, {-1,-1}};

    public Bishop(PlayerColor color) {

        super(color);
        super.setName(PieceType.BISHOP);
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

    @Override
    public Piece copy() {
        return new Bishop(this.color);
    }
}

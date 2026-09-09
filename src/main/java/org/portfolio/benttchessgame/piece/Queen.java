package org.portfolio.benttchessgame.piece;

import org.portfolio.benttchessgame.engine.player.PlayerColor;

public final class Queen extends Piece {


    private final int[][] offsets = {{-1,-1}, {-1,0}, {-1,1},{0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};

    public Queen(PlayerColor color) {
        super(color);
        super.setName(PieceType.QUEEN);;
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

    @Override
    public Piece copy() {
        return new Queen(this.color);
    }

}

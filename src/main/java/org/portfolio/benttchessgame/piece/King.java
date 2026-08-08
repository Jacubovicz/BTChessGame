package org.portfolio.benttchessgame.piece;

import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.player.PlayerColor;

import java.util.ArrayList;

public final class King extends Piece {

    private final int[][] offsets = {{-1,-1}, {-1,0}, {-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};

    private boolean moved = false;

    public King(PlayerColor color) {
        super(color);
        super.setName("king");
    }

    @Override
    public boolean isKing()
    {
        return true;
    }

    @Override
    public int getBaseScore() {
        return 10000;
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
        return this.moved;
    }
}

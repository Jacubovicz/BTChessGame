package org.portfolio.benttchessgame.figures;

import org.portfolio.benttchessgame.chessengine.MoveOperation;

public class King extends Figure{

    private MoveOperation castling;
    private final int[][] offsets = {{-1,-1}, {-1,0}, {-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};

    private boolean moved = false;

    public King(boolean color) {
        super(color);
        super.setName("king");
    }

    @Override
    public int[][] getOffset() {

        return offsets;
    }

    public MoveOperation getCastling()
    {
        return castling;
    }

    public void setCastling(MoveOperation castlingOperation)
    {
        this.castling = castlingOperation;
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

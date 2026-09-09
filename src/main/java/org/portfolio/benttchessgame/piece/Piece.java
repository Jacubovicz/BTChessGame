package org.portfolio.benttchessgame.piece;

import org.portfolio.benttchessgame.engine.player.PlayerColor;

public abstract sealed class Piece permits Bishop, King, Knight, Pawn, Queen, Rook {

    protected final PlayerColor color;
    private final String colorName;
    private PieceType TYPE;

    public Piece(PlayerColor color)
    {
        this.color = color;
        this.colorName = color == PlayerColor.WHITE ? "WHITE" : "DARK";
    }

    public boolean isRook()
    {
        return false;
    }

    public boolean isPawn()
    {
        return false;
    }

    public boolean isKing()
    {
        return false;
    }

    public abstract int getBaseScore();

    protected void setName(PieceType pieceType)
    {
        this.TYPE = pieceType;
    }

    public PieceType getType()
    {
        return this.TYPE;
    }


    public String getColorName()
    {
        return this.colorName;
    }

    public boolean doesSlide()
    {
        return false;
    }

    public abstract int[][] getOffset();

    public PlayerColor getColor()
    {
        return color;
    }

    public abstract Piece copy();

    @Override
    public String toString()
    {
        return this.getColorName() + "_" + this.TYPE;
    }

}

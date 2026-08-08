package org.portfolio.benttchessgame.piece;

import org.portfolio.benttchessgame.engine.player.PlayerColor;

public abstract sealed class Piece permits Bishop, King, Knight, Pawn, Queen, Rook {

    private final PlayerColor color;
    private final String colorName;
    private String NAME = "";

    public Piece(PlayerColor color)
    {
        this.color = color;
        this.colorName = color == PlayerColor.WHITE ? "white" : "dark";
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

    protected void setName(String name)
    {
        this.NAME = name;
    }

    public String getName()
    {
        return this.NAME;
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

    @Override
    public String toString()
    {
        return this.getColorName() + "_" + NAME;
    }

}

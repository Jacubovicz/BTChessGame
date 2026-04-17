package org.portfolio.benttchessgame.figures;

public abstract class Figure {

    private final boolean color;
    private final String colorName;


    private String NAME = "";

    private boolean WHITE = true;
    private boolean DARK = false;

    public Figure(boolean color)
    {
        this.color = color;
        this.colorName = color == WHITE ? "white" : "dark";
    }

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

    public boolean getColor()
    {
        return this.color;
    }

    @Override
    public String toString()
    {
        return this.getColorName() + "_" + NAME;
    }


}

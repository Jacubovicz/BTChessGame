package org.portfolio.benttchessgame.figures;

public class Pawn extends Figure{

    private final int DOWN = 1;
    private final int UP = -1;
    private int vector= 1;/* 1 = down, -1 = UP */
    private int[][] offsetsStart = {{1,0},{2,0}};
    private int[][] offsets = {{1,0}};
    private int[][] beatingOffset = {{1,1},{1,-1}};
    private boolean firstMove = true;

    private boolean isLocked = false;


    public Pawn(boolean color) {
        super(color);
        super.setName("pawn");
    }

    public Pawn(boolean color, int vector)
    {
        super(color);
        super.setName("pawn");
        this.vector = vector;

        if(vector == -1)
        {
            for(int[] offset : offsetsStart)
            {
                offset[0] *= vector;
            }
            this.offsets[0][0] *= vector;

        }

    }


    @Override
    public int[][] getOffset() {

        if(firstMove)
        {
            return offsetsStart;
        }
        else
            return offsets;
    }

    public int[][] getBeatingOffset()
    {
        return this.beatingOffset;
    }

    public void disableFirstMove()
    {
        this.firstMove = false;
    }


    public void setVector(int vector)
    {
        try
        {
            this.vector = vector;
            this.offsetsStart = new int[][]{{-1,0},{-2,0}};
            this.offsets = new int[][]{{-1,0}};

        }
        catch(IllegalArgumentException iax)
        {
            iax.printStackTrace();
            System.err.print("\nWrong arguments passed into the method. 1 for down, -1 for up");
        }

    }

    public int getVector()
    {
        return this.vector;
    }

    public void setLocked()
    {
        this.isLocked = true;
    }

    public boolean isLocked()
    {
        return this.isLocked;
    }

    public boolean isFirstMove()
    {
        return this.firstMove;
    }
}

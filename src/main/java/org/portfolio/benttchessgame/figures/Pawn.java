package org.portfolio.benttchessgame.figures;

public class Pawn extends Figure{

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

        if(vector == -1)
        {
            for(int[] offset : offsetsStart)
            {
                offset[0] *= vector;
            }
            for(int[] offset : beatingOffset)
            {
                offset[0] *= vector;
                offset[1] *= vector;
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

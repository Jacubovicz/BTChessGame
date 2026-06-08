package org.portfolio.benttchessgame.figures;

import org.portfolio.benttchessgame.chessengine.Cell;

public class Pawn extends Figure{

    private int[][] offsetsStart = {{1,0},{2,0}};
    private int[][] offsets = {{1,0}};
    private int[][] beatingOffset = {{1,1},{1,-1}};

    private int vector = 1;

    private int rank = 1; //1 is initial(starting position) consider it in using evaluation later.
    // rank 7 is highest. When rank 7 -> pawn promotion (Queen, Rook, Bishop)

    private boolean firstMove = true;
    private boolean isLocked = false;

    private Cell enpassant = null;

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
            this.vector = -1;
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

    public void setEnpassant(Cell cell)
    {
        this.enpassant = cell;
    }

    public boolean isEnpassantPossible()
    {
        return enpassant != null ? true : false;
    }

    public Cell getEnpassantCell()
    {
        return enpassant;
    }

    public void incTheRank()
    {
        this.rank++;
    }

    public int getRank()
    {
        return this.rank;
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

    public int getVector()
    {
        return this.vector;
    }

    public void disableEnpassant()
    {
        this.enpassant = null;
    }
}

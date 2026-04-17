package org.portfolio.benttchessgame.figures;

public class Pawn extends Figure{

    private final int DOWN = 1;
    private final int UP = -1;

    private int[][] offsetsStart = {{1,0},{2,0}};
    private int vector; /* 1 = down, -1 = UP */
    private int[][] offsets = {{1,0}};
    boolean firstMove = true;



    public Pawn(boolean color) {
        super(color);
        super.setName("pawn");
    }

    public Pawn(boolean color, int vector)
    {
        super(color);
        super.setName("pawn");
        this.vector = vector;
    }


    @Override
    public int[][] getOffset() {

        if(firstMove) return offsetsStart;

        firstMove = false;
        return offsets;

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
}

package org.portfolio.benttchessgame.piece;

import org.portfolio.benttchessgame.engine.GameState;
import org.portfolio.benttchessgame.engine.player.PlayerColor;

public final class Pawn extends Piece {

    private int[][] offsetsStart = {{1,0},{2,0}};
    private int[][] offsets = {{1,0}};
    private int[][] beatingOffset = {{1,1},{1,-1}};

    private int vector = 1;

    private int rank = 1; //1 is initial(starting position) consider it in using evaluation later.
    // rank 7 is highest. When rank 7 -> pawn promotion (Queen, Rook, Bishop)

    private boolean firstMove = true;
    private boolean enpassantPossible = false;
    private int enpassantTurn;

    public Pawn(PlayerColor color) {
        super(color);
        super.setName("pawn");
    }

    @Override
    public boolean isPawn()
    {
        return true;
    }

    @Override
    public int getBaseScore() {
        return 10;
    }

    public Pawn(PlayerColor color, int vector)
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



    public void setEnpassantPossible(GameState state)
    {
        this.enpassantPossible = true;
        this.enpassantTurn = state.moveCounter();
    }

    public boolean isEnpassantPossible(GameState state)
    {
        return state.moveCounter() - this.enpassantTurn == 1;
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
        this.enpassantPossible= false;
    }
}

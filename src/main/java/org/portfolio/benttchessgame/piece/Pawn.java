package org.portfolio.benttchessgame.piece;

import org.portfolio.benttchessgame.engine.GameState;
import org.portfolio.benttchessgame.engine.player.PlayerColor;

public final class Pawn extends Piece {

    private final int[][] offsetsStart = {{1,0},{2,0}};
    private final int[][] offsets = {{1,0}};
    private final int[][] beatingOffset = {{1,1},{1,-1}};

    private int vector = 1;

    private int rank = 1; //1 is initial(starting position) consider it in using evaluation later.
    // rank 7 is highest. When rank 7 -> pawn promotion (Queen, Rook, Bishop)

    private boolean firstMove = true;
    private boolean enpassantPossible = false;
    private int enpassantTurn;

    public Pawn(PlayerColor color) {
        super(color);
        super.setName(PieceType.PAWN);
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

    public Pawn(PlayerColor color, int vector) {
        super(color);
        super.setName(PieceType.PAWN);

        if(vector == -1) {
            this.vector = -1;
            for(int[] offset : offsetsStart) {
                offset[0] *= vector;
            }
            for(int[] offset : beatingOffset) {
                offset[0] *= vector;
                offset[1] *= vector;
            }
            this.offsets[0][0] *= vector;
        }
    }



    public void setEnpassantPossible(int moveCounter) {
        this.enpassantPossible = true;
        this.enpassantTurn = moveCounter;
    }

    public boolean isEnpassantPossible(int activeTurn) {
        return enpassantPossible && activeTurn - this.enpassantTurn == 1;
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

    @Override
    public Piece copy() {
        Pawn newCopy = new Pawn(this.color, this.vector);
        newCopy.rank = this.rank;
        newCopy.firstMove = this.firstMove;
        newCopy.enpassantPossible = this.enpassantPossible;
        newCopy.enpassantTurn = this.enpassantTurn;

        return newCopy;
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

}

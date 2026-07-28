package org.portfolio.benttchessgame.piece;

import org.portfolio.benttchessgame.engine.board.Move;

import java.util.ArrayList;

public class King extends Piece {

    private final ArrayList<Move> castling = new ArrayList<>(2);
    private final int[][] offsets = {{-1,-1}, {-1,0}, {-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};

    private final int QUEENSIDE_CASTLING = 0; //more space between the king and the Rook
    private final int KINGSIDE_CASTLING = 1; //less space inbetween the king and the Rook

    private boolean moved = false;

    public King(boolean color) {
        super(color);
        super.setName("king");
    }

    @Override
    public int getBaseScore() {
        return 10000;
    }

    @Override
    public int[][] getOffset() {

        return offsets;
    }

    public void setCastling(Move castlingOperation, int colPositionOfAKing)
    {
        if(Math.abs(castlingOperation.getInitCell().getCol() - colPositionOfAKing) == 4)
        {
            setQueenSideCastling(castlingOperation);
        }
        else
            setKingSideCastling(castlingOperation);
    }

    public void setQueenSideCastling(Move castlingOperation)
    {
        castling.add(QUEENSIDE_CASTLING, castlingOperation);
    }

    public void setKingSideCastling(Move castlingOperation)
    {
        castling.add(KINGSIDE_CASTLING, castlingOperation);
    }

    public Move getCastling(Move kingMove)
    {
        if(!isCastlingPossible()) return null;



        if(castling.get(QUEENSIDE_CASTLING) != null && Math.abs(castling.get(QUEENSIDE_CASTLING).getInitCell().getCol() - kingMove.getDestCell().getCol()) == 2)
        {
            return castling.get(QUEENSIDE_CASTLING);
        }
        else
            if(castling.get(KINGSIDE_CASTLING) != null)
            {
                return castling.get(KINGSIDE_CASTLING);

            }
          return null;
    }

    public boolean isCastlingPossible()
    {
        return !castling.isEmpty();
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

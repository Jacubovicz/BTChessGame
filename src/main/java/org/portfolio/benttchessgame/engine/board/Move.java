package org.portfolio.benttchessgame.engine.board;

import org.portfolio.benttchessgame.engine.BoardSquare;

public class Move {

    BoardSquare initBoardSquare, destBoardSquare;

    private boolean owner;

    public Move() {}

    public Move(BoardSquare initBoardSquare, BoardSquare destBoardSquare) {
        this.initBoardSquare = initBoardSquare;
        this.destBoardSquare = destBoardSquare;
    }

    public void clear()
    {
        this.initBoardSquare = null;
        this.destBoardSquare = null;
    }

    public boolean getOwner()
    {
        return owner;
    }

    public void setOwner(boolean owner)
    {
        this.owner = owner;
    }

    public BoardSquare getInitCell()
    {
        return initBoardSquare;
    }

    public BoardSquare getDestCell()
    {
        return destBoardSquare;
    }

    public void setInitCell(BoardSquare initBoardSquare)
    {
        this.initBoardSquare = initBoardSquare;
    }

    public void setDestCell(BoardSquare destBoardSquare)
    {
        this.destBoardSquare = destBoardSquare;
    }
}

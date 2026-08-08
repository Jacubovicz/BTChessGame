package org.portfolio.benttchessgame.engine.board;

import org.portfolio.benttchessgame.core.BoardSquare;

public record Move(BoardSquare initBoardSquare, BoardSquare destBoardSquare, boolean owner) {

    public Move(BoardSquare initBoardSquare, BoardSquare destBoardSquare) {
        this(initBoardSquare, destBoardSquare, false);
    }

    public BoardSquare getInitCell() {
        return initBoardSquare;
    }

    public BoardSquare getDestCell() {
        return destBoardSquare;
    }

    public boolean getOwner() {
        return owner;
    }
}

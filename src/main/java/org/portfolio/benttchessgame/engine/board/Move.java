package org.portfolio.benttchessgame.engine.board;

import org.portfolio.benttchessgame.core.BoardSquare;

import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.piece.PieceType;

public record Move(BoardSquare initBoardSquare, BoardSquare destBoardSquare, PieceType promotionType, PlayerColor owner) {

    public Move(BoardSquare initBoardSquare, BoardSquare destBoardSquare) {
        this(initBoardSquare, destBoardSquare, null, null);
    }

    public Move(BoardSquare initBoardSquare, BoardSquare destBoardSquare, PieceType promotionType) {
        this(initBoardSquare, destBoardSquare, promotionType, null);
    }

    public BoardSquare getInitCell() {
        return initBoardSquare;
    }

    public BoardSquare getDestCell() {
        return destBoardSquare;
    }

    public PieceType getPromotionType() {
        return promotionType;
    }

}

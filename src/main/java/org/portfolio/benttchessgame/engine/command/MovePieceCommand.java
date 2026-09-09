package org.portfolio.benttchessgame.engine.command;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.ChessEngine;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.piece.PieceType;

public class MovePieceCommand implements GameCommand {

    private final BoardSquare destBoardSquare;
    private final PieceType promotionType;

    public MovePieceCommand(BoardSquare destBoardSquare) {
        this(destBoardSquare, null);
    }

    public MovePieceCommand(BoardSquare destBoardSquare, PieceType promotionType) {
        this.destBoardSquare = destBoardSquare;
        this.promotionType = promotionType;
    }

    @Override
    public void execute(ChessEngine engine) {
        try {
            engine.moveTheFigure(destBoardSquare, promotionType);
        } catch (IllegalMoveException e) {
            // Handle appropriately, maybe publish an error event
            e.printStackTrace();
        }
    }
}

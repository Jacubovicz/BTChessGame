package org.portfolio.benttchessgame.engine.command;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.ChessEngine;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;

public class MovePieceCommand implements GameCommand {

    private final BoardSquare destBoardSquare;

    public MovePieceCommand(BoardSquare destBoardSquare) {
        this.destBoardSquare = destBoardSquare;
    }

    @Override
    public void execute(ChessEngine engine) {
        try {
            engine.moveTheFigure(destBoardSquare);
        } catch (IllegalMoveException e) {
            // Handle appropriately, maybe publish an error event
            e.printStackTrace();
        }
    }
}

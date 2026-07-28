package org.portfolio.benttchessgame.engine.command;

import org.portfolio.benttchessgame.engine.BoardSquare;

public class MovePieceCommand implements GameCommand {

    BoardSquare destBoardSquare;

    public MovePieceCommand(BoardSquare destBoardSquare){
        this.destBoardSquare = destBoardSquare;
    }

    public BoardSquare getMoveOperation()
    {
        return destBoardSquare;
    }
}

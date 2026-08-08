package org.portfolio.benttchessgame.engine.command;

import org.portfolio.benttchessgame.engine.ChessEngine;

public class RequestLegalMovesCommand implements GameCommand {

    private final int row, col;

    public RequestLegalMovesCommand(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public void execute(ChessEngine engine) {
        engine.proposeMove(row, col);
    }
}

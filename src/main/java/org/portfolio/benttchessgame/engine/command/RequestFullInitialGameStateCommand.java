package org.portfolio.benttchessgame.engine.command;

import org.portfolio.benttchessgame.engine.ChessEngine;

public class RequestFullInitialGameStateCommand implements GameCommand {
    @Override
    public void execute(ChessEngine engine) {
        engine.fillTheStarterMoves();
    }
}

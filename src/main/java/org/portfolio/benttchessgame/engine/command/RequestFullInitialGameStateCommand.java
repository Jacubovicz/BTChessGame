package org.portfolio.benttchessgame.engine.command;

import org.portfolio.benttchessgame.engine.ChessEngine;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;

public class RequestFullInitialGameStateCommand implements GameCommand {
    @Override
    public void execute(ChessEngine engine) throws IllegalMoveException {
        engine.publishInitialState();
    }
}

package org.portfolio.benttchessgame.engine.command;

import org.portfolio.benttchessgame.engine.ChessEngine;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;

public interface GameCommand {
    void execute(ChessEngine engine) throws IllegalMoveException;
}



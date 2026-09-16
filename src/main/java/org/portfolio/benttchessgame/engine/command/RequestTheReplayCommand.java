package org.portfolio.benttchessgame.engine.command;

import org.portfolio.benttchessgame.engine.ChessEngine;

public class RequestTheReplayCommand implements GameCommand{

    public RequestTheReplayCommand(){ //we will see how it works, at worst case will do list parameter
    }

    @Override
    public void execute(ChessEngine engine) {
        engine.requestReplay();
    }
}

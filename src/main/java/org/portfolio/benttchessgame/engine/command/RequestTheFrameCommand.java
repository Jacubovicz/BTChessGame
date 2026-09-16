package org.portfolio.benttchessgame.engine.command;

import org.portfolio.benttchessgame.engine.ChessEngine;

public class RequestTheFrameCommand implements GameCommand{

    private final int frameNumber;

    public RequestTheFrameCommand(int frameNumber){
        this.frameNumber = frameNumber;
    }

    @Override
    public void execute(ChessEngine engine) {
        engine.requestFrame(frameNumber);
    }
}

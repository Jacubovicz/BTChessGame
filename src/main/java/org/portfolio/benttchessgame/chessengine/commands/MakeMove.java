package org.portfolio.benttchessgame.chessengine.commands;

import org.portfolio.benttchessgame.chessengine.MoveOperation;

public class MakeMove implements EngineCommand{

    MoveOperation operation;

    public MakeMove(MoveOperation moveOperation){
        this.operation = moveOperation;
    }

    public MoveOperation getMoveOperation()
    {
        return operation;
    }
}

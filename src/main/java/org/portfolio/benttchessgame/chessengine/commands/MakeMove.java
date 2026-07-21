package org.portfolio.benttchessgame.chessengine.commands;

import org.portfolio.benttchessgame.chessengine.Cell;

public class MakeMove implements EngineCommand{

    Cell destCell;

    public MakeMove(Cell destCell){
        this.destCell = destCell;
    }

    public Cell getMoveOperation()
    {
        return destCell;
    }
}

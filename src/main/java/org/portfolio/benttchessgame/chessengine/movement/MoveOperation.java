package org.portfolio.benttchessgame.chessengine.movement;

import org.portfolio.benttchessgame.chessengine.Cell;

public class MoveOperation {

    Cell initCell, destCell;

    private boolean owner;

    public MoveOperation() {}

    public MoveOperation(Cell initCell, Cell destCell) {
        this.initCell = initCell;
        this.destCell = destCell;
    }

    public void clear()
    {
        this.initCell = null;
        this.destCell = null;
    }

    public boolean getOwner()
    {
        return owner;
    }

    public void setOwner(boolean owner)
    {
        this.owner = owner;
    }

    public Cell getInitCell()
    {
        return initCell;
    }

    public Cell getDestCell()
    {
        return destCell;
    }

    public void setInitCell(Cell initCell)
    {
        this.initCell = initCell;
    }

    public void setDestCell(Cell destCell)
    {
        this.destCell = destCell;
    }
}

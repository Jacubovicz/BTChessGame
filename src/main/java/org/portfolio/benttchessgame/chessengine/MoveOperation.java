package org.portfolio.benttchessgame.chessengine;

public class MoveOperation {

    Cell initCell, destCell;

    public MoveOperation(Cell initCell, Cell destCell)
    {
        this.initCell = initCell;
        this.destCell = destCell;
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

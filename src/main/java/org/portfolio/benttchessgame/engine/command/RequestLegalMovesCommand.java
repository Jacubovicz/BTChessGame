package org.portfolio.benttchessgame.engine.command;


public class RequestLegalMovesCommand implements GameCommand {

    int row, col;

    public RequestLegalMovesCommand(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    public int getRow()
    {
        return row;
    }

    public int getCol()
    {
        return col;
    }
}

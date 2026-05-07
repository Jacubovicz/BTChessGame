package org.portfolio.benttchessgame.chessengine.commands;


public class ProposeMove implements EngineCommand{

    int row, col;

    public ProposeMove(int row, int col)
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

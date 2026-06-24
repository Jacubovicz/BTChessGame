package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.figures.Figure;

public interface GameBridge{

    public void initialize(Session session);

    public void proposeMove(int row, int col);

    public void moveTheFigure(MoveOperation operation);

    public void resetTheGame();

    public Figure[][] getSnapshot();

    public void fillTheStarterMoves();
}

package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.exceptions.IllegalMoveException;
import org.portfolio.benttchessgame.chessengine.movement.MoveOperation;
import org.portfolio.benttchessgame.figures.Figure;

public interface GameBridge{

    public void initialize(Session session);

    public void proposeMove(int row, int col);

    public void moveTheFigure(Cell destCell) throws IllegalMoveException;

    public void resetTheGame();

    public Figure[][] getSnapshot();

    public void fillTheStarterMoves();
}

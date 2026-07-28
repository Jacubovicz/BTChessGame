package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.piece.Piece;

public interface GameBackend {

    public void initialize(GameSession gameSession, boolean isOfflineGame);

    public void proposeMove(int row, int col);

    public void moveTheFigure(BoardSquare destBoardSquare) throws IllegalMoveException;

    public void resetTheGame();

    public Piece[][] getSnapshot();

    public void fillTheStarterMoves();
}

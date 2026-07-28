package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.piece.Piece;

public class LocalGameBackend implements GameBackend {

    private ChessGame localChessGame;

    @Override
    public void initialize(GameSession gameSession, boolean isOfflineGame) {
        this.localChessGame = new ChessGame(gameSession);
    }

    @Override
    public void proposeMove(int row, int col) {

        if(localChessGame != null)
        {
            localChessGame.proposeMove(row, col);
        }
    }

    @Override
    public void moveTheFigure(BoardSquare destBoardSquare) throws IllegalMoveException {
        if(localChessGame != null)
        {
            localChessGame.move(destBoardSquare);
        }
    }

    @Override
    public void resetTheGame() {
        if(localChessGame != null)
        {
            localChessGame.resetTheGame();
        }
    }

    @Override
    public Piece[][] getSnapshot()
    {
        if(localChessGame != null)
        {
            return localChessGame.getSnapshot();
        }

        return null;
    }

    @Override
    public void fillTheStarterMoves()
    {
        localChessGame.fillTheStarterMoves();
    }
}

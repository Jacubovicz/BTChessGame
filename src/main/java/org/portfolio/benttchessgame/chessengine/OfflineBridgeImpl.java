package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.players.Player;
import org.portfolio.benttchessgame.figures.Figure;

public class OfflineBridgeImpl implements GameBridge{

    private Game localGame;

    @Override
    public void initialize(Session session) {
        this.localGame = new Game(session);
    }

    @Override
    public void proposeMove(int row, int col) {

        if(localGame != null)
        {
            localGame.proposeMove(row, col);
        }
    }

    @Override
    public void moveTheFigure(MoveOperation operation) {
        if(localGame != null)
        {
            localGame.move(operation);
        }
    }

    @Override
    public void resetTheGame() {
        if(localGame != null)
        {
            localGame.resetTheGame();
        }
    }

    @Override
    public Figure[][] getSnapshot()
    {
        if(localGame != null)
        {
            return localGame.getSnapshot();
        }

        return null;
    }

    @Override
    public void fillTheStarterMoves()
    {
        localGame.fillTheStarterMoves();
    }
}

package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.players.BasePlayer;
import org.portfolio.benttchessgame.figures.Figure;

public class ChessEngine{

    private Session gameSession;
    private GameBridge bridge;

    public ChessEngine(GameBridge bridge)
    {
        this.gameSession = new Session(new BasePlayer("Kuba"), new BasePlayer("Kinga"));
        this.bridge = bridge;

        bridge.initialize(gameSession);
    }

    public void setTheNewGame()
    {
        gameSession = new Session(new BasePlayer("Kuba"), new BasePlayer("Kinga"));

        bridge.initialize(gameSession);
    }

    public void proposeMove(int row, int col)
    {
        if(bridge != null)
        {
            bridge.proposeMove(row, col);
        }
    }

    public void moveTheFigure(MoveOperation operation)
    {
        if(bridge != null)
        {
            bridge.moveTheFigure(operation);
        }
    }

    public Figure[][] getSnapshot()
    {
        if(bridge != null) return bridge.getSnapshot();

        return null;
    }

    public void fillTheStarterMoves()
    {
        if(bridge != null)
        {
            bridge.fillTheStarterMoves();
        }
    }
}

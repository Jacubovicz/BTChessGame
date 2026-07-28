package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.engine.player.Player;
import org.portfolio.benttchessgame.piece.Piece;

public class ChessEngine{

    private GameSession gameSession;
    private GameBackend bridge;

    public ChessEngine()
    {

    }

    public ChessEngine(GameBackend bridge)
    {
       // this.gameSession = new Session(new BasePlayer("Kuba"), new BasePlayer("Kinga"));
     //   this.bridge = bridge;

    //    bridge.initialize(gameSession);
    }

    public void setSession(GameSession gameSession)
    {
        this.gameSession = gameSession;
    }

    public void setTheNewGame(Player p1, Player p2, boolean isOfflineGame)
    {
        gameSession = new GameSession(p1, p2);

        if(isOfflineGame)
        {
            bridge = new LocalGameBackend();
        }
        else
            bridge = new OnlineGameBackend();

        bridge.initialize(gameSession, isOfflineGame);
    }

    public void proposeMove(int row, int col)
    {
        if(bridge != null)
        {
            bridge.proposeMove(row, col);
        }
    }

    public void moveTheFigure(BoardSquare destBoardSquare) throws IllegalMoveException {
        if(bridge != null)
        {
            bridge.moveTheFigure(destBoardSquare);
        }
    }

    public Piece[][] getSnapshot()
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

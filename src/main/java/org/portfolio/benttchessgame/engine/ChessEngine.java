package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.configuration.TimeSettings;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.engine.player.Player;
import org.portfolio.benttchessgame.piece.Piece;

public class ChessEngine{

    private final GameEventPublisher eventPublisher;
    private GameBackend bridge;

    public ChessEngine(GameEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public ChessEngine(GameBackend bridge, GameEventPublisher eventPublisher)
    {
        this.bridge = bridge;
        this.eventPublisher = eventPublisher;
    }


    public void setTheNewGame(GameConfiguration gameConfig)
    {
        switch(gameConfig.mode())
        {
            case GameMode.PvPOnline -> bridge = new OnlineGameBackend();
            default -> bridge = new LocalGameBackend();
        }

        bridge.initialize(gameConfig, eventPublisher);
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

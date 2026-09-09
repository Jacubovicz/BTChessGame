package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;

public class ChessEngine{

    private final GameEventPublisher eventPublisher;
    private GameBackend bridge;

    public ChessEngine(GameEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    public void setTheNewGame(GameConfiguration gameConfig) {
        switch(gameConfig.mode())
        {
            case GameMode.PvPOnline -> bridge = new OnlineGameBackend();
            case GameMode.PvAI -> bridge = new AIGameBackend();
            default -> bridge = new LocalGameBackend();
        }
        bridge.initialize(gameConfig, eventPublisher);
    }


    public void proposeMove(int row, int col) {
        if(bridge != null)
        {
            bridge.proposeMove(row, col);
        }
        else throw new IllegalStateException("Bridge is not initialized");
    }

    public void moveTheFigure(BoardSquare destBoardSquare) throws IllegalMoveException {
        moveTheFigure(destBoardSquare, null);
    }

    public void moveTheFigure(BoardSquare destBoardSquare, org.portfolio.benttchessgame.piece.PieceType promotionType) throws IllegalMoveException {
        if(bridge != null)
        {
            bridge.moveTheFigure(destBoardSquare, promotionType);
        }
        else throw new IllegalStateException("Bridge is not initialized");
    }


    public void publishInitialState() {
        if(bridge != null)
        {
            bridge.publishInitialState();
        }
        else throw new IllegalStateException("Bridge is not initialized");
    }

}

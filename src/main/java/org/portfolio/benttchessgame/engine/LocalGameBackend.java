package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.piece.PieceType;

public class LocalGameBackend implements GameBackend {

    private ChessGame localChessGame;

    @Override
    public void initialize(GameConfiguration gameConfig, GameEventPublisher eventPublisher) {
        this.localChessGame = new ChessGame(gameConfig, eventPublisher);
    }

    @Override
    public void proposeMove(int row, int col) {
        if(localChessGame != null)
        {
            localChessGame.proposeMove(row, col);
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }

    @Override
    public void moveTheFigure(BoardSquare destBoardSquare) throws IllegalMoveException {
        if(localChessGame != null)
        {
            localChessGame.move(destBoardSquare);
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }

    @Override
    public void moveTheFigure(BoardSquare destBoardSquare, PieceType promotionType) throws IllegalMoveException {
        if(localChessGame != null)
        {
            localChessGame.move(destBoardSquare, promotionType);
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }

    @Override
    public void resetTheGame() {
        if(localChessGame != null)
        {
            localChessGame.resetTheGame();
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }


    @Override
    public void publishInitialState()
    {
        localChessGame.publishInitialState();
    }

    @Override
    public void requestFrame(int frameNumber) {
        if(localChessGame != null && localChessGame.gameEnded())
        {
            localChessGame.requestFrame(frameNumber);
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }

    @Override
    public void requestReplay(GameEventPublisher publisher){
        if(localChessGame != null)
        {
            localChessGame.requestReplay(publisher);
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }
}

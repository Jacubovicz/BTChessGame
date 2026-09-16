package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.piece.Piece;

public interface GameBackend {

    public void initialize(GameConfiguration gameConfig, GameEventPublisher eventPublisher);

    public void proposeMove(int row, int col);

    public void moveTheFigure(BoardSquare destBoardSquare) throws IllegalMoveException;

    default void moveTheFigure(BoardSquare destBoardSquare, org.portfolio.benttchessgame.piece.PieceType promotionType) throws IllegalMoveException {
        moveTheFigure(destBoardSquare);
    }

    public void resetTheGame();


    public void publishInitialState();

    public void requestFrame(int frameNumber);
    public void requestReplay(GameEventPublisher publisher);
}

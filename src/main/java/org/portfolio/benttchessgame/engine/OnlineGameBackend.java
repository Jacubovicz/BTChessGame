package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.piece.Piece;

public class OnlineGameBackend implements GameBackend {


    @Override
    public void initialize(GameConfiguration gameConfig, GameEventPublisher eventPublisher) {

    }

    @Override
    public void proposeMove(int row, int col) {

    }

    @Override
    public void moveTheFigure(BoardSquare boardSquare) {

    }

    @Override
    public void resetTheGame() {

    }

    @Override
    public Piece[][] getSnapshot() {
        return new Piece[0][];
    }

    @Override
    public void fillTheStarterMoves() {

    }
}

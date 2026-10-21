package org.portfolio.benttchessgame.engine.ai;

import org.portfolio.benttchessgame.piece.Pawn;
import org.portfolio.benttchessgame.piece.Piece;

public record StatePawnFirstMoveEvent(StateEventEnum stateEventEnum, Pawn pawn, boolean isPawnFirstMove, int turnNum) implements StateEvent {
    @Override
    public StateEventEnum getStateEventEnum(){
        return stateEventEnum;
    }

    @Override
    public int getTurnNum(){
        return turnNum;
    }
}

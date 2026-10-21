package org.portfolio.benttchessgame.engine.ai;

import org.portfolio.benttchessgame.piece.Rook;

public record StateRookFirstMoveEvent(StateEventEnum stateEventEnum, Rook rook, boolean isFirstMove, int turnNum) implements StateEvent{
    @Override
    public StateEventEnum getStateEventEnum(){
        return stateEventEnum;
    }

    @Override
    public int getTurnNum(){
        return turnNum;
    }
}

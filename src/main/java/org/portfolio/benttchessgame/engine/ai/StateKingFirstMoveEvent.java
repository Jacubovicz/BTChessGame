package org.portfolio.benttchessgame.engine.ai;

import org.portfolio.benttchessgame.piece.King;

public record StateKingFirstMoveEvent(StateEventEnum stateEventEnum, King king, boolean didMove, int turnNum) implements StateEvent {
    @Override
    public StateEventEnum getStateEventEnum(){
        return stateEventEnum;
    }

    @Override
    public int getTurnNum(){
        return turnNum;
    }
}

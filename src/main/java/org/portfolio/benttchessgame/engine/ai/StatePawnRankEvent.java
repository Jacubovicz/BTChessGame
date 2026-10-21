package org.portfolio.benttchessgame.engine.ai;

import org.portfolio.benttchessgame.piece.Pawn;

public record StatePawnRankEvent(StateEventEnum stateEventEnum, Pawn pawn, int rank, int turnNum) implements StateEvent {
    @Override
    public StateEventEnum getStateEventEnum(){
        return stateEventEnum;
    }

    @Override
    public int getTurnNum(){
        return turnNum;
    }
}

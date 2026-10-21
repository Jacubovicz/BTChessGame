package org.portfolio.benttchessgame.engine.ai;

import org.portfolio.benttchessgame.piece.Pawn;

public record StatePassantEvent(StateEventEnum stateEventEnum, Pawn pawn, boolean passantPossible, int turnNum) implements StateEvent {
    @Override
    public StateEventEnum getStateEventEnum(){
        return stateEventEnum;
    }

    @Override
    public int getTurnNum(){
        return turnNum;
    }
}

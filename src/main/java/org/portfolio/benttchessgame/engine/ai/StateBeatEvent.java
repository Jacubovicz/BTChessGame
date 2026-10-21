package org.portfolio.benttchessgame.engine.ai;

import org.portfolio.benttchessgame.piece.Piece;

public record StateBeatEvent(StateEventEnum stateEventEnum, int toRow, int toColumn, Piece piece, int turnNum) implements StateEvent {

    @Override
    public StateEventEnum getStateEventEnum(){
        return stateEventEnum;
    }

    @Override
    public int getTurnNum(){
        return turnNum;
    }
}

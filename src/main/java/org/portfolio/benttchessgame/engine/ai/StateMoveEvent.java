package org.portfolio.benttchessgame.engine.ai;

public record StateMoveEvent(StateEventEnum stateEventEnum, int fromRow, int fromColumn,int toRow, int toColumn, int turnNum) implements StateEvent {
    @Override
    public StateEventEnum getStateEventEnum(){
        return stateEventEnum;
    }

    @Override
    public int getTurnNum(){
        return turnNum;
    }
}

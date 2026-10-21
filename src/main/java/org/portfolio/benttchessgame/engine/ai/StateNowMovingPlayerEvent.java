package org.portfolio.benttchessgame.engine.ai;

import org.portfolio.benttchessgame.engine.player.PlayerColor;

public record StateNowMovingPlayerEvent(StateEventEnum stateEventEnum, PlayerColor actualMoving, int turnNum) implements StateEvent {
    @Override
    public StateEventEnum getStateEventEnum(){
        return stateEventEnum;
    }

    @Override
    public int getTurnNum(){
        return turnNum;
    }
}

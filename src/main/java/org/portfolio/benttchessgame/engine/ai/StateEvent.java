package org.portfolio.benttchessgame.engine.ai;

public sealed interface StateEvent permits StateBeatEvent, StateKingFirstMoveEvent, StateMoveEvent,
        StateNowMovingPlayerEvent, StatePassantEvent, StatePawnFirstMoveEvent, StatePawnRankEvent,
        StateRookFirstMoveEvent, StatePromotionReplacementEvent{

    StateEventEnum getStateEventEnum();

    int getTurnNum();
}



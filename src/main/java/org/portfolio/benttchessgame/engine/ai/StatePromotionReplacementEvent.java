package org.portfolio.benttchessgame.engine.ai;

public record StatePromotionReplacementEvent() implements StateEvent{
    @Override
    public StateEventEnum getStateEventEnum(){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getTurnNum() {
        return 0;
    }
}

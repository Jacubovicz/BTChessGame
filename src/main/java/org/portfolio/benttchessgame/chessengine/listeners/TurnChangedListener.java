package org.portfolio.benttchessgame.chessengine.listeners;

@FunctionalInterface
public interface TurnChangedListener {

    public void onTurnChanged(boolean turn);
}

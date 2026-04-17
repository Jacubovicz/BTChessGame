package org.portfolio.benttchessgame.chessengine.listeners;

@FunctionalInterface
public interface StarterMovesAvailableListener {

    public void onStarterMovesAvailableChange(boolean[][] starterMovesAvailable);
}

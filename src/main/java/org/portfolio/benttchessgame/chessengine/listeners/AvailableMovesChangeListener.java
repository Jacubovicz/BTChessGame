package org.portfolio.benttchessgame.chessengine.listeners;

import org.portfolio.benttchessgame.chessengine.Cell;

import java.util.ArrayList;

@FunctionalInterface
public interface AvailableMovesChangeListener {

    public void onMovesChangeEvent(ArrayList<Cell> moves);
}

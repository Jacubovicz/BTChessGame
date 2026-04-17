package org.portfolio.benttchessgame.chessengine.listeners;

import org.portfolio.benttchessgame.figures.Figure;

@FunctionalInterface
public interface BoardChangedListener {

    public void onBoardChanged(Figure[][] newBoard);
}

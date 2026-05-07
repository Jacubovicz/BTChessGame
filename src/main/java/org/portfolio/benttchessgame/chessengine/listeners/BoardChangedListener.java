package org.portfolio.benttchessgame.chessengine.listeners;

import org.portfolio.benttchessgame.figures.Figure;

@FunctionalInterface
public interface BoardChangedListener {

    void onBoardChanged(Figure[][] newBoard);
}

package org.portfolio.benttchessgame.chessengine.events;

import org.portfolio.benttchessgame.figures.Figure;

public record BoardChangedEvent(Figure[][] board) { }

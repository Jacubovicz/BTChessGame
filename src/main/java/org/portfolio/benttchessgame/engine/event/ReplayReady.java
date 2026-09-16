package org.portfolio.benttchessgame.engine.event;

import org.portfolio.benttchessgame.engine.board.ChessBoard;

public record ReplayReady(ChessBoard initialBoard, int amountOfFrames) {
}

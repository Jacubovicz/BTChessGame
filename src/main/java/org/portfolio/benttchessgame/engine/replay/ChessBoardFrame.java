package org.portfolio.benttchessgame.engine.replay;

import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.Move;

public record ChessBoardFrame(String[][] boardRepresentation, Move move, int frameNumber){}

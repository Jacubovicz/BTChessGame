package org.portfolio.benttchessgame.engine.replay;

import org.portfolio.benttchessgame.engine.board.ChessBoard;

public interface ReplayService {

    void sendFrameToUI(ChessBoardFrame frame);

    void notifyReplayReady(ChessBoard board, int numberOfFrames);
}

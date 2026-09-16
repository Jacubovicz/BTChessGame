package org.portfolio.benttchessgame.engine.replay;

import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.event.ReplayFrameData;
import org.portfolio.benttchessgame.engine.event.ReplayReady;

public class ReplayFrameSender {

    private final GameEventPublisher publisher;

    public ReplayFrameSender(GameEventPublisher publisher){
        this.publisher = publisher;
    }

    public void sendFrameToUI(ChessBoardFrame frameData){
       publisher.publishEvent(new ReplayFrameData(frameData));
    }

    public void notifyReplayReady(ChessBoard board, int amountOfFrames){
        publisher.publishEvent(new ReplayReady(board, amountOfFrames));
    }
}

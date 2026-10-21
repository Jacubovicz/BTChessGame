package org.portfolio.benttchessgame.engine.replay;

import org.portfolio.benttchessgame.engine.ChessGameListener;
import org.portfolio.benttchessgame.engine.board.BoardManager;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.tools.UtilityMethods;

import java.util.ArrayList;
import java.util.List;

public class ReplayCreator {

    ArrayList<ChessBoardFrame> frames;
    BoardManager boardManager;
    ReplayService replayService;

    public ReplayCreator(List<Move> moves, ReplayService replayService) {
        frames = new ArrayList<>();
        this.boardManager = new BoardManager();
        this.replayService = replayService;
        createFrameList(moves);
    }

    private void createFrameList(List<Move> moves){
        ChessBoard board = new ChessBoard();
        board.setupTheBoard();
        ChessBoard firstSetup = board.getSnapshot();
        int frameNumber = 0;
        for(Move move : moves){
            ChessBoard frameBoard = board.getSnapshot();
            frameBoard = boardManager.rawMove(move, frameBoard);
            frames.add(new ChessBoardFrame(UtilityMethods.chessBoardToStringArray(frameBoard), move, frameNumber));
            board = frameBoard;
            frameNumber++;
        }
        notifyReplayReady(firstSetup,frames.size());
    }

    public void requestFrame(int frameNumber){
        sendFrameToUI(frameNumber);
    }

    private void sendFrameToUI(int frameNumber){
        replayService.sendFrameToUI(frames.get(frameNumber));
    }

    private void notifyReplayReady(ChessBoard board, int numberOfFrames){
        replayService.notifyReplayReady(board, numberOfFrames);
    }
}

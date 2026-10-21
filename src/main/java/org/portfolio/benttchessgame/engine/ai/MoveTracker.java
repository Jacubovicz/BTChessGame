package org.portfolio.benttchessgame.engine.ai;

import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.engine.player.AIPlayer;
import org.portfolio.benttchessgame.engine.player.BasePlayer;
import org.portfolio.benttchessgame.engine.player.Player;
import org.portfolio.benttchessgame.engine.player.PlayerColor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MoveTracker {

    Player whitePlayer, blackPlayer, currentPlayer;
    private final AIEntity aiEntity;
    private int activeTurn;

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledTask;

    public MoveTracker(Player whitePlayer, Player blackPlayer, AIEntity aiEntity){
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.aiEntity = aiEntity;

        this.activeTurn = 0;
        this.currentPlayer = null; //at this moment the currentPlayer is unknown
    }

    public void flushActiveSquares(Runnable callback){
        if(currentPlayer != null && currentPlayer instanceof BasePlayer){
            callback.run();
        }
        if(currentPlayer != null && currentPlayer instanceof AIPlayer){
            if(scheduledTask != null && !scheduledTask.isDone()){
                scheduledTask.cancel(false);
            }
            scheduledTask = executorService.schedule(() -> {
                try {
                    aiEntity.makeMove(currentPlayer.getColor(), activeTurn);
                } catch (IllegalMoveException e) {
                    System.err.println("Illegal move detected: " + e.getMessage());
                }
            }, 1, TimeUnit.SECONDS);
        }
    }

    public void publishAvlMovesChanged(Runnable callback){
        if(currentPlayer != null && currentPlayer instanceof BasePlayer){
            callback.run();
        }
    }

    public void setCurrentPlayer(Player currentPlayer){
        this.currentPlayer = currentPlayer;
    }

    public void setCurrentPlayer(PlayerColor newPlayer){
        this.currentPlayer = newPlayer == PlayerColor.WHITE ? whitePlayer : blackPlayer;
    }

    public void notifyBoardChangedListener(ChessBoard chessBoard){
        if(currentPlayer instanceof AIPlayer){
            aiEntity.updateChessBoard(chessBoard);
            try{
                aiEntity.makeMove(currentPlayer.getColor(), activeTurn);
            }
            catch(IllegalMoveException e){
                System.err.println("Illegal move detected: " + e.getMessage());
            }

        }

    }

    public void setCurrentActiveTurn(int activeTurn){
        this.activeTurn = activeTurn;
    }

}

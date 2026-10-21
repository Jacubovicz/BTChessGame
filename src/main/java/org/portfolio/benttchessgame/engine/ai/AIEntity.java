package org.portfolio.benttchessgame.engine.ai;

import org.portfolio.benttchessgame.engine.AIGameBackend;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.rule.MoveEvaluator;

public class AIEntity {


    private final AIGameBackend bridge;
    private volatile ChessBoard currentChessBoard;

    private static int DEPTH = 6; //as a target, there will be depth 6, 3 for now is choice of performance

    public AIEntity(AIGameBackend bridge){
        this.bridge = bridge;
    }

    public void updateChessBoard(ChessBoard newChessBoard){
        this.currentChessBoard = newChessBoard;
    }

    public void makeMove(PlayerColor color, int activeTurn) throws IllegalMoveException {

       // ChessBoard board = currentGameState.board();

        boolean isMaximizing = color == PlayerColor.WHITE;
        Move bestMove = null;
        try{

            ScoreBoard scoreBoard = MoveEvaluator.getBestestMove(isMaximizing, currentChessBoard, DEPTH, null, activeTurn); //maybe it will be better to send gameState
            bestMove = scoreBoard.bestMove();
        }
        catch(RuntimeException re){
            re.printStackTrace();
        }

        // still need to move the figure
        if(bestMove != null){
            bridge.proposeMove(bestMove.initBoardSquare().getRow(), bestMove.initBoardSquare().getCol());
            bridge.moveTheFigure(bestMove.destBoardSquare());
        }

    }
}

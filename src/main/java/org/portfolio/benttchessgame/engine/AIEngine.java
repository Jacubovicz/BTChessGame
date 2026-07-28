package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.engine.board.BoardManager;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.rule.*;

import java.util.Objects;


public class AIEngine {

    private AIEngine() throws InstantiationException {
        throw new InstantiationException("This class is not meant to be initialized.");
    }

    public static int calculateBestMove(BoardManager board, GameSession currentGameSession)
    {

        int bestMovePossible = minimax(board, 4, -100, 100, currentGameSession.getTurn());

        return bestMovePossible;
    }

    private static int minimax(BoardManager board, int depth, int alpha, int beta, boolean isMaximizingPlayer)
    {
       if(depth == 0) return MoveEvaluator.eval(board.getSnapshot().getPiecesOnBoard());

        if(isMaximizingPlayer)
        {
            int maxEval = -1000;

            for(Move operation : Objects.requireNonNull(MoveValidator.getAllPossibleMoveOperations(board.getSnapshot(), isMaximizingPlayer)))
            {
                //NEEDS REFACTORING AGAIN - I need to get rid of tightly coupling.
                //make the PieceBoardManager manage every PieceBoard ever assigned.
                //if I assign table b, I want to see B only
                //if A, I want to see A only etc..
                int eval = minimax(board, depth -1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);

                if(beta <= alpha) break;

                return maxEval;
            }
        }
        else //is black player which tends to lower values
        {
            int minEval = 1000;

            for(Move operation : Objects.requireNonNull(MoveValidator.getAllPossibleMoveOperations(board.getSnapshot(), isMaximizingPlayer)))
            {

                int eval = minimax(board.rawMove(operation), depth-1, alpha,beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);

                if(beta <= alpha) break;

                return minEval;
            }
        }

        return 0;
    }
}

package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.movement.*;

import java.util.Objects;


public class AIEngine {

    private AIEngine() throws InstantiationException {
        throw new InstantiationException("This class is not meant to be initialized.");
    }

    public static int calculateBestMove(PieceBoardManager board, Session currentSession)
    {

        int bestMovePossible = minimax(board, 4, -100, 100, currentSession.getTurn());

        return bestMovePossible;
    }

    private static int minimax(PieceBoardManager board, int depth, int alpha, int beta, boolean isMaximizingPlayer)
    {
       if(depth == 0) return MoveEvaluator.eval(board.getSnapshot().getPiecesOnBoard());

        if(isMaximizingPlayer)
        {
            int maxEval = -1000;

            for(MoveOperation operation : Objects.requireNonNull(Validator.getAllPossibleMoveOperations(board.getSnapshot(), isMaximizingPlayer)))
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

            for(MoveOperation operation : Objects.requireNonNull(Validator.getAllPossibleMoveOperations(board.getSnapshot(), isMaximizingPlayer)))
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

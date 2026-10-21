package org.portfolio.benttchessgame.engine.rule;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.ai.BoardRepresentation;
import org.portfolio.benttchessgame.engine.ai.ScoreBoard;
import org.portfolio.benttchessgame.engine.ai.SearchBoard;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.piece.Piece;

import java.util.ArrayList;
import java.util.List;

public class MoveEvaluator {

    //iteration only for testing, needs to be deleted for deployed program.
    private static int iteration = 0;
    private MoveEvaluator() throws InstantiationException
    {
        throw new InstantiationException("The class is not meant to be constructed. ");
    }

    //ScoreBoard consists of an (int score) and (Move move)
    public static ScoreBoard getBestMove(boolean isMaximizing, BoardRepresentation searchBoard, int depth, Move currentMove, int activeTurn) {
System.out.println(iteration++);
        if (depth == 0) {// OR GAME OVER
            return new ScoreBoard(eval(searchBoard.getBoard()), currentMove);
        }
        PlayerColor c = isMaximizing ? PlayerColor.WHITE : PlayerColor.BLACK;
        List<BoardSquare> activeFields = MoveValidator.getActiveSquares(searchBoard.getBoard(), c);
            // Each level records the move IT made; the child's move is ignored, so the root ends up with its own best move.
            if (isMaximizing) {
                ScoreBoard maxEval = new ScoreBoard(-1000000, null);
                for(BoardSquare af : activeFields){
                    List<BoardSquare> avlMoves = MoveValidator.getLegalMovesOf(af.getRow(), af.getCol(), searchBoard.getBoard(), activeTurn);
                    for(BoardSquare avlMove : avlMoves){
                        Move move = new Move(af, avlMove);
                        searchBoard.move(af.getRow(), af.getCol(), avlMove.getRow(), avlMove.getCol(), activeTurn, c);

                        ScoreBoard eval = getBestMove(false, searchBoard, depth - 1, move, activeTurn + 1);

                        if(maxEval.bestMove() == null || eval.points() > maxEval.points()){
                            maxEval = new ScoreBoard(eval.points(), move);
                        }
                        searchBoard.reverseMove();
                    }
                }
                return maxEval;
            } else {
                ScoreBoard minEval = new ScoreBoard(1000000, null);
                for(BoardSquare af : activeFields){
                    List<BoardSquare> avlMoves = MoveValidator.getLegalMovesOf(af.getRow(), af.getCol(), searchBoard.getBoard(), activeTurn);
                    for(BoardSquare avlMove : avlMoves){
                        Move move = new Move(af, avlMove);
                        searchBoard.move(af.getRow(), af.getCol(), avlMove.getRow(), avlMove.getCol(), activeTurn, c);

                        ScoreBoard eval = getBestMove(true, searchBoard, depth - 1, move, activeTurn + 1);

                        if(minEval.bestMove() == null || eval.points() < minEval.points()){
                            minEval = new ScoreBoard(eval.points(), move);
                        }
                        searchBoard.reverseMove();
                    }
                }
                return minEval;
            }
    }

    public static ScoreBoard getBestestMove(boolean isMaximizing, ChessBoard board, int depth, Move currentMove, int activeTurn){
     //   SearchBoard searchBoard = new SearchBoard(board);
        BoardRepresentation searchBoard = new BoardRepresentation(board);
        ScoreBoard bestMove = getBestMove(isMaximizing, searchBoard, depth, currentMove, activeTurn);

        return bestMove;
    }

    public static int eval(Piece[][] pieceBoard)
    {
        int score = 0;
        for (int r = 0; r < pieceBoard.length; r++) {
            for (int c = 0; c < pieceBoard[r].length; c++) {
                Piece p = pieceBoard[r][c];
                if (p != null) {
                    if (p.getColor() == PlayerColor.WHITE) {
                        score += p.getBaseScore();
                    } else {
                        score -= p.getBaseScore();
                    }
                }
            }
        }
        return score;
    }

    public static int eval(ChessBoard chessBoard)
    {
        int score = 0;
        for (int r = 0; r < ChessBoard.GRID_SIZE; r++) {
            for (int c = 0; c < ChessBoard.GRID_SIZE; c++) {
                Piece p = chessBoard.getPieceAt(r, c);
                if (p != null) {
                    if (p.getColor() == PlayerColor.WHITE) {
                        score += p.getBaseScore();
                    } else {
                        score -= p.getBaseScore();
                    }
                }
            }
        }
        return score;
    }

}

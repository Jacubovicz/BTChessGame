package org.portfolio.benttchessgame.engine.rule;

import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.piece.Piece;

public class MoveEvaluator {

    private MoveEvaluator() throws InstantiationException
    {
        throw new InstantiationException("The class is not meant to be constructed. ");
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

package org.portfolio.benttchessgame.engine;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.exception.IllegalMoveException;
import org.portfolio.benttchessgame.engine.player.AIPlayer;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.rule.MoveEvaluator;
import org.portfolio.benttchessgame.engine.rule.MoveValidator;
import org.portfolio.benttchessgame.piece.Piece;
import org.portfolio.benttchessgame.piece.PieceType;

import java.util.List;

public class AIGameBackend implements GameBackend {

    private ChessGame chessGame;
    private PlayerColor aiColor;

    @Override
    public void initialize(GameConfiguration gameConfig, GameEventPublisher eventPublisher) {
        this.chessGame = new ChessGame(gameConfig, eventPublisher);
        if (gameConfig.whitePlayer() instanceof AIPlayer) {
            aiColor = PlayerColor.WHITE;
        } else {
            aiColor = PlayerColor.BLACK;
        }
    }

    @Override
    public void proposeMove(int row, int col) {
        if (chessGame != null) {
            chessGame.proposeMove(row, col);
        }
    }

    @Override
    public void moveTheFigure(BoardSquare destBoardSquare) throws IllegalMoveException {
        moveTheFigure(destBoardSquare, null);
    }

    @Override
    public void moveTheFigure(BoardSquare destBoardSquare, PieceType promotionType) throws IllegalMoveException {
        if (chessGame == null) {
            throw new IllegalStateException("ChessGame is not initialized");
        }
        chessGame.move(destBoardSquare, promotionType);

        if (chessGame.getNowMovingPlayer() == aiColor) {
            makeAIMove();
        }
    }

    private void makeAIMove() {
        BoardSquare bestFrom = null;
        BoardSquare bestTo = null;
        int bestScore = aiColor == PlayerColor.WHITE ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        ChessBoard board = chessGame.getGameState().board();
        int turn = chessGame.getMoveCounter();

        for (int r = 0; r < ChessBoard.GRID_SIZE; r++) {
            for (int c = 0; c < ChessBoard.GRID_SIZE; c++) {
                Piece p = board.getPieceAt(r, c);
                if (p != null && p.getColor() == aiColor) {
                    List<BoardSquare> legalMoves = MoveValidator.getLegalMovesOf(r, c, board, turn);
                    for (BoardSquare dest : legalMoves) {
                        ChessBoard testBoard = board.getSnapshot();
                        Piece movingPiece = testBoard.getPieceAt(r, c);
                        testBoard.setPieceAt(r, c, null);
                        testBoard.setPieceAt(dest.getRow(), dest.getCol(), movingPiece);

                        int score = MoveEvaluator.eval(testBoard);
                        if (aiColor == PlayerColor.WHITE) {
                            if (score > bestScore || bestFrom == null) {
                                bestScore = score;
                                bestFrom = new BoardSquare(r, c);
                                bestTo = dest;
                            }
                        } else {
                            if (score < bestScore || bestFrom == null) {
                                bestScore = score;
                                bestFrom = new BoardSquare(r, c);
                                bestTo = dest;
                            }
                        }
                    }
                }
            }
        }

        if (bestFrom != null && bestTo != null) {
            try {
                chessGame.proposeMove(bestFrom.getRow(), bestFrom.getCol());
                chessGame.move(bestTo, PieceType.QUEEN);
            } catch (IllegalMoveException e) {
                System.err.println("AI failed to make move: " + e.getMessage());
            }
        }
    }

    @Override
    public void resetTheGame() {
        if (chessGame != null) {
            chessGame.resetTheGame();
        }
    }

    @Override
    public void publishInitialState() {
        if (chessGame != null) {
            chessGame.publishInitialState();
            if (chessGame.getNowMovingPlayer() == aiColor) {
                makeAIMove();
            }
        }
    }

    @Override
    public void requestFrame(int frameNumber) {

        if(chessGame != null && chessGame.gameEnded())
        {
            chessGame.requestFrame(frameNumber);
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }

    @Override
    public void requestReplay(GameEventPublisher publisher) {
        if(chessGame != null)
        {
            chessGame.requestReplay(publisher);
        }
        else throw new IllegalStateException("LocalChessGame is not initialized");
    }
}

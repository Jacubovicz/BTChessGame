package org.portfolio.benttchessgame.engine.board;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.GameState;
import org.portfolio.benttchessgame.engine.event.*;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.rule.ChessRules;
import org.portfolio.benttchessgame.engine.rule.MoveValidator;
import org.portfolio.benttchessgame.piece.*;

import java.util.ArrayList;
import java.util.List;

public class BoardManager {

    private ChessBoard chessBoard;
    private final int GRID_SIZE;
    private final GameEventPublisher eventPublisher;
    private  GameState gameState;

    public BoardManager(ChessBoard chessBoard, GameEventPublisher eventPublisher) {
        this.chessBoard = chessBoard;
        this.GRID_SIZE = chessBoard.GRID_SIZE;
        this.eventPublisher = eventPublisher;
    }

    public void setState(GameState gameState)
    {
        this.gameState = gameState;
    }

    public BoardManager rawMove(Move operation) {

        BoardSquare from = operation.getInitCell();
        BoardSquare to = operation.getDestCell();

        Piece f = chessBoard.getPieceAt(from.getRow(), from.getCol());
        chessBoard.setPieceAt(from.getRow(), from.getCol(), null);
        chessBoard.setPieceAt(to.getRow(), to.getCol(), f);

        return null;
    }

    public ChessBoard getSnapshot() {
        return chessBoard.getSnapshot();
    }


    public List<BoardSquare> getPositionSquaresOfThePlayer(PlayerColor playerColor)
    {
        List<BoardSquare> positionBoardSquares = new ArrayList<>();

        for(int row = 0; row < GRID_SIZE; row++)
        {
            for(int col = 0; col < GRID_SIZE; col++)
            {
                if(chessBoard.getPiecesOnBoard()[row][col] != null && chessBoard.getPiecesOnBoard()[row][col].getColor() == playerColor)
                {
                    positionBoardSquares.add(new BoardSquare(row,col));
                }
            }
        }
        return positionBoardSquares;
    }


    //eliminates the figure on said position
    public void kill(BoardSquare boardSquare) {
        chessBoard.setPieceAt(boardSquare.getRow(), boardSquare.getCol(), null);
        notifyBoardChangedListeners();
    }


    // returns legal moves of said figure
    public List<BoardSquare> proposeMoves(BoardSquare boardSquare, GameState gameState) {

        Piece piece = chessBoard.getPieceAt(boardSquare.getRow(), boardSquare.getCol());

        if (piece == null) {
            return new ArrayList<>();
        }
        return MoveValidator.getLegalMovesOf(boardSquare.getRow(), boardSquare.getCol(), gameState, this.getSnapshot());
    }


    public void move(Move operation, GameState state)
    {
        BoardSquare from = operation.getInitCell();
        BoardSquare to = operation.getDestCell();

        if(chessBoard.getPieceAt(from.getRow(), from.getCol()) != null)
        {
            Piece f = chessBoard.getPieceAt(from.getRow(), from.getCol());
            chessBoard.setPieceAt(from.getRow(), from.getCol(), null);
            chessBoard.setPieceAt(to.getRow(), to.getCol(), f);

            switch(f)
            {
                case Pawn p -> handleThePawn(from, to, p);
                case Rook r -> r.setDidMove();
                case King k -> handleTheKing(k, operation);
                default -> {}
            }

            //checkmate straight away after movement, before switching the player colors. Looks only for complete lack of movements
            //no possible escape left:
            var opponentOfNowMovingPlayer = state.nowMovingPlayer() == PlayerColor.WHITE ? PlayerColor.DARK : PlayerColor.WHITE;
            boolean checkmate = ChessRules.isCheckmate(getSnapshot(), gameState, opponentOfNowMovingPlayer);
            if(checkmate)
            {
                //if checkmate(no possible moves for the next player) then the gameOverEvent is being sent.
                notifyBoardChangedListeners();
                notifyGameOverListener(state.nowMovingPlayer());
                System.err.print("CHECKMATE");
                return;
            }
        }

        //notify the Board listeners that it has changed
        notifyBoardChangedListeners();

    }

    private void handleTheKing(King k, Move kingOperation)
    {
        int row = kingOperation.getInitCell().getRow();
        int fromCol = kingOperation.getInitCell().getCol();
        int toCol = kingOperation.getDestCell().getCol();

        if (Math.abs(fromCol - toCol) == 2) {
            // Castling detected
            int rookInitCol = toCol > fromCol ? 7 : 0;
            int rookDestCol = toCol > fromCol ? fromCol + 1 : fromCol - 1;
            rawMove(new Move(new BoardSquare(row, rookInitCol), new BoardSquare(row, rookDestCol)));
        }
        k.setDidMove();
    }

    private boolean movesSideways(Move operation)
    {
        return operation.getInitCell().getRow() == operation.getDestCell().getRow();
    }

    private void handleThePawn(BoardSquare from, BoardSquare to, Pawn p)
    {
        int diff = from.getCol() > to.getCol() ? -1 : 1;
        Piece enpassant = null;
        if((from.getCol() + diff) < GRID_SIZE && from.getCol() + diff >= 0)
        {
            enpassant = chessBoard.getPieceAt(from.getRow(), from.getCol() + diff);
        }
        if(enpassant instanceof Pawn capturedPawn && capturedPawn.getColor() != p.getColor())
        {
            BoardSquare boardSquare = new BoardSquare(from.getRow(), from.getCol() + diff);
            if(capturedPawn.isEnpassantPossible(gameState)) this.kill(boardSquare);
        }

        p.incTheRank();
        if(Math.abs(from.getRow() - to.getRow()) == 2)
        {
            p.incTheRank();
            p.setEnpassantPossible(gameState);
        }
        p.disableFirstMove(); //This is special case for the Pawn.
        if(p.getRank() == 7)
        {
            promotePawn(new Queen(p.getColor()), to.getRow(), to.getCol());
        }
    }

    private void promotePawn(Piece fig, int row, int col)
    {
        this.chessBoard.setPieceAt(row, col, fig);
    }

    public void notifyBoardChangedListeners()
    {
        String[][] representation = new String[GRID_SIZE][GRID_SIZE];
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                Piece p = chessBoard.getPieceAt(r, c);
                representation[r][c] = (p != null) ? p.toString() : null;
            }
        }
        eventPublisher.publishEvent(new BoardChangedEvent(representation));
    }

    public void notifyGameOverListener(PlayerColor winningPlayer)
    {
        eventPublisher.publishEvent(new GameEndedEvent(winningPlayer));
    }


}

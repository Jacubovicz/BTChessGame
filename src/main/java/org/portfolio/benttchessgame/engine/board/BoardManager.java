package org.portfolio.benttchessgame.engine.board;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.GameState;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.rule.ChessRules;
import org.portfolio.benttchessgame.engine.rule.MoveValidator;
import org.portfolio.benttchessgame.piece.Piece;
import org.portfolio.benttchessgame.piece.Pawn;
import org.portfolio.benttchessgame.piece.King;
import org.portfolio.benttchessgame.piece.Rook;
import org.portfolio.benttchessgame.piece.Queen;
import org.portfolio.benttchessgame.piece.Bishop;
import org.portfolio.benttchessgame.piece.Knight;
import org.portfolio.benttchessgame.piece.PieceType;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BoardManager {

    private GameState state;
    private GameEventPublisher publisher;

    public BoardManager() {}

    public BoardManager(GameState gameState, GameEventPublisher publisher) {
        this.state = gameState;
        this.publisher = publisher;
    }

    public void setState(GameState gameState) {
        this.state = gameState;
    }

    public List<BoardSquare> getValidMoves(BoardSquare boardSquare) {
        return getValidMoves(boardSquare, this.state);
    }

    public MoveResult move(Move operation) {
        MoveResult result = move(operation, this.state);
        this.state = result.newState();
        return result;
    }

    public List<BoardSquare> getValidMoves(BoardSquare boardSquare, GameState gameState) {
        int activeTurn = gameState.moveCounter();
        ChessBoard snapshotBoard = gameState.board().getSnapshot();
        Piece piece = snapshotBoard.getPieceAt(boardSquare.getRow(), boardSquare.getCol());

        if (piece == null) {
            return new ArrayList<>();
        }

       return MoveValidator.getLegalMovesOf(boardSquare.getRow(), boardSquare.getCol(), snapshotBoard, activeTurn);
    }

    public MoveResult move(Move operation, GameState gameState) {

        ChessBoard snapshotBoard = gameState.board().getSnapshot();

        boolean inCheck = false;

        BoardSquare initialSquare = operation.getInitCell();
        BoardSquare targetSquare = operation.getDestCell();

        List<BoardSquare> legalMoves = this.getValidMoves(initialSquare, gameState);
        if(legalMoves.contains(targetSquare)) {
            if(snapshotBoard.getPieceAt(initialSquare.getRow(), initialSquare.getCol()) == null) {
                throw new IllegalArgumentException("No piece at the source square");
            }

            Piece originalPiece = snapshotBoard.getPieceAt(initialSquare.getRow(), initialSquare.getCol());
            Piece targetPiece = snapshotBoard.getPieceAt(targetSquare.getRow(), targetSquare.getCol());

            snapshotBoard.setPieceAt(initialSquare.getRow(), initialSquare.getCol(), null);
            snapshotBoard.setPieceAt(targetSquare.getRow(), targetSquare.getCol(), originalPiece);

            switch(originalPiece) {
                case Pawn pawn -> handleThePawnMove(initialSquare, targetSquare, pawn, targetPiece, operation.getPromotionType(), gameState, snapshotBoard);
                case Rook rook -> rook.setDidMove();
                case King king -> handleTheKingMove(king, operation, snapshotBoard);
                default -> {}
            }
        }
        else {
            throw new IllegalArgumentException("Invalid move");
        }

        GameState newState = gameState.next(snapshotBoard, operation);
        PlayerColor opponent = gameState.nowMovingPlayer().opposite();
        boolean checkmate = ChessRules.isCheckmate(newState, opponent);
        Optional<BoardSquare> optionalKingInCheck = MoveValidator.getIfInCheck(snapshotBoard, opponent);
        if(optionalKingInCheck.isPresent()) {
            inCheck = true;
        }

        return new MoveResult(newState, operation, inCheck, checkmate, optionalKingInCheck);
    }

    public boolean isEmptyFieldOrOppositePlayer(BoardSquare boardSquare, GameState gameState){
        ChessBoard snapshotBoard = gameState.board().getSnapshot();
        Piece piece = snapshotBoard.getPieceAt(boardSquare.getRow(), boardSquare.getCol());

        return piece == null || piece.getColor() != gameState.nowMovingPlayer();
    }

    public ChessBoard rawMove(Move operation, ChessBoard snapshotBoard) {
        BoardSquare from = operation.getInitCell();
        BoardSquare to = operation.getDestCell();

        Piece f = snapshotBoard.getPieceAt(from.getRow(), from.getCol());
        snapshotBoard.setPieceAt(from.getRow(), from.getCol(), null);
        snapshotBoard.setPieceAt(to.getRow(), to.getCol(), f);

        return snapshotBoard;
    }

    private void removePieceAt(BoardSquare boardSquare, ChessBoard snapshotBoard) {
        snapshotBoard.setPieceAt(boardSquare.getRow(), boardSquare.getCol(), null);
    }

    private void handleTheKingMove(King k, Move kingOperation, ChessBoard snapshot) {
        int row = kingOperation.getInitCell().getRow();
        int fromCol = kingOperation.getInitCell().getCol();
        int toCol = kingOperation.getDestCell().getCol();

        if (Math.abs(fromCol - toCol) == 2) {
            int rookInitCol = toCol > fromCol ? 7 : 0;
            int rookDestCol = toCol > fromCol ? fromCol + 1 : fromCol - 1;
            rawMove(new Move(new BoardSquare(row, rookInitCol), new BoardSquare(row, rookDestCol)), snapshot);
        }
        k.setDidMove();
    }

    private void handleThePawnMove(BoardSquare from, BoardSquare to, Pawn pawn, Piece targetPiece, PieceType promotionType, GameState gameState, ChessBoard snapshot) {

        int activeTurn = gameState.moveCounter();

        boolean movedDiagonally = from.getCol() != to.getCol();
        boolean targetSquareWasEmpty = targetPiece == null;

        if(movedDiagonally && targetSquareWasEmpty){
            int capturedPawnRow = from.getRow();
            int capturedPawnCol = to.getCol();

            Piece enPassantTarget = snapshot.getPieceAt(capturedPawnRow, capturedPawnCol);

            if(enPassantTarget instanceof Pawn capturedPawn &&
            capturedPawn.getColor() != pawn.getColor() &&
            capturedPawn.isEnpassantPossible(activeTurn)){
                removePieceAt(new BoardSquare(capturedPawnRow, capturedPawnCol), snapshot);
            }
        }

        pawn.incTheRank();
        if(Math.abs(from.getRow() - to.getRow()) == 2) {
            pawn.incTheRank();
            pawn.setEnpassantPossible(activeTurn);
        }

        pawn.disableFirstMove(); //This is special case for the Pawn.

        if(pawn.getRank() >= 7 || (pawn.getColor() == PlayerColor.WHITE && to.getRow() == 7) || (pawn.getColor() == PlayerColor.BLACK && to.getRow() == 0)) {
            Piece promotedPiece;
            if (promotionType == null) {
                promotedPiece = new Queen(pawn.getColor());
            } else {
                promotedPiece = switch (promotionType) {
                    case ROOK -> new Rook(pawn.getColor());
                    case BISHOP -> new Bishop(pawn.getColor());
                    case KNIGHT -> new Knight(pawn.getColor());
                    default -> new Queen(pawn.getColor());
                };
            }
            promotePawn(promotedPiece, to.getRow(), to.getCol(), snapshot);
        }
    }

    private void promotePawn(Piece fig, int row, int col, ChessBoard chessBoard) {
        chessBoard.setPieceAt(row, col, fig);
    }




}

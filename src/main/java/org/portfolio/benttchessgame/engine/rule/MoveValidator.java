package org.portfolio.benttchessgame.engine.rule;


import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.piece.Piece;
import org.portfolio.benttchessgame.piece.Pawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public final class MoveValidator {

    private MoveValidator(){};

    /*** GOOD ***/
    public static List<BoardSquare> getLegalMovesOf(int row, int col, ChessBoard snapshot, int activeTurn) {
        Piece piece = snapshot.getPieceAt(row,col);

        if(piece == null) return new ArrayList<>();

        ArrayList<BoardSquare> potentialMoves = new ArrayList<>();

        if (piece.isPawn()) {
            addPawnForwardMoves(row, col, (Pawn) piece, snapshot, potentialMoves);
        } else {
            for(int[] offset : piece.getOffset()) {
                addMovesInDirection(row,col,offset[0], offset[1],piece, snapshot, potentialMoves);
            }
        }

        return ChessRules.applyRules(row, col, potentialMoves, snapshot, activeTurn);
    }

    /*** GOOD ***/
    private static void addPawnForwardMoves(int row, int col, Pawn pawn, ChessBoard snapshotBoard, ArrayList<BoardSquare> potentialMoves) {
        int v = pawn.getVector();

        // One square forward
        if (inBounds(row + v, col)) {
            if (snapshotBoard.getPieceAt(row + v, col) == null) {
                if(noCheckmateOccurs(row, col, row + v, col, snapshotBoard)) {
                    potentialMoves.add(new BoardSquare(row + v, col));
                }
                // Two squares forward
                if (pawn.isFirstMove() && inBounds(row + 2 * v, col)) {
                    if (snapshotBoard.getPieceAt(row + 2 * v, col) == null) {
                        if(noCheckmateOccurs(row, col, row + 2 * v, col, snapshotBoard)) {
                            potentialMoves.add(new BoardSquare(row + 2 * v, col));
                        }
                    }
                }
            }
        }
    }


    public static boolean noCheckmateOccurs(int row, int col, int potentialMoveRow, int potentialMoveCol, ChessBoard chessBoard) {
        Piece piece = chessBoard.getPieceAt(row, col);

        if(piece != null) {
            Piece target = chessBoard.getPieceAt(potentialMoveRow, potentialMoveCol);
            chessBoard.setPieceAt(row, col, null);
            chessBoard.setPieceAt(potentialMoveRow, potentialMoveCol, piece);

            boolean isSafe = !doesGenerateCheckDangerForPlayer(chessBoard, piece.getColor());

            // Restore the board state
            chessBoard.setPieceAt(row, col, piece);
            chessBoard.setPieceAt(potentialMoveRow, potentialMoveCol, target);

            return isSafe;
        }
        return true;
    }

    /*** GOOD ***/
    public static boolean noCheckmateEnPassant(int row, int col, int toRow, int toCol, int captureRow, int captureCol, ChessBoard chessBoard) {
        Piece piece = chessBoard.getPieceAt(row, col);
        if(piece != null) {
            Piece capturedPawn = chessBoard.getPieceAt(captureRow, captureCol);
            chessBoard.setPieceAt(row, col, null);
            chessBoard.setPieceAt(toRow, toCol, piece);
            chessBoard.setPieceAt(captureRow, captureCol, null);

            boolean isSafe = !doesGenerateCheckDangerForPlayer(chessBoard, piece.getColor());

            // Restore the board state
            chessBoard.setPieceAt(row, col, piece);
            chessBoard.setPieceAt(toRow, toCol, null);
            chessBoard.setPieceAt(captureRow, captureCol, capturedPawn);

            return isSafe;
        }
        return true;
    }

    public static Optional<BoardSquare> getIfInCheck(ChessBoard chessBoard, PlayerColor player) {
        if(doesGenerateCheckDangerForPlayer(chessBoard, player)){
            return getTheKingBoardSquare(chessBoard, player);
        }
        return Optional.empty();
    }

    /*** GOOD ***/
    public static boolean doesGenerateCheckDangerForPlayer(ChessBoard board, PlayerColor playerColor) {
        Optional<BoardSquare> kingBoardSquare = getTheKingBoardSquare(board, playerColor);
        if (kingBoardSquare.isEmpty()) return false;

        return isSquareAttackedByOpponent(board, kingBoardSquare.get(), playerColor);
    }

    /*** GOOD ***/
    public static boolean isSquareAttackedByOpponent(ChessBoard board, BoardSquare square, PlayerColor playerColor) {
        PlayerColor opponentColor = playerColor.opposite();

        for(int row = 0; row < 8; row++) {
          for(int col = 0; col < 8; col++) {
              Piece piece = board.getPieceAt(row,col);
              if(piece != null && piece.getColor() == opponentColor) {
                  List<BoardSquare> rawMovesOfPiece = getRawMoves(board, new BoardSquare(row, col));
                  for(BoardSquare move : rawMovesOfPiece) {
                      if(move.equals(square)) {
                          return true;
                      }
                  }
              }
          }
        }

        return false;
    }

    /*** GOOD ***/
    public static void addMovesInDirection(int row, int col, int rowOff, int colOff, Piece piece, ChessBoard chessBoard, ArrayList<BoardSquare> potentialMoves) {

        int potentialMoveRow = row + rowOff;
        int potentialMoveCol = col + colOff;

        while(potentialMoveRow >= 0 && potentialMoveRow < 8 && potentialMoveCol >= 0 && potentialMoveCol < 8) {
            Piece target = chessBoard.getPieceAt(potentialMoveRow, potentialMoveCol);

            if(target == null) {
                if(noCheckmateOccurs(row, col, potentialMoveRow, potentialMoveCol, chessBoard)) {
                    potentialMoves.add(new BoardSquare(potentialMoveRow,potentialMoveCol));
                }

                if(!(piece.doesSlide())) {
                    return;
                }
            }
            else {
                // we check if the piece(to be moved) is an enemy of the target position piece
                if(areEnemies(piece, target)) {
                    // if the both the pieces are enemies and piece to be moved is not a Pawn
                    if(!piece.isPawn()) {
                        // we add the position of a target as possible move(green) or create a new list if is empty
                        if(noCheckmateOccurs(row, col, potentialMoveRow, potentialMoveCol, chessBoard)) {
                            potentialMoves.add(new BoardSquare(potentialMoveRow,potentialMoveCol));
                        }
                    }
                }

                return;
            }
            //increase the row and col of potential move with offsets for another iteration
            potentialMoveRow += rowOff;
            potentialMoveCol += colOff;
        }
    }

    /*** GOOD ***/
    public static List<BoardSquare> getRawMoves(ChessBoard board, BoardSquare boardSquare) {
        ArrayList<BoardSquare> rawPieceMoves = new ArrayList<>();

        Piece piece = board.getPieceAt(boardSquare.getRow(), boardSquare.getCol());
        if (piece == null) return rawPieceMoves;

        int[][] offsetsToUse = piece.isPawn() ? ((Pawn) piece).getBeatingOffset() : piece.getOffset();

        for(int[] offsets : offsetsToUse) {
            int rowOffset = offsets[0];
            int colOffset = offsets[1];

            int localRow = boardSquare.getRow() + rowOffset;
            int localCol = boardSquare.getCol() + colOffset;

            while(inBounds(localRow, localCol)) {

                Piece target = board.getPieceAt(localRow, localCol);

                //if the figure exists and is the enemy player
                if(target != null && target.getColor() != board.getPieceAt(boardSquare.getRow(), boardSquare.getCol()).getColor()) {
                    //add the cell to moves array and break the loop, because we stop iterating while there is at least one hit.
                    rawPieceMoves.add(new BoardSquare(localRow, localCol));
                    break;
                }
                // if there is not target, you can move here
                else if(target == null) {
                    rawPieceMoves.add(new BoardSquare(localRow, localCol));
                }
                // if the said figure doesn't slide, flush the list which was filled before.
                if(!board.getPieceAt(boardSquare.getRow(), boardSquare.getCol()).doesSlide()) {
                    break;
                }
                // if there is figure of the same color, flush the list.
                if(target != null && target.getColor() == board.getPieceAt(boardSquare.getRow(), boardSquare.getCol()).getColor()) {
                    break;
                }

                localRow += rowOffset;
                localCol += colOffset;
            }
        }
        return rawPieceMoves;
    }


    /*** GOOD ***/
    public static Optional<BoardSquare> getTheKingBoardSquare(ChessBoard board, PlayerColor ofPlayer) {
        //iterate through every possible figure on board
        for(int row = 0; row < ChessBoard.GRID_SIZE; row++) {
            for(int col = 0; col < ChessBoard.GRID_SIZE; col++) {
                Piece piece = board.getPieceAt(row,col);
                if(piece != null && piece.isKing() && piece.getColor() == ofPlayer) {

                    return Optional.of(new BoardSquare(row, col));
                }
            }
        }
        //if no king found, throw an exception because game shouldn't be on

        return Optional.empty();
    }

    private static boolean areEnemies(Piece fig1, Piece fig2) {
        return fig2 != null && fig1.getColor() != fig2.getColor();
    }

    public static ArrayList<BoardSquare> getActiveSquares(ChessBoard board, PlayerColor playerColor){
        ArrayList<BoardSquare> activeSquares = new ArrayList<>();
        for(int r = 0; r < ChessBoard.GRID_SIZE; r++){
            for(int c = 0; c < ChessBoard.GRID_SIZE; c++){
                if(board.getPieceAt(r,c) != null && board.getPieceAt(r,c).getColor() == playerColor)
                    activeSquares.add(new BoardSquare(r,c));
            }
        }

        return activeSquares;
    }


    public static boolean inBounds(int row, int col)
    {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }



}

package org.portfolio.benttchessgame.engine.rule;

import org.portfolio.benttchessgame.core.AppContext;
import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.GameState;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.event.KingCellAttackedEvent;
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.tools.UtilityMethods;
import org.portfolio.benttchessgame.piece.Piece;
import org.portfolio.benttchessgame.piece.Pawn;

import java.util.ArrayList;
import java.util.List;


public final class MoveValidator {

    private MoveValidator(){};

    /*** GOOD ***/
    public static List<BoardSquare> getLegalMovesOf(int row, int col, GameState state, ChessBoard chessBoard)
    {
        Piece[][] figuresOnBoard = chessBoard.getSnapshot().getPiecesOnBoard();
        Piece piece = figuresOnBoard[row][col];

        if(piece == null) return new ArrayList<>();

        ArrayList<BoardSquare> potentialMoves = new ArrayList<>();

        if (piece.isPawn()) {
            addPawnForwardMoves(row, col, (Pawn) piece, chessBoard, potentialMoves);
        } else {
            for(int[] offset : piece.getOffset())
            {
                addMovesInDirection(row,col,offset[0], offset[1],piece, chessBoard, potentialMoves);
            }
        }

        return ChessRules.applyRules(row, col, potentialMoves, figuresOnBoard, state);
    }

    /*** GOOD ***/
    private static void addPawnForwardMoves(int row, int col, Pawn p, ChessBoard chessBoard, ArrayList<BoardSquare> potentialMoves) {
        int v = p.getVector();
        // One square forward
        if (inBounds(row + v, col)) {
            if (chessBoard.getPieceAt(row + v, col) == null) {
                if(noCheckmate(row, col, row + v, col, chessBoard))
                {
                    potentialMoves.add(new BoardSquare(row + v, col));
                }
                // Two squares forward
                if (p.isFirstMove() && inBounds(row + 2 * v, col)) {
                    if (chessBoard.getPieceAt(row + 2 * v, col) == null) {
                        if(noCheckmate(row, col, row + 2 * v, col, chessBoard))
                        {
                            potentialMoves.add(new BoardSquare(row + 2 * v, col));
                        }
                    }
                }
            }
        }
    }

    /*** GOOD ***/
    private static boolean noCheckmate(int row, int col, int potentialMoveRow, int potentialMoveCol, ChessBoard chessBoard)
    {
        Piece[][] pieces = chessBoard.getSnapshot().getPiecesOnBoard();
        Piece p;

        if(pieces[row][col] != null)
        {
            p = pieces[row][col];
            pieces[row][col] = null;
            pieces[potentialMoveRow][potentialMoveCol] = p;

            return !doesGenerateCheckDangerForPlayer(pieces, p.getColor());
        }
        else
            throw new IllegalArgumentException("No piece at given position");

    }

    /*** GOOD ***/
    public static boolean doesGenerateCheckDangerForPlayer(Piece[][] pieces, PlayerColor playerColor)
    {
        PlayerColor opponentColor = playerColor == PlayerColor.WHITE ? PlayerColor.DARK : PlayerColor.WHITE;
        BoardSquare kingBoardSquare = getTheKingBoardSquare(pieces, playerColor);

        for(int row = 0; row < 8; row++)
        {
          for(int col = 0; col < 8; col++)
          {
              Piece piece = pieces[row][col];
              if(piece != null && piece.getColor() == opponentColor)
              {
                  List<BoardSquare> rawMovesOfPiece = calculateRawMoves(pieces, new BoardSquare(row, col));
                  for(BoardSquare move : rawMovesOfPiece)
                  {
                      if(move.equals(kingBoardSquare))
                      {
                          notifyKingCellAttacked(kingBoardSquare);
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

        //values after potential movement
        int r = row + rowOff;
        int c = col + colOff;

        while(r >= 0 && r < 8 && c >= 0 && c < 8)
        {
            Piece target = chessBoard.getPieceAt(r, c);

            if(target == null)
            {
                if(noCheckmate(row, col, r, c, chessBoard))
                {
                    potentialMoves.add(new BoardSquare(r,c));
                }

                if(!(piece.doesSlide()))
                {
                    return;
                }
            }
            else
            {
                // we check if the piece(to be moved) is an enemy of the target position piece
                if(isEnemy(piece, target))
                {
                    // if the both the pieces are enemies and piece to be moved is not a Pawn
                    if(!piece.isPawn())
                    {
                        // we add the position of a target as possible move(green) or create a new list if is empty
                        if(noCheckmate(row, col, r, c, chessBoard))
                        {
                            potentialMoves.add(new BoardSquare(r,c));
                        }
                    }
                }

                return;
            }
            //increase the row and col of potential move with offsets for another iteration
            r += rowOff;
            c += colOff;
        }
    }

    /*** GOOD ***/
    public static List<BoardSquare> calculateRawMoves(Piece[][] snapshot, BoardSquare boardSquare)
    {
        ArrayList<BoardSquare> rawMovesOfABoardSquare = new ArrayList<>();

        Piece[][] snapshotCopy = UtilityMethods.copyPieceArray(snapshot);

        for(int[] offsets : snapshotCopy[boardSquare.getRow()][boardSquare.getCol()].getOffset())
        {

            int rowOffset = offsets[0];
            int colOffset = offsets[1];

            int localRow = boardSquare.getRow() + rowOffset;
            int localCol = boardSquare.getCol() + colOffset;

            while(inBounds(localRow, localCol))
            {

                Piece target = snapshotCopy[localRow][localCol];

                //if the figure exists and is the enemy player
                if(target != null && target.getColor() != snapshotCopy[boardSquare.getRow()][boardSquare.getCol()].getColor())
                {
                    //add the cell to moves array and break the loop, because we stop iterating while there is at least one hit.
                    rawMovesOfABoardSquare.add(new BoardSquare(localRow, localCol));
                    break;
                }
                // if there is not target, you can move here
                else if(target == null)
                {
                    rawMovesOfABoardSquare.add(new BoardSquare(localRow, localCol));
                }
                // if the said figure doesn't slide, flush the list which was filled before.
                if(!snapshot[boardSquare.getRow()][boardSquare.getCol()].doesSlide())
                {
                    return rawMovesOfABoardSquare;
                }
                // if there is figure of the same color, flush the list.
                if(target != null && target.getColor() == snapshot[boardSquare.getRow()][boardSquare.getCol()].getColor())
                {
                    return rawMovesOfABoardSquare;
                }

                localRow += rowOffset;
                localCol += colOffset;
            }
        }
        return rawMovesOfABoardSquare;
    }


    /*** GOOD ***/
    public static BoardSquare getTheKingBoardSquare(Piece[][] snapshot, PlayerColor ofPlayer)
    {
        //iterate through every possible figure on board
        for(int row = 0; row < snapshot.length; row++)
        {
            for(int col = 0; col < snapshot[row].length; col++)
            {
                Piece piece = snapshot[row][col];
                if(piece != null && piece.isKing() && piece.getColor() == ofPlayer)
                {
                    return new BoardSquare(row, col);
                }
            }
        }
        //if no king found, throw an exception because game shouldn't be on

        throw new IllegalArgumentException("No king found for player " + ofPlayer);
    }

    /*** GOOD ***/
    private static boolean isEnemy(Piece fig1, Piece fig2)
    {
        return fig2 != null && fig1.getColor() != fig2.getColor();
    }

    /*** GOOD ***/
    public static boolean inBounds(int row, int col)
    {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /*** GOOD ***/
    private static void notifyKingCellAttacked(BoardSquare kingBoardSquare)
    {
        GameEventBus bus = AppContext.getAppContext().getEventBus();
        bus.publishEvent(new KingCellAttackedEvent(kingBoardSquare));
    }

}

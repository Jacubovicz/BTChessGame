package org.portfolio.benttchessgame.engine.rule;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.GameState;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.piece.Piece;
import org.portfolio.benttchessgame.piece.King;
import org.portfolio.benttchessgame.piece.Pawn;
import org.portfolio.benttchessgame.piece.Rook;

import java.util.ArrayList;
import java.util.List;

public final class ChessRules {

    private ChessRules()
    {
        throw new UnsupportedOperationException("This class is only for rule application.");
    }

    /*** GOOD ***/
    public static List<BoardSquare> applyRules(int row, int col, List<BoardSquare> potentialMoves, Piece[][] grid, GameState state)
    {
        //create an instance of new list, in which only legalMoves are existant
        List<BoardSquare> legalMoves = new ArrayList<>(potentialMoves);

        //get object of the figure which is about to be moved.
        Piece f = grid[row][col];

        //get objects of both left and right, it will be needed for calculation of enpassant
        Piece left=null,right=null;

        //if the column is in bounds of a board
        if(col > 0)
        {
            // assign leftSide to left variable
            left = grid[row][col-1];
        }
        if(col<7)
        {
            //assign right side to right variable
            right = grid[row][col+1];
        }

        // Use pattern matching in switch for cleaner rule application
        switch (f) {
            case King k when !k.didMove() -> checkCastlings(k, row, col, grid, legalMoves);
            case Pawn p -> {
                //if it isn't the first move of a pawn
                if(!p.isFirstMove())
                {
                    //if left side of a pawn before movement is also a pawn and is not of the same player, and fulfills rules for enpassant
                    if(left != null && left.isPawn() && f.getColor() != left.getColor() && ((Pawn)left).isEnpassantPossible(state))
                    {
                        //add the cell representation of the figure movement for the enpassant
                        legalMoves.add(new BoardSquare(row - ((Pawn)left).getVector(), col-1));
                    }
                    //if right side of a pawn before movement is also a pawn and is not of the same player, and fulfills rules for enpassant
                    else if(right != null && right.isPawn() && f.getColor() != right.getColor() && ((Pawn)right).isEnpassantPossible(state))
                    {
                        legalMoves.add(new BoardSquare(row - ((Pawn)right).getVector(), col+1));
                    }
                }

                // knowing this is the Pawn, we check if it has any beating positions iterating through beating offsets
                for(int[] offset : p.getBeatingOffset())
                {
                    int rowOffset = offset[0];
                    int colOffset = offset[1];

                    if(MoveValidator.inBounds(row + rowOffset, col + colOffset))
                    {
                        Piece target = grid[row + rowOffset][col + colOffset];
                        if(target != null && p.getColor() != target.getColor())
                        {
                            legalMoves.add(new BoardSquare(row + rowOffset, col + colOffset));
                        }
                    }
                }
            }
            default -> {}
        }

        return legalMoves;
    }

    /*** GOOD ***/
    public static boolean isCheckmate(ChessBoard board, GameState gameState, PlayerColor playerColor) {
        if (MoveValidator.doesGenerateCheckDangerForPlayer(board.getPiecesOnBoard(), playerColor)) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    List<BoardSquare> legalMoves = MoveValidator.getLegalMovesOf(row, col, gameState, board);
                    if (!legalMoves.isEmpty()) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }


    /*** GOOD ***/
      private static void checkCastlings(King k, int row, int col, Piece[][] grid, List<BoardSquare> legalMoves)
      {
          //initalize the starting indexes of a Rook (Rook can't move in order to castling be possible
          int[] columnIndexesOfRook = {0, 7};

          //go through every possible column where the Rook may be existent to fulfill the rule
          for(int columnOfARook : columnIndexesOfRook)
          {
              //if the Rook is on position and didn't move
              var r = grid[row][columnOfARook];
              if(r.isRook() && !((Rook) r).didMove())
              {
                  // if the cells inbetween the king and the rook are empty
                  if(cellsAreEmptyBetween(row,col, columnOfARook,grid))
                  {
                      //choose which Rook based on QueenSideCastling or KingSideCastling
                      var expression = col > columnOfARook ? col-2 : col+2;

                      //add the field for castling to be possible
                      legalMoves.add(new BoardSquare(row, expression, true));
                  }
              }
          }
      }

      /*** GOOD ***/
    private static boolean cellsAreEmptyBetween(int row, int col, int rookCol, Piece[][] copyOfGrid)
    {
        if(col > rookCol)
        {
            for(int i = col-1; i > rookCol; i--)
            {
                if((copyOfGrid[row][i]) != null) return false;
            }
        }
        else
        {
            for(int i = col+1; i < rookCol; i++)
            {
                if((copyOfGrid[row][i]) != null) return false;
            }
        }
        return true;
    }


}

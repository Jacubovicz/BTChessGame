package org.portfolio.benttchessgame.engine.rule;

import org.portfolio.benttchessgame.engine.BoardSquare;
import org.portfolio.benttchessgame.engine.GameSession;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.piece.Piece;
import org.portfolio.benttchessgame.piece.King;
import org.portfolio.benttchessgame.piece.Pawn;
import org.portfolio.benttchessgame.piece.Rook;

import java.util.ArrayList;

public final class ChessRules {

    private ChessRules()
    {
        throw new UnsupportedOperationException("This class is only for rule application.");
    }
//What is actually the role of starterMoves?
    public static ArrayList<BoardSquare> applyRules(int row, int col, ArrayList<BoardSquare> potentialMoves, Piece[][] grid, GameSession currentGameSession)
    {
        //create an instance of new list, in which only legalMoves are existant
        ArrayList<BoardSquare> legalMoves = (ArrayList<BoardSquare>) potentialMoves.clone();

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

        //if the figure to be moved is a King and it didn't move
        if(f instanceof King k && !k.didMove())
        {
            // we check if the castling is possible
            checkCastlings(k, row, col, grid, legalMoves);
        }
        //if the figure is Pawn
        if(f instanceof Pawn p)
        {
            //if it isn't the first move of a pawn
            if(!p.isFirstMove())
            {
                //if left side of a pawn before movement is also a pawn and is not of the same player, and fulfills rules for enpassant
                if(left instanceof Pawn leftP && f.getColor() != leftP.getColor() && leftP.isEnpassantPossible(currentGameSession))
                {
                    //add the cell representation of the figure movement for the enpassant
                    legalMoves.add(new BoardSquare(row - leftP.getVector(), col-1));
                }
                //if right side of a pawn before movement is also a pawn and is not of the same player, and fulfills rules for enpassant
                else if(right instanceof Pawn rightP && f.getColor() != rightP.getColor() && rightP.isEnpassantPossible(currentGameSession))
                {
                    legalMoves.add(new BoardSquare(row - rightP.getVector(), col+1));
                }
            }

            // knowing this is the Pawn, we check if it has any beating positions iterating through beating offsets
         for(int[] offset : p.getBeatingOffset())
         {
             //we initialize the beating offsets
             int rowOffset = offset[0];
             int colOffset= offset[1];


             //while in limits of the board
             if(inBounds(row + rowOffset, col + colOffset))
             {
                 //assume the target figure
                 Piece target = grid[row + rowOffset][col + colOffset];

                 //if there is figure on this cell and are of different colors
                 if(target != null && p.getColor() != target.getColor())
                 {
                     //add the possible cell to the list
                     legalMoves.add(new BoardSquare(row + rowOffset, col + colOffset));
                 }
             }

         }
        }

        return legalMoves;
    }

    //isWhitePlayer means for that person/color will be calculcated a checkmate.
    public static boolean isCheckmate(Piece[][] snapshot, boolean isWhitePlayer)
    {
        BoardSquare kingBoardSquare = null; //it is empty just yet but will be filled by the method.
        //if King of this player is not in danger
        if(!isKingInCheck(snapshot, isWhitePlayer, kingBoardSquare))
        {
            //king not in check, game still on.
            return false;
        }
        //we get the King cell of the given player
       // kingCell = Objects.requireNonNull(Validator.getTheKingCell(snapshot, isWhitePlayer)); //take a look at isWhitePlayer

        for(int row = 0; row < snapshot.length; row++)
        {
            //Iterate over every figure on board
            for(int col = 0; col < snapshot[row].length; col++)
            {
                Piece f = snapshot[row][col];
                if(f != null && f.getColor() == isWhitePlayer)
                {
                    //get all the raw moves of the figure of same player which king is attacked
                    ArrayList<BoardSquare> possibleFigureMoves = MoveValidator.calculateRawMoves(snapshot, new BoardSquare(row,col));
                    //filter the illegal moves. If there is at least one move ending the check, there is not checkmate.
                    if(!MoveValidator.filterIllegalMoves(possibleFigureMoves, snapshot, kingBoardSquare).isEmpty())
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    //isWhitePlayer is the player being checked. potentially
    public static boolean isKingInCheck(Piece[][] snapshot, boolean isWhitePlayer, BoardSquare kingBoardSquare)
    {
         kingBoardSquare = MoveValidator.getTheKingCell(snapshot, isWhitePlayer);

         return isKingCellAttackedBy(snapshot, kingBoardSquare, !isWhitePlayer);

    }

    public static boolean isKingCellAttackedBy(Piece[][] snapshot, BoardSquare targetBoardSquare, boolean attackerColor)
    {
        for(int row = 0; row < snapshot.length; row++)
        {
            for(int col = 0; col < snapshot.length; col++)
            {
                Piece attacker = snapshot[row][col];
                if(attacker != null && attacker.getColor() == attackerColor)
                {
                    BoardSquare attackerBoardSquare = new BoardSquare(row,col);

                    ArrayList<BoardSquare> attackerMoves = MoveValidator.calculateRawMoves(snapshot, attackerBoardSquare);
                    return isInTheAttackingList(targetBoardSquare, attackerMoves);
                }
            }
        }
        return false;
    }

    public static boolean isInTheAttackingList(BoardSquare targetBoardSquare, ArrayList<BoardSquare> attackerMoves)
    {
        return attackerMoves.contains(targetBoardSquare);
    }

      private static void checkCastlings(King k, int row, int col, Piece[][] grid, ArrayList<BoardSquare> legalMoves)
      {
          //initalize the starting indexes of a Rook (Rook can't move in order to castling be possible
          int[] columnIndexesOfRook = {0, 7};

          //go through every possible column where the Rook may be existant to fulfill the rule
          for(int columnOfARook : columnIndexesOfRook)
          {
              //if the Rook is on position and didn't move
              if(grid[row][columnOfARook] instanceof Rook r && !r.didMove())
              {
                  // if the cells inbetween the king and the rook are empty
                  if(cellsAreEmptyBetween(row,col, columnOfARook,grid))
                  {
                      //choose which Rook based on QueenSideCastling or KingSideCastling
                      var expression = col > columnOfARook ? col-2 : col+2;
                      //based on Rook, choose the position(Cell) where it should go
                      var expressionForRookMovement = col > columnOfARook ? col-1 : col+1;
                      //add the field for castling to be possible
                      legalMoves.add(new BoardSquare(row, expression, true));

                      //set the castling value in King object
                      k.setCastling(new Move(new BoardSquare(row, columnOfARook), new BoardSquare(row, expressionForRookMovement)), col);
                  }
                  else
                  {
                      disableInvalidCastling(columnOfARook, col, k);
                  }
              }
              else
                  disableInvalidCastling(columnOfARook, col, k);

          }
      }

      private static void disableInvalidCastling(int columnOfARook, int col, King k)
      {
          int val = Math.abs(columnOfARook - col);
          if(val == 4)
          {
              k.setQueenSideCastling(null);
          }
          else if(val == 3)
          {
              k.setKingSideCastling(null);
          }
      }


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

    public static boolean inBounds(int row, int col)
    {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

}

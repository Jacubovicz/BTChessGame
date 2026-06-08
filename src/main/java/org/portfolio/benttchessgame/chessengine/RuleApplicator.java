package org.portfolio.benttchessgame.chessengine;


import org.portfolio.benttchessgame.figures.Figure;
import org.portfolio.benttchessgame.figures.King;
import org.portfolio.benttchessgame.figures.Pawn;
import org.portfolio.benttchessgame.figures.Rook;

import java.util.ArrayList;

public final class RuleApplicator {

    private RuleApplicator()
    {
        throw new UnsupportedOperationException("This class is only for rule application.");
    }
//What is actually the role of starterMoves?
    public static ArrayList<Cell> apply(int row, int col, ArrayList<Cell> possibleMoves, Figure[][] grid, Game currentGame)
    {
        ArrayList<Cell> adjustedList = (ArrayList<Cell>) possibleMoves.clone();

        Figure f = grid[row][col];

        if(f instanceof King k && k.getMoved() == false)
        {
            checkCastlings(k, row, col);
        }
        if(f instanceof Pawn p)
        {
            if(p.isEnpassantPossible())
            {
                adjustedList.add(p.getEnpassantCell());
            }

         for(int[] offset : p.getBeatingOffset())
         {
             int rowOffset = offset[0];
             int colOffset= offset[1];

             if(inBounds(row + rowOffset, col + colOffset))
             {
                 Figure target = grid[row + rowOffset][col + colOffset];

                 if(target != null && p.getColor() != target.getColor())
                 {
                     adjustedList.add(new Cell(row + rowOffset, col + colOffset));
                     currentGame.setStarterMoves(row+rowOffset,col+colOffset, true);

                 }
             }

         }
        }
        return adjustedList;
    }

      private static void checkCastlings(King k, int row, int col)
      {
          checkQueenSideCastling(k, row, col);
          checkKingSideCastling(k, row, col);
      }


      private static void checkQueenSideCastling(King k, int row, int col)
      {
          if(k.getColor() == true)//white
          {
              if(cellsAreEmptyBetween(row,col))
              {

              }
          }
      }


    private boolean cellsAreEmptyBetween(int row, int col)
    {

    }

    private static void checkKingSideCastling(King k, int row, int col)
    {

    }

    public static boolean inBounds(int row, int col)
    {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}

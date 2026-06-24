package org.portfolio.benttchessgame.chessengine;


import UILogic.helperclasses.EnumColors;
import org.portfolio.benttchessgame.figures.Figure;
import org.portfolio.benttchessgame.figures.King;
import org.portfolio.benttchessgame.figures.Pawn;
import org.portfolio.benttchessgame.figures.Rook;

import java.util.ArrayList;
import java.util.HashMap;

public final class RuleApplicator {

    private RuleApplicator()
    {
        throw new UnsupportedOperationException("This class is only for rule application.");
    }
//What is actually the role of starterMoves?
    public static ArrayList<Cell> apply(int row, int col, ArrayList<Cell> possibleMoves, Figure[][] grid, Session currentSession, HashMap<EnumColors, ArrayList<Cell>> allMarkedCells)
    {
        ArrayList<Cell> adjustedList = (ArrayList<Cell>) possibleMoves.clone();//available moves (GREEN)

        Figure f = grid[row][col];
        Figure left=null,right=null;
        if(col > 0)
        {
            left = grid[row][col-1];
        }
        if(col<7)
        {
            right = grid[row][col+1];
        }

        if(f instanceof King k && !k.didMove())
        {
            checkCastlings(k, row, col, grid, allMarkedCells);
        }
        if(f instanceof Pawn p)
        {
            if(!p.isFirstMove())
            {
                if(left instanceof Pawn leftP && f.getColor() != leftP.getColor() && leftP.isEnpassantPossible(currentSession))
                {
                    adjustedList.add(new Cell(row - leftP.getVector(), col-1));
                    allMarkedCells.computeIfAbsent(EnumColors.BLANK, func -> new ArrayList<>()).add(new Cell(row - leftP.getVector(), col-1));
                }
                else if(right instanceof Pawn rightP && f.getColor() != rightP.getColor() && rightP.isEnpassantPossible(currentSession))
                {
                    adjustedList.add(new Cell(row - rightP.getVector(), col+1));
                    allMarkedCells.computeIfAbsent(EnumColors.BLANK, func -> new ArrayList<>()).add(new Cell(row- rightP.getVector(), col+1));
                }
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
                     allMarkedCells.computeIfAbsent(EnumColors.BLANK, func -> new ArrayList<>()).add(new Cell(row+rowOffset,col+colOffset));

                 }
             }

         }
        }

        return adjustedList;
    }

    public static boolean checkTheGameFinished(Figure[][] piecesOnBoard)
    {
        return false;
    }

      private static void checkCastlings(King k, int row, int col, Figure[][] grid, HashMap<EnumColors, ArrayList<Cell>> allMarkedCells)
      {
          int[] columnIndexesOfRook = {0, 7};
          ArrayList<Cell> blueFields = new ArrayList<>();
          for(int columnOfARook : columnIndexesOfRook)
          {
              if(grid[row][columnOfARook] instanceof Rook r && !r.didMove())
              {
                  if(cellsAreEmptyBetween(row,col, columnOfARook,grid))
                  {
                      var expression = col > columnOfARook ? col-2 : col+2; //this defines the way in which is moving
                      var expressionForRookMovement = col > columnOfARook ? col-1 : col+1;
                      blueFields.add(new Cell(row, expression));
                      k.setCastling(new MoveOperation(new Cell(row, columnOfARook), new Cell(row, expressionForRookMovement)));
                  }
              }
          }
          allMarkedCells.put(EnumColors.BLUE, blueFields);
      }


    private static boolean cellsAreEmptyBetween(int row, int col, int rookCol, Figure[][] copyOfGrid)
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

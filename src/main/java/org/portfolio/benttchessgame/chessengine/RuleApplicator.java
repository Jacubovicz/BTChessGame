package org.portfolio.benttchessgame.chessengine;


import org.portfolio.benttchessgame.figures.Figure;
import org.portfolio.benttchessgame.figures.Pawn;

import java.util.ArrayList;

public final class RuleApplicator {

    private RuleApplicator()
    {
        throw new UnsupportedOperationException("This class is only for rule application.");
    }
//What is actually the role of starterMoves?
    public static ArrayList<Cell> apply(int row, int col, ArrayList<Cell> possibleMoves, Figure[][] grid, boolean[][] starterMoves)
    {
        ArrayList<Cell> adjustedList = (ArrayList<Cell>) possibleMoves.clone();
        Figure f = grid[row][col];

        if(f instanceof Pawn p)
        {
         for(int[] offset : p.getBeatingOffset())
         {
             int rowOffset = offset[0]*p.getVector();
             int colOffset= offset[1]*p.getVector();

             if(inBounds(row + rowOffset, col + colOffset))
             {
                 Figure target = grid[row + rowOffset][col + colOffset];

                 if(target != null && p.getColor() != target.getColor())
                 {
                     adjustedList.add(new Cell(row + rowOffset, col + colOffset));
                     starterMoves[row+rowOffset][col + colOffset] = true;
                 }
             }

         }
        }
        return adjustedList;
    }

    public static boolean inBounds(int row, int col)
    {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}

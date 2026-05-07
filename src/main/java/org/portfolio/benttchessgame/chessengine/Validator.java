package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.listeners.AvailableMovesChangeListener;
import org.portfolio.benttchessgame.figures.Figure;
import org.portfolio.benttchessgame.figures.Pawn;


import java.util.ArrayList;
import java.util.List;


public class Validator {

    Game currentGame;

    List<AvailableMovesChangeListener> avlMovesListeners = new ArrayList<>();

    private Figure[][] originalGrid;
    private Figure[][] snapshotGrid;
    private ArrayList<Cell> possibleMoves;

    //First showup of Validator
    public Validator(Game currentGame)
    {
        this.currentGame = currentGame;
        this.originalGrid = currentGame.getSnapshot();
    }

    private void updateTheBoard()
    {
        this.originalGrid = currentGame.getSnapshot();
    }

    public ArrayList<Cell> getPossibleMoves()
    {
        return possibleMoves;
    }

    public void checkMoves(Cell initialCell)
    {
        snapshotGrid = originalGrid.clone();
        Figure piece = snapshotGrid[initialCell.getRow()][initialCell.getCol()];

        if(piece == null)
        {
            possibleMoves = new ArrayList<>();
            return;
        }
        possibleMoves = new ArrayList<>();

        for(int[] offset : piece.getOffset())
        {
            addMovesInDirection(initialCell.getRow(), initialCell.getCol(), offset[0], offset[1], piece);
        }

        notifyAvlMovesListeners();

    }

    public void addMovesInDirection(int row, int col, int rowOff, int colOff, Figure piece) {

        if(piece instanceof Pawn p)
        {
            if(p.isLocked() && p.isFirstMove())
            {
                return;
            }
            if(row == 6)
            {
                p.setVector(-1);
            }
        }

        int r = row + rowOff;
        int c = col + colOff;

        while(inBounds(r,c))
        {
            Figure target = snapshotGrid[r][c];

            if(target == null)
            {
                possibleMoves.add(new Cell(r,c));
                currentGame.setStarterMoves(r,c,true);

                if(!(piece.doesSlide()))
                {
                    possibleMoves = RuleApplicator.apply(row, col, possibleMoves, snapshotGrid, currentGame.getStarterMoves());
                    return;
                }
            }
            else
            {
                if(isEnemy(piece, target))
                {
                    if(!(piece instanceof Pawn))//Pawn can't attack forward.
                    {
                        possibleMoves.add(new Cell(r,c));
                        currentGame.setStarterMoves(r,c,true);
                    }
                    else
                    {
                        ((Pawn) piece).setLocked();
                    }
                }
                this.possibleMoves = RuleApplicator.apply(row, col, possibleMoves, snapshotGrid, currentGame.getStarterMoves());
                return;
            }

            r += rowOff;
            c += colOff;

        }

        this.possibleMoves = RuleApplicator.apply(row, col, possibleMoves, snapshotGrid, currentGame.getStarterMoves());


    }



    private boolean isEnemy(Figure fig1, Figure fig2)
    {
        return fig2 != null && fig1.getColor() != fig2.getColor();
    }

    private boolean inBounds(int rowPos, int colPos)
    {
        if(rowPos < 0 || rowPos >= originalGrid.length) return false;
        return colPos >= 0 && colPos < originalGrid.length;
    }

    public void addAvlMovesChangedListener(AvailableMovesChangeListener listener)
    {
        avlMovesListeners.add(listener);
    }

    public void removeAvlMovesChangedListener(AvailableMovesChangeListener listener)
    {
        avlMovesListeners.remove(listener);
    }


    private void notifyAvlMovesListeners()
    {
        for(AvailableMovesChangeListener listener : avlMovesListeners)
        {
            listener.onMovesChangeEvent(possibleMoves);
        }
    }

}

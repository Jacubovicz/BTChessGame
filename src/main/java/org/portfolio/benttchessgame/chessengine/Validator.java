package org.portfolio.benttchessgame.chessengine;

import org.portfolio.benttchessgame.chessengine.listeners.AvailableMovesChangeListener;
import org.portfolio.benttchessgame.figures.Figure;
import org.portfolio.benttchessgame.figures.King;
import org.portfolio.benttchessgame.figures.Pawn;

import java.util.ArrayList;
import java.util.List;

public class Validator {

    Game currentGame;

    List<AvailableMovesChangeListener> avlMovesListeners = new ArrayList<>();

    private Figure[][] originalGrid;
    private Figure[][] snapshotGrid;
    private ArrayList<Cell> possibleMoves;

    private ArrayList<Cell> fieldsToCheck; //this is only for the use of MoveEvaluation


    public Validator(Game currentGame)
    {
        this.currentGame = currentGame;
        this.originalGrid = currentGame.getSnapshot();
    }

    public ArrayList<Cell> checkMoves(Cell initialCell)
    {
        snapshotGrid = currentGame.getSnapshot();
        Figure piece = snapshotGrid[initialCell.getRow()][initialCell.getCol()];

        if(piece == null)
        {
            possibleMoves = new ArrayList<>();
            return null;
        }
        possibleMoves = new ArrayList<>();

        for(int[] offset : piece.getOffset())
        {
            addMovesInDirection(initialCell.getRow(), initialCell.getCol(), offset[0], offset[1], piece);
        }

        checkForCheckmate(possibleMoves, initialCell, snapshotGrid);

        notifyAvlMovesListeners();

        return null;
    }

    private void checkForCheckmate(ArrayList<Cell> possibleMoves, Cell initialCell, Figure[][] snapshotGrid)
    {

        int indexToRemove = 0;
        ArrayList<Cell> cellsToBeRemoved = new ArrayList<>();
        for(Cell cell : possibleMoves)
        {
            boolean skip = false;

            Figure[][] localSnapshotCopy = cloneArray(snapshotGrid);
            Figure potentialMove = localSnapshotCopy[initialCell.getRow()][initialCell.getCol()];
            localSnapshotCopy[cell.getRow()][cell.getCol()] = potentialMove;
            localSnapshotCopy[initialCell.getRow()][initialCell.getCol()] = null;


            Cell cellToRemove = lookForKing(localSnapshotCopy, !potentialMove.getColor());
            if(cellToRemove != null)
            {
                cellsToBeRemoved.add(possibleMoves.get(indexToRemove));
            }

            indexToRemove++;
        }
        this.possibleMoves.removeAll(cellsToBeRemoved);
    }

    private Figure[][] cloneArray(Figure[][] array)
    {
        Figure[][] copy = new Figure[array.length][array[0].length];

        for(int row = 0; row < copy.length; row++)
        {
            copy[row] = array[row].clone();
        }

        return copy;
    }

    private Cell lookForKing(Figure[][] snapshotGrid, boolean enemy)
    {
        int r = 0; //row

        for(Figure[] pieceRow : snapshotGrid)
        {
            int c = 0; //col
            for(Figure piece : pieceRow)
            {
                if(piece == null || piece.getColor() != enemy)
                {
                    c++;
                    continue;
                }

                for(int[] offsets : piece.getOffset())
                {
                    int localRowOffset = r + offsets[0];
                    int localColOffset = c + offsets[1];
                    while(inBounds(localRowOffset, localColOffset))
                    {
                        Figure target = snapshotGrid[localRowOffset][localColOffset];

                        if(target != null && target.getColor() == piece.getColor()) break;
                        if(target != null && target.getColor() != piece.getColor() && target instanceof King)
                        {
                            return new Cell(localRowOffset, localColOffset); //it is enough that only one figure declares possible hit.
                        }
                        if(target != null && target.getColor() != piece.getColor()) break;
                        if(!piece.doesSlide())
                        {
                            break;
                        }
                        localRowOffset += offsets[0];
                        localColOffset += offsets[1];
                    }
                }
                c++;
            }

            r++;
        }
        return null;
    }

    public void addMovesInDirection(int row, int col, int rowOff, int colOff, Figure piece) {

        if(piece instanceof Pawn p)
        {
            if(p.isLocked() && p.isFirstMove())
            {
                return;
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
                    this.possibleMoves = RuleApplicator.apply(row, col, possibleMoves, snapshotGrid, currentGame);
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
                this.possibleMoves = RuleApplicator.apply(row, col, possibleMoves, snapshotGrid, currentGame);
                return;
            }
            r += rowOff;
            c += colOff;
        }
        this.possibleMoves = RuleApplicator.apply(row, col, possibleMoves, snapshotGrid, currentGame);
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

    private void notifyAvlMovesListeners()
    {
        for(AvailableMovesChangeListener listener : avlMovesListeners)
        {
            listener.onMovesChangeEvent(possibleMoves);
        }
    }
}

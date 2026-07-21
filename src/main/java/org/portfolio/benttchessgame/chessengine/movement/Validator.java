package org.portfolio.benttchessgame.chessengine.movement;

import UILogic.helperclasses.EnumColors;
import org.portfolio.benttchessgame.chessengine.Cell;
import org.portfolio.benttchessgame.chessengine.Session;
import org.portfolio.benttchessgame.figures.Figure;
import org.portfolio.benttchessgame.figures.King;
import org.portfolio.benttchessgame.figures.Pawn;

import java.util.ArrayList;
import java.util.HashMap;


public final class Validator {

    private Validator(){};

    //**
    public static ArrayList<Cell> getLegalMovesOf(int row, int col, Session currentSession, PieceBoard pieceBoard)
    {
        Figure[][] figuresOnBoard = pieceBoard.piecesOnBoard;
        Figure piece = figuresOnBoard[row][col];

        if(piece == null) return new ArrayList<>();

        ArrayList<Cell> potentialMoves = new ArrayList<>();

        for(int[] offset : piece.getOffset())
        {
            addMovesInDirection(row,col,offset[0], offset[1],piece, pieceBoard, potentialMoves);
        }
        eliminateCheckPositions(potentialMoves, new Cell(row,col), figuresOnBoard);

        return RuleApplicator.applyRules(row, col, potentialMoves, figuresOnBoard, currentSession);


    }

    //for the given cords of the cell,
    public static void addMovesInDirection(int row, int col, int rowOff, int colOff, Figure piece, PieceBoard pieceBoard, ArrayList<Cell> potentialMoves) {

        if(piece instanceof Pawn p)
        {
            // if in front of a pawn(only possible move if not attacking) is no free field
            // and is first move of the pawn(can't jump over the figure), then return and come back to the invoking method.
             if(p.isLocked() && p.isFirstMove())
             {
                 p.unlock();
                 return;
             }
        }

        // initialize the fields with cords increased by offsets
        int r = row + rowOff;
        int c = col + colOff;

        //as long as the potential movement fields are in limit of a board, continue
        while(inBounds(r,c, pieceBoard.getPiecesOnBoard()))
        {
            //get the potential target figure from the pieceBoard
            Figure target = pieceBoard.getPiecesOnBoard()[r][c];

            //if there is no piece on specific cell
            if(target == null)
            {
                //add the cell to the available(green moves) or create one if it doesn't exist
                potentialMoves.add(new Cell(r,c));

                //if the piece is not the one which is sliding return the method because no more moves are possible in this turn
                if(!(piece.doesSlide()))
                {
                    return;
                }
            }
            // if there is a piece on board
            else
            {
                // we check if the piece(to be moved) is an enemy of the target position piece
                if(isEnemy(piece, target))
                {
                    // if the both the pieces are enemies and piece to be moved is not a Pawn
                    if(!(piece instanceof Pawn))
                    {
                        // we add the position of a target as possible move(green) or create a new list if is empty
                        potentialMoves.add(new Cell(r,c));
                    }
                }
                if(piece instanceof Pawn p)
                {
                   p.setLocked();
                }

                return;
            }
            //increase the row and col of potential move with offsets for another iteration
            r += rowOff;
            c += colOff;
        }
    }


    public static ArrayList<MoveOperation> getAllPossibleMoveOperations(PieceBoard board, boolean isWhitePlayer)
    {
        ArrayList<MoveOperation> allPossibleMoveOperations = new ArrayList<>();

        Figure[][] copy = Validator.copy(board.getSnapshot().getPiecesOnBoard());

       //for every possible figure of that color, check the available spaces where it can move
        // and construct the MoveOperation

        return null;
    }

    public static ArrayList<Cell> calculateRawMoves(Figure[][] snapshot, Cell cell)
    {
        // initialize an array in which the moves will be stored
        ArrayList<Cell> rawMovesOfACell = new ArrayList<>();

        //initialize the immutable copy of a snapshot.
        Figure[][] snapshotCopy = copy(snapshot);

        // iterate through every pair of the offsets of given figure(cell)
        for(int offsets[] : snapshotCopy[cell.getRow()][cell.getCol()].getOffset())
        {
            //assign the offsets of a pair
            int rowOffset = offsets[0];
            int colOffset = offsets[1];

            //increasing/decreasing values by offsets to get the Cell to be checked.
            int localRow = cell.getRow() + rowOffset;
            int localCol = cell.getCol() + colOffset;

            // as long as cords of a cell are in minimum/maximum position of the board, repeat
            while(inBounds(localRow, localCol, snapshotCopy))
            {

                //assign target object to the field in check, we expect figure
                Figure target = snapshotCopy[localRow][localCol];

                //if the figure exists and is the enemy player
                if(target != null && target.getColor() != snapshotCopy[cell.getRow()][cell.getCol()].getColor())
                {
                    //add the cell to moves array and break the loop, because we stop iterating while there is at least one hit.
                    rawMovesOfACell.add(new Cell(localRow, localCol));
                    break;
                }
                // if there is not target, you can move here
                else if(target == null)
                {
                    rawMovesOfACell.add(new Cell(localRow, localCol));
                }
                // if the said figure doesn't slide, flush the list which was filled before.
                if(!snapshot[cell.getRow()][cell.getCol()].doesSlide())
                {
                    return rawMovesOfACell;

                }
                // if there is figure of the same color, flush the list.
                if(target != null && target.getColor() == snapshot[cell.getRow()][cell.getCol()].getColor())
                {
                    return rawMovesOfACell;
                }

                //update the values before starting another iteration
                localRow += rowOffset;
                localCol += colOffset;
            }
        }
        return rawMovesOfACell;
    }

    private static void eliminateCheckPositions(ArrayList<Cell> possibleMoves, Cell initialCell, Figure[][] snapshotGrid)
    {
        //initialize the value of removal
        int indexToRemove = 0;

        //create a list of cells which will be removed
        ArrayList<Cell> cellsToBeRemoved = new ArrayList<>();

        //iterate over every possible move left
        for(Cell cell : possibleMoves)
        {
            //create the snapshot of an original array
            Figure[][] localSnapshotCopy = cloneArray(snapshotGrid);

            //initialize the figure on position where it will be moved from
            Figure potentialMove = localSnapshotCopy[initialCell.getRow()][initialCell.getCol()];
            //simulate the movement
            localSnapshotCopy[cell.getRow()][cell.getCol()] = potentialMove;
            localSnapshotCopy[initialCell.getRow()][initialCell.getCol()] = null;

            //if the cell is subject of attack of at least one figure, get it
            Cell cellToRemove = getTheInCheckPositions(localSnapshotCopy, !potentialMove.getColor());
            //if the cell exists, add it to removal list
            if(cellToRemove != null)
            {
                cellsToBeRemoved.add(possibleMoves.get(indexToRemove));
            }


            indexToRemove++;
        }
        //remove all of the cells which are subject of attacks if movement performed
        possibleMoves.removeAll(cellsToBeRemoved);


    }

    private static Figure[][] cloneArray(Figure[][] array)
    {
        Figure[][] copy = new Figure[array.length][array[0].length];

        for(int row = 0; row < copy.length; row++)
        {
            copy[row] = array[row].clone();
        }

        return copy;
    }

    // get the King of a given Player from given board
    public static Cell getTheKingCell(Figure[][] snapshot, boolean ofPlayer)
    {
        //iterate through every possible figure on board
        for(int row = 0; row < snapshot.length; row++)
        {
            for(int col = 0; col < snapshot[row].length; col++)
            {
                //if the King is found and it is of given color, then return the Cell of these cords.
                if(snapshot[row][col] instanceof King k && k.getColor() == ofPlayer)
                {
                    return new Cell(row, col);
                }
            }
        }
        //if no king found, return empty object.
        return null;
    }

    //returns the Cell which is subject of attack of a king
    public static Cell getTheInCheckPositions(Figure[][] snapshotGrid, boolean enemy)
    {
        //row position helper
        int r = 0;

        //iterate through every row of a grid
        for(Figure[] pieceRow : snapshotGrid)
        {
            //col position helper
            int c = 0;

            //iterate through every piece of a extracted row
            for(Figure piece : pieceRow)
            {
                //change enemy to something more logical
                //if piece is not there or is of the same color as current player
                if(piece == null || piece.getColor() != enemy)
                {
                    //continue with the column iteration
                    c++;
                    continue;
                }

                //assign the offset of current's player enemy piece to a variable
                var x = piece.getOffset();

                //if it is a Pawn, reasign the value to the Pawn beating offset
                if(piece instanceof Pawn p)
                {
                    x = p.getBeatingOffset();
                }

                //iterate over every offset of an enemy figure
                for(int[] offsets : x)
                {
                    //create helper variables representing local cords
                    int localRowOffset = r + offsets[0];
                    int localColOffset = c + offsets[1];

                    //as long as it is in limits continue
                    while(inBounds(localRowOffset, localColOffset, snapshotGrid))
                    {

                        //assign the target figure
                        Figure target = snapshotGrid[localRowOffset][localColOffset];

                        //if the target is there and is the same color as enemy break the loop
                        if(target != null && target.getColor() == piece.getColor()) break;
                        //if target figure is there and is of different colors, and is a king
                        if(target != null && target.getColor() != piece.getColor())
                        {
                            if(target instanceof King)
                            {
                                //return the Cell which attacks king defining check
                                return new Cell(localRowOffset, localColOffset); //it is enough that only one figure declares possible hit.
                            }
                            else
                                break;

                        }

                        //if piece has only one move (doesnt slide) break the loop
                        if(!piece.doesSlide())
                        {
                            break;
                        }
                        //update the values with the same offsets and keep iteration going
                        localRowOffset += offsets[0];
                        localColOffset += offsets[1];
                    }
                }
                //update the column iteration
                c++;
            }
            //update the row iteration
            r++;
        }
        return null;
    }

    public static ArrayList<Cell> filterIllegalMoves(ArrayList<Cell> rawMoves, Figure[][] snapshot, Cell movingFromCell)
    {
        if(movingFromCell == null) return null;
        boolean color = snapshot[movingFromCell.getRow()][movingFromCell.getCol()].getColor();

        for(Cell rawMove : rawMoves)
        {
            Figure[][] snapshotCopy = copy(snapshot);

            Figure f = snapshotCopy[movingFromCell.getRow()][movingFromCell.getCol()];
            snapshotCopy[movingFromCell.getRow()][movingFromCell.getCol()] = null;
            snapshotCopy[rawMove.getRow()][rawMove.getCol()] = f;

            if(RuleApplicator.isCheckmate(snapshotCopy, color))
            {
                rawMoves.remove(rawMove);
            }
        }
        return rawMoves;
    }

    public static Figure[][] copy(Figure[][] sourceArray)
    {
        //create new array which will keep the references
        Figure[][] newArray = new Figure[sourceArray.length][sourceArray[0].length];

        for(int i = 0; i < sourceArray.length; i++)
        {
            //copy the rows of an original array into the new array
            System.arraycopy(sourceArray[i], 0, newArray[i], 0, sourceArray[i].length);
        }
        return newArray;
    }


    private static boolean isEnemy(Figure fig1, Figure fig2)
    {
        return fig2 != null && fig1.getColor() != fig2.getColor();
    }

    private static boolean inBounds(int rowPos, int colPos, Figure[][] originalGrid)
    {
        if(rowPos < 0 || rowPos >= originalGrid.length) return false;
        return colPos >= 0 && colPos < originalGrid.length;
    }

}

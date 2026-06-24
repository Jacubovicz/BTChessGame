package org.portfolio.benttchessgame.chessengine;

import UILogic.helperclasses.EnumColors;
import org.portfolio.benttchessgame.figures.Figure;
import org.portfolio.benttchessgame.figures.King;
import org.portfolio.benttchessgame.figures.Pawn;

import java.util.ArrayList;
import java.util.HashMap;


public final class Validator {

    private Validator(){};

    public static HashMap<EnumColors, ArrayList<Cell>> checkMoves(int row, int col, PieceBoard pieceBoard, PieceBoardManager pieceBoardManager, Session currentSession, HashMap<EnumColors, ArrayList<Cell>> paletteMap)
    {
     //   HashMap<EnumColors, ArrayList<Cell>> paletteMap = new HashMap<>();
        ArrayList<Cell> possibleMoves;

        Cell initialCell = new Cell(row, col);
        paletteMap.computeIfAbsent(EnumColors.YELLOW, k-> new ArrayList<>()).add(initialCell);
        Figure[][] snapshotGrid = pieceBoard.getSnapshot().getPiecesOnBoard(); //check later

        Figure piece = snapshotGrid[initialCell.getRow()][initialCell.getCol()];

        if(piece == null)
        {
            possibleMoves = new ArrayList<>();
            ArrayList<Cell> finalPossibleMoves = possibleMoves;
            paletteMap.computeIfAbsent(EnumColors.GREEN, k -> finalPossibleMoves);

            return null;
        }
        possibleMoves = new ArrayList<>();
        ArrayList<Cell> finalPossibleMoves1 = possibleMoves;
        paletteMap.computeIfAbsent(EnumColors.GREEN, k -> finalPossibleMoves1);

        for(int[] offset : piece.getOffset())
        {
            addMovesInDirection(initialCell.getRow(), initialCell.getCol(), offset[0], offset[1], piece, pieceBoard, paletteMap);
        }
        possibleMoves = RuleApplicator.apply(initialCell.getRow(), initialCell.getCol(), possibleMoves, snapshotGrid, currentSession, paletteMap);

        //ArrayList<Cell> finalPossibleMoves2 = possibleMoves;
        possibleMoves = checkForCheckmate(possibleMoves, initialCell, snapshotGrid);
        paletteMap.put(EnumColors.GREEN, possibleMoves);

      //  paletteMap.put(EnumColors.BLANK, pieceBoardManager.fillTheStarterMoves(currentSession.getTurn()));

        return paletteMap;
    }

    private static ArrayList<Cell> checkForCheckmate(ArrayList<Cell> possibleMoves, Cell initialCell, Figure[][] snapshotGrid)
    {
        int indexToRemove = 0;
        ArrayList<Cell> cellsToBeRemoved = new ArrayList<>();

        for(Cell cell : possibleMoves)
        {
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
        possibleMoves.removeAll(cellsToBeRemoved);

        return possibleMoves;
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

    private static Cell lookForKing(Figure[][] snapshotGrid, boolean enemy)
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

                var x = piece.getOffset();
                if(piece instanceof Pawn p)
                {
                    x = p.getBeatingOffset();
                }

                for(int[] offsets : x)
                {
                    int localRowOffset = r + offsets[0];
                    int localColOffset = c + offsets[1];
                    while(inBounds(localRowOffset, localColOffset, snapshotGrid))
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

    public static void addMovesInDirection(int row, int col, int rowOff, int colOff, Figure piece, PieceBoard pieceBoard, HashMap<EnumColors, ArrayList<Cell>> paletteMap) {

        ArrayList<Cell> possibleMoves = new ArrayList<>();

        if(piece instanceof Pawn p)
        {
            if(p.isLocked() && p.isFirstMove())
            {
                return;
            }
        }

        int r = row + rowOff;
        int c = col + colOff;

        while(inBounds(r,c, pieceBoard.getPiecesOnBoard()))
        {
            int finalR = r; int finalC = c;
            Figure target = pieceBoard.getPiecesOnBoard()[r][c];

            if(target == null)
            {
                paletteMap.computeIfAbsent(EnumColors.GREEN, k -> new ArrayList<>()).add(new Cell(finalR,finalC));
                //  currentGame.setStarterMoves(r,c,true);// might be unnecessary

                if(!(piece.doesSlide()))
                {
                    return;
                }
            }
            else
            {
                if(isEnemy(piece, target))
                {
                    if(!(piece instanceof Pawn))//Pawn can't attack forward.
                    {
                        paletteMap.computeIfAbsent(EnumColors.GREEN, k -> new ArrayList<>()).add(new Cell(finalR,finalC));
                    //    currentGame.setStarterMoves(r,c,true);//might be unnecesarry
                    }
                    else
                    {
                        ((Pawn) piece).setLocked();
                    }
                }
                return;
            }
            r += rowOff;
            c += colOff;
        }
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

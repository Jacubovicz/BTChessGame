package org.portfolio.benttchessgame.engine.rule;

import org.portfolio.benttchessgame.engine.BoardSquare;
import org.portfolio.benttchessgame.engine.GameSession;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.piece.Piece;
import org.portfolio.benttchessgame.piece.King;
import org.portfolio.benttchessgame.piece.Pawn;

import java.util.ArrayList;


public final class MoveValidator {

    private MoveValidator(){};

    //**
    public static ArrayList<BoardSquare> getLegalMovesOf(int row, int col, GameSession currentGameSession, ChessBoard chessBoard)
    {
        Piece[][] figuresOnBoard = chessBoard.piecesOnBoard;
        Piece piece = figuresOnBoard[row][col];

        if(piece == null) return new ArrayList<>();

        ArrayList<BoardSquare> potentialMoves = new ArrayList<>();

        for(int[] offset : piece.getOffset())
        {
            addMovesInDirection(row,col,offset[0], offset[1],piece, chessBoard, potentialMoves);
        }
        eliminateCheckPositions(potentialMoves, new BoardSquare(row,col), figuresOnBoard);

        return ChessRules.applyRules(row, col, potentialMoves, figuresOnBoard, currentGameSession);


    }

    //for the given cords of the cell,
    public static void addMovesInDirection(int row, int col, int rowOff, int colOff, Piece piece, ChessBoard chessBoard, ArrayList<BoardSquare> potentialMoves) {

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
        while(inBounds(r,c, chessBoard.getPiecesOnBoard()))
        {
            //get the potential target figure from the pieceBoard
            Piece target = chessBoard.getPiecesOnBoard()[r][c];

            //if there is no piece on specific cell
            if(target == null)
            {
                //add the cell to the available(green moves) or create one if it doesn't exist
                potentialMoves.add(new BoardSquare(r,c));

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
                        potentialMoves.add(new BoardSquare(r,c));
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


    public static ArrayList<Move> getAllPossibleMoveOperations(ChessBoard board, boolean isWhitePlayer)
    {
        ArrayList<Move> allPossibleMoves = new ArrayList<>();

        Piece[][] copy = MoveValidator.copy(board.getSnapshot().getPiecesOnBoard());

       //for every possible figure of that color, check the available spaces where it can move
        // and construct the MoveOperation

        return null;
    }

    public static ArrayList<BoardSquare> calculateRawMoves(Piece[][] snapshot, BoardSquare boardSquare)
    {
        // initialize an array in which the moves will be stored
        ArrayList<BoardSquare> rawMovesOfABoardSquare = new ArrayList<>();

        //initialize the immutable copy of a snapshot.
        Piece[][] snapshotCopy = copy(snapshot);

        // iterate through every pair of the offsets of given figure(cell)
        for(int offsets[] : snapshotCopy[boardSquare.getRow()][boardSquare.getCol()].getOffset())
        {
            //assign the offsets of a pair
            int rowOffset = offsets[0];
            int colOffset = offsets[1];

            //increasing/decreasing values by offsets to get the Cell to be checked.
            int localRow = boardSquare.getRow() + rowOffset;
            int localCol = boardSquare.getCol() + colOffset;

            // as long as cords of a cell are in minimum/maximum position of the board, repeat
            while(inBounds(localRow, localCol, snapshotCopy))
            {

                //assign target object to the field in check, we expect figure
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

                //update the values before starting another iteration
                localRow += rowOffset;
                localCol += colOffset;
            }
        }
        return rawMovesOfABoardSquare;
    }

    private static void eliminateCheckPositions(ArrayList<BoardSquare> possibleMoves, BoardSquare initialBoardSquare, Piece[][] snapshotGrid)
    {
        //initialize the value of removal
        int indexToRemove = 0;

        //create a list of cells which will be removed
        ArrayList<BoardSquare> cellsToBeRemoved = new ArrayList<>();

        //iterate over every possible move left
        for(BoardSquare boardSquare : possibleMoves)
        {
            //create the snapshot of an original array
            Piece[][] localSnapshotCopy = cloneArray(snapshotGrid);

            //initialize the figure on position where it will be moved from
            Piece potentialMove = localSnapshotCopy[initialBoardSquare.getRow()][initialBoardSquare.getCol()];
            //simulate the movement
            localSnapshotCopy[boardSquare.getRow()][boardSquare.getCol()] = potentialMove;
            localSnapshotCopy[initialBoardSquare.getRow()][initialBoardSquare.getCol()] = null;

            //if the cell is subject of attack of at least one figure, get it
            BoardSquare boardSquareToRemove = getTheInCheckPositions(localSnapshotCopy, !potentialMove.getColor());
            //if the cell exists, add it to removal list
            if(boardSquareToRemove != null)
            {
                cellsToBeRemoved.add(possibleMoves.get(indexToRemove));
            }


            indexToRemove++;
        }
        //remove all of the cells which are subject of attacks if movement performed
        possibleMoves.removeAll(cellsToBeRemoved);


    }

    private static Piece[][] cloneArray(Piece[][] array)
    {
        Piece[][] copy = new Piece[array.length][array[0].length];

        for(int row = 0; row < copy.length; row++)
        {
            copy[row] = array[row].clone();
        }

        return copy;
    }

    // get the King of a given Player from given board
    public static BoardSquare getTheKingCell(Piece[][] snapshot, boolean ofPlayer)
    {
        //iterate through every possible figure on board
        for(int row = 0; row < snapshot.length; row++)
        {
            for(int col = 0; col < snapshot[row].length; col++)
            {
                //if the King is found and it is of given color, then return the Cell of these cords.
                if(snapshot[row][col] instanceof King k && k.getColor() == ofPlayer)
                {
                    return new BoardSquare(row, col);
                }
            }
        }
        //if no king found, return empty object.
        return null;
    }

    //returns the Cell which is subject of attack of a king
    public static BoardSquare getTheInCheckPositions(Piece[][] snapshotGrid, boolean enemy)
    {
        //row position helper
        int r = 0;

        //iterate through every row of a grid
        for(Piece[] pieceRow : snapshotGrid)
        {
            //col position helper
            int c = 0;

            //iterate through every piece of a extracted row
            for(Piece piece : pieceRow)
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
                        Piece target = snapshotGrid[localRowOffset][localColOffset];

                        //if the target is there and is the same color as enemy break the loop
                        if(target != null && target.getColor() == piece.getColor()) break;
                        //if target figure is there and is of different colors, and is a king
                        if(target != null && target.getColor() != piece.getColor())
                        {
                            if(target instanceof King)
                            {
                                //return the Cell which attacks king defining check
                                return new BoardSquare(localRowOffset, localColOffset); //it is enough that only one figure declares possible hit.
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

    public static ArrayList<BoardSquare> filterIllegalMoves(ArrayList<BoardSquare> rawMoves, Piece[][] snapshot, BoardSquare movingFromBoardSquare)
    {
        if(movingFromBoardSquare == null) return null;
        boolean color = snapshot[movingFromBoardSquare.getRow()][movingFromBoardSquare.getCol()].getColor();

        for(BoardSquare rawMove : rawMoves)
        {
            Piece[][] snapshotCopy = copy(snapshot);

            Piece f = snapshotCopy[movingFromBoardSquare.getRow()][movingFromBoardSquare.getCol()];
            snapshotCopy[movingFromBoardSquare.getRow()][movingFromBoardSquare.getCol()] = null;
            snapshotCopy[rawMove.getRow()][rawMove.getCol()] = f;

            if(ChessRules.isCheckmate(snapshotCopy, color))
            {
                rawMoves.remove(rawMove);
            }
        }
        return rawMoves;
    }

    public static Piece[][] copy(Piece[][] sourceArray)
    {
        //create new array which will keep the references
        Piece[][] newArray = new Piece[sourceArray.length][sourceArray[0].length];

        for(int i = 0; i < sourceArray.length; i++)
        {
            //copy the rows of an original array into the new array
            System.arraycopy(sourceArray[i], 0, newArray[i], 0, sourceArray[i].length);
        }
        return newArray;
    }


    private static boolean isEnemy(Piece fig1, Piece fig2)
    {
        return fig2 != null && fig1.getColor() != fig2.getColor();
    }

    private static boolean inBounds(int rowPos, int colPos, Piece[][] originalGrid)
    {
        if(rowPos < 0 || rowPos >= originalGrid.length) return false;
        return colPos >= 0 && colPos < originalGrid.length;
    }

}

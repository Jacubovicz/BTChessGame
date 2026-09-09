package org.portfolio.benttchessgame.engine.board;

import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.tools.UtilityMethods;
import org.portfolio.benttchessgame.piece.*;

public class ChessBoard {

    public final int INVERSE = -1;
    private final Piece[][] piecesOnBoard;
    public final static int GRID_SIZE = 8;

    private boolean firstDeployment = true;

    public ChessBoard() {
        piecesOnBoard = new Piece[GRID_SIZE][GRID_SIZE];
    }

    public void setupTheBoard() {
        setInitialPositions();
    }

    private ChessBoard(ChessBoard chessBoard) {
        this.piecesOnBoard = UtilityMethods.copyPieceArray(chessBoard.piecesOnBoard);
    }

    private void setInitialPositions() {
        if(!firstDeployment){
            removePiecesOffBoard();
        }
        putPiecesOnBoard();
        firstDeployment = false;
    }

    private void putPiecesOnBoard() {
        for(int i = 0; i < GRID_SIZE; i++) {
            for(int j = 0; j < GRID_SIZE; j++) {
                if(i == 0) {
                    if(j==0 || j == 7)
                    {
                        setPieceAt(i,j,new Rook(PlayerColor.WHITE));
                        setPieceAt(GRID_SIZE - 1,j,new Rook(PlayerColor.BLACK));
                    }
                    else if(j==1 || j == 6)
                    {
                        setPieceAt(i,j,new Knight(PlayerColor.WHITE));
                        setPieceAt(GRID_SIZE - 1,j,new Knight(PlayerColor.BLACK));
                    }
                    else if(j==2 || j== 5)
                    {
                        setPieceAt(i,j,new Bishop(PlayerColor.WHITE));
                        setPieceAt(GRID_SIZE - 1,j,new Bishop(PlayerColor.BLACK));
                    }
                    else if(j == 3)
                    {
                        setPieceAt(i,j,new Queen(PlayerColor.WHITE));
                        setPieceAt(GRID_SIZE - 1,j,new Queen(PlayerColor.BLACK));
                    }
                    else {
                        setPieceAt(i,j,new King(PlayerColor.WHITE));
                        setPieceAt(GRID_SIZE - 1,j,new King(PlayerColor.BLACK));
                    }
                }
                else if( i == 1) {
                    setPieceAt(i,j,new Pawn(PlayerColor.WHITE));
                    setPieceAt(GRID_SIZE - 2,j,new Pawn(PlayerColor.BLACK, INVERSE));
                }
            }
        }
    }

    private void removePiecesOffBoard()
    {
        for(int i = 0; i < GRID_SIZE; i++) {
            for(int j = 0; j < GRID_SIZE; j++) {
                setPieceAt(i, j, null);
            }
        }
    }


    public Piece getPieceAt(int row, int col) {
        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE) {
            return null;
        }
        return piecesOnBoard[row][col];
    }

    public void setPieceAt(int row, int col, Piece piece) {
        if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
            piecesOnBoard[row][col] = piece;
        }
    }

    public ChessBoard getSnapshot() {
        return new ChessBoard(this);
    }
}


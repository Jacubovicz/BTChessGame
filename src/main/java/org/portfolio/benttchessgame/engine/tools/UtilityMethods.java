package org.portfolio.benttchessgame.engine.tools;

import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.piece.Piece;

public class UtilityMethods {

    private UtilityMethods(){}


    public static Piece[][] copyPieceArray(Piece[][] sourceArray)
    {
        Piece[][] newArray = new Piece[sourceArray.length][sourceArray[0].length];

        for(int i = 0; i < sourceArray.length; i++)
        {
            for(int j = 0; j < sourceArray.length; j++)
            {
                if(sourceArray[i][j] != null)
                {
                    Piece oldReference = sourceArray[i][j];
                    Piece newReference = oldReference.copy();
                    newArray[i][j] = newReference;
                }
            }
        }
        return newArray;
    }

    public static String[][] chessBoardToStringArray(ChessBoard chessBoard){
        String[][] representation = new String[8][8];
        for(int r = 0; r < representation.length; r++){
            for(int c = 0; c < representation[r].length; c++){
                Piece p = chessBoard.getPieceAt(r,c);
                representation[r][c] = (p != null) ? p.toString() : null;
            }
        }
        return representation;
    }

}

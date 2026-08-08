package org.portfolio.benttchessgame.engine.tools;

import org.portfolio.benttchessgame.piece.Piece;

public class UtilityMethods {

    private UtilityMethods(){}


    public static Piece[][] copyPieceArray(Piece[][] sourceArray)
    {
        Piece[][] newArray = new Piece[sourceArray.length][sourceArray[0].length];

        for(int i = 0; i < sourceArray.length; i++)
        {
            System.arraycopy(sourceArray[i], 0, newArray[i], 0, sourceArray[i].length);
        }
        return newArray;
    }

}

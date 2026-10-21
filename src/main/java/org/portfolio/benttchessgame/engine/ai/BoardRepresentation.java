package org.portfolio.benttchessgame.engine.ai;

import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.piece.Piece;
import org.portfolio.benttchessgame.piece.PieceType;

public class BoardRepresentation {

    public static final int VERTICAL = 8;
    public static final int HORIZONTAL = 1;

    private static final int WHITE_PAWNS = 0;
    private static final int WHITE_ROOKS = 1;
    private static final int WHITE_KNIGHTS = 2;
    private static final int WHITE_BISHOPS = 3;
    private static final int WHITE_QUEENS = 4;
    private static final int WHITE_KING = 5;

    private static final int BLACK_PAWNS = 6;
    private static final int BLACK_ROOKS = 7;
    private static final int BLACK_KNIGHTS = 8;
    private static final int BLACK_BISHOPS = 9;
    private static final int BLACK_QUEENS = 10;
    private static final int BLACK_KING = 11;

    private static final int WHITE_OCCUPANCY = 12;
    private static final int BLACK_OCCUPANCY = 13;
    private static final int ALL_OCCUPANCY = 14;

    private static final int FIRST_MOVE_ACTIVE = 15;
    private static final int EN_PASSANT_FLAG = 16;

    private static final long KNIGHT_MOVES[] = new long[64];
    private static final long KING_MOVES[] = new long[64];

    long NOT_A = 0xFEFEFEFEFEFEFEFEL;
    long NOT_B = 0xFDFDFDFDFDFDFDFDL;
    long NOT_G = 0xBFBFBFBFBFBFBFBFL;
    long NOT_H = 0x7F7F7F7F7F7F7F7FL;

    private final long[] bitboards;

    public BoardRepresentation(ChessBoard board){
        bitboards = new long[19];
        translateAndFill(board, bitboards);

        prefillTheKnightMoves();
        prefillTheKingMoves();

        bitboards[WHITE_OCCUPANCY] = bitboards[WHITE_PAWNS] | bitboards[WHITE_ROOKS] | bitboards[WHITE_KNIGHTS] | bitboards[WHITE_BISHOPS] | bitboards[WHITE_QUEENS] | bitboards[WHITE_KING];
        bitboards[BLACK_OCCUPANCY] = bitboards[BLACK_PAWNS] | bitboards[BLACK_ROOKS] | bitboards[BLACK_KNIGHTS] | bitboards[BLACK_BISHOPS] | bitboards[BLACK_QUEENS] | bitboards[BLACK_KING];
        bitboards[ALL_OCCUPANCY] = bitboards[WHITE_OCCUPANCY] | bitboards[BLACK_OCCUPANCY];
    }

    public void prefillTheKnightMoves(){
        for (int sq = 0; sq < 64; sq++) {
            long knight = 1L << sq;
            long moves = 0L;

            moves |= (knight << 17) & NOT_A;
            moves |= (knight << 10) & NOT_A & NOT_B;
            moves |= (knight >>>  6) & NOT_A & NOT_B;
            moves |= (knight >>> 15) & NOT_A;
            moves |= (knight << 15) & NOT_H;
            moves |= (knight <<  6) & NOT_G & NOT_H;
            moves |= (knight >>> 10) & NOT_G & NOT_H;
            moves |= (knight >>> 17) & NOT_H;

            KNIGHT_MOVES[sq] = moves;
        }
    }

    public void prefillTheKingMoves(){
        for (int sq = 0; sq < 64; sq++) {
            long king = 1L << sq;
            long moves = 0L;

            moves |= (king << 8);
            moves |= (king >>> 8);
            moves |= (king << 1) & NOT_A;
            moves |= (king >>> 1) & NOT_H;
            moves |= (king << 9) & NOT_A;
            moves |= (king << 7) & NOT_H;
            moves |= (king >>> 7) & NOT_A;
            moves |= (king >>> 9) & NOT_H;

            KING_MOVES[sq] = moves;
        }
    }

    public void translateAndFill(ChessBoard board, long[] bitboards){
        int longBitValue = 1;
        for(int i = 7; i >= 0; i--){
            for(int j = 7; j >= 0; j--){
                Piece piece = board.getPieceAt(i, j);
                if(piece != null){
                    bitboards[Integer.parseInt(piece.toString())] |= longBitValue;
                    if(piece.getType() == PieceType.KING || piece.getType() == PieceType.ROOK || piece.getType() == PieceType.PAWN)
                        bitboards[FIRST_MOVE_ACTIVE] &= ~(longBitValue);
                }
                longBitValue <<= 1;
            }
        }
    }

    public void getBestMove(boolean isWhitePlayer){
        int start = isWhitePlayer ? 0 : 5;
        int end = isWhitePlayer ? 6 : 11;

        for(int i = start; i < end; i++){
            long chosenBit = 1L;
            int amountOfBitsToShift = Long.numberOfTrailingZeros(bitboards[i]);
            int row = 7 - amountOfBitsToShift / 8;
            int col = (amountOfBitsToShift % 8) -1;

            chosenBit <<= amountOfBitsToShift;

            long avlMoves = calcAvlMoves(isWhitePlayer, chosenBit, row, col, i);
        }

    }

    private long calcAvlMoves(boolean isWhitePlayer, long chosenBit, int row, int col, int pieceType){

        long result = 0;

        switch(pieceType){
            case 0,6: result |= addPawnMoves(isWhitePlayer, chosenBit,row, col);
            break;
            case 1,7: result |= addRookMoves(isWhitePlayer, chosenBit, row, col);
            break;
            case 2,8: result |= addKnightMoves(isWhitePlayer, chosenBit, row, col);
            break;
            case 3,9: result |= addBishopMoves(isWhitePlayer, chosenBit, row, col);
            break;
            case 4,10: result |= addQueenMoves(isWhitePlayer, chosenBit, row, col);
            break;
            case 5,11: result |= addKingMoves(isWhitePlayer, chosenBit, row, col);
            break;
        }
        return result;
    }

    private long addKingMoves(boolean isWhitePlayer, long chosenBit, int row, int col){
        return KING_MOVES[Long.numberOfTrailingZeros(chosenBit)] & ~bitboards[WHITE_OCCUPANCY];
    }

    private long addQueenMoves(boolean isWhitePlayer, long chosenBit, int row, int col){

        long avlMoves = 0;
        avlMoves |= addBishopMoves(isWhitePlayer, chosenBit, row, col);
        avlMoves |= addRookMoves(isWhitePlayer, chosenBit, row, col);
        return avlMoves;
    }

    private long addBishopMoves(boolean isWhitePlayer, long chosenBit, int row, int col){

       long avlMoves = 0;

        //add topright moves
        long currentBit = chosenBit;
        int localCol = col;
        int localRow = row;
        while((((currentBit << VERTICAL - HORIZONTAL & ~bitboards[ALL_OCCUPANCY]) != 0) || (currentBit << VERTICAL - HORIZONTAL & bitboards[BLACK_OCCUPANCY]) > 0) && localCol < 8 && localRow > 0){
            currentBit <<= VERTICAL - HORIZONTAL;
            avlMoves |= currentBit;
            localCol++;
            localRow--;
        }

        //add topleft moves
        currentBit = chosenBit;
        localCol = col;
        localRow = row;
        while((((currentBit << VERTICAL + HORIZONTAL & ~bitboards[ALL_OCCUPANCY]) != 0) || (currentBit << VERTICAL + HORIZONTAL & bitboards[BLACK_OCCUPANCY]) >0) && localCol > 0 && localRow > 0){
            currentBit <<= VERTICAL + HORIZONTAL;
            avlMoves |= currentBit;
            localCol--;
            localRow--;
        }
        //add downleft
        currentBit = chosenBit;
        localCol = col;
        localRow = row;
        while((((currentBit >>> VERTICAL - HORIZONTAL & ~bitboards[ALL_OCCUPANCY]) != 0) || (currentBit >>> VERTICAL - HORIZONTAL & bitboards[BLACK_OCCUPANCY]) > 0) && localCol > 0 && localRow < 8){
            currentBit >>>= VERTICAL - HORIZONTAL;
            avlMoves |= currentBit;
            localCol--;
            localRow++;
        }
        //add downright
        currentBit = chosenBit;
        localCol = col;
        localRow = row;
        while((((currentBit >>> VERTICAL + HORIZONTAL & ~bitboards[ALL_OCCUPANCY]) != 0) || (currentBit >>> VERTICAL + HORIZONTAL & bitboards[BLACK_OCCUPANCY]) > 0) && localCol < 8 && localRow < 8) {
            currentBit <<= VERTICAL;
            avlMoves |= currentBit;
            localRow--;
        }
       return avlMoves;
    }

    private long addKnightMoves(boolean isWhitePlayer, long chosenBit, int row, int col){
        return KING_MOVES[Long.numberOfTrailingZeros(chosenBit)] & ~bitboards[WHITE_OCCUPANCY];
    }

    private long addPawnMoves(boolean isWhitePlayer, long chosenBit, int row, int col){
        long avlMoves = 0;
        if(isWhitePlayer) {
            avlMoves |= (chosenBit >>> VERTICAL) & ~bitboards[ALL_OCCUPANCY];
            if(avlMoves == 0){
                return avlMoves;
            }
            long firstMove = bitboards[FIRST_MOVE_ACTIVE] & chosenBit;
            if(firstMove != 0 && (chosenBit >>> VERTICAL * 2 & ~bitboards[ALL_OCCUPANCY]) != 0) //second move
            {
                avlMoves |= firstMove >>> (VERTICAL * 2);
            }
            long sidewaysBeats = 1L;
            if(col < 7){
                sidewaysBeats <<= Long.numberOfTrailingZeros(chosenBit) - VERTICAL - HORIZONTAL;
            }
            if(col > 0){
                sidewaysBeats <<= Long.numberOfTrailingZeros(chosenBit) - VERTICAL + HORIZONTAL;
            }
            avlMoves |= sidewaysBeats;
        }
        return avlMoves;
    }

    private long addRookMoves(boolean isWhitePlayer, long chosenBit, int row, int col){ //The version as of now is MVP
        long avlMoves = 0;

        //add right moves
        long currentBit = chosenBit;
        int localCol = col;
        while((((currentBit >>> HORIZONTAL & ~bitboards[ALL_OCCUPANCY]) != 0) || (currentBit >>> HORIZONTAL & bitboards[BLACK_OCCUPANCY]) > 0) && localCol < 8){
            currentBit >>>= HORIZONTAL;
            avlMoves |= currentBit;
            localCol++;
        }

        //add left moves
        currentBit = chosenBit;
        localCol = col;
        while((((currentBit << HORIZONTAL & ~bitboards[ALL_OCCUPANCY]) != 0) || (currentBit << HORIZONTAL & bitboards[BLACK_OCCUPANCY]) >0) && localCol > 0){
            currentBit <<= HORIZONTAL;
            avlMoves |= currentBit;
            localCol--;
        }
        //add verticalDown
        currentBit = chosenBit;
        int localRow = row;
        while((((currentBit >>> VERTICAL & ~bitboards[ALL_OCCUPANCY]) != 0) || (currentBit >>> VERTICAL & bitboards[BLACK_OCCUPANCY]) > 0) && localRow < 8){
            currentBit >>>= VERTICAL;
            avlMoves |= currentBit;
            localRow++;
        }
        //add verticalUp
        currentBit = chosenBit;
        localRow = row;
        while((((currentBit << VERTICAL & ~bitboards[ALL_OCCUPANCY]) != 0) || (currentBit << VERTICAL & bitboards[BLACK_OCCUPANCY]) > 0) && localRow > 0){
            currentBit <<= VERTICAL;
            avlMoves |= currentBit;
            localRow--;
        }

        return avlMoves;
    }

    public long getBitBoard(int pieceType){
        return bitboards[pieceType];
    }

    public long getAvlMove(boolean isWhitePlayer, long chosenBit){
        long seeking = isWhitePlayer ? getBitBoard(BoardRepresentation.WHITE_OCCUPANCY) : getBitBoard(BoardRepresentation.BLACK_OCCUPANCY);


        return seeking & ~chosenBit;
    }

    public long getValidMove(boolean isWhiteMovingPlayer, int row, int col){
        long chosenBit = 1L << (8 - row) * 8 - col;
        long seeking = isWhiteMovingPlayer ? getBitBoard(BoardRepresentation.WHITE_OCCUPANCY) : getBitBoard(BoardRepresentation.BLACK_OCCUPANCY);



        return getAvlMove(isWhiteMovingPlayer, chosenBit);
    }
}

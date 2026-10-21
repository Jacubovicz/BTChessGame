package org.portfolio.benttchessgame.engine.ai;

import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.piece.King;
import org.portfolio.benttchessgame.piece.Pawn;
import org.portfolio.benttchessgame.piece.Piece;
import org.portfolio.benttchessgame.piece.Rook;

import java.util.ArrayDeque;

public class SearchBoard {

    private ChessBoard chessBoard;
    private ArrayDeque<StateEvent> stateEvents;

    PlayerColor nowMovingPlayer;

    public SearchBoard(ChessBoard chessBoard){
        this.chessBoard = chessBoard.getSnapshot();
        this.stateEvents = new ArrayDeque<>();
    }

    //the stateEvents are used to RECREATE data. Means: it stores actual data before movement.
    public void move(int fromRow, int fromColumn, int toRow, int toColumn, int turnNum, PlayerColor actualMoving){
        Piece piece = chessBoard.getPieceAt(fromRow, fromColumn);
        if(isEnemyOf(chessBoard.getPieceAt(toRow, toColumn), actualMoving)){
            stateEvents.add(new StateBeatEvent(StateEventEnum.BEAT, toRow, toColumn, chessBoard.getPieceAt(toRow, toColumn), turnNum));
            chessBoard.setPieceAt(toRow, toColumn, null);
        }
        stateEvents.add(new StateMoveEvent(StateEventEnum.MOVE, fromRow, fromColumn, toRow, toColumn, turnNum));
        chessBoard.setPieceAt(toRow, toColumn, chessBoard.getPieceAt(fromRow, fromColumn));
        chessBoard.setPieceAt(fromRow, fromColumn, null);

        if(piece instanceof Pawn pawn){
            if(pawn.isFirstMove() && Math.abs(fromRow - toRow) == 2){
                stateEvents.add(new StatePassantEvent(StateEventEnum.EN_PASSANT_STATE, pawn, false, turnNum));
                pawn.setEnpassantPossible(turnNum);
            }
            if(Math.abs(fromColumn - toColumn) == 1){
                if(isOppositePawnLeftOfPassantPossible(fromRow, fromColumn, toRow, toColumn, turnNum)){
                    stateEvents.add(new StateBeatEvent(StateEventEnum.BEAT, toRow, toColumn-1, chessBoard.getPieceAt(toRow, toColumn-1), turnNum));
                    chessBoard.setPieceAt(toRow, toColumn-1, null);
                }
                else if(isOppositePawnRightOfPassantPossible(fromRow, fromColumn, toRow, toColumn, turnNum)){
                    stateEvents.add(new StateBeatEvent(StateEventEnum.BEAT, toRow, toColumn+1, chessBoard.getPieceAt(toRow, toColumn+1), turnNum));
                    chessBoard.setPieceAt(toRow, toColumn+1, null);
                }
            }
            if(pawn.isFirstMove()){
                stateEvents.add(new StatePawnFirstMoveEvent(StateEventEnum.PAWN_FIRST_MOVE_FLAG, pawn, true, turnNum));
                pawn.disableFirstMove();
            }
            stateEvents.add(new StatePawnRankEvent(StateEventEnum.PAWN_RANK, pawn, pawn.getRank(), turnNum));
            pawn.incTheRank();
            if(Math.abs(fromRow - toRow) == 2){
               pawn.incTheRank();
            }
            //still the promotion to be implemented.
        }
        if(piece instanceof King k){
            stateEvents.add(new StateKingFirstMoveEvent(StateEventEnum.KING_FIRST_MOVE_FLAG, k, false, turnNum));
            k.setDidMove();

            //still need to implement the castling
        }
        if(piece instanceof Rook r){
            stateEvents.add(new StateRookFirstMoveEvent(StateEventEnum.ROOK_FIRST_MOVE_FLAG, r, true, turnNum));
            r.setDidMove();
        }
        stateEvents.add(new StateNowMovingPlayerEvent(StateEventEnum.NOW_MOVING_PLAYER, actualMoving, turnNum));

    }

    public void reverseMove(){
        int turnToReverse = stateEvents.getLast().getTurnNum();
        while(!stateEvents.isEmpty() && stateEvents.peekLast().getTurnNum() == turnToReverse){
            StateEvent event = stateEvents.removeLast();
            switch(event.getStateEventEnum()){
                case StateEventEnum.NOW_MOVING_PLAYER -> nowMovingPlayer = ((StateNowMovingPlayerEvent) event).actualMoving();
                case StateEventEnum.ROOK_FIRST_MOVE_FLAG -> ((StateRookFirstMoveEvent) event).rook().setFirstMove(((StateRookFirstMoveEvent) event).isFirstMove());
                case StateEventEnum.KING_FIRST_MOVE_FLAG -> ((StateKingFirstMoveEvent) event).king().setDidMove(((StateKingFirstMoveEvent) event).didMove());
                case StateEventEnum.PAWN_FIRST_MOVE_FLAG -> ((StatePawnFirstMoveEvent) event).pawn().setFirstMove(((StatePawnFirstMoveEvent) event).isPawnFirstMove());
                case StateEventEnum.PAWN_RANK -> ((StatePawnRankEvent) event).pawn().setRank(((StatePawnRankEvent) event).rank());
                case StateEventEnum.EN_PASSANT_STATE -> ((StatePassantEvent) event).pawn().setEnpassantPossible(((StatePassantEvent) event).turnNum());
                case StateEventEnum.PROMOTION_REPLACEMENT -> throw new UnsupportedOperationException("Not implemented yet.");
                case StateEventEnum.MOVE -> {
                    int fromRow = ((StateMoveEvent)event).fromRow();
                    int fromCol = ((StateMoveEvent)event).fromColumn();
                    int toRow = ((StateMoveEvent)event).toRow();
                    int toCol = ((StateMoveEvent)event).toColumn();

                    chessBoard.setPieceAt(fromRow, fromCol, chessBoard.getPieceAt(toRow, toCol));
                    chessBoard.setPieceAt(toRow, toCol, null);
                }
                case StateEventEnum.BEAT -> {
                    int toRow = ((StateBeatEvent)event).toRow();
                    int toCol = ((StateBeatEvent)event).toColumn();

                    chessBoard.setPieceAt(toRow, toCol, ((StateBeatEvent) event).piece());
                }
                default -> throw new RuntimeException("Wrong StateEvent!");
            }


        }
    }

    private boolean isOppositePawnLeftOfPassantPossible(int fromRow, int fromColumn, int afterMovePawnRow, int afterMovePawnColumn, int turnNum){
       Piece piece = chessBoard.getPieceAt(fromRow, fromColumn-1);
       if(piece instanceof Pawn p && p.getColor() != chessBoard.getPieceAt(afterMovePawnRow, afterMovePawnColumn).getColor()){
           return p.isEnpassantPossible(turnNum);
       }
       return false;
    }

    private boolean isOppositePawnRightOfPassantPossible(int fromRow, int fromColumn, int afterMovePawnRow, int afterMovePawnColumn, int turnNum){
       Piece piece = chessBoard.getPieceAt(fromRow, fromColumn+1);
       if(piece instanceof Pawn p && p.getColor() != chessBoard.getPieceAt(afterMovePawnRow, afterMovePawnColumn).getColor()){
           return p.isEnpassantPossible(turnNum);
       }
       return false;
    }

    private boolean isEnemyOf(Piece piece, PlayerColor otherPiece){
        if(piece != null){
            return piece.getColor() != otherPiece;
        }
        return false;
    }

    public ChessBoard getBoard(){
        return this.chessBoard;
    }
}

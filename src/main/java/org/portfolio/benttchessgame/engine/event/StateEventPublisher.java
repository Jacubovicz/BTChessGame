package org.portfolio.benttchessgame.engine.event;

import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.MoveResult;
import org.portfolio.benttchessgame.engine.player.Player;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.time.TimeSettings;
import org.portfolio.benttchessgame.piece.Piece;

import java.util.ArrayList;
import java.util.List;

import static org.portfolio.benttchessgame.engine.board.ChessBoard.GRID_SIZE;

public class StateEventPublisher {

    private GameEventPublisher eventPublisher;

    public StateEventPublisher(GameEventPublisher eventPublisher){
        this.eventPublisher = eventPublisher;
    }

    public void publishEndGame(String message, PlayerColor winningPlayer, int totalMovePerformed, String winningPlayerName){
        notifyGameOverListener(message, winningPlayer, totalMovePerformed, winningPlayerName);
    }

    public void publishClickedMovedAndAvlMovesChanged(BoardSquare boardSquare, List<BoardSquare> avlMoves) {
        notifyPaintClickedMovedEvent(boardSquare);
        notifyAvlMovesChangedListeners(avlMoves);
    }

    public void publishKingCellAttackedEvent(BoardSquare square) {
        notifyKingCellAttackedEvent(square);
    }

    //This needs to be refactored. All decision should happen in the game engine, this is only the class for state change events
    public void flushMoveResultData(MoveResult moveResult, List<BoardSquare> activeSquares){
        ChessBoard snapshot = moveResult.newState().board().getSnapshot();
        notifyBoardChangedListeners(snapshot);
        if(moveResult.check()){
            if(moveResult.checkedBoardSquare().isPresent()){
                notifyKingCellAttackedEvent(moveResult.checkedBoardSquare().get());
            }
        }
        notifyActivateMoves(moveResult.newState().nowMovingPlayer());
        flushActiveSquares(activeSquares);
    }

    public void publishInitialState(Player player1,Player player2, List<BoardSquare> activeSquares, PlayerColor nowMovingPlayer, ChessBoard chessBoard, TimeSettings timeSettings){
        notifyPlayersChangedListener(player1,player2);
        flushActiveSquares(activeSquares);
        notifyActivateMoves(nowMovingPlayer);
        notifyBoardChangedListeners(chessBoard);
        setTheTimeCounter(timeSettings);
    }

    public void setTheTimeCounter(TimeSettings timeSettings){
        long baseTimeMillis = timeSettings.baseTimeMillis();
        eventPublisher.publishEvent(new TimeCounterEvent(PlayerColor.WHITE, baseTimeMillis));
        eventPublisher.publishEvent(new TimeCounterEvent(PlayerColor.BLACK, baseTimeMillis));
    }

    //

    private void notifyAvlMovesChangedListeners(List<BoardSquare> avlMoves) {
        List<BoardSquare> copy = new ArrayList<>();
        for(BoardSquare boardSquare : avlMoves)
        {
            copy.add(new BoardSquare(boardSquare.getRow(), boardSquare.getCol(), boardSquare.isCastlingCell()));
        }
        eventPublisher.publishEvent(new AvailableMovesChangedEvent(copy));
    }

    private void notifyKingCellAttackedEvent(BoardSquare attackedKing)
    {
        eventPublisher.publishEvent(new KingCellAttackedEvent(attackedKing));
    }

    private void notifyActivateMoves(PlayerColor playerColor)
    {
        eventPublisher.publishEvent(new TurnChangedEvent(playerColor));
    }

    private void flushActiveSquares(List<BoardSquare> squares)
    {
        eventPublisher.publishEvent(new ActiveSquaresChangedEvent(squares));
    }

    private void notifyPaintClickedMovedEvent(BoardSquare boardSquare)
    {
        eventPublisher.publishEvent(new MoveHighlightChangedEvent(boardSquare));
    }

    public void notifyBoardChangedListeners(ChessBoard chessBoard) {

        String[][] representation = new String[GRID_SIZE][GRID_SIZE];
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                Piece p = chessBoard.getPieceAt(r, c);
                representation[r][c] = (p != null) ? p.toString() : null;
            }
        }
        eventPublisher.publishEvent(new BoardChangedEvent(representation));
    }

    private void notifyPlayersChangedListener(Player player1, Player player2)
    {
        eventPublisher.publishEvent(new PlayersChangedEvent(player1, player2));

    }

    private void notifyGameOverListener(String message, PlayerColor winnerColor, int totalMovePerformed, String winningPlayerName) {
        eventPublisher.publishEvent(new GameEndedEvent(message, winnerColor, totalMovePerformed, winningPlayerName));
    }
}

package org.portfolio.benttchessgame.core;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.portfolio.benttchessgame.engine.GameState;
import org.portfolio.benttchessgame.engine.board.BoardManager;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.piece.Rook;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BoardManagerRookTest {

    ChessBoard board;
    GameState gameState;
    GameEventPublisher eventPublisher;
    BoardManager manager;
    Rook rook;
    @BeforeEach
    void init(){

        board = new ChessBoard();
        gameState = new GameState(board, 0, PlayerColor.WHITE,new ArrayList<>());
        eventPublisher = Mockito.mock(GameEventPublisher.class);
        manager = new BoardManager(gameState, eventPublisher);

        rook = new Rook(PlayerColor.WHITE);
        board.setPieceAt(3, 3, rook);
    }

    @ParameterizedTest
    @ValueSource(ints = {4,2})
    void rookMovesHorizontally(int column){
        manager.move(new Move(new BoardSquare(3,3), new BoardSquare(3,column)));

        assertEquals(board.getPieceAt(3,column), rook);
        assertNull(board.getPieceAt(3,3));
    }

    @ParameterizedTest
    @ValueSource(ints = {4,2})
    void rookMovesVertically(int row){
        manager.move(new Move(new BoardSquare(3,3), new BoardSquare(row,3)));

        assertEquals(board.getPieceAt(row,3), rook);
        assertNull(board.getPieceAt(3,3));
    }

    @Test
    void rookStopsBeforeFriendlyPiece(){
        board.setPieceAt(3,4, new Rook(PlayerColor.WHITE));

        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(3,3), new BoardSquare(3,6))));
        assertEquals(board.getPieceAt(3,3), rook);
        assertInstanceOf(Rook.class, board.getPieceAt(3,4));
        assertNotEquals(board.getPieceAt(3,4), rook);
        assertNull(board.getPieceAt(3,5));
    }

    @Test
    void rookCanCaptureEnemyPiece(){
        board.setPieceAt(3,4, new Rook(PlayerColor.BLACK));

        assertDoesNotThrow(() -> manager.move(new Move(new BoardSquare(3,3), new BoardSquare(3,4))));
        assertEquals(board.getPieceAt(3,4), rook);
        assertNull(board.getPieceAt(3,3));
    }

    @Test
    void rookCannotMoveThroughAnotherPiece(){
        board.setPieceAt(3,4, new Rook(PlayerColor.BLACK));

        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(3,3), new BoardSquare(3,5))));
        assertNotNull(board.getPieceAt(3,4));
        assertNotEquals(board.getPieceAt(3,4), rook);
        assertEquals(board.getPieceAt(3,3), rook);
    }

    @ParameterizedTest
    @CsvSource({"2,2", "2,4", "4,2", "4,4"})
    void rookCannotMoveDiagonally(int row, int col){
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(3,3), new BoardSquare(row,col))));
    }
}

package org.portfolio.benttchessgame.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.portfolio.benttchessgame.engine.GameState;
import org.portfolio.benttchessgame.engine.board.BoardManager;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.piece.Knight;
import org.portfolio.benttchessgame.piece.Rook;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BoardManagerKnightTest {


    private final int KNIGHT_ROW = 3;
    private final int KNIGHT_COL = 3;

    ChessBoard board;
    BoardManager manager;
    Knight knight;

    @BeforeEach
    void setUp(){
        board = new ChessBoard();
        GameEventPublisher publisher = Mockito.mock(GameEventPublisher.class);
        GameState gameState = new GameState(board, 0, PlayerColor.WHITE, new ArrayList<>());
        manager = new BoardManager(gameState, publisher);
        knight = new Knight(PlayerColor.WHITE);
        board.setPieceAt(KNIGHT_ROW,KNIGHT_COL, knight);
    }

    @ParameterizedTest
    @CsvSource({"-1,-2", "-2,-1", "-2, 1", "-1,2",
            "1,-2", "2,-1", "2,1", "1,2"})
    void knightHasCorrectLShapedMoves(int offsetRow, int offsetCol){
        assertDoesNotThrow(() -> manager.move(new Move(new BoardSquare(KNIGHT_ROW, KNIGHT_COL), new BoardSquare(KNIGHT_ROW + offsetRow, KNIGHT_COL + offsetCol))));
        assertNull(board.getPieceAt(KNIGHT_ROW, KNIGHT_COL));
        assertNotNull(board.getPieceAt(KNIGHT_ROW + offsetRow, KNIGHT_COL + offsetCol));
        assertEquals(knight, board.getPieceAt(KNIGHT_ROW + offsetRow, KNIGHT_COL + offsetCol));
    }

    @ParameterizedTest
    @CsvSource({"-1,-2", "-2,-1", "-2, 1", "-1,2",
            "1,-2", "2,-1", "2,1", "1,2"})
    void knightCanJumpOverPieces(int offsetRow, int offsetCol){
        board.setPieceAt(KNIGHT_ROW-1, KNIGHT_COL, new Rook(PlayerColor.BLACK));
        board.setPieceAt(KNIGHT_ROW+1, KNIGHT_COL, new Rook(PlayerColor.BLACK));
        board.setPieceAt(KNIGHT_ROW, KNIGHT_COL+1, new Rook(PlayerColor.BLACK));
        board.setPieceAt(KNIGHT_ROW, KNIGHT_COL-1, new Rook(PlayerColor.BLACK));
        assertDoesNotThrow(() -> manager.move(new Move(new BoardSquare(KNIGHT_ROW, KNIGHT_COL), new BoardSquare(KNIGHT_ROW + offsetRow, KNIGHT_COL + offsetCol))));
        assertNull(board.getPieceAt(KNIGHT_ROW, KNIGHT_COL));
        assertNotNull(board.getPieceAt(KNIGHT_ROW + offsetRow, KNIGHT_COL + offsetCol));
        assertEquals(knight, board.getPieceAt(KNIGHT_ROW + offsetRow, KNIGHT_COL + offsetCol));
    }

    @Test
    void knightCanCaptureEnemyPiece(){
        board.setPieceAt(KNIGHT_ROW-2, KNIGHT_COL-1, new Rook(PlayerColor.BLACK));
        assertDoesNotThrow(() -> manager.move(new Move(new BoardSquare(KNIGHT_ROW, KNIGHT_COL), new BoardSquare(KNIGHT_ROW - 2, KNIGHT_COL-1))));
        assertNull(board.getPieceAt(KNIGHT_ROW, KNIGHT_COL));
        assertNotNull(board.getPieceAt(KNIGHT_ROW - 2, KNIGHT_COL-1));
        assertEquals(knight, board.getPieceAt(KNIGHT_ROW - 2, KNIGHT_COL-1));
    }

    @Test
    void knightCannotLandOnFriendlyPiece(){
        board.setPieceAt(KNIGHT_ROW-2, KNIGHT_COL-1, new Rook(PlayerColor.WHITE));
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(KNIGHT_ROW, KNIGHT_COL), new BoardSquare(KNIGHT_ROW - 2, KNIGHT_COL-1))));
        assertEquals(knight, board.getPieceAt(KNIGHT_ROW, KNIGHT_COL));
        assertNotEquals(knight, board.getPieceAt(KNIGHT_ROW - 2, KNIGHT_COL-1));
    }

    @Test
    void knightNearBoardEdgeCannotMoveOffBoard(){
        board.setPieceAt(0, 7, new Rook(PlayerColor.WHITE));
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(0, 7), new BoardSquare(-2, 9))));
    }

    @Test
    void knightInCornerGetsExactlyTwoLegalMoves(){
        board.setPieceAt(0, 7, new Knight(PlayerColor.WHITE));
        assertEquals(2, manager.getValidMoves(new BoardSquare(0, 7)).size());
    }
}

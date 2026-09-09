package org.portfolio.benttchessgame.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.portfolio.benttchessgame.engine.GameState;
import org.portfolio.benttchessgame.engine.board.BoardManager;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.piece.*;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BoardManagerBishopTest {

    final int BISHOP_ROW = 3;
    final int BISHOP_COL = 3;

    ChessBoard board;
    BoardManager manager;
    GameEventPublisher publisher;
    GameState gameState;

    Bishop bishop;

    static Stream<Piece> pieces(){
            return Stream.of(
                    new Pawn(PlayerColor.WHITE),
                    new Bishop(PlayerColor.WHITE),
                    new King(PlayerColor.WHITE),
                    new Queen(PlayerColor.WHITE),
                    new Knight(PlayerColor.WHITE),
                    new Rook(PlayerColor.WHITE)
            );
    }

    @BeforeEach
    void init() {
        board = new ChessBoard();
        publisher = Mockito.mock(GameEventPublisher.class);
        gameState = new GameState(board, 0, PlayerColor.WHITE, new ArrayList<>());
        manager = new BoardManager(gameState, publisher);

        bishop = new Bishop(PlayerColor.WHITE);
        board.setPieceAt(BISHOP_ROW,BISHOP_COL,bishop);
    }

    @ParameterizedTest
    @CsvSource({"2, 2", "2, 4", "4, 2", "4,4"})
    void bishopMovesDiagonally(int row, int col){
        assertDoesNotThrow(() -> manager.move(new Move(new BoardSquare(BISHOP_ROW,BISHOP_COL), new BoardSquare(row,col))));
        assertEquals(bishop, board.getPieceAt(row,col));
        assertNull(board.getPieceAt(BISHOP_ROW,BISHOP_COL));
    }

    @ParameterizedTest
    @CsvSource({"1, 1", "1, 5", "5, 1", "5, 5"})
    void bishopMovesMultipleSquares(int row, int col){
        assertDoesNotThrow(() -> manager.move(new Move(new BoardSquare(BISHOP_ROW,BISHOP_COL), new BoardSquare(row,col))));
        assertEquals(bishop, board.getPieceAt(row,col));
        assertNull(board.getPieceAt(BISHOP_ROW,BISHOP_COL));
    }

    @ParameterizedTest
    @MethodSource("pieces")
    void bishopStopsBeforeFriendlyPiece(Piece piece){
        board.setPieceAt(BISHOP_ROW-1,BISHOP_COL-1,piece);
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(BISHOP_ROW,BISHOP_COL), new BoardSquare(BISHOP_ROW-2,BISHOP_COL-2))));
        assertNull(board.getPieceAt(BISHOP_ROW -2, BISHOP_COL-2));
        assertEquals(piece, board.getPieceAt(BISHOP_ROW-1,BISHOP_COL-1));
        assertEquals(bishop, board.getPieceAt(BISHOP_ROW,BISHOP_COL));
    }

    @Test
    void bishopCanCaptureEnemyPiece(){
        board.setPieceAt(BISHOP_ROW-1,BISHOP_COL-1,new Pawn(PlayerColor.BLACK));
        assertDoesNotThrow(() -> manager.move(new Move(new BoardSquare(BISHOP_ROW,BISHOP_COL), new BoardSquare(BISHOP_ROW-1,BISHOP_COL-1))));
        assertNull(board.getPieceAt(BISHOP_ROW,BISHOP_COL));
        assertEquals(bishop, board.getPieceAt(BISHOP_ROW-1,BISHOP_COL-1));
    }

    @Test
    void bishopCannotMoveBeyondCapturedBlockedPiece(){
        board.setPieceAt(BISHOP_ROW-1,BISHOP_COL-1,new Pawn(PlayerColor.BLACK));
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(BISHOP_ROW,BISHOP_COL), new BoardSquare(BISHOP_ROW-2,BISHOP_COL-2))));
        assertNull(board.getPieceAt(BISHOP_ROW -2, BISHOP_COL-2));
        assertEquals(bishop, board.getPieceAt(BISHOP_ROW,BISHOP_COL));
    }

    @Test
    void bishopCannotMoveHorizontallyAndVertically(){
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(BISHOP_ROW,BISHOP_COL), new BoardSquare(BISHOP_ROW,BISHOP_COL+1))));
        assertNull(board.getPieceAt(BISHOP_ROW,BISHOP_COL+1));
        assertEquals(bishop, board.getPieceAt(BISHOP_ROW,BISHOP_COL));
    }

}

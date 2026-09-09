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
import org.portfolio.benttchessgame.piece.Pawn;
import org.portfolio.benttchessgame.piece.Queen;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BoardManagerQueenTest {

    private final int QUEEN_ROW = 3;
    private final int QUEEN_COL = 3;

    ChessBoard board;
    BoardManager manager;
    GameEventPublisher publisher;
    GameState gameState;
    Queen queen;

    @BeforeEach
    void setup(){
        board = new ChessBoard();
        gameState = new GameState(board, 0, PlayerColor.WHITE, new ArrayList<>());
        publisher = Mockito.mock(GameEventPublisher.class);
        manager = new BoardManager(gameState, publisher);

        queen = new Queen(PlayerColor.WHITE);
        board.setPieceAt(QUEEN_ROW, QUEEN_COL, queen);
    }

    @ParameterizedTest
    @CsvSource({"0,1", "0,-1", "-1,0", "1,0"}) // Rook offsets
    void queenMovesLikeRook(int rowOffset, int colOffset){
        assertDoesNotThrow(() -> manager.move(new Move(new BoardSquare(QUEEN_ROW, QUEEN_COL), new BoardSquare(QUEEN_ROW + rowOffset, QUEEN_COL + colOffset))));
        assertEquals(queen, board.getPieceAt(QUEEN_ROW + rowOffset, QUEEN_COL + colOffset));
        assertNull(board.getPieceAt(QUEEN_ROW, QUEEN_COL));

    }

    @ParameterizedTest
    @CsvSource({"1,1", "1,-1", "-1, 1", "-1,-1"}) // Bishop offsets
    void queenMovesLikeBishop(int rowOffset, int colOffset){
        assertDoesNotThrow(() -> manager.move(new Move(new BoardSquare(QUEEN_ROW, QUEEN_COL), new BoardSquare(QUEEN_ROW + rowOffset, QUEEN_COL + colOffset))));
        assertEquals(queen, board.getPieceAt(QUEEN_ROW + rowOffset, QUEEN_COL + colOffset));
        assertNull(board.getPieceAt(QUEEN_ROW, QUEEN_COL));
    }

    @ParameterizedTest
    @CsvSource({"-1,-2", "-2,-1", "-2, 1", "-1,2",
            "1,-2", "2,-1", "2,1", "1,2"})
    void queenCannotMoveLikeKnight(int rowOffset, int colOffset){
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(QUEEN_ROW, QUEEN_COL), new BoardSquare(QUEEN_ROW + rowOffset, QUEEN_COL + colOffset))));
        assertNull(board.getPieceAt(QUEEN_ROW+rowOffset, QUEEN_COL+colOffset));
        assertNotNull(board.getPieceAt(QUEEN_ROW, QUEEN_COL));
    }

    @Test
    void queenCannotPassThroughPieces(){
        Pawn friendlyPawn = new Pawn(PlayerColor.WHITE);
        board.setPieceAt(QUEEN_ROW+1, QUEEN_COL, friendlyPawn);
        assertThrows(IllegalArgumentException.class, () -> manager.move(new Move(new BoardSquare(QUEEN_ROW, QUEEN_COL), new BoardSquare(QUEEN_ROW+2, QUEEN_COL))));
        assertEquals(friendlyPawn, board.getPieceAt(QUEEN_ROW+1, QUEEN_COL));
        assertEquals(queen, board.getPieceAt(QUEEN_ROW, QUEEN_COL));
    }

    @Test
    void queenCapturesCorrectly(){
        Pawn enemyPawn = new Pawn(PlayerColor.BLACK);
        board.setPieceAt(QUEEN_ROW+1, QUEEN_COL, enemyPawn);
        assertDoesNotThrow(() -> manager.move(new Move(new BoardSquare(QUEEN_ROW, QUEEN_COL), new BoardSquare(QUEEN_ROW+1, QUEEN_COL))));
        assertEquals(queen, board.getPieceAt(QUEEN_ROW+1, QUEEN_COL));
        assertNull(board.getPieceAt(QUEEN_ROW, QUEEN_COL));
    }
}

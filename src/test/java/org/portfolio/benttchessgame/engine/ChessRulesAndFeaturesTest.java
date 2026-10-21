package org.portfolio.benttchessgame.engine;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.board.MoveResult;
import org.portfolio.benttchessgame.engine.board.BoardManager;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.player.AIPlayer;
import org.portfolio.benttchessgame.engine.player.BasePlayer;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.rule.ChessRules;
import org.portfolio.benttchessgame.engine.rule.MoveEvaluator;
import org.portfolio.benttchessgame.engine.time.TimeSettings;
import org.portfolio.benttchessgame.piece.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ChessRulesAndFeaturesTest {

    @Test
    void testStalemateDetection() {
        ChessBoard board = new ChessBoard();
        // Place Black King in corner (7, 0), White King at (5, 1), White Queen at (6, 2)
        // Black King has no legal moves and is not in check -> Stalemate for Black
        board.setPieceAt(7, 0, new King(PlayerColor.BLACK));
        board.setPieceAt(5, 1, new King(PlayerColor.WHITE));
        board.setPieceAt(6, 2, new Queen(PlayerColor.WHITE));

        GameState state = new GameState(board, 10, PlayerColor.BLACK, new ArrayList<>());
        assertTrue(ChessRules.isStalemate(state, PlayerColor.BLACK));
        assertFalse(ChessRules.isCheckmate(state, PlayerColor.BLACK));
    }

    @Test
    void testPromotionToKnightAndRook() {
        ChessBoard board = new ChessBoard();
        Pawn pawn = new Pawn(PlayerColor.WHITE);
        pawn.disableFirstMove();
        // Pawn at row 6, moving to row 7
        board.setPieceAt(6, 0, pawn);

        GameState state = new GameState(board, 10, PlayerColor.WHITE, new ArrayList<>());
        BoardManager manager = new BoardManager(state, Mockito.mock(GameEventPublisher.class));

        MoveResult resultKnight = manager.move(new Move(new BoardSquare(6, 0), new BoardSquare(7, 0), PieceType.KNIGHT));
        Piece promotedKnight = resultKnight.newState().board().getPieceAt(7, 0);
        assertNotNull(promotedKnight);
        assertInstanceOf(Knight.class, promotedKnight);
        assertEquals(PlayerColor.WHITE, promotedKnight.getColor());

        // Test rook promotion
        ChessBoard board2 = new ChessBoard();
        Pawn pawn2 = new Pawn(PlayerColor.WHITE);
        pawn2.disableFirstMove();
        board2.setPieceAt(6, 1, pawn2);
        GameState state2 = new GameState(board2, 10, PlayerColor.WHITE, new ArrayList<>());
        BoardManager manager2 = new BoardManager(state2, Mockito.mock(GameEventPublisher.class));

        MoveResult resultRook = manager2.move(new Move(new BoardSquare(6, 1), new BoardSquare(7, 1), PieceType.ROOK));
        Piece promotedRook = resultRook.newState().board().getPieceAt(7, 1);
        assertNotNull(promotedRook);
        assertInstanceOf(Rook.class, promotedRook);
        assertEquals(PlayerColor.WHITE, promotedRook.getColor());
    }

    @Test
    void testMoveEvaluator() {
        ChessBoard board = new ChessBoard();
        board.setPieceAt(0, 0, new Queen(PlayerColor.WHITE));
        board.setPieceAt(7, 7, new Pawn(PlayerColor.BLACK));

        int score = MoveEvaluator.eval(board);
        // Queen = 90, Pawn = 10 -> score = 90 - 10 = 80
        assertEquals(80, score);
    }

    @Test
    void testAIGameBackendInitializationAndMove() {
        GameEventPublisher publisher = Mockito.mock(GameEventPublisher.class);
        BasePlayer human = new BasePlayer("Human");
        human.setColor(PlayerColor.WHITE);
        AIPlayer ai = new AIPlayer("Computer");
        ai.setColor(PlayerColor.BLACK);

        GameConfiguration config = new GameConfiguration(human, ai, GameMode.PvAI, TimeSettings.standard());
        AIGameBackend backend = new AIGameBackend();
        backend.initialize(config, publisher);

        assertDoesNotThrow(() -> {
            backend.requestInitialState();
            backend.proposeMove(1, 4);
            backend.moveTheFigure(new BoardSquare(3, 4));
        });
    }
}

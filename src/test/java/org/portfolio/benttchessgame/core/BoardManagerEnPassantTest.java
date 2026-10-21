package org.portfolio.benttchessgame.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.portfolio.benttchessgame.engine.GameState;
import org.portfolio.benttchessgame.engine.board.BoardManager;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.event.GameEventPublisher;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;
import org.portfolio.benttchessgame.engine.ChessEngineWorker;
import org.portfolio.benttchessgame.piece.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BoardManagerEnPassantTest {

    private ChessBoard board;
    private GameState gameState;
    private BoardManager manager;
    private GameEventPublisher publisher;

    @BeforeEach
    void setUp() {
        board = new ChessBoard();
        publisher = Mockito.mock(GameEventPublisher.class);
        // Start at move 10 to test en passant turn logic
        gameState = new GameState(board, 10, PlayerColor.WHITE, new ArrayList<>());
        manager = new BoardManager(gameState, publisher);

        // Initialize AppContext
        GameEventBus eventBus = Mockito.mock(GameEventBus.class);
        ChessEngineWorker engineWorker = Mockito.mock(ChessEngineWorker.class);
        AppContext context = new AppContext(eventBus, engineWorker);
        AppContext.init(context);
    }

    @Test
    void enPassantAvailableImmediatelyAfterDoublePawnMove() {
        Pawn whitePawn = new Pawn(PlayerColor.WHITE);
        Pawn blackPawn = new Pawn(PlayerColor.BLACK, -1);
        board.setPieceAt(4, 4, whitePawn);
        board.setPieceAt(6, 5, blackPawn);
        whitePawn.disableFirstMove();

        // Black double moves from (6, 5) to (4, 5)
        manager.setState(new GameState(board, 10, PlayerColor.BLACK, new ArrayList<>()));
        manager.move(new Move(new BoardSquare(6, 5), new BoardSquare(4, 5)));
        
        // It's now White's turn (move 11)
        manager.setState(new GameState(board, 11, PlayerColor.WHITE, new ArrayList<>()));
        
        assertTrue(manager.getValidMoves(new BoardSquare(4, 4)).contains(new BoardSquare(5, 5)));
    }

    @Test
    void correctPawnIsCaptured() {
        Pawn whitePawn = new Pawn(PlayerColor.WHITE);
        Pawn blackPawn = new Pawn(PlayerColor.BLACK, -1);
        board.setPieceAt(4, 4, whitePawn);
        board.setPieceAt(6, 5, blackPawn);
        whitePawn.disableFirstMove();

        manager.setState(new GameState(board, 10, PlayerColor.BLACK, new ArrayList<>()));
        manager.move(new Move(new BoardSquare(6, 5), new BoardSquare(4, 5)));
        
        manager.setState(new GameState(board, 11, PlayerColor.WHITE, new ArrayList<>()));
        manager.move(new Move(new BoardSquare(4, 4), new BoardSquare(5, 5)));
        
        assertNull(board.getPieceAt(4, 5)); // Black pawn at (4, 5) should be gone
    }

    @Test
    void capturingPawnEndsOnCorrectSquare() {
        Pawn whitePawn = new Pawn(PlayerColor.WHITE);
        Pawn blackPawn = new Pawn(PlayerColor.BLACK, -1);
        board.setPieceAt(4, 4, whitePawn);
        board.setPieceAt(6, 5, blackPawn);
        whitePawn.disableFirstMove();

        manager.setState(new GameState(board, 10, PlayerColor.BLACK, new ArrayList<>()));
        manager.move(new Move(new BoardSquare(6, 5), new BoardSquare(4, 5)));
        
        manager.setState(new GameState(board, 11, PlayerColor.WHITE, new ArrayList<>()));
        manager.move(new Move(new BoardSquare(4, 4), new BoardSquare(5, 5)));
        
        assertEquals(whitePawn, board.getPieceAt(5, 5));
    }

    @Test
    void enPassantUnavailableAfterAnotherMoveHappens() {
        Pawn whitePawn = new Pawn(PlayerColor.WHITE);
        Pawn blackPawn = new Pawn(PlayerColor.BLACK, -1);
        board.setPieceAt(4, 4, whitePawn);
        board.setPieceAt(6, 5, blackPawn);
        whitePawn.disableFirstMove();

        // Black double moves
        manager.setState(new GameState(board, 10, PlayerColor.BLACK, new ArrayList<>()));
        manager.move(new Move(new BoardSquare(6, 5), new BoardSquare(4, 5)));
        
        // White does another move instead
        board.setPieceAt(0, 0, new King(PlayerColor.WHITE));
        manager.setState(new GameState(board, 11, PlayerColor.WHITE, new ArrayList<>()));
        manager.move(new Move(new BoardSquare(0, 0), new BoardSquare(1, 0)));
        
        // Next move (12), even if it was White's turn again (impossible but for testing), en passant is gone
        manager.setState(new GameState(board, 12, PlayerColor.WHITE, new ArrayList<>()));
        assertFalse(manager.getValidMoves(new BoardSquare(4, 4)).contains(new BoardSquare(5, 5)));
    }

    @Test
    void unavailableAfterSingleSquarePawnMove() {
        Pawn whitePawn = new Pawn(PlayerColor.WHITE);
        Pawn blackPawn = new Pawn(PlayerColor.BLACK, -1);
        board.setPieceAt(4, 4, whitePawn);
        board.setPieceAt(5, 5, blackPawn);
        whitePawn.disableFirstMove();

        // Black single moves
        manager.setState(new GameState(board, 10, PlayerColor.BLACK, new ArrayList<>()));
        manager.move(new Move(new BoardSquare(5, 5), new BoardSquare(4, 5)));
        
        manager.setState(new GameState(board, 11, PlayerColor.WHITE, new ArrayList<>()));
        assertFalse(manager.getValidMoves(new BoardSquare(4, 4)).contains(new BoardSquare(5, 5)));
    }

    @Test
    void unavailableAgainstUnrelatedPawn() {
        Pawn whitePawn = new Pawn(PlayerColor.WHITE);
        Pawn blackPawn = new Pawn(PlayerColor.BLACK, -1);
        board.setPieceAt(4, 4, whitePawn);
        board.setPieceAt(6, 0, blackPawn); // Far away
        whitePawn.disableFirstMove();

        manager.setState(new GameState(board, 10, PlayerColor.BLACK, new ArrayList<>()));
        manager.move(new Move(new BoardSquare(6, 0), new BoardSquare(4, 0)));
        
        manager.setState(new GameState(board, 11, PlayerColor.WHITE, new ArrayList<>()));
        assertFalse(manager.getValidMoves(new BoardSquare(4, 4)).contains(new BoardSquare(5, 5)));
    }

    @Test
    void illegalIfEnPassantExposesYourOwnKing() {
        Pawn whitePawn = new Pawn(PlayerColor.WHITE);
        Pawn blackPawn = new Pawn(PlayerColor.BLACK, -1);
        board.setPieceAt(4, 4, whitePawn);
        board.setPieceAt(6, 5, blackPawn);
        whitePawn.disableFirstMove();
        
        // Place King at (4, 0) and enemy Rook at (4, 7). 
        // If (4, 4) white pawn captures (4, 5) black pawn via en passant, row 4 is cleared!
        board.setPieceAt(4, 0, new King(PlayerColor.WHITE));
        board.setPieceAt(4, 7, new Rook(PlayerColor.BLACK));

        manager.setState(new GameState(board, 10, PlayerColor.BLACK, new ArrayList<>()));
        manager.move(new Move(new BoardSquare(6, 5), new BoardSquare(4, 5)));
        
        manager.setState(new GameState(board, 11, PlayerColor.WHITE, new ArrayList<>()));
        assertFalse(manager.getValidMoves(new BoardSquare(4, 4)).contains(new BoardSquare(5, 5)));
    }
}

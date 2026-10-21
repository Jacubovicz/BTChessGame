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

public class BoardManagerPromotionTest {

    private ChessBoard board;
    private GameState gameState;
    private BoardManager manager;
    private GameEventPublisher publisher;

    @BeforeEach
    void setUp() {
        board = new ChessBoard();
        publisher = Mockito.mock(GameEventPublisher.class);
        gameState = new GameState(board, 10, PlayerColor.WHITE, new ArrayList<>());
        manager = new BoardManager(gameState, publisher);

        // Initialize AppContext
        GameEventBus eventBus = Mockito.mock(GameEventBus.class);
        ChessEngineWorker engineWorker = Mockito.mock(ChessEngineWorker.class);
        AppContext context = new AppContext(eventBus, engineWorker);
        AppContext.init(context);
    }

    @Test
    void whitePawnPromotesOnCorrespondingRank() {
        Pawn p = new Pawn(PlayerColor.WHITE);
        // Rank 1 initially. We want it to reach rank 7.
        // It's currently at row 6. Moving to row 7 will make it rank 7.
        for(int i=0; i<5; i++) p.incTheRank(); // Now rank 6
        board.setPieceAt(6, 4, p);
        
        manager.move(new Move(new BoardSquare(6, 4), new BoardSquare(7, 4)));
        
        assertInstanceOf(Queen.class, board.getPieceAt(7, 4));
    }

    @Test
    void cannotPromoteBeforeFinalRank() {
        Pawn p = new Pawn(PlayerColor.WHITE);
        board.setPieceAt(1, 4, p);
        
        manager.move(new Move(new BoardSquare(1, 4), new BoardSquare(2, 4)));
        
        assertInstanceOf(Pawn.class, board.getPieceAt(2, 4));
    }

    @Test
    void promotionAfterCaptureWorks() {
        Pawn p = new Pawn(PlayerColor.WHITE);
        for(int i=0; i<5; i++) p.incTheRank(); // Now rank 6
        board.setPieceAt(6, 4, p);
        board.setPieceAt(7, 5, new Rook(PlayerColor.BLACK));
        
        manager.move(new Move(new BoardSquare(6, 4), new BoardSquare(7, 5)));
        
        assertInstanceOf(Queen.class, board.getPieceAt(7, 5));
    }

    @Test
    void pawnIsActuallyReplacedByPromotedPiece() {
        Pawn p = new Pawn(PlayerColor.WHITE);
        for(int i=0; i<5; i++) p.incTheRank();
        board.setPieceAt(6, 4, p);
        
        manager.move(new Move(new BoardSquare(6, 4), new BoardSquare(7, 4)));
        
        Piece result = board.getPieceAt(7, 4);
        assertFalse(result instanceof Pawn);
        assertEquals(PlayerColor.WHITE, result.getColor());
    }
    
    @Test
    void canPromoteToRook() {
        Pawn p = new Pawn(PlayerColor.WHITE);
        for(int i=0; i<5; i++) p.incTheRank();
        board.setPieceAt(6, 4, p);
        
        manager.move(new Move(new BoardSquare(6, 4), new BoardSquare(7, 4), PieceType.ROOK));
        
        assertInstanceOf(Rook.class, board.getPieceAt(7, 4));
    }

    @Test
    void canPromoteToBishop() {
        Pawn p = new Pawn(PlayerColor.WHITE);
        for(int i=0; i<5; i++) p.incTheRank();
        board.setPieceAt(6, 4, p);
        
        manager.move(new Move(new BoardSquare(6, 4), new BoardSquare(7, 4), PieceType.BISHOP));
        
        assertInstanceOf(Bishop.class, board.getPieceAt(7, 4));
    }

    @Test
    void canPromoteToKnight() {
        Pawn p = new Pawn(PlayerColor.WHITE);
        for(int i=0; i<5; i++) p.incTheRank();
        board.setPieceAt(6, 4, p);
        
        manager.move(new Move(new BoardSquare(6, 4), new BoardSquare(7, 4), PieceType.KNIGHT));
        
        assertInstanceOf(Knight.class, board.getPieceAt(7, 4));
    }
}

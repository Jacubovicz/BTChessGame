package org.portfolio.benttchessgame.ui.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.portfolio.benttchessgame.engine.ChessEngineWorker;
import org.portfolio.benttchessgame.engine.event.*;
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;
import org.portfolio.benttchessgame.engine.player.Player;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.piece.PieceType;
import org.portfolio.benttchessgame.ui.view.BoardPainter;
import org.portfolio.benttchessgame.ui.view.BoardRenderer;
import org.portfolio.benttchessgame.ui.view.PromotionDialog;
import org.portfolio.benttchessgame.core.BoardSquare;
import org.portfolio.benttchessgame.engine.command.MovePieceCommand;
import org.portfolio.benttchessgame.engine.command.RequestLegalMovesCommand;
import org.portfolio.benttchessgame.core.AppContext;
import org.portfolio.benttchessgame.service.NavigationService;

import java.util.Objects;

public class GameBoardController {

    private final AppContext appContext;
    private final NavigationService navigationService;

    private ChessEngineWorker engineRunner;
    private BoardPainter boardPainter;
    private BoardRenderer boardRenderer;
    private GameBoardInitializer initializer;

    private String[][] currentBoard;
    private int selectedRow = -1;
    private int selectedCol = -1;

    @FXML
    private GridPane chessBoard;

    @FXML
    private Label timer;

    @FXML
    private TextField nicknameWhite, nicknameBlack, timeElapsedWhite, timeElapsedBlack;

    @FXML
    private ImageView whitePlayerIcon, blackPlayerIcon, whitesTurnIndicator, blacksTurnIndicator;

    private final SimpleStringProperty timerText = new SimpleStringProperty();
    private final SimpleObjectProperty<Image> whitePlayerIconImage = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<Image> blackPlayerIconImage = new SimpleObjectProperty<>();
    private final SimpleStringProperty whitePlayerName = new SimpleStringProperty();
    private final SimpleStringProperty blackPlayerName = new SimpleStringProperty();

    private StackPane[][] gameBoardCells;

    public GameBoardController(AppContext appContext, NavigationService navigationService) {
        this.appContext = appContext;
        this.navigationService = navigationService;
    }

    @FXML
    public void initialize() {
        gameBoardCells = new StackPane[chessBoard.getRowCount()][chessBoard.getColumnCount()];
        initializer = new GameBoardInitializer(chessBoard.getChildren(), gameBoardCells, this);
        boardPainter = new BoardPainter(gameBoardCells);
        boardRenderer = new BoardRenderer(gameBoardCells, initializer);

        // Bindings
        timer.textProperty().bind(timerText);
        whitePlayerIcon.imageProperty().bind(whitePlayerIconImage);
        blackPlayerIcon.imageProperty().bind(blackPlayerIconImage);
        nicknameWhite.textProperty().bind(whitePlayerName);
        nicknameBlack.textProperty().bind(blackPlayerName);

        wireListeners();
    }

    public void setChessEngineRunner(ChessEngineWorker engineRunner) {
        this.engineRunner = engineRunner;

        whitesTurnIndicator.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/gameview/whites_turn.png")).toExternalForm()));
        blacksTurnIndicator.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/gameview/blacks_turn.png")).toExternalForm()));
    }


    public void proposeMove(StackPane sp, int row, int col) {
        if (!boardPainter.isInAvailableMoves(row, col)) {
            selectedRow = row;
            selectedCol = col;
            engineRunner.post(new RequestLegalMovesCommand(row, col));
        } else {
            PieceType promotionType = null;
            if (isPromotionMove(selectedRow, selectedCol, row, col)) {
                PlayerColor color = row == 7 ? PlayerColor.WHITE : PlayerColor.BLACK;
                promotionType = PromotionDialog.showPromotionDialog(color, chessBoard.getScene() != null ? chessBoard.getScene().getWindow() : null);
            }
            engineRunner.post(new MovePieceCommand(new BoardSquare(row, col), promotionType));
            selectedRow = -1;
            selectedCol = -1;
        }
    }

    private boolean isPromotionMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (currentBoard == null || fromRow < 0 || fromCol < 0 || fromRow >= currentBoard.length || fromCol >= currentBoard[0].length) {
            return false;
        }
        String piece = currentBoard[fromRow][fromCol];
        if (piece == null) return false;
        if (piece.equals("WHITE_PAWN") && toRow == 7) return true;
        if (piece.equals("DARK_PAWN") && toRow == 0) return true;
        return false;
    }

    private void wireListeners() {
        GameEventBus eventBus = appContext.getEventBus();
        eventBus.subscribe(AvailableMovesChangedEvent.class, event -> boardPainter.paintAvlMoves(event.avlMoves()));
        eventBus.subscribe(BoardChangedEvent.class, event -> {
            currentBoard = event.board();
            boardRenderer.render(event.board());
        });
        eventBus.subscribe(TimerChangedEvent.class, event -> timerText.set(event.time()));
        eventBus.subscribe(ActiveSquaresChangedEvent.class, event -> boardPainter.setClickableFields(event.boardSquares()));
        eventBus.subscribe(MoveHighlightChangedEvent.class, event -> boardPainter.paintClickedOrMoved(event.clickedOrMoved()));
        eventBus.subscribe(PlayersChangedEvent.class, event -> setupTheFXPlayers(event.player1(), event.player2()));
        eventBus.subscribe(TurnChangedEvent.class, event -> updateTurnIndicators(event.playerColor()));
        eventBus.subscribe(KingCellAttackedEvent.class, event -> boardPainter.paintKingAttacked(event.attackedKing()));
        eventBus.subscribe(TimeCounterEvent.class, event -> updateTimeCounter(event.playerColor(), event.timeLeft()));
        eventBus.subscribe(GameEndedEvent.class, event -> showGameEndedMessage(event.message(), event.winnerColor(), event.totalMovePerformed(), event.winningPlayerName()));
    }

    private void showGameEndedMessage(String message, PlayerColor winnerColor, int totalMovePerformed, String winningPlayerName){
        navigationService.loadGameOverView(message, winnerColor, totalMovePerformed, winningPlayerName);
    }

    public void updateTimeCounter(PlayerColor playerColor, long timeLeft) {
       if(playerColor == PlayerColor.WHITE){
           timeElapsedWhite.setText(String.valueOf(formatForMinutes(timeLeft/1000)));
       }
       else{
           timeElapsedBlack.setText(String.valueOf(formatForMinutes(timeLeft/1000)));
       }
    }

    public String formatForMinutes(long timeLeft){
        long minutes = timeLeft / 60;
        long seconds = timeLeft % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void updateTurnIndicators(PlayerColor playerColor) {
        whitesTurnIndicator.setVisible(playerColor == PlayerColor.WHITE);
        blacksTurnIndicator.setVisible(playerColor == PlayerColor.BLACK);
    }
    private void setupTheFXPlayers(Player whitePlayer, Player blackPlayer) {
        if (whitePlayer != null) {
            whitePlayerName.set(whitePlayer.getName());
            whitePlayerIconImage.set(new Image(Objects.requireNonNull(getClass().getResource(whitePlayer.getProfileIconPath())).toExternalForm()));
        }
        if (blackPlayer != null) {
            blackPlayerName.set(blackPlayer.getName());
            blackPlayerIconImage.set(new Image(Objects.requireNonNull(getClass().getResource(blackPlayer.getProfileIconPath())).toExternalForm()));
        }
    }
}

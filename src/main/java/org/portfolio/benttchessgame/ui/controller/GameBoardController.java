package org.portfolio.benttchessgame.ui.controller;

import org.portfolio.benttchessgame.ui.view.BoardHighlight;
import org.portfolio.benttchessgame.ui.view.BoardPainter;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.portfolio.benttchessgame.engine.BoardSquare;
import org.portfolio.benttchessgame.engine.ChessEngine;
import org.portfolio.benttchessgame.engine.ChessEngineWorker;
import org.portfolio.benttchessgame.engine.event.*;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.command.MovePieceCommand;
import org.portfolio.benttchessgame.engine.command.RequestLegalMovesCommand;
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;
import org.portfolio.benttchessgame.engine.player.Player;
import org.portfolio.benttchessgame.piece.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class GameBoardController {

   GameBoardInitializer initializer;

    ChessEngineWorker engineRunner;
    ChessEngine engine;
    BoardPainter boardPainter;

    Move operation = new Move();
    HashMap<BoardHighlight, ArrayList<BoardSquare>> paletteMap;

    @FXML
    GridPane chessBoard;

    @FXML
    Label timer, playerOne, playerTwo;

    @FXML
    TextField nickname_white, nickname_black, timeElapsed_white, timeElapsed_black;

    @FXML
    ImageView white_player_icon, black_player_icon, whites_turn_indicator, blacks_turn_indicator;

    boolean turn = true; // true - white, false - dark

    StackPane[][] gameBoard; //this is the physical grid with options of figure placement

    @FXML
    public void initialize()
    {
        ObservableList<Node> list = chessBoard.getChildren();
        gameBoard = new StackPane[chessBoard.getRowCount()][chessBoard.getColumnCount()];
        initializer = new GameBoardInitializer(list, gameBoard, this);

        this.boardPainter = new BoardPainter(gameBoard);

    }

    private void renderTheBoard(Piece[][] fig)
    {
        for(int row = 0; row < fig.length; row++)
        {
            for(int col = 0; col < fig.length; col++)
            {
                ImageView v = (ImageView) gameBoard[row][col].getChildren().getLast();
                v.setImage(null);
                if(fig[row][col] != null)
                {
                    String key = fig[row][col].toString();
                    Image img = initializer.getPieces().get(key);
                    v.setImage(img);
                }
            }
        }
    }

    public void proposeMove(StackPane sp, int row, int col)
    {
        if(!boardPainter.isInAvailableMoves(row, col))
        {
            engineRunner.post(new RequestLegalMovesCommand(row, col));
            return;
        }
        if(boardPainter.isInAvailableMoves(row, col))
        {
            engineRunner.post(new MovePieceCommand(new BoardSquare(row,col)));
        }
    }

    public void setChessEngine(ChessEngine engine) {
        this.engine = engine;
        wireListeners();
        renderTheBoard(engine.getSnapshot());
        engine.fillTheStarterMoves();

        whites_turn_indicator.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/whites_turn.png")).toExternalForm()));
        whites_turn_indicator.setVisible(true);
        blacks_turn_indicator.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/blacks_turn.png")).toExternalForm()));
        blacks_turn_indicator.setVisible(true);
    }

    public void setChessEngineRunner(ChessEngineWorker engineRunner)
    {
        this.engineRunner = engineRunner;
        setChessEngine(engineRunner.getEngine());
    }


    public void wireListeners()
    {
        GameEventBus eventBus = GameEventBus.getInstance();
        eventBus.subscribe(AvailableMovesChangedEvent.class, event -> Platform.runLater(()-> boardPainter.paintAvlMoves(event.avlMoves())));
        eventBus.subscribe(BoardChangedEvent.class, event -> Platform.runLater(() -> renderTheBoard(event.board())));
        eventBus.subscribe(TimerChangedEvent.class, event -> Platform.runLater(() -> timer.setText(event.time())));
        eventBus.subscribe(ActiveSquaresChangedEvent.class, event -> Platform.runLater(() -> boardPainter.setClickableFields(event.boardSquares())));
        eventBus.subscribe(MoveHighlightChangedEvent.class, event -> Platform.runLater(() -> boardPainter.paintClickedOrMoved(event.clickedOrMoved())));
        eventBus.subscribe(PlayersChangedEvent.class, event -> Platform.runLater(() -> setupTheFXPlayers(event.player1(), event.player2())));
    }

    private void setupTheFXPlayers(Player whitePlayer, Player blackPlayer)
    {
        List<Player> players = List.of(whitePlayer, blackPlayer);

        for(Player player : players)
        {
            if (player.getColor()) {
                nickname_white.setText(player.getName());
                white_player_icon.setImage(player.getProfileIcon());

            } else {

                nickname_black.setText(player.getName());
                black_player_icon.setImage(player.getProfileIcon());
            }
        }
    }

    public void setTurn(boolean turn)
    {
        this.turn = turn;
    }

}

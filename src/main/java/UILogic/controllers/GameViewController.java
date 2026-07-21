package UILogic.controllers;

import UILogic.helperclasses.EnumColors;
import UILogic.helperclasses.Painter;
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
import org.portfolio.benttchessgame.chessengine.Cell;
import org.portfolio.benttchessgame.chessengine.ChessEngine;
import org.portfolio.benttchessgame.chessengine.ChessEngineRunner;
import org.portfolio.benttchessgame.chessengine.events.*;
import org.portfolio.benttchessgame.chessengine.movement.MoveOperation;
import org.portfolio.benttchessgame.chessengine.commands.MakeMove;
import org.portfolio.benttchessgame.chessengine.commands.ProposeMove;
import org.portfolio.benttchessgame.chessengine.helperclasses.GameEventBus;
import org.portfolio.benttchessgame.chessengine.players.Player;
import org.portfolio.benttchessgame.figures.Figure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class GameViewController {

   GameViewInitializer initializer;

    ChessEngineRunner engineRunner;
    ChessEngine engine;
    Painter painter;

    MoveOperation operation = new MoveOperation();
    HashMap<EnumColors, ArrayList<Cell>> paletteMap;

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
        initializer = new GameViewInitializer(list, gameBoard, this);

        this.painter = new Painter(gameBoard);

    }

    private void renderTheBoard(Figure[][] fig)
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
        if(!painter.isInAvailableMoves(row, col))
        {
            engineRunner.post(new ProposeMove(row, col));
            return;
        }
        if(painter.isInAvailableMoves(row, col))
        {
            engineRunner.post(new MakeMove(new Cell(row,col)));
        }
    }

    public void setChessEngine(ChessEngine engine) {
        this.engine = engine;
        wireListeners();
        renderTheBoard(engine.getSnapshot());
        engine.fillTheStarterMoves();

        whites_turn_indicator.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/whites_turn.png")).toExternalForm()));
        whites_turn_indicator.setVisible(false);
        blacks_turn_indicator.setImage(new Image(Objects.requireNonNull(getClass().getResource("images/blacks_turn.png")).toExternalForm()));
        blacks_turn_indicator.setVisible(false);
    }

    public void setChessEngineRunner(ChessEngineRunner engineRunner)
    {
        this.engineRunner = engineRunner;
        setChessEngine(engineRunner.getEngine());
    }


    public void wireListeners()
    {
        GameEventBus eventBus = GameEventBus.getInstance();
        eventBus.subscribe(AvlMovesChangedEvent.class, event -> Platform.runLater(()-> painter.paintAvlMoves(event.avlMoves())));
        eventBus.subscribe(BoardChangedEvent.class, event -> Platform.runLater(() -> renderTheBoard(event.board())));
        eventBus.subscribe(OnTimeChangeEvent.class, event -> Platform.runLater(() -> timer.setText(event.time())));
        eventBus.subscribe(ReactivateFieldsEvent.class, event -> Platform.runLater(() -> painter.setClickableFields(event.cells())));
        eventBus.subscribe(PaintClickedMovedEvent.class, event -> Platform.runLater(() -> painter.paintClickedOrMoved(event.clickedOrMoved())));
        eventBus.subscribe(SetupFXPlayersEvent.class, event -> Platform.runLater(() -> setupTheFXPlayers(event.player1(), event.player2())));
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

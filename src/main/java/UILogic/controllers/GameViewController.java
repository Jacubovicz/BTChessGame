package UILogic.controllers;

import UILogic.helperclasses.EnumColors;
import UILogic.helperclasses.Painter;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.portfolio.benttchessgame.chessengine.Cell;
import org.portfolio.benttchessgame.chessengine.ChessEngine;
import org.portfolio.benttchessgame.chessengine.ChessEngineRunner;
import org.portfolio.benttchessgame.chessengine.MoveOperation;
import org.portfolio.benttchessgame.chessengine.commands.MakeMove;
import org.portfolio.benttchessgame.chessengine.commands.ProposeMove;
import org.portfolio.benttchessgame.chessengine.events.AvlMovesChangedEvent;
import org.portfolio.benttchessgame.chessengine.events.BoardChangedEvent;
import org.portfolio.benttchessgame.chessengine.events.OnTimeChangeEvent;
import org.portfolio.benttchessgame.chessengine.helperclasses.GameEventBus;
import org.portfolio.benttchessgame.figures.Figure;

import java.util.ArrayList;
import java.util.HashMap;


public class GameViewController {

   GameViewInitializer initializer;

    ChessEngineRunner engineRunner;
    ChessEngine engine;
    Painter painter;

    MoveOperation operation = new MoveOperation();
    HashMap<EnumColors, ArrayList<Cell>> paletteMap;

    boolean[][] clickableFields;

    @FXML
    GridPane chessBoard;

    @FXML
    Label timer, playerOne, playerTwo;

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
        if(operation.getInitCell() == null || !painter.isInAvailableMoves(row, col))
        {
            painter.removeAvlMvsPaint();
            painter.cleanTheBuffer();
            operation.clear();
            operation.setInitCell(new Cell(row, col));
            painter.addToCleanupBuffer(operation.getInitCell());
            engineRunner.post(new ProposeMove(row, col));
            System.out.println("Init cell obtained");
            return;
        }
        if(operation.getInitCell() != null && operation.getDestCell() == null  && painter.isInAvailableMoves(row, col))
        {
            operation.setDestCell(new Cell(row, col));
            painter.addToCleanupBuffer(operation.getDestCell());
            engineRunner.post(new MakeMove(operation));
            painter.removeAvlMvsPaint();
            painter.paintClickedOrMoved(row, col);
            System.out.println("Dest cell obtained");
        }
    }

    public void setChessEngine(ChessEngine engine) {
        this.engine = engine;
        wireListeners();
        renderTheBoard(engine.getSnapshot());
        engine.fillTheStarterMoves();
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
    }


    public void setTurn(boolean turn)
    {
        this.turn = turn;
    }

}

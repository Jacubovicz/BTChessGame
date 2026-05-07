package UILogic.controllers;



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
import org.portfolio.benttchessgame.figures.Figure;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class GameViewController {

   GameViewInitializer initializer;

    ChessEngineRunner engineRunner;
    ChessEngine engine;
    Painter painter;

    MoveOperation operation = new MoveOperation();

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

        engine.getCurrentGame().addBoardChangedListener(
                board -> Platform.runLater(() -> renderTheBoard(board))
        );

        renderTheBoard(engine.getCurrentGame().getSnapshot());
        activateTheFields(engine.getCurrentGame().getStarterMoves());
        wireListeners();
    }

    public void setChessEngineRunner(ChessEngineRunner engineRunner)
    {
        this.engineRunner = engineRunner;
        setChessEngine(engineRunner.getEngine());
    }

    public void activateTheFields(boolean[][] starterMoves)
    {
        for(int row = 0; row < starterMoves.length; row++)
        {
            for(int col = 0; col < starterMoves[row].length; col++)
            {
                gameBoard[row][col].setDisable(true);
                if(starterMoves[row][col])
                {
                    gameBoard[row][col].setDisable(false);
                }
            }
        }

    }

    public void wireListeners()
    {
        engine.getValidator().addAvlMovesChangedListener(avlMoves -> Platform.runLater(()-> painter.paintAvlMoves(avlMoves)));
        engine.getCurrentGame().addTimeChangeListener((time) -> Platform.runLater(() -> timer.setText(time)));
        engine.getCurrentGame().addStarterMovesAvailableListener((clickableFields) -> Platform.runLater(() -> activateTheFields(clickableFields)));
        engine.getCurrentGame().addTurnListener(this::setTurn);
    }


    public void setTurn(boolean turn)
    {
        this.turn = turn;
    }

}

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


    private final int FIT_WIDTH = 60;
    private final int FIT_HEIGHT = 60;

    public Map<String, Image> pieces = new HashMap<>();

    ChessEngineRunner engineRunner;
    ChessEngine engine;
    Painter painter;

    MoveOperation operation = new MoveOperation();
    Cell moveFrom, moveTo;

    @FXML
    GridPane chessBoard;

    @FXML
    Label timer, playerOne, playerTwo;


    boolean turn = true; // true - white, false - dark

    StackPane[][] gameBoard; //this is the physical grid with options of figure placement
  //  boolean[][] clickableFields;


    @FXML
    public void initialize()
    {
        ObservableList<Node> list = chessBoard.getChildren();
        gameBoard = new StackPane[chessBoard.getRowCount()][chessBoard.getColumnCount()];
        fillTheBoard(list, gameBoard);
        preloadTheImages();

        this.painter = new Painter(gameBoard);

    }


    private void updateTheBoard(Figure[][] fig)
    {

        for(int i = 0; i < fig.length; i++)
        {
            for(int j = 0; j < fig.length; j++)
            {
                ImageView v = (ImageView) gameBoard[i][j].getChildren().getLast();

                v.setImage(null);

                if(fig[i][j] != null)
                {
                    String key = fig[i][j].toString();

                    Image img = pieces.get(key);
                    v.setImage(img);
                }

            }
        }
    }

    //The process of preloading all the figures
    private void preloadTheImages()
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/images/figures/figures_list.txt"))));

        reader.lines().forEach(this::addTheVal);

    }

    //The one step of adding the value to the map of figures
    private void addTheVal(String line)
    {
        String clean = line.replace("\uFEFF", "").trim();
        String lineWithoutPNG = line.replace(".png", "").trim();
        String path = "/images/figures/" + clean;

        var stream = getClass().getResourceAsStream(path);

        if (stream == null) {
            System.out.println("Resource not found: " + path);
            return;
        }
        Image img = new Image(stream);

        this.pieces.put(lineWithoutPNG, img);
    }

    /*
    * Fills the board over GridPane with StackPanes and concrete ImageViews
    * needed later for figure placement.
     */
    public void fillTheBoard(ObservableList<Node> list, StackPane[][] gameBoard)
    {
        this.gameBoard = gameBoard;

        for(Node n : list)
        {
            if(!(n instanceof StackPane sp)) continue;
            Integer r = GridPane.getRowIndex(sp);
            Integer c = GridPane.getColumnIndex(sp);

            ImageView imgView = new ImageView();
            imgView.setFitWidth(FIT_WIDTH);
            imgView.setFitHeight(FIT_HEIGHT);

            sp.getChildren().add(imgView);

            int row = (r==null) ? 0 : r;
            int col = (c==null) ? 0 : c;

            sp.setOnMouseClicked(event -> proposeMove(sp, row, col));
            gameBoard[row][col] = sp;

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
                board -> Platform.runLater(() -> updateTheBoard(board))
        );

        updateTheBoard(engine.getCurrentGame().getSnapshot());
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

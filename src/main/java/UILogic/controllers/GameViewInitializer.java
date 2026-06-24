package UILogic.controllers;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameViewInitializer {

    private final int FIT_WIDTH = 60;
    private final int FIT_HEIGHT = 60;
    public Map<String, Image> pieces = new HashMap<>();
    ObservableList<Node> list;
    StackPane[][] gameBoard;
    GameViewController gameViewController;

    public GameViewInitializer(ObservableList<Node> list, StackPane[][] gameBoard, GameViewController gameViewController)
    {
        this.list = list;
        this.gameBoard = gameBoard;
        this.gameViewController = gameViewController;

        mapTheBoard();
        preloadTheImages();
    }

    public void mapTheBoard()
    {
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

            sp.setOnMouseClicked(event -> gameViewController.proposeMove(sp, row, col));
            //
             sp.setDisable(true);
            //
            gameBoard[row][col] = sp;
        }
    }

    private void preloadTheImages()
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/images/figures/figures_list.txt"))));
        reader.lines().forEach(this::addImage);
    }

    //The one step of adding the value to the map of figures
    private void addImage(String line)
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

    public Map<String, Image> getPieces()
    {
        return this.pieces;
    }
}

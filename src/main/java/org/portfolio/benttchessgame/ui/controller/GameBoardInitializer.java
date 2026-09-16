package org.portfolio.benttchessgame.ui.controller;

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

public class GameBoardInitializer extends AbstractBoardInitializer implements ClickableBoard{

    GameBoardController gameBoardController;

    public GameBoardInitializer(ObservableList<Node> list, StackPane[][] gameBoard, GameBoardController gameBoardController)
    {
        super.list = list;
        super.gameBoard = gameBoard;
        this.gameBoardController = gameBoardController;

        mapTheBoard();
        preloadTheImages();
        setBoardClickable();
    }

    @Override
    public void setBoardClickable() {
        for(Node n : list)
        {
            if(!(n instanceof StackPane sp)) continue;
            Integer r = GridPane.getRowIndex(sp);
            Integer c = GridPane.getColumnIndex(sp);

            int row = (r==null) ? 0 : r;
            int col = (c==null) ? 0 : c;

            sp.setOnMouseClicked(event -> gameBoardController.proposeMove(sp, row, col));
            sp.setDisable(true);

            gameBoard[row][col] = sp;
        }
    }
}

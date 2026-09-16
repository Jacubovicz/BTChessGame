package org.portfolio.benttchessgame.ui.controller;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class ReplayBoardInitializer extends AbstractBoardInitializer{


    public ReplayBoardInitializer(ObservableList<Node> list, StackPane[][] gameBoard) {
        super.list = list;
        super.gameBoard = gameBoard;

        mapTheBoard();
        preloadTheImages();
    }

}

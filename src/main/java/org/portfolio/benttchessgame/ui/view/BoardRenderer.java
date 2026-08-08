package org.portfolio.benttchessgame.ui.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.portfolio.benttchessgame.ui.controller.GameBoardInitializer;

public class BoardRenderer {
    private final StackPane[][] gameBoard;
    private final GameBoardInitializer initializer;

    public BoardRenderer(StackPane[][] gameBoard, GameBoardInitializer initializer) {
        this.gameBoard = gameBoard;
        this.initializer = initializer;
    }

    public void render(String[][] pieces) {
        if (pieces == null) return;
        for (int row = 0; row < pieces.length; row++) {
            for (int col = 0; col < pieces[row].length; col++) {
                ImageView v = (ImageView) gameBoard[row][col].getChildren().get(gameBoard[row][col].getChildren().size() - 1);
                v.setImage(null);
                if (pieces[row][col] != null) {
                    String key = pieces[row][col];
                    Image img = initializer.getPieces().get(key);
                    v.setImage(img);
                }
            }
        }
    }
}

package org.portfolio.benttchessgame.ui.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.piece.PieceType;

import java.util.Objects;

public class PromotionDialog {

    public static PieceType showPromotionDialog(PlayerColor color, Window owner) {

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.setTitle("Pawn Promotion");
        dialog.initStyle(StageStyle.UTILITY);

        final PieceType[] selected = new PieceType[]{PieceType.QUEEN};

        String prefix = color == PlayerColor.WHITE ? "/images/figures/white_" : "/images/figures/dark_";

        PieceType[] types = new PieceType[]{PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};
        String[] names = new String[]{"queen.png", "rook.png", "bishop.png", "knight.png"};

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < types.length; i++) {
            PieceType type = types[i];
            String imgPath = prefix + names[i];
            Button button = new Button();
            try {
                Image img = new Image(Objects.requireNonNull(PromotionDialog.class.getResource(imgPath)).toExternalForm());
                ImageView iv = new ImageView(img);
                iv.setFitWidth(60);
                iv.setFitHeight(60);
                button.setGraphic(iv);
            } catch (Exception e) {
                button.setText(type.name());
            }

            button.setOnAction(e -> {
                selected[0] = type;
                dialog.close();
            });
            buttonBox.getChildren().add(button);
        }

        Label promptLabel = new Label("Choose a piece for pawn promotion:");
        promptLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox layout = new VBox(20, promptLabel, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 25px; -fx-background-color: #2b2b2b;");

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(PromotionDialog.class.getResource("/css/styles.css").toExternalForm());

        dialog.setScene(scene);
        dialog.showAndWait();

        return selected[0];
    }
}

package org.portfolio.benttchessgame.service;

import javafx.stage.Stage;
import org.portfolio.benttchessgame.engine.board.Move;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.ui.controller.RevengeDecision;

import java.util.List;

public interface NavigationService {
    void loadMainMenu();
    void loadBoardView(GameConfiguration gameConfiguration);
    GameConfiguration loadPvpPopUp();
    Object getController(String viewName);
    void setStage(Stage stage);
    RevengeDecision loadGameOverView(String message, PlayerColor winnerColor, int totalMovePerformed, String winningPlayerName);
    void loadReplayView(int totalAmountOfMoves);
}

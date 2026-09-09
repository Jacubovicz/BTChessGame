package org.portfolio.benttchessgame.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.portfolio.benttchessgame.core.AppContext;
import org.portfolio.benttchessgame.engine.GameMode;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.configuration.GameSettings;
import org.portfolio.benttchessgame.engine.player.AIPlayer;
import org.portfolio.benttchessgame.engine.player.BasePlayer;
import org.portfolio.benttchessgame.engine.player.Player;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.engine.player.ServerPlayer;
import org.portfolio.benttchessgame.engine.time.TimeSettings;
import org.portfolio.benttchessgame.service.NavigationService;

public class MainMenuController {

    private final AppContext appContext;
    private final NavigationService navigationService;

    @FXML
    private Button playerVsPlayer;
    @FXML
    private Button playerVsComputer;
    @FXML
    private Button playerVsOnline;
    @FXML
    private Label serverStatus;

    public void initialize(){
        serverStatus.setText("Offline");
        playerVsPlayer.setOnAction((event) -> startTheGameWithPlayer());
        playerVsComputer.setOnAction((event) -> startTheGameWithComputer());
        playerVsOnline.setOnAction((event) -> startTheGameOnServer());
    }

    public MainMenuController(AppContext appContext, NavigationService navigationService) {
        this.appContext = appContext;
        this.navigationService = navigationService;
    }


    public void startTheGameWithComputer() {
        Player whitePlayer = new BasePlayer("Player");
        whitePlayer.setColor(PlayerColor.WHITE);
        Player blackPlayer = new AIPlayer("Computer");
        blackPlayer.setColor(PlayerColor.BLACK);
        GameConfiguration gameConfig = new GameConfiguration(
                whitePlayer,
                blackPlayer,
                GameMode.PvAI,
                TimeSettings.standard()
        );
        navigationService.loadBoardView(gameConfig);
    }


    public void startTheGameWithPlayer() {
        GameConfiguration gameConfiguration = navigationService.loadPvpPopUp();

        if(gameConfiguration != null) {
            navigationService.loadBoardView(gameConfiguration);
        }
    }

    public void startTheGameOnServer() {
        Player whitePlayer = new BasePlayer("Player 1");
        whitePlayer.setColor(PlayerColor.WHITE);
        Player blackPlayer = new ServerPlayer("Online Player");
        blackPlayer.setColor(PlayerColor.BLACK);
        GameConfiguration gameConfig = new GameConfiguration(
                whitePlayer,
                blackPlayer,
                GameMode.PvPOnline,
                TimeSettings.standard()
        );
        navigationService.loadBoardView(gameConfig);
    }
}

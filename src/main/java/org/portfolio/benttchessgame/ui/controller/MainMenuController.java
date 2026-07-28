package org.portfolio.benttchessgame.ui.controller;

import org.portfolio.benttchessgame.ui.ViewNavigator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.portfolio.benttchessgame.engine.ChessEngineWorker;

public class MainMenuController{

    @FXML
    Button playerVsPlayer;
    @FXML
    Button playerVsComputer;
    @FXML
    Button playerVsOnline;
    @FXML
    Label serverStatus;

    private ChessEngineWorker runner;

    ViewNavigator ui;


    @FXML
    public void startTheGameWithComputer()
    {

    }

    @FXML
    public void startTheGameWithPlayer()
    {
        ui = ViewNavigator.getInstance();
        Platform.runLater(() -> ui.loadBoardView());
    }

    @FXML
    public void startTheGameOnServer()
    {

    }

}

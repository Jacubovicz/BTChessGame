package UILogic.controllers;

import UILogic.UIEngine;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.portfolio.benttchessgame.chessengine.ChessEngineRunner;

public class StartViewController implements RunnerAware{

    @FXML
    Button playerVsPlayer;
    @FXML
    Button playerVsComputer;
    @FXML
    Button playerVsOnline;
    @FXML
    Label serverStatus;

    private ChessEngineRunner runner;

    UIEngine ui;


    @FXML
    public void startTheGameWithComputer()
    {

    }

    @FXML
    public void startTheGameWithPlayer()
    {
        ui = UIEngine.getInstance();
        Platform.runLater(() -> ui.setTheGameView());
    }

    @FXML
    public void startTheGameOnServer()
    {

    }

    public void setChessEngineRunner(ChessEngineRunner runner)
    {
        this.runner = runner;
    }


    @Override
    public void setRunner(ChessEngineRunner runner) {

    }

    @Override
    public ChessEngineRunner getRunner() {
        return null;
    }
}

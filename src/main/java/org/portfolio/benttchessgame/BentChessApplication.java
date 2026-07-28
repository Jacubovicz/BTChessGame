package org.portfolio.benttchessgame;

import org.portfolio.benttchessgame.ui.ViewNavigator;
import javafx.application.Application;
import javafx.stage.Stage;
import org.portfolio.benttchessgame.engine.ChessEngine;
import org.portfolio.benttchessgame.engine.ChessEngineWorker;
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;

import java.io.IOException;

public class BentChessApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        GameEventBus globalBus = GameEventBus.getInstance();

        ChessEngineWorker engineRunner = new ChessEngineWorker();
        ChessEngine chessEngine = new ChessEngine();
        engineRunner.setEngine(chessEngine);
        Thread engineRunnerThread = new Thread(engineRunner);
        engineRunnerThread.start();

        ViewNavigator ui = ViewNavigator.init(stage, engineRunner);
        ui.loadMainMenu();
    }

    public static void main(String[] args) {

       launch();
    }
}
package org.portfolio.benttchessgame.core;

import javafx.stage.Stage;
import org.portfolio.benttchessgame.engine.ChessEngine;
import org.portfolio.benttchessgame.engine.ChessEngineWorker;
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;
import org.portfolio.benttchessgame.service.DefaultNavigationService;

public class AppLifecycle {
    private AppContext appContext;

    public void start(Stage stage) {
        // 1. Initialize core components
        GameEventBus eventBus = GameEventBus.getInstance();
        ChessEngineWorker engineRunner = new ChessEngineWorker();
        ChessEngine chessEngine = new ChessEngine(eventBus);
        engineRunner.setEngine(chessEngine);
        
        Thread engineRunnerThread = new Thread(engineRunner);
        engineRunnerThread.setName("ChessEngineThread");
        engineRunnerThread.setDaemon(true);
        engineRunnerThread.start();

        // 2. Initialize context
        appContext = new AppContext(eventBus, engineRunner);
        AppContext.init(appContext);

        // 3. Initialize services
        DefaultNavigationService navigationService = new DefaultNavigationService(appContext);
        navigationService.setStage(stage);
        
        ViewLoader viewLoader = new ViewLoader(appContext, navigationService);
        navigationService.setViewLoader(viewLoader);
        
        appContext.setNavigationService(navigationService);

        // 4. Start UI
        navigationService.loadMainMenu();
    }

    public void stop() {
        // Cleanup if necessary
    }
}

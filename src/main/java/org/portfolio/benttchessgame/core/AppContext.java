package org.portfolio.benttchessgame.core;

import org.portfolio.benttchessgame.engine.ChessEngineWorker;
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;
import org.portfolio.benttchessgame.service.NavigationService;

public class AppContext {
    private static AppContext instance;

    private final GameEventBus eventBus;
    private final ChessEngineWorker engineWorker;
    private NavigationService navigationService;

    public AppContext(GameEventBus eventBus, ChessEngineWorker engineWorker) {
        this.eventBus = eventBus;
        this.engineWorker = engineWorker;
    }

    public static void init(AppContext context) {
        instance = context;
    }

    public static AppContext getAppContext() {
        return instance;
    }

    public GameEventBus getEventBus() {
        return eventBus;
    }

    public ChessEngineWorker getEngineWorker() {
        return engineWorker;
    }

    public NavigationService getNavigationService() {
        return navigationService;
    }

    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }
}

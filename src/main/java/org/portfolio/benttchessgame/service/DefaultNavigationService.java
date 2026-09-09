package org.portfolio.benttchessgame.service;

import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.portfolio.benttchessgame.core.AppContext;
import org.portfolio.benttchessgame.core.ViewLoader;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.ui.controller.RevengeDecision;
import org.portfolio.benttchessgame.ui.controller.GameOverViewController;
import org.portfolio.benttchessgame.ui.controller.PvpPopUpWindowController;
import org.portfolio.benttchessgame.ui.navigation.AppView;
import org.portfolio.benttchessgame.ui.controller.GameBoardController;
import org.portfolio.benttchessgame.engine.command.StartGameCommand;
import org.portfolio.benttchessgame.engine.command.RequestFullInitialGameStateCommand;

import java.util.HashMap;
import java.util.Map;

public class DefaultNavigationService implements NavigationService {
    private Stage stage;
    private ViewLoader viewLoader;
    private final AppContext appContext;
    private final Map<AppView, Scene> scenes = new HashMap<>();
    private final Map<AppView, Object> controllers = new HashMap<>();

    public DefaultNavigationService(AppContext appContext) {
        this.appContext = appContext;
    }

    public void setViewLoader(ViewLoader viewLoader) {
        this.viewLoader = viewLoader;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void loadMainMenu() {
        showView(AppView.MAIN_MENU_VIEW);
    }

    @Override
    public void loadBoardView(GameConfiguration gameConfiguration) {
        showView(AppView.LOADING_VIEW);

        // Pre-load Board view if not already cached
        if (!scenes.containsKey(AppView.BOARD_VIEW)) {
            Object[] controllerOut = new Object[1];
            Scene scene = viewLoader.loadScene(AppView.BOARD_VIEW, controllerOut);
            scenes.put(AppView.BOARD_VIEW, scene);
            controllers.put(AppView.BOARD_VIEW, controllerOut[0]);
        }

        GameBoardController controller = (GameBoardController) controllers.get(AppView.BOARD_VIEW);
        if (controller != null) {
            controller.setChessEngineRunner(appContext.getEngineWorker());
        }

        appContext.getEngineWorker().post(new StartGameCommand(gameConfiguration));
        appContext.getEngineWorker().post(new RequestFullInitialGameStateCommand());

        // Wait a bit to show the loading animation, then switch to the board
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> showView(AppView.BOARD_VIEW));
        delay.play();
    }

    public RevengeDecision loadGameOverView(String message, PlayerColor winnerColor, int totalMovePerformed, String winningPlayerName){
        Object[] controllerOut = new Object[1];
        Scene scene = viewLoader.loadScene(AppView.GAME_OVER_VIEW, controllerOut);
        GameOverViewController controller = (GameOverViewController) controllerOut[0];

        controller.setTheData(message, winnerColor, totalMovePerformed, winningPlayerName);

        Stage dialog = new Stage();
        dialog.setScene(scene);
        dialog.initOwner(stage);
        dialog.setFullScreen(true);

        dialog.showAndWait();
        return controller.getRevengeDecision(); //not implemented yet
    }

    @Override
    public GameConfiguration loadPvpPopUp() {
        Object[] controllerOut = new Object[1];
        Scene scene = viewLoader.loadScene(AppView.PVP_POP_UP, controllerOut);
        PvpPopUpWindowController controller = (PvpPopUpWindowController) controllerOut[0];

        Stage dialog = new Stage();
        dialog.setScene(scene);
        dialog.initOwner(stage);
        dialog.setFullScreen(true);

        dialog.showAndWait();

        return controller.getGameConfiguration();
    }


    private void showView(AppView view) {
        if (!scenes.containsKey(view)) {
            Object[] controllerOut = new Object[1];
            Scene scene = viewLoader.loadScene(view, controllerOut);
            scenes.put(view, scene);
            controllers.put(view, controllerOut[0]);
        }
        stage.setScene(scenes.get(view));
        stage.setFullScreen(true);
        stage.show();
    }

    @Override
    public Object getController(String viewName) {
        for (Map.Entry<AppView, Object> entry : controllers.entrySet()) {
            if (entry.getKey().name().equals(viewName)) {
                return entry.getValue();
            }
        }
        return null;
    }
}

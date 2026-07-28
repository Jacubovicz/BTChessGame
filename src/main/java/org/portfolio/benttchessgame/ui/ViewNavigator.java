package org.portfolio.benttchessgame.ui;

import org.portfolio.benttchessgame.ui.navigation.AppView;
import org.portfolio.benttchessgame.ui.navigation.SceneNavigator;
import javafx.stage.Stage;
import org.portfolio.benttchessgame.engine.ChessEngineWorker;

import java.util.logging.Logger;

public class ViewNavigator {

    public static Logger log = Logger.getLogger(ViewNavigator.class.getName());

    Stage stage;
    private static ViewNavigator instance;

    ChessEngineWorker engineRunner;

    SceneNavigator sm;

    public static ViewNavigator init(Stage stage, ChessEngineWorker engineRunner)
    {
        if(instance == null)
        {
            instance = new ViewNavigator(stage, engineRunner);
        }

        return instance;
    }

    public Object getController(AppView view)
    {
        return sm.getController(view);
    }

    private ViewNavigator(Stage stage, ChessEngineWorker engineRunner)
    {
        this.stage = stage;
        this.engineRunner = engineRunner;
        sm = SceneNavigator.init(stage);
    }

    public static ViewNavigator getInstance() {
        try
        {
            if(instance == null)
            {
                throw new InstantiationException();
            }
        }
        catch(InstantiationException ie)
        {
            log.severe("The UIEngine instantiation attempt incorrect.");
            ie.printStackTrace();
        }

        return instance;

    }

    public void loadMainMenu()
    {
        sm.loadMainMenu();
    }

    public void loadBoardView()
    {
        sm.loadBoardView(engineRunner);
    }

}

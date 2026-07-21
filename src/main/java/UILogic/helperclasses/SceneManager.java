package UILogic.helperclasses;

import UILogic.controllers.GameViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.portfolio.benttchessgame.chessengine.ChessEngine;
import org.portfolio.benttchessgame.chessengine.ChessEngineRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class SceneManager {

    static Logger log = Logger.getLogger(SceneManager.class.getName());

    private static final int HEIGHT = 700;
    private static final int WIDTH = 1280;

    private static SceneManager instance;

    private Stage stage;
    private Map<EnumViews, Scene> viewList;

    public static SceneManager init(Stage stage)
    {
        if(instance == null)
        {
            instance = new SceneManager(stage);
        }

        return instance;
    }

    private SceneManager(Stage stage)
    {

        loadFxmlFiles();

            setStage(stage);
            setView(EnumViews.START_VIEW);
    }


    public <T> T getController(EnumViews view)
    {
        return (T)viewList.get(view);
    }

    private void loadFxmlFiles()
    {
        Map<EnumViews, Scene> map = new HashMap<>();
        for(EnumViews e: EnumViews.values())
        {
            try
            {

                FXMLLoader loader = new FXMLLoader(getClass().getResource(e.getFxmlPath()));
                Parent root = loader.load();
                if(e == EnumViews.GAME_VIEW)
                {
                    GameViewController controller = loader.getController();
                    ChessEngineRunner engineRunner = new ChessEngineRunner();

                    Thread engineThread = new Thread(engineRunner);
                    engineThread.start();

                    controller.setChessEngineRunner(engineRunner);
                }


                Scene scene = new Scene(root, WIDTH, HEIGHT);
                map.put(e, scene);
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
                log.severe(ex.getMessage());
            }

        }
        this.viewList = map;
    }

    public static SceneManager getInstance()
    {
        try
        {
            if(instance == null)
            {
                throw new InstantiationException("The instance of SceneManager was not properly set. ");
            }
        }
        catch(InstantiationException ie)
        {
            System.err.println(ie.getMessage());
            log.severe(ie.getMessage());
        }
        return instance;
    }

    private void setStage(Stage stage)
    {
        this.stage =stage;
    }

    public Stage getStage()
    {
        return this.stage;
    }

    public void setView(EnumViews views)
    {
      this.stage.setScene(viewList.get(views));
      getStage().show();
    }
}

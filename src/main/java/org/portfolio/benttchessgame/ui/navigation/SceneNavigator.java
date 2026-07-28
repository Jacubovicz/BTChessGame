package org.portfolio.benttchessgame.ui.navigation;

import org.portfolio.benttchessgame.ui.controller.GameBoardController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.portfolio.benttchessgame.engine.ChessEngineWorker;
import org.portfolio.benttchessgame.engine.command.StartGameCommand;
import org.portfolio.benttchessgame.engine.player.BasePlayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class SceneNavigator {

    static Logger log = Logger.getLogger(SceneNavigator.class.getName());

    private static final int HEIGHT = 700;
    private static final int WIDTH = 1366;

    private static SceneNavigator instance;

    private Stage stage;
    private final Map<AppView, Scene> viewList = new HashMap<>();
    public final Map<AppView, Object> controllers = new HashMap<>();

    public static SceneNavigator init(Stage stage)
    {
        if(instance == null)
        {
            instance = new SceneNavigator(stage);
        }

        return instance;
    }

    private SceneNavigator(Stage stage)
    {
            setStage(stage);
            setView(AppView.MAIN_MENU_VIEW);
    }

    public void loadMainMenu()
    {
            if(!viewList.containsKey(AppView.MAIN_MENU_VIEW))
            {
                Map.Entry<AppView, Scene> entry = loadTheScene(AppView.MAIN_MENU_VIEW);
                viewList.put(AppView.MAIN_MENU_VIEW, entry.getValue());
            }
         setView(AppView.MAIN_MENU_VIEW);
    }

    public void loadBoardView(ChessEngineWorker runner)
    {
        runner.post(new StartGameCommand(new BasePlayer("Jakub"), new BasePlayer("Kinga"), true));

         if(!viewList.containsKey(AppView.BOARD_VIEW))
         {
            Map.Entry<AppView, Scene> entry = loadTheScene(AppView.BOARD_VIEW);
            viewList.put(AppView.BOARD_VIEW, entry.getValue());
         }
         GameBoardController controller = (GameBoardController)getController(AppView.BOARD_VIEW);
        controller.setChessEngineRunner(runner);
         setView(AppView.BOARD_VIEW);

    }

    public Map.Entry<AppView, Scene> loadTheScene(AppView view)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFxmlPath()));
        Parent root = null;
        try
        {
            root = loader.load();
            controllers.put(view, loader.getController());
        }
        catch(IOException e)
        {
            log.severe("Can't load the " + view.name() + " view.");
        }

       return Map.entry(view, new Scene(root, WIDTH, HEIGHT));
    }



    public Object getController(AppView view)
    {
        return controllers.get(view);
    }

 //   private void loadFxmlFiles()
 ////   {
    //    Map<EnumViews, Scene> map = new HashMap<>();
   //     for(EnumViews e: EnumViews.values())
   //     {
      //      try
      //      {

            //    FXMLLoader loader = new FXMLLoader(getClass().getResource(e.getFxmlPath()));
           //     Parent root = loader.load();
            //    if(e == EnumViews.BOARD_VIEW)
            //    {
                 //   GameViewController controller = loader.getController();
                   // ChessEngineRunner engineRunner = new ChessEngineRunner();

                 //   Thread engineThread = new Thread(engineRunner);
                 //   engineThread.start();

               //     controller.setChessEngineRunner(engineRunner);
      //          }


             //   Scene scene = new Scene(root, WIDTH, HEIGHT);
              //  map.put(e, scene);
      //      }
        //    catch(IOException ex)
       //     {
        //     //   ex.printStackTrace();
             //   log.severe(ex.getMessage());
        //    }

   //     }
     //   this.viewList = map;
  //  }

    public static SceneNavigator getInstance()
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

    private void setView(AppView views)
    {
      this.stage.setScene(viewList.get(views));
      getStage().show();
    }
}

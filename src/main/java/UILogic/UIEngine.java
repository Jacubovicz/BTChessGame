package UILogic;

import UILogic.controllers.GameViewController;
import UILogic.helperclasses.EnumViews;
import UILogic.helperclasses.SceneManager;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class UIEngine {

    public static Logger log = Logger.getLogger(UIEngine.class.getName());

    Stage stage;
    private static UIEngine instance;

    SceneManager sm;

    public static UIEngine init(Stage stage)
    {
        if(instance == null)
        {
            instance = new UIEngine(stage);
        }

        return instance;
    }

    public <T> T getController(EnumViews view)
    {
        return sm.getController(view);
    }

    private UIEngine(Stage stage)
    {
        this.stage = stage;
        sm = SceneManager.init(stage);
    }

    public static UIEngine getInstance() {
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

    public void setTheGameView()
    {
        sm.setView(EnumViews.GAME_VIEW);
    }

}

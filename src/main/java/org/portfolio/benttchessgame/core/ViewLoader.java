package org.portfolio.benttchessgame.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.portfolio.benttchessgame.service.NavigationService;
import org.portfolio.benttchessgame.ui.navigation.AppView;

import java.io.IOException;
import java.util.logging.Logger;

public class ViewLoader {
    private static final Logger log = Logger.getLogger(ViewLoader.class.getName());
    private static final int HEIGHT = 700;
    private static final int WIDTH = 1366;

    private final AppContext appContext;
    private final NavigationService navigationService;

    public ViewLoader(AppContext appContext, NavigationService navigationService) {
        this.appContext = appContext;
        this.navigationService = navigationService;
    }

    public Scene loadScene(AppView view, Object[] controllersOut) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFxmlPath()));

        loader.setControllerFactory(type -> {
            try {
                // Check if constructor matches
                for (var constructor : type.getConstructors()) {
                    if (constructor.getParameterCount() == 2 &&
                        constructor.getParameterTypes()[0] == AppContext.class &&
                        constructor.getParameterTypes()[1] == NavigationService.class) {
                        return constructor.newInstance(appContext, navigationService);
                    }
                }
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

        Parent root = null;
        try {
            root = loader.load();
            if (controllersOut != null && controllersOut.length > 0) {
                controllersOut[0] = loader.getController();
            }
        } catch (IOException e) {
            log.severe("Can't load the " + view.name() + " view.");
            e.printStackTrace();
        }

        return new Scene(root, WIDTH, HEIGHT);
    }
}

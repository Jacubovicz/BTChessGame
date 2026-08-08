package org.portfolio.benttchessgame;

import org.portfolio.benttchessgame.core.AppLifecycle;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class BentChessApplication extends Application {

    private AppLifecycle lifecycle;

    @Override
    public void start(Stage stage) throws IOException {
        lifecycle = new AppLifecycle();
        lifecycle.start(stage);
    }

    @Override
    public void stop() throws Exception {
        if (lifecycle != null) {
            lifecycle.stop();
        }
        super.stop();
    }

    public static void main(String[] args) {

       launch();
    }
}
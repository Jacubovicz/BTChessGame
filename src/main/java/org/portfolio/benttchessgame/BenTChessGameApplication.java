package org.portfolio.benttchessgame;

import UILogic.UIEngine;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class BenTChessGameApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        UIEngine ui = UIEngine.init(stage);
    }

    public static void main(String[] args) {

       launch();
    }
}
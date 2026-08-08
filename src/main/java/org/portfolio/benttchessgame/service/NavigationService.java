package org.portfolio.benttchessgame.service;

import javafx.stage.Stage;

public interface NavigationService {
    void loadMainMenu();
    void loadBoardView();
    Object getController(String viewName);
    void setStage(Stage stage);
}

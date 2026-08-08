package org.portfolio.benttchessgame.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.portfolio.benttchessgame.core.AppContext;
import org.portfolio.benttchessgame.service.NavigationService;

public class MainMenuController {

    private final AppContext appContext;
    private final NavigationService navigationService;

    @FXML
    private Button playerVsPlayer;
    @FXML
    private Button playerVsComputer;
    @FXML
    private Button playerVsOnline;
    @FXML
    private Label serverStatus;

    public MainMenuController(AppContext appContext, NavigationService navigationService) {
        this.appContext = appContext;
        this.navigationService = navigationService;
    }

    @FXML
    public void startTheGameWithComputer() {
        // Not implemented yet
    }

    @FXML
    public void startTheGameWithPlayer() {
        navigationService.loadBoardView();
    }

    @FXML
    public void startTheGameOnServer() {
        // Not implemented yet
    }
}

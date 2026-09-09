package org.portfolio.benttchessgame.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.portfolio.benttchessgame.core.AppContext;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.service.NavigationService;

import java.util.Objects;

public class GameOverViewController {

    @FXML
    private TextArea resultInfoTextArea;

    @FXML
    private Label winningPlayerNameField, movesPerformedField;

    @FXML
    private Button replayTheMatchButton, yesRevengeButton, yesDifferentSettingsButton, noRevengeButton;

    private AppContext appContext;
    private NavigationService navigationService;
    private RevengeDecision revengeDecision;


    public void initialize(){
        replayTheMatchButton.setOnAction((event) -> replayTheMatchSequence());
        yesRevengeButton.setOnAction((event) -> startDefaultRevenge());
        yesDifferentSettingsButton.setOnAction((event) -> showTheOptionWindow());
        noRevengeButton.setOnAction((event) -> showTheMainMenu());

    }

    public GameOverViewController(AppContext context, NavigationService navigationService){
        this.appContext = context;
        this.navigationService = navigationService;
    }

    public void setTheData(String message, PlayerColor winnerColor, int totalMovePerformed, String winningPlayerName){
        winningPlayerNameField.setText(winningPlayerName);
        movesPerformedField.setText(String.valueOf(totalMovePerformed));
        resultInfoTextArea.setText(message);
    }

    public void replayTheMatchSequence(){;
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void startDefaultRevenge(){
        revengeDecision = RevengeDecision.RESTART;
    }

    public void showTheOptionWindow(){
        revengeDecision = RevengeDecision.RESTART_WITH_OPTIONS_WINDOW;
    }

    public void showTheMainMenu(){
        revengeDecision = RevengeDecision.QUIT;
    }

    public RevengeDecision getRevengeDecision(){
        return Objects.requireNonNullElse(revengeDecision, RevengeDecision.QUIT);
    }

}

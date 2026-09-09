package org.portfolio.benttchessgame.ui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.portfolio.benttchessgame.core.AppContext;
import org.portfolio.benttchessgame.engine.GameMode;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;
import org.portfolio.benttchessgame.engine.configuration.GameSettings;
import org.portfolio.benttchessgame.engine.time.TimeSettings;
import org.portfolio.benttchessgame.engine.player.BasePlayer;
import org.portfolio.benttchessgame.engine.player.Player;
import org.portfolio.benttchessgame.engine.player.PlayerColor;
import org.portfolio.benttchessgame.service.NavigationService;
import org.portfolio.benttchessgame.ui.button.ButtonGroup;


public class PvpPopUpWindowController {

    @FXML
    TextField playerOneTextField;

    @FXML
    TextField playerTwoTextField;

    @FXML
    TextField customTimeTextField;

    @FXML
    TextField customIncrementTextField;

    @FXML
    Button whiteFiguresButton;

    @FXML
    Button randomFiguresButton;

    @FXML
    Button blackFiguresButton;

    @FXML
    Button cancelButton;

    @FXML
    Button startButton;

    @FXML
    ComboBox<String> timeComboBox; //time in minutes

    @FXML
    ComboBox<String> incrementComboBox; //time in seconds

    @FXML
    ComboBox<GameSettings> gameModeComboBox; //game modes taken from enum

    private AppContext appContext;
    private NavigationService navigationService;

    private GameConfiguration gameConfiguration;
    private ButtonGroup figuresColorChoice;

    private SimpleStringProperty customTime;
    private SimpleStringProperty customIncrement;

    public void initialize() {
        addNumsUpTo(1, 60, timeComboBox, true);
        addNumsUpTo(0, 30, incrementComboBox, false);

        customTime = new SimpleStringProperty();
        customIncrement = new SimpleStringProperty();

        customTime.bind(customTimeTextField.textProperty());
        customIncrement.bind(customIncrementTextField.textProperty());

        gameModeComboBox.getItems().addAll(GameSettings.values());

        gameModeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> prefillTheSettings(newValue));

        timeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("custom")){
                customTimeTextField.setDisable(false);
            }
            if(oldValue != null && oldValue.equals("custom")){
                customTimeTextField.setDisable(true);
                customTimeTextField.setText("");
            }
                });

        timeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("∞")){
                incrementComboBox.setDisable(true);
            }
            if(oldValue != null && oldValue.equals("∞")){
                incrementComboBox.setDisable(false);
            }
        });

        incrementComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("custom")){
                customIncrementTextField.setDisable(false);
            }
            if(oldValue != null && oldValue.equals("custom")){
                customIncrementTextField.setText("");
                customIncrementTextField.setDisable(true);
            }
                });



        figuresColorChoice = new ButtonGroup();
        figuresColorChoice.addButtons(whiteFiguresButton, blackFiguresButton, randomFiguresButton);

        startButton.setOnAction(event -> fillTheGameConfiguration(playerOneTextField.getText(), playerTwoTextField.getText(), GameMode.PvPOffline, gameModeComboBox.getSelectionModel().getSelectedItem(), timeComboBox.getSelectionModel().getSelectedItem(), incrementComboBox.getSelectionModel().getSelectedItem()));
        cancelButton.setOnAction(event -> abortAndClose());

    }

    private void prefillTheSettings(GameSettings gameSettings){
        switch(gameSettings){
            case BLITZ -> {
                disableTimeAndIncrementBoxes(true);
                timeComboBox.getSelectionModel().select((Integer)Math.toIntExact((TimeSettings.blitz().baseTimeMillis()/1000)/60) + " min");
                incrementComboBox.getSelectionModel().select((Integer)Math.toIntExact(TimeSettings.blitz().incrementMillis()/1000));
            }
            case RAPID -> {
                disableTimeAndIncrementBoxes(true);
                timeComboBox.getSelectionModel().select((Integer)Math.toIntExact((TimeSettings.rapid().baseTimeMillis()/1000)/60) + " min");
                incrementComboBox.getSelectionModel().select((Integer)Math.toIntExact(TimeSettings.rapid().incrementMillis()/1000));
            }
            case CASUAL -> {
                disableTimeAndIncrementBoxes(true);
                timeComboBox.getSelectionModel().select("∞");
                incrementComboBox.getSelectionModel().select((Integer)Math.toIntExact(TimeSettings.casual().incrementMillis()/1000));
            }
            case STANDARD -> {
                disableTimeAndIncrementBoxes(true);
                timeComboBox.getSelectionModel().select((Integer)Math.toIntExact(TimeSettings.standard().baseTimeMillis()/1000)/60 + " min");
                incrementComboBox.getSelectionModel().select((Integer)Math.toIntExact(TimeSettings.standard().incrementMillis()/1000));
            }
            case CUSTOM -> disableTimeAndIncrementBoxes(false);



        }
    }

    private void disableTimeAndIncrementBoxes(boolean disable){
        timeComboBox.setDisable(disable);
        incrementComboBox.setDisable(disable);
    }

    private void abortAndClose() {

        navigationService.loadMainMenu();
    }

    public void fillTheGameConfiguration(String playerOneName, String playerTwoName, GameMode gameMode, GameSettings gameSettings, String time, String increment) {
        TimeSettings timeSettings = null;
        switch (gameSettings) {
            case GameSettings.BLITZ -> timeSettings = TimeSettings.blitz();
            case GameSettings.CASUAL -> timeSettings = TimeSettings.casual();
            case GameSettings.RAPID -> timeSettings = TimeSettings.rapid();
            case GameSettings.STANDARD -> timeSettings = TimeSettings.standard();
            case GameSettings.CUSTOM ->
                    {
                        long baseTimeMinutes;
                        long incrementSeconds;
                        long delayMillis = 2000L;

                        if(time.equals("∞")){
                            timeSettings = TimeSettings.infinite();
                            break;
                        }

                        if(time.equals("custom")){
                            baseTimeMinutes = Long.parseLong(customTime.get().split(" ")[0]);
                        }
                        else {
                         //   baseTimeMinutes = Long.parseLong(time);
                            baseTimeMinutes = Long.parseLong(time.split(" ")[0]);
                        }

                        if(increment.equals("custom")){
                            incrementSeconds = Long.parseLong(customIncrement.get().split(" ")[0]);
                        }
                        else {
                            //incrementSeconds = Long.parseLong(increment)*1000L;
                            incrementSeconds = Long.parseLong(increment.split(" ")[0]);
                        }

                        timeSettings = TimeSettings.custom(baseTimeMinutes, incrementSeconds, delayMillis);
                    }


             //delay needs to be addressed
            default -> System.err.println("Invalid game settings");
        }

        Player player1 = new BasePlayer(playerOneName);
        Player player2 = new BasePlayer(playerTwoName);

        if(figuresColorChoice.getSelectedButton() != null){
            switch(figuresColorChoice.getSelectedButton().getId()){
                case "whiteFiguresButton" -> player1.setColor(PlayerColor.WHITE);
                case "blackFiguresButton" -> player1.setColor(PlayerColor.BLACK);
                case "randomFiguresButton" -> player1.setColor(PlayerColor.random());
            }
            if(player1.getColor() != null){
                player2.setColor(player1.getColor().opposite());
            }
        }
        Player whitePlayer = player1.getColor() == PlayerColor.WHITE ? player1 : player2;
        Player blackPlayer = player2.getColor() == PlayerColor.BLACK ? player2 : player1;

        this.gameConfiguration = new GameConfiguration(whitePlayer, blackPlayer, gameMode, timeSettings);

        closeWindow();
    }

    public void closeWindow(){
        Stage stage = (Stage) startButton.getScene().getWindow();
        stage.close();
    }

    public GameConfiguration getGameConfiguration() {
        if (gameConfiguration == null) {
            System.err.println("Game configuration not initialized");
            return null;
        }
        return gameConfiguration;
    }

    private void addNumsUpTo(int minNum, int maxNum, ComboBox<String> comboBox, boolean inMins){
        var unit = inMins ? " min" : " sec";
        for(int i = minNum; i <= maxNum; i++){
            comboBox.getItems().add(i + unit);
        }
        comboBox.getItems().add("custom");

        if(inMins) { //because it means that this is a length of the game in minutes
            comboBox.getItems().add("∞");
        }

    }

    public PvpPopUpWindowController(AppContext appContext, NavigationService navigationService){
        this.appContext = appContext;
        this.navigationService = navigationService;
    }

}

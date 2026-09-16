package org.portfolio.benttchessgame.ui.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.portfolio.benttchessgame.core.AppContext;
import org.portfolio.benttchessgame.engine.ChessEngineWorker;
import org.portfolio.benttchessgame.engine.board.ChessBoard;
import org.portfolio.benttchessgame.engine.command.RequestTheFrameCommand;
import org.portfolio.benttchessgame.engine.command.RequestTheReplayCommand;
import org.portfolio.benttchessgame.engine.event.ReplayFrameData;
import org.portfolio.benttchessgame.engine.event.ReplayReady;
import org.portfolio.benttchessgame.engine.eventbus.GameEventBus;
import org.portfolio.benttchessgame.engine.replay.ChessBoardFrame;
import org.portfolio.benttchessgame.engine.tools.UtilityMethods;
import org.portfolio.benttchessgame.piece.Piece;
import org.portfolio.benttchessgame.service.NavigationService;
import org.portfolio.benttchessgame.ui.view.BoardRenderer;

public class GameReplayController {

    @FXML
    GridPane chessBoard;

    @FXML
    Button rewindToStartButton, prevMoveButton, playButton, pauseButton, nextMoveButton, rewindToEndButton;

    @FXML
    Slider speedSlider;

    private final int ONE_TICK = 1;

    private StackPane[][] chessBoardSquares;

    Timeline chessBoardTimeline;

    private final ChessEngineWorker worker;
    private double speedMultiplier = 1.0;

    private int currentFrameIndex = 0;
    private int totalFrames;
    private ReplayBoardInitializer replayBoardInitializer;
    private BoardRenderer boardRenderer;


    public GameReplayController(AppContext appContext, NavigationService navigationService){
        this.worker = appContext.getEngineWorker();
    }

    public void initialize(){
        chessBoardSquares = new StackPane[chessBoard.getRowCount()][chessBoard.getColumnCount()];
        replayBoardInitializer = new ReplayBoardInitializer(chessBoard.getChildren(), chessBoardSquares);
        boardRenderer = new BoardRenderer(chessBoardSquares, replayBoardInitializer);

        wireControls();
        wireListeners();
        requestReplayFrames();
    }

    private void requestReplayFrames(){
        worker.post(new RequestTheReplayCommand());
    }

    private void wireControls(){
        rewindToStartButton.setOnAction(event -> backToBeginning());
        prevMoveButton.setOnAction(event -> backOneMove());
        playButton.setOnAction(event -> play());
        pauseButton.setOnAction(event -> pause());
        nextMoveButton.setOnAction(event -> nextMove());
        rewindToEndButton.setOnAction(event -> rewindToEnd());

        chessBoardTimeline = new Timeline();
        speedSlider.setMin(0.5);
        speedSlider.setMax(3);
        speedSlider.setValue(1.0);
        chessBoardTimeline.rateProperty().bind(speedSlider.valueProperty());
    }


    public void backToBeginning(){//
        pause();
        requestFrameOfIndex(0);
    }

    public void backOneMove(){//
        pause();
        if(currentFrameIndex > 0){
            requestFrameOfIndex(currentFrameIndex-1);
        }
    }

    public void play(){
        pause();
        chessBoardTimeline.setCycleCount(Timeline.INDEFINITE);
        chessBoardTimeline.play();
    }

    public void pause(){
        chessBoardTimeline.pause();
    }

    public void nextMove(){
        pause();
        if(currentFrameIndex < totalFrames){
            requestFrameOfIndex(currentFrameIndex+1);
        }
    }

    public void rewindToEnd(){
        if(totalFrames != 0){
            requestFrameOfIndex(totalFrames -1);
        }
    }

    public void updateSpeed(double speed){
        speedMultiplier = speed;
    }

    private void wireListeners(){
        GameEventBus eventbus = GameEventBus.getInstance();
        eventbus.subscribe(ReplayFrameData.class, this::receiveFrame);
        eventbus.subscribe(ReplayReady.class, this::startBroadcast);
    }

    public void startBroadcast(ReplayReady replayReadyFrames){
        totalFrames = replayReadyFrames.amountOfFrames();
        ChessBoard initialChessBoard = replayReadyFrames.initialBoard();
        boardRenderer.render(UtilityMethods.chessBoardToStringArray(initialChessBoard));

        chessBoardTimeline.stop();
        chessBoardTimeline.getKeyFrames().clear();
        chessBoardTimeline.setCycleCount(Timeline.INDEFINITE);
        chessBoardTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> requestNextFrame()));
        chessBoardTimeline.playFromStart();
    }

    private void requestFrameOfIndex(int index){
        if(index >= 0 && index < totalFrames){
            worker.post(new RequestTheFrameCommand(index));
            currentFrameIndex = index;
        }
    }

    private void requestNextFrame(){
        if(currentFrameIndex < totalFrames){
            worker.post(new RequestTheFrameCommand(currentFrameIndex));
            currentFrameIndex++;
        }
    }

    private void receiveFrame(ReplayFrameData frameData){
        ChessBoardFrame frames = frameData.completeFrame();

        boardRenderer.render(frames.boardRepresentation());
    }



}

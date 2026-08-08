package org.portfolio.benttchessgame.ui.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.portfolio.benttchessgame.core.AppContext;
import org.portfolio.benttchessgame.service.NavigationService;

public class LoadingViewController {

    private final AppContext appContext;
    private final NavigationService navigationService;

    @FXML private SVGPath chessBoardAnimationObject;

    public LoadingViewController(AppContext appContext, NavigationService navigationService) {
        this.appContext = appContext;
        this.navigationService = navigationService;
    }

    @FXML
    private void initialize() {
        // Safety check (helps debug fx:id mismatches)
        if (chessBoardAnimationObject == null) {
            throw new IllegalStateException("fx:id 'chessBoardAnimationObject' was not injected. " +
                    "Did you set fx:id in the FXML (not id)?");
        }

        // Rotate forever
        RotateTransition rotate = new RotateTransition(Duration.seconds(2.0), chessBoardAnimationObject);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.setCycleCount(Animation.INDEFINITE);

        // Subtle scale pulse
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(0.8), chessBoardAnimationObject);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.06);
        pulse.setToY(1.06);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setInterpolator(Interpolator.EASE_BOTH);

        new ParallelTransition(rotate, pulse).play();
    }
}

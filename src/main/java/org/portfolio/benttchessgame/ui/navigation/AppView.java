package org.portfolio.benttchessgame.ui.navigation;

public enum AppView {

    MAIN_MENU_VIEW("/FXMLViews/StartView.fxml"),
    LOADING_VIEW("/FXMLViews/LoadingView.fxml"),
    BOARD_VIEW("/FXMLViews/GameView.fxml"),
    PVP_POP_UP("/FXMLViews/PvpPopUpWindowView.fxml"),
    GAME_OVER_VIEW("/FXMLViews/GameOverView.fxml");

    private final String fxmlPath;

    AppView(String fxmlPath)
    {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath()
    {
        return fxmlPath;
    }
}

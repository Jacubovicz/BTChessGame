package UILogic.helperclasses;

public enum EnumViews {

    START_VIEW("/FXMLViews/StartView.fxml"),
    LOADING_VIEW("/FXMLViews/LoadingView.fxml"),
    GAME_VIEW("/FXMLViews/GameView.fxml");

    private final String fxmlPath;

    EnumViews(String fxmlPath)
    {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath()
    {
        return fxmlPath;
    }
}

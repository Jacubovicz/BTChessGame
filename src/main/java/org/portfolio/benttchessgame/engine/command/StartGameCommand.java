package org.portfolio.benttchessgame.engine.command;

import org.portfolio.benttchessgame.engine.ChessEngine;
import org.portfolio.benttchessgame.engine.configuration.GameConfiguration;

public class StartGameCommand implements GameCommand {

    private final GameConfiguration gameConfig;

    public StartGameCommand(GameConfiguration gameConfig) {
        this.gameConfig = gameConfig;
    }

    @Override
    public void execute(ChessEngine engine) {
        engine.setTheNewGame(gameConfig);
    }
}

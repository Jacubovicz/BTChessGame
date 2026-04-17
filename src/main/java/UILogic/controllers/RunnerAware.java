package UILogic.controllers;

import org.portfolio.benttchessgame.chessengine.ChessEngineRunner;

public interface RunnerAware {

    ChessEngineRunner runner = null;

    void setRunner(ChessEngineRunner runner);
    ChessEngineRunner getRunner();
}

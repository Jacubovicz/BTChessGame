package org.portfolio.benttchessgame.engine.configuration;

import org.portfolio.benttchessgame.engine.GameMode;
import org.portfolio.benttchessgame.engine.player.Player;
import org.portfolio.benttchessgame.engine.time.TimeSettings;

public record GameConfiguration(Player whitePlayer, Player blackPlayer, GameMode mode, TimeSettings timeSettings /*, String fenNotation*/) {
}

package org.portfolio.benttchessgame.engine.configuration;

import org.portfolio.benttchessgame.engine.GameMode;
import org.portfolio.benttchessgame.engine.player.Player;

public record GameConfiguration(Player whitePlayer, Player blackPlayer, GameMode mode, TimeSettings timeSettings /*, String fenNotation*/) {
}

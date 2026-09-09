package org.portfolio.benttchessgame.engine.event;

import org.portfolio.benttchessgame.engine.player.PlayerColor;

public record TimeCounterEvent(PlayerColor playerColor, long timeLeft) {
}

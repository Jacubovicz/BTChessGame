package org.portfolio.benttchessgame.engine.event;

import org.portfolio.benttchessgame.engine.player.PlayerColor;

public record TurnChangedEvent(PlayerColor playerColor) { }

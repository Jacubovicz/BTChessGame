package org.portfolio.benttchessgame.engine.event;

import org.portfolio.benttchessgame.engine.player.PlayerColor;

public record GameEndedEvent(PlayerColor winningPlayer) {}

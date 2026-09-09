package org.portfolio.benttchessgame.engine.player;

import java.util.Random;

public enum PlayerColor
{
    WHITE,
    BLACK;

    public PlayerColor opposite()
    {
        return this == WHITE ? BLACK : WHITE;
    }

    public static PlayerColor random(){
        Random random = new Random();
        return random.nextBoolean() ? WHITE : BLACK;
    }
}

package org.portfolio.benttchessgame.engine.configuration;

//delayMillis is responsible for the delay before the game starts
public record TimeSettings(long baseTimeMillis, long incrementMillis, long delayMillis, boolean isInfinite)
{
    public static TimeSettings blitz()
    {
        return new TimeSettings(180_000, 2000, 2000, false);
    }

    public static TimeSettings rapid()
    {
        return new TimeSettings(600_000, 0, 2000, false);
    }

    public static TimeSettings standard()
    {
        return new TimeSettings(900_000, 0, 2000, false);
    }

    public static TimeSettings casual()
    {
        return new TimeSettings(0, 0, 0, true);
    }

    public static TimeSettings custom(long baseTimeMillis, long incrementMillis, long delayMillis)
    {
        return new TimeSettings(baseTimeMillis, incrementMillis, delayMillis, false);
    }

}

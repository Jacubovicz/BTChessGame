package org.portfolio.benttchessgame.chessengine.helperclasses;

import org.portfolio.benttchessgame.chessengine.Game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class GameEventBus {

    private static final GameEventBus INSTANCE = new GameEventBus();
    Map<Class<?>, List<Consumer<?>>> subscribers = new ConcurrentHashMap<>();

   public static GameEventBus getInstance()
   {
       return INSTANCE;
   }

   public <T> void subscribe(Class<T> eventType, Consumer<T> action)
   {
       subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(action);
   }

   public void publishEvent(Object event)
   {
       List<Consumer<?>> actions = subscribers.get(event.getClass());
       if(actions != null)
       {
           for(Consumer action : actions)
           {
               action.accept(event);
           }
       }
   }

}

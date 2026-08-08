package org.portfolio.benttchessgame.engine.eventbus;

import javafx.application.Platform;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.portfolio.benttchessgame.engine.event.GameEventPublisher;

public class GameEventBus implements GameEventPublisher {

    private static final GameEventBus INSTANCE = new GameEventBus();
    private final Map<Class<?>, List<Consumer<?>>> subscribers = new ConcurrentHashMap<>();

    public static GameEventBus getInstance() {
        return INSTANCE;
    }

    public <T> void subscribe(Class<T> eventType, Consumer<T> action) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(action);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void publishEvent(Object event) {
        List<Consumer<?>> actions = subscribers.get(event.getClass());
        if (actions != null) {
            for (Consumer action : actions) {
                try {
                    if (Platform.isFxApplicationThread()) {
                        action.accept(event);
                    } else {
                        Platform.runLater(() -> action.accept(event));
                    }
                } catch (NoClassDefFoundError | Exception e) {
                    // Fallback for non-FX environments or if FX is not yet initialized
                    action.accept(event);
                }
            }
        }
    }
}

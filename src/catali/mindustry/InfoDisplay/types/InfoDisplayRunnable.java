package catali.mindustry.InfoDisplay.types;

import mindustry.gen.Player;

@FunctionalInterface
public interface InfoDisplayRunnable {
    void get(Player player);
}
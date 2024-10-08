package catali.mindustry.InfoDisplay;

import mindustry.gen.Player;

@FunctionalInterface
public interface InfoDisplayRunnable {
    void get(Player player);
}
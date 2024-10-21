package catali.mindustry.InfoDisplay.types;

import mindustry.gen.Player;

@FunctionalInterface
public interface InfoTextRunnable {
    void get(Player player, String message);
}
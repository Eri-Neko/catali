package catali.mindustry.InfoDisplay;

import mindustry.gen.Player;

@FunctionalInterface
public interface InfoTextRunnable {
    void get(Player player, String message);
}
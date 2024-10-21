package catali;

import catali.core.GamemodeCore;
import catali.core.NekoGameplay;
import catali.java.service.TaskScheduler;
import catali.mindustry.InfoDisplay.InfoDisplay;
import catali.mindustry.world.MapControl;

public class NekoVars {
    public static TaskScheduler taskScheduler = new TaskScheduler();
    public static InfoDisplay infoDisplay = InfoDisplay.getInstance();
    public static NekoGameplay nekoGameplay = NekoGameplay.getNekoGameplay();
    public static GamemodeCore gamemodeCore = GamemodeCore.getInstance();
    public static MapControl mapControl = MapControl.getInstance();

    public static void init() {
        infoDisplay.init();
        nekoGameplay.init();
    }

    public static void shutdown() {
        taskScheduler.shutdown();
    }
}

package catali;

import catali.java.TaskScheduler;
import catali.mindustry.InfoDisplay.InfoDisplay;
import catali.singeton.NekoGameplay;

public class NekoVars {
    public static TaskScheduler taskScheduler = new TaskScheduler();
    public static InfoDisplay infoDisplay = InfoDisplay.getInstance();
    public static NekoGameplay nekoGameplay = NekoGameplay.getNekoGameplay();

    public static void init() {
        infoDisplay.init();
        nekoGameplay.init();
    }

    public static void shutdown() {
        taskScheduler.shutdown();
    }
}

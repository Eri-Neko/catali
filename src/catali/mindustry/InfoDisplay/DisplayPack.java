package catali.mindustry.InfoDisplay;

public class DisplayPack {
    public final int id;
    public final String title;
    public final String display;
    public final String[][] options;
    public final InfoDisplayRunnable[] optionRunnable;

    public DisplayPack(int id, String title, String display, String[][] options, InfoDisplayRunnable[] optionRunnable) {
        this.id = id;
        this.title = title;
        this.display = display;
        this.options = options;
        this.optionRunnable = optionRunnable;
    }
}
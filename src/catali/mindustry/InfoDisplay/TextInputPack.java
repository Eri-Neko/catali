package catali.mindustry.InfoDisplay;

public class TextInputPack {
    public final int id;
    public final String title;
    public final String message;
    public final boolean isNumeric;
    public final InfoTextRunnable optionRunnable;

    public TextInputPack(int id, String title, String message, boolean isNumeric, InfoTextRunnable optionRunnable) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.isNumeric = isNumeric;
        this.optionRunnable = optionRunnable;
    }

    public TextInputPack(int id, String title, boolean isNumeric, InfoTextRunnable optionRunnable) {
        this(id, title, null, isNumeric, optionRunnable);
    }

    public TextInputPack(int id, String title, InfoTextRunnable optionRunnable) {
        this(id, title, null, false, optionRunnable);
    }
}
package catali.mindustry.InfoDisplay.types;

public class TimedPack<T> {
    public T pack;
    public long timestamp;

    public TimedPack(T pack, long timestamp) {
        this.pack = pack;
        this.timestamp = timestamp;
    }
}
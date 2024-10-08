package catali.mindustry.InfoDisplay;

public class TimedPack<T> {
    T pack;
    long timestamp;

    TimedPack(T pack, long timestamp) {
        this.pack = pack;
        this.timestamp = timestamp;
    }
}
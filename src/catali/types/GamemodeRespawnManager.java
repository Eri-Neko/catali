package catali.types;

import arc.util.Time;
import java.util.HashMap;
import java.util.Map;

// 100% Chat GPT
public class GamemodeRespawnManager {
    // Lưu trữ UUID của người chơi và thời gian chờ để hồi sinh
    private Map<String, Long> respawnQueue = new HashMap<>();
    private final long defaultRespawnTime = 30000; // 30 giây cho thời gian chờ hồi sinh

    // Thêm người chơi vào hàng chờ hồi sinh với thời gian chờ mặc định
    public void addPlayerToRespawn(String uuid) {
        long respawnTime = Time.millis() + defaultRespawnTime;
        respawnQueue.put(uuid, respawnTime);
    }

    // Thêm người chơi vào hàng chờ với thời gian chờ tùy chỉnh
    public void addPlayerToRespawn(String uuid, long customTimeout) {
        long respawnTime = Time.millis() + customTimeout;
        respawnQueue.put(uuid, respawnTime);
    }

    // Kiểm tra xem người chơi có trong danh sách chờ hồi sinh không (có tự động cập nhật)
    public boolean isPlayerInRespawn(String uuid) {
        updateRespawnQueue(); // Tự động cập nhật hàng chờ
        return respawnQueue.containsKey(uuid);
    }

    // Lấy thời gian còn lại trước khi người chơi có thể hồi sinh (tự động cập nhật)
    public long getRemainingRespawnTime(String uuid) {
        updateRespawnQueue(); // Tự động cập nhật hàng chờ
        
        if (!respawnQueue.containsKey(uuid)) return -1;

        long remainingTime = respawnQueue.get(uuid) - Time.millis();
        return remainingTime > 0 ? remainingTime : 0;
    }

    // Xóa người chơi khỏi hàng chờ hồi sinh (dù họ đang chờ hay không)
    public void removePlayerFromRespawn(String uuid) {
        respawnQueue.remove(uuid);
    }

    // Cập nhật hàng chờ hồi sinh, xóa những người chơi đã hết thời gian chờ
    private void updateRespawnQueue() {
        long currentTime = Time.millis();
        respawnQueue.entrySet().removeIf(entry -> entry.getValue() <= currentTime);
    }
}

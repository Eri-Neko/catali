package catali.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import arc.util.Time;

public class GamemodeLeaveTeamControl {
    private final Set<Integer> leaved = new HashSet<>();
    private final Map<Integer, Long> leave = new HashMap<>();
    private static final long EXPIRY_TIME = 60 * 1000;

    public void add(int teamId) {
        leave.put(teamId, Time.millis());
    }

    public void remove(int teamId) {
        leave.remove(teamId);
    }

    public boolean isPlayerLeave(int teamId) {
        refresh();
        return leave.containsKey(teamId);
    }

    public void refresh() {
        long currentTime = Time.millis();
        leave.entrySet().removeIf(entry -> {
            boolean isExpired = (currentTime - entry.getValue()) > EXPIRY_TIME;
            if (isExpired) {
                leaved.add(entry.getKey());
            }
            return isExpired;
        });
    }

    public Set<Integer> getExpiredLeave() {
        Set<Integer> expiredTeams = new HashSet<>(leaved);
        leaved.clear(); 
        return expiredTeams;
    }

    public Set<Integer> getAllLeaved() {
        refresh();
        return new HashSet<>(leaved);
    }
}

package catali.singeton;

import mindustry.game.Team;
import mindustry.gen.Player;

import static mindustry.Vars.state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.Random;

public class GamemodeCore {
    private Random random = new Random();
    private static GamemodeCore gamemodeCore = new GamemodeCore();
    private Map<Integer, Set<String>> playerPlayMap = new HashMap<>();
    private Set<String> playerPlaySet = new HashSet<>();

    private GamemodeCore() {

    }

    public static GamemodeCore getInstance() {
        return gamemodeCore;
    }

    public void playerJoin(Player player) {

    }

    public void playerPlay(String uuid) {
        if (!playerPlaySet.contains(uuid)) {
            
        }
    }

    public int pickRandomizeTeam() {
        int teamId = random.nextInt(1, 255);
        if (playerPlayMap.get(teamId) == null) {
            return teamId;
        } else {
            return pickRandomizeTeam();
        }
    }
}

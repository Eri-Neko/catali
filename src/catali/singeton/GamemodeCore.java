package catali.singeton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import arc.util.Time;

import java.util.Map;
import java.util.Random;

import catali.NekoVars;
import catali.mindustry.WorldService;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Player;
import mindustry.type.UnitType;

import static mindustry.Vars.*;

public class GamemodeCore {
    private Random random = new Random();
    private static GamemodeCore gamemodeCore = new GamemodeCore();
    private final MapControl mapControl = MapControl.getInstance();
    private Map<Integer, GamemodeTeam> teams = new HashMap<>();
    private Set<String> players = new HashSet<>();
    private Map<String, long[]> leaveTeam = new HashMap<>();

    private GamemodeCore() {
        NekoVars.taskScheduler.addTask(this::refresh, 0, 1000);

    }

    public static GamemodeCore getInstance() {
        return gamemodeCore;
    }

    public void handleBlockDestroy() {

    }

    public void refresh() {
        teams.forEach((tId, team) -> {
            team.getAllPlayer().forEach((uuid) -> {
                
            });
        });
    }

    public void handlePlayerJoin(Player player) {
        WorldService.changeTeamForPlayer(player.uuid(), Team.derelict);

        if (leaveTeam.containsKey(player.uuid())) {
            leaveTeam.remove(player.uuid());
        }
    }

    public void handlePlayerLeave(String uuid) {
        if (isPlayerPlaying(uuid) && getTeamByLeaderUuid(uuid) != -1) {
            leaveTeam.put(uuid, new long[] { getTeamByLeaderUuid(uuid), Time.millis() });
        }
    }

    public void handlePlayerPlay(String uuid) {
        if (!isPlayerPlaying(uuid)) {
            players.add(uuid);
            teams.put(pickRandomizeTeam(), new GamemodeTeam(uuid));
        }
    }

    public static class GamemodeTeam {
        private final String leaderUuid;
        private final Set<String> teammateUUids = new HashSet<>();
        private final Set<UnitType> units = new HashSet<>();
        private int level = 1;
        private int exp = 0;
        private int expNeed = 10;

        public GamemodeTeam(String uuid) {
            leaderUuid = uuid;
            units.add(UnitTypes.poly);
        }

        public String getLeader() {
            return leaderUuid;
        }

        public boolean isPlayerInTeam(String uuid) {
            return leaderUuid == uuid | teammateUUids.contains(uuid);
        }

        public void addPlayer(String uuid) {
            teammateUUids.add(uuid);
        }

        public void removePlayer(String uuid) {
            teammateUUids.remove(uuid);
        }

        public Set<String> getAllPlayer() {
            Set<String> list = new HashSet<>(teammateUUids);
            list.add(leaderUuid);
            return list;
        }

        public Set<UnitType> getAvalableUnits() {
            return units;
        }
    }

    public long getTeamByLeaderUuid(String uuid) {
        final int[] teamId = { -1 };

        teams.forEach((tId, team) -> {
            if (team.leaderUuid.equals(uuid)) {
                teamId[0] = tId;
            }
        });

        return teamId[0];
    }

    public long getTeamByPlayerUuid(String uuid) {
        final int[] teamId = { -1 };

        teams.forEach((tId, team) -> {
            if (team.isPlayerInTeam(uuid)) {
                teamId[0] = tId;
            }
        });

        return teamId[0];
    }

    public boolean isPlayerPlaying(String uuid) {
        return players.contains(uuid);
    }

    public int pickRandomizeTeam() {
        int teamId = random.nextInt(1, 255);
        if (teams.size() > 50)
            return -1;
        if (teams.get(teamId) == null)
            return teamId;
        else
            return pickRandomizeTeam();
    }
}

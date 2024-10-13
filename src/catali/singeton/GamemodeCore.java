package catali.singeton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import arc.math.Mathf;
import arc.util.Align;
import arc.util.Time;
import catali.NekoVars;
import catali.mindustry.WorldService;
import catali.types.GamemodeTeam;
import catali.types.GamemodeTeamManager;
import catali.types.UpgradeMap;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;

import static mindustry.Vars.state;

public class GamemodeCore {
    private Random random = new Random();
    private static GamemodeCore gamemodeCore = new GamemodeCore();
    private final MapControl mapControl = MapControl.getInstance();
    private GamemodeTeamManager teams = new GamemodeTeamManager();
    private Map<String, int[]> leaveTeam = new HashMap<>();

    private GamemodeCore() {
        NekoVars.taskScheduler.addTask(() -> {
            if (mapControl.isGame()) {
                refresh();
            }
        }, 1000, 1000);
    }

    public static GamemodeCore getInstance() {
        return gamemodeCore;
    }

    public void refresh() {
        // handle leave
        leaveTeam.forEach((uuid, items) -> {
            if (items[1] > Time.millis() + 30000) {
                teams.removeTeam(items[0]);
            }
        });

        // apply modifier later
        Groups.player.forEach(player -> {
            Integer playerTeamId = teams.getTeamIdByPlayerUuid(player.uuid());
            Integer leaderTeamId = teams.getTeamIdByLeaderUuid(player.uuid());
            StringBuilder string = new StringBuilder();

            if (playerTeamId != null) {
                if (player.team().id != playerTeamId)
                    player.team(Team.get(playerTeamId));
                GamemodeTeam playerTeam = teams.getTeam(playerTeamId);
                UpgradeMap upgrade = playerTeam.getUpgradeMapInstane();

                string.append("Team of: " + playerTeam.getLeader() + "[][][][][][][][][][][][]\n");
                string.append("Lvl ").append(playerTeam.getLevel()).append(", XP: ")
                        .append(playerTeam.getXp()).append("/").append(playerTeam.getCost())
                        .append(" - ").append(playerTeam.getXp() / playerTeam.getCost() * 100).append("%\n");
                string.append("HP: + x").append(upgrade.getMaxHpUpgradeCount() * 5).append("%\n");
                string.append("DMG: + x").append(upgrade.getDamageUpgradeCount() * 5).append("%\n");
                string.append("MSPD: + x").append(upgrade.getMovementSpeedUpgradeCount() * 5).append("%\n");
                string.append("Heal: + ").append(upgrade.getHealUpgradeCount() * 40).append("/s\n");

            } else if (leaderTeamId != null) {
                if (player.team().id != leaderTeamId)
                    player.team(Team.get(leaderTeamId));
                GamemodeTeam leaderTeam = teams.getTeam(leaderTeamId);
                UpgradeMap upgrade = leaderTeam.getUpgradeMapInstane();

                string.append("Your team stat: \n");
                string.append("Lvl ").append(leaderTeam.getLevel()).append(", XP: ")
                        .append(leaderTeam.getXp()).append("/").append(leaderTeam.getCost())
                        .append(" - ").append(leaderTeam.getXp() / leaderTeam.getCost() * 100).append("%\n");
                string.append("HP: + x").append(upgrade.getMaxHpUpgradeCount() * 5).append("%\n");
                string.append("DMG: + x").append(upgrade.getDamageUpgradeCount() * 5).append("%\n");
                string.append("MSPD: + x").append(upgrade.getMovementSpeedUpgradeCount() * 5).append("%\n");
                string.append("Heal: + ").append(upgrade.getHealUpgradeCount() * 40).append("/s\n");

            } else if (playerTeamId == null && leaderTeamId == null) {
                player.team(Team.derelict);
                string.append("Type /play to play...");
            }

            string.append(getLeaderboard());
            Call.infoPopupReliable(player.con, string.toString(), 1, 10, player.con.mobile ? 160 : 90, 5, 0, 0);
        });
    }

    public String getLeaderboard() {
        return """
                --- Leaderboard ---
                1: [pink]<meo> []neko-chan
                2: [pink]<meo> []neko-chan
                3: [pink]<meo> []neko-chan
                """;
    }

    public void handlePlayerJoin(Player player) {
        WorldService.changeTeamForPlayer(player.uuid(), Team.derelict);

        if (leaveTeam.containsKey(player.uuid())) {
            leaveTeam.remove(player.uuid());
        }
    }

    public void handlePlayerPlay(String uuid) {
        Player player = WorldService.findPlayerWithUUid(uuid);
        if (!isPlayerPlaying(uuid) && player != null) {
            Postion2D pos = getRandomPlace();
            int teamId = pickRandomizeTeam();

            if (pos == null) {
                Call.sendMessage("You cannot spawn now because server not have enough safety place for new spawm");
                return;
            } else {
                teams.addTeam(teamId, new GamemodeTeam(uuid));
                teams.getTeam(teamId).getAvalableUnits().forEach(unit -> {
                    unit.spawn(Team.get(teamId), pos.x, pos.y);
                });

                player.set(pos.x, pos.y);
                player.unit(Groups.unit.find(u -> u.team == Team.get(teamId)));
            }
        }
    }

    public void handlePlayerLeave(String uuid) {
        if (isPlayerPlaying(uuid) && teams.getTeamIdByLeaderUuid(uuid) != null) {
            leaveTeam.put(uuid, new int[] { teams.getTeamIdByLeaderUuid(uuid), (int) Time.millis() });
        }
    }

    // functions
    public boolean isPlayerPlaying(String uuid) {
        return teams.isPlayerPlaying(uuid);
    }

    public int pickRandomizeTeam() {
        int teamId = random.nextInt(1, 255);

        if (teams.hasTeam(teamId))
            return pickRandomizeTeam();
        else
            return teamId;
    }

    public Postion2D getRandomPlace() {
        boolean[][] playerGrid = getPlayerGrid();
        int width = playerGrid.length;
        int height = playerGrid[0].length;

        List<Postion2D> emptyPositions = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!playerGrid[x][y]) {
                    emptyPositions.add(new Postion2D(x * 50 + 25, y * 50 + 25));
                }
            }
        }

        if (emptyPositions.isEmpty())
            return null;
        return emptyPositions.get(Mathf.random(emptyPositions.size() - 1));
    }

    public boolean[][] getPlayerGrid() {
        int mapWidth = state.map.width / 50;
        int mapHeight = state.map.height / 50;

        boolean[][] gridMap = new boolean[mapWidth][mapHeight];

        Groups.player.forEach(player -> {
            if (player.unit() != null) {
                int gridX = Mathf.clamp((int) player.unit().x / 50, 0, mapWidth - 1);
                int gridY = Mathf.clamp((int) player.unit().y / 50, 0, mapHeight - 1);
                gridMap[gridX][gridY] = true;
            }
        });

        return gridMap;
    }

    public class Postion2D {
        public final int x;
        public final int y;

        public Postion2D(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}

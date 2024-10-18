package catali.core;

import static mindustry.Vars.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Random;

import arc.util.Log;
import arc.util.Time;

import catali.NekoVars;
import catali.java.functions.CommonFunction;
import catali.java.functions.StatExample;
import catali.mindustry.service.WorldService;
import catali.mindustry.world.MapControl;
import catali.types.GamemodeRespawnManager;
import catali.types.GamemodeTeam;
import catali.types.GamemodeTeamManager;
import catali.types.Position2D;
import catali.types.UpgradeMap;

import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;

public class GamemodeCore {
    private final MapControl mapControl = MapControl.getInstance();
    private GamemodeTeamManager teams = new GamemodeTeamManager();
    private Map<String, int[]> leaveTeam = new HashMap<>();
    private static GamemodeCore gamemodeCore = new GamemodeCore();
    private static GamemodeRespawnManager respawnManager = new GamemodeRespawnManager();

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
                handleDestroyTeam(items[0]);
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
                string.append(StatExample.getPlayerTeamStat(playerTeam));
            } else if (leaderTeamId != null) {
                if (player.team().id != leaderTeamId)
                    player.team(Team.get(leaderTeamId));
                GamemodeTeam leaderTeam = teams.getTeam(leaderTeamId);
                string.append(StatExample.getLeaderTeamStat(leaderTeam));
            } else if (playerTeamId == null && leaderTeamId == null) {
                player.team(Team.derelict);
                if (respawnManager.isPlayerInRespawn(player.uuid())) {
                    string.append(StatExample.getDefaultMessage(respawnManager, getLeaderboard()));
                } else {
                    string.append("Type /play to play... \n");
                }
            }

            string.append(getLeaderboard());
            Call.infoPopupReliable(player.con, string.toString(), 1, 10, player.con.mobile ? 160 : 90, 5, 0, 0);
        });
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

            if (respawnManager.isPlayerInRespawn(uuid)) {
                player.sendMessage("You have eliminated. Please waiting...");
            }

            Position2D pos = CommonFunction.getRandomPlace();
            int teamId = pickRandomizeTeam();

            if (pos == null) {
                player.sendMessage("You cannot spawn now because server not have enough safety place for new spawm");
                respawnManager.addPlayerToRespawn(uuid);
                return;
            } else {
                player.team(Team.all[teamId]);
                teams.addTeam(teamId, new GamemodeTeam(uuid));
                
                Call.unitControl(player, WorldService.spawnUnit(UnitTypes.poly, teamId, pos.x, pos.y));
                player.sendMessage("You have spawned in: " + pos.x + ", " + pos.y);
            }
        }
    }

    public void handlePlayerLeave(String uuid) {
        if (isPlayerPlaying(uuid) && teams.getTeamIdByLeaderUuid(uuid) != null) {
            leaveTeam.put(uuid, new int[] { teams.getTeamIdByLeaderUuid(uuid), (int) Time.millis() });
        }
    }

    public void handleBuildingDestroy(Building building, int teamId) {
        if (teams.getTeam(teamId) != null)
            teams.getTeam(teamId).earnExp(10);
    }

    public void handleUnitDestroy(int killerTeamId, Unit destroyTarget) {
        if (teams.getTeam(killerTeamId) != null) {
            teams.getTeam(killerTeamId).earnExp(10);

            if (teams.hasTeam(destroyTarget.team.id)) {
                handleDestroyTeam(destroyTarget.team.id);
            }
        }
    }

    public void handleTeamEarnXp(int teamId, int amount) {

    }

    public void handleSpawnRandomBlock(Position2D pos, boolean isPlayerLowLevel) {
        Position2D randPos = getRandomPositionFromTarget(pos, 50);
        world.tile(randPos.x, randPos.y).setNet(Blocks.copperWall, Team.derelict, 0);
    }

    public void handleDestroyTeam(int teamId) {
        teams.getTeam(teamId).getAllPlayers().forEach(uuid -> {
            respawnManager.addPlayerToRespawn(uuid);
        });
        
        Groups.unit.forEach(unit -> {
            if (unit.team.id == teamId) {
                unit.destroy();
            }
        });

        teams.removeTeam(teamId);
    }

    // :)))))))
    public String getLeaderboard() {
        return """
                --- Leaderboard ---
                1: [pink]<meo> []neko-chan
                2: [pink]<meo> []neko-chan
                3: [pink]<meo> []neko-chan
                """;
    }

    // ---------------------------------------------------------- //

    // functions
    public Set<String> getAllPlayers() {
        return teams.getAllPlayers();
    }
    
    public boolean isPlayerPlaying(String uuid) {
        return teams.isPlayerPlaying(uuid);
    }

    public int pickRandomizeTeam() {
        int teamId = new Random().nextInt(1, 255);

        if (teams.hasTeam(teamId))
            return pickRandomizeTeam();
        else
            return teamId;
    }

    public Position2D getRandomPositionFromTarget(Position2D pos, int radius) {
        Position2D randPos = CommonFunction.getRandomPositionInRadius(radius);
        return new Position2D(randPos.x + pos.x, randPos.y + pos.y);
    }

    public void printTeamStat() {
        Log.info("--- All team stat ---");
        teams.getAllTeams().forEach((teamId, team) -> {
            UpgradeMap upgrade = team.getUpgradeMapInstane();
            StringBuilder string = new StringBuilder();
            string.append("Team ").append(teamId).append("\n");
            string.append("    Leader: ").append(team.getLeader()).append("\n");
            string.append("    Level: ").append(team.getLevel()).append("\n");

            if (team.getAllPlayers().size() > 0) {
                string.append("    Players: \n");
                team.getAllPlayers().forEach(uuid -> string.append("- ").append(uuid).append("\n"));
            } else
                string.append("    No player in this team");

            string.append("    Units avalable: \n");
            team.getAvalableUnits().forEach(units -> string.append("- ").append(units.name).append("\n"));

            string.append("    Upgrades: \n");
            string.append("- HP: + x").append(upgrade.getMaxHpUpgradeCount() * 5).append("%\n");
            string.append("- DMG: + x").append(upgrade.getDamageUpgradeCount() * 5).append("%\n");
            string.append("- MSPD: + x").append(upgrade.getMovementSpeedUpgradeCount() * 5).append("%\n");
            string.append("- Heal: + ").append(upgrade.getHealUpgradeCount() * 40).append("/s\n");

            Log.info(string.toString());
        });
    }
}

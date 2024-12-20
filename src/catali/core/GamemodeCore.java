package catali.core;

import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;
import static catali.NekoVars.*;

import java.util.Set;
import java.util.Random;

import arc.util.Log;
import arc.util.Timer;
import catali.java.functions.CommonFunction;
import catali.java.functions.GamemodeFunction;
import catali.java.functions.StatExample;
import catali.mindustry.service.WorldService;
import catali.mindustry.world.MapControl;
import catali.types.GamemodeLeaveTeamControl;
import catali.types.GamemodeRespawnManager;
import catali.types.GamemodeTeam;
import catali.types.GamemodeTeamManager;
import catali.types.Position2D;
import catali.types.UpgradeMap;

import mindustry.content.Blocks;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.world.Tile;

public class GamemodeCore {
    private final MapControl mapControl = MapControl.getInstance();
    private GamemodeTeamManager teams = new GamemodeTeamManager();
    private GamemodeLeaveTeamControl leaveTeam = new GamemodeLeaveTeamControl();
    private static GamemodeCore gamemodeCore = new GamemodeCore();
    private GamemodeRespawnManager respawnManager = new GamemodeRespawnManager();
    private Random random = new Random();

    // 1. Update and refresh
    private GamemodeCore() {
        taskScheduler.addTask(() -> {
            if (mapControl.isGame()) {
                refresh();
            }
        }, 1000, 1000);
    }

    public static GamemodeCore getInstance() {
        return gamemodeCore;
    }

    public void refresh() {
        leaveTeam.getAllLeaved().forEach(this::handleDestroyTeam);

        Groups.player.forEach(player -> {
            Integer playerTeamId = teams.getTeamIdByPlayerUuid(player.uuid());
            Integer leaderTeamId = teams.getTeamIdByLeaderUuid(player.uuid());

            if (playerTeamId != null) {
                if (player.team().id != playerTeamId)
                    player.team(Team.get(playerTeamId));
            } else if (leaderTeamId != null) {
                if (player.team().id != leaderTeamId)
                    player.team(Team.get(leaderTeamId));
            } else if (playerTeamId == null && leaderTeamId == null) {
                player.team(Team.derelict);
            }

            Call.infoPopupReliable(player.con, getPlayerInfoDisplay(player),
                    1, 10, player.con.mobile ? 160 : 90, 5, 0, 0);
        });

        Groups.unit.forEach(unit -> {
            if (teams.getTeam(unit.team.id) != null) {
                handleApplyEffect(unit, teams.getTeam(unit.team.id).getUpgradeMapInstane());
            }

            if (unit.isPlayer() && teams.getTeamIdByLeaderUuid(unit.getPlayer().uuid()) != null) {
                handleSpawnRandomBlock(new Position2D((int) unit.x / tilesize, (int) unit.y / tilesize),
                        teams.getTeam(unit.team.id).getLevel() < 20 ? true : false);
                handleSpawnRandomUnit(new Position2D((int) unit.x / tilesize, (int) unit.y / tilesize),
                        teams.getTeam(unit.team.id).getLevel() < 20 ? true : false);
            }
        });
    }

    // 2. Player Control
    public void handlePlayerJoin(Player player) {
        WorldService.changeTeamForPlayer(player.uuid(), Team.derelict);

        if (teams.getTeamIdByLeaderUuid(player.uuid()) != null
                && leaveTeam.isPlayerLeave(teams.getTeamIdByLeaderUuid(player.uuid()))) {
            leaveTeam.remove(teams.getTeamIdByLeaderUuid(player.uuid()));
        }

        if (teams.isPlayerPlaying(player.uuid())) {
            Integer playerId = teams.getTeamIdByPlayerUuid(player.uuid());
            Integer leaderId = teams.getTeamIdByLeaderUuid(player.uuid());
            Unit unit;

            if (playerId != null) {
                unit = Groups.unit.find(u -> u.team.id == playerId && !u.isPlayer());
            } else {
                unit = Groups.unit.find(u -> u.team.id == leaderId && !u.isPlayer());
            }

            if (unit != null) {
                player.team(unit.team);
                Call.unitControl(player, unit);
                Call.setCameraPosition(unit.getX(), unit.getY());
            }
        }
    }

    public void handlePlayerLeave(String uuid) {
        if (isPlayerPlaying(uuid) && teams.getTeamIdByLeaderUuid(uuid) != null) {
            leaveTeam.add(teams.getTeamIdByLeaderUuid(uuid));
        }
    }

    public void handlePlayerPlay(String uuid) {
        Player player = WorldService.findPlayerWithUUid(uuid);
        if (!isPlayerPlaying(uuid) && player != null) {
            if (respawnManager.isPlayerInRespawn(uuid)) {
                player.sendMessage("You have eliminated. Please waiting...");
            } else {
                handleCreateTeam(pickRandomizeTeam(), player, CommonFunction.getRandomPlace());
            }
        }
    }

    // 3. Team Manager
    public void handleCreateTeam(int teamId, Player player, Position2D pos) {
        if (pos == null) {
            player.sendMessage("You cannot spawn now because server not have enough safety place for new spawm");
        } else if (teams.hasTeam(teamId)) {
            player.sendMessage("Error. Please try again. (Debug message: Team ID already exists.)");
        } else {
            player.team(Team.all[teamId]);
            teams.addTeam(teamId, new GamemodeTeam(player.uuid()));
            Call.unitControl(player, WorldService.spawnUnit(UnitTypes.poly, teamId, pos.x, pos.y));
            Call.setCameraPosition(player.con, pos.x * tilesize, pos.y * tilesize);
            player.sendMessage("You have spawned in: " + pos.x + ", " + pos.y);
        }
    }

    public void handleDestroyTeam(int teamId) {
        Timer.schedule(() -> {
            if (teams.getTeam(teamId) != null) {
                teams.getTeam(teamId).getAllPlayers().forEach(uuid -> {
                    respawnManager.addPlayerToRespawn(uuid);
                });

                Groups.unit.forEach(unit -> {
                    if (unit.team.id == teamId) {
                        unit.destroy();
                    }
                });

                teams.removeTeam(teamId);
                leaveTeam.remove(teamId);
            }
        }, 1);
    }

    public void handleTeamEarnXp(int teamId, int amount) {
        teams.getTeam(teamId).earnExp(amount);
    }

    public void handleTeamUpgrade(int teamId) {
        teams.getTeam(teamId).upgrade();
    }

    public void handleTeamUpgradeCommand(Player player) {
        Integer team = teams.getTeamIdByLeaderUuid(player.uuid());

        if (team != null) {
            handleTeamUpgrade(team);
        } else {
            player.sendMessage("You are not the leader/ not playing!");
        }
    }

    public void handleTeamRefund(int teamId) {
        teams.getTeam(teamId).refund();
    }

    public void handleTeamRefundCommand(Player player) {
        Integer team = teams.getTeamIdByLeaderUuid(player.uuid());

        if (team != null) {
            handleTeamRefund(team);
        } else {
            player.sendMessage("You are not the leader/ not playing!");
        }
    }

    // 4. Game Event Action handlers
    public void handleBuildingDestroy(Building building, int teamId) {
        if (teams.getTeam(teamId) != null) {
            handleTeamEarnXp(teamId, GamemodeFunction.getXpFromDestroyBuilding(building.block.name));
        }
    }

    public void handleUnitDestroy(int killerTeamId, Unit unit) {
        if (teams.getTeam(killerTeamId) != null) {
            handleTeamEarnXp(killerTeamId, GamemodeFunction.getXpFromKillingUnit(unit.type.name));
        }
    }

    public void handleUnitDestroyEvent(Unit unit) {
        GamemodeTeam team = teams.getTeam(unit.team.id);
        if (team != null) {
            team.removeUnit(unit.type);

            if (team.getAvalableUnits().isEmpty()) {
                handleDestroyTeam(unit.team.id);
            }
        }
    }

    public void handleSpawnRandomBlock(Position2D pos, boolean isPlayerLowLevel) {
        Tile tile = world.tile(Math.min(Math.max(pos.x + randomNumber(50), 4), state.map.width - 4),
                Math.min(Math.max(pos.y + randomNumber(50), 4), state.map.height - 4));
        if (tile.build == null) {
            tile.setNet(Blocks.copperWall, Team.crux, 0);
        }
    }

    public void handleSpawnRandomUnit(Position2D pos, boolean isPlayerLowLevel) {
        int isSpawn = random.nextInt(100);
        if (isSpawn < 50 && isPlayerLowLevel != true){
            Unit unit = WorldService.spawnEUnit(UnitTypes.crawler, Math.min(Math.max(pos.x + randomNumber(50), 4), state.map.width - 4),
                Math.min(Math.max(pos.y + randomNumber(50), 4), state.map.height - 4));
            StatusEffect effect = StatusEffects.none;
            effect.healthMultiplier = 100;
            effect.speedMultiplier = 10;
            effect.damageMultiplier = 100;
            unit.apply(effect, 1.01f);
        }
    }

    public int randomNumber(int range) {
        return random.nextInt(range) - (range / 2);
    }

    public void handleApplyEffect(Unit unit, UpgradeMap upgradeMap) {
        StatusEffect effect = StatusEffects.none;
        effect.healthMultiplier = (upgradeMap.getHealUpgradeCount() * 0.05f) + 1;
        effect.speedMultiplier = (upgradeMap.getMovementSpeedUpgradeCount() * 0.05f) + 1;
        effect.damageMultiplier = (upgradeMap.getDamageUpgradeCount() * 0.05f) + 1;
        effect.damage = -(upgradeMap.getHealUpgradeCount() * 0.67f);
        unit.apply(effect, 1.01f);
    }

    // 5. Info & Stats
    public String getPlayerInfoDisplay(Player player) {
        StringBuilder string = new StringBuilder();
        Integer playerTeamId = teams.getTeamIdByPlayerUuid(player.uuid());
        Integer leaderTeamId = teams.getTeamIdByLeaderUuid(player.uuid());

        if (playerTeamId != null) {
            GamemodeTeam playerTeam = teams.getTeam(playerTeamId);
            string.append(StatExample.getPlayerTeamStat(playerTeam));
        } else if (leaderTeamId != null) {
            GamemodeTeam leaderTeam = teams.getTeam(leaderTeamId);
            string.append(StatExample.getLeaderTeamStat(leaderTeam));
        } else {
            if (respawnManager.isPlayerInRespawn(player.uuid())) {
                string.append("Please wait " + respawnManager.getRemainingRespawnTime(player.uuid()) / 1000
                        + "s until respawn.\n");
            } else {
                string.append("Type /play to play... \n");
            }
        }

        string.append(teams.getLeaderboard());
        return string.toString();
    }

    // 6. Support Functions
    public Set<String> getAllPlayers() {
        return teams.getAllPlayers();
    }

    public boolean isPlayerPlaying(String uuid) {
        Integer playerId = teams.getTeamIdByPlayerUuid(uuid);
        Unit unit = Groups.unit.find(u -> u.team.id == playerId && u.isPlayer());
        return teams.isPlayerPlaying(uuid) && unit != null;
    }

    public int pickRandomizeTeam() {
        int teamId = random.nextInt(7, 255);

        if (teams.hasTeam(teamId))
            return pickRandomizeTeam();
        else
            return teamId;
    }

    public void printTeamStat() {
        Log.info("--- All team stat ---");
        teams.getAllTeams().forEach((teamId, team) -> {
            String teamStat = StatExample.buildTeamStatString(teamId, team);
            Log.info(teamStat);
        });
    }
}
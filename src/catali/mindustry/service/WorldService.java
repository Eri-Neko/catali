package catali.mindustry.service;

import mindustry.game.Gamemode;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.maps.Map;
import mindustry.net.WorldReloader;
import mindustry.type.UnitType;

import arc.util.Log;

import static mindustry.Vars.*;
import static catali.NekoVars.*;

public class WorldService {
    public static Player findPlayerWithUUid(String uuid) {
        return Groups.player.find(p -> p.uuid().equals(uuid));
    }

    public static void changeTeamForPlayer(String uuid, Team team) {
        Player player = findPlayerWithUUid(uuid);
        if (player != null) {
            player.team(team);
        }
    }

    public static boolean loadMap(Map map) {
        try {
            if (state.isGame()) {
                WorldReloader worldReloader = new WorldReloader();
                logic.reset();
                worldReloader.begin();
                world.loadMap(map);
                worldReloader.end();
                logic.play();
            } else {
                logic.reset();
                world.loadMap(map);
                logic.play();
            }

            state.rules = map.applyRules(Gamemode.pvp);
            state.rules.pvpAutoPause = false;
            state.rules.canGameOver = false;
            Log.info("Map loaded. Map name: @", map.plainName());
            return true;
        } catch (Exception e) {
            Log.err("Fail to load map: " + map.plainName(), e);
            return false;
        }
    }

    public static Unit spawnUnit(UnitType unitType, int teamId, int x, int y) {
        if (mapControl.isGame()) {
            unitType.flying = true;
            Unit unit = unitType.spawn(x, y);
            unit.team(Team.get(teamId));
            unit.set(x * tilesize, y * tilesize);
            return unit;
        } else
            return null;
    }
}

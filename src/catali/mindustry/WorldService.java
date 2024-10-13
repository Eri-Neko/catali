package catali.mindustry;

import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.maps.Map;
import mindustry.net.WorldReloader;

import static mindustry.Vars.*;

import arc.util.Log;

public class WorldService {
    public static Player findPlayerWithUUid(String uuid) {
        return Groups.player.find(p -> p.uuid() == uuid);
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
                Log.info("mew mew");
            } else {
                world.loadMap(map);
            }

            state.rules.pvpAutoPause = false;
            state.rules.canGameOver = false;
            Log.info("Map loaded. Map name: @", map.plainName());
            return true;
        } catch (Exception e) {
            Log.err("Fail to load map: " + map.plainName(), e);
            return false;
        }
    }
}

package catali.core;

import arc.Events;
import catali.mindustry.service.MindustryService;
import catali.mindustry.world.MapControl;
import mindustry.game.EventType.BuildingBulletDestroyEvent;
import mindustry.game.EventType.PlayerJoin;
import mindustry.game.EventType.PlayerLeave;
import mindustry.game.EventType.UnitBulletDestroyEvent;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.gen.Unit;

public class NekoGameplay {
    private static final NekoGameplay nekoGameplay = new NekoGameplay();
    private static final GamemodeCore gamemodeCore = GamemodeCore.getInstance();
    private final MapControl mapControl = MapControl.getInstance();

    private NekoGameplay() {

    }

    public static NekoGameplay getNekoGameplay() {
        return nekoGameplay;
    }

    // look like call event more...
    public void init() {
        mapControl.startAutomaticRandomEndless();
        Events.on(PlayerJoin.class, event -> handlePlayerJoin(event.player));
        Events.on(PlayerLeave.class, event -> handlePlayerLeave(event.player));
        Events.on(BuildingBulletDestroyEvent.class, event -> handleBuildingDestroy(event.build, event.bullet.team.id));
        Events.on(UnitBulletDestroyEvent.class, event -> handleUnitDestroy(event.bullet.team.id, event.unit));
    }

    public void handlePlayerJoin(Player player) {
        if (mapControl.isGame()) {
            MindustryService.showJoin(player);
            showPlay(player);
            gamemodeCore.handlePlayerJoin(player);
        }
    }
    
    public void handlePlayerLeave(Player player) {
        if (mapControl.isGame()) {
            gamemodeCore.handlePlayerLeave(player.uuid());
        }
    }
    
    public void handlePlayerPlay(String uuid) {
        if (mapControl.isGame()) {
            gamemodeCore.handlePlayerPlay(uuid);
        }
    }

    public void handleBuildingDestroy(Building building, int teamId) {
        if (mapControl.isGame()) {
            gamemodeCore.handleBuildingDestroy(building, teamId);
        }
    }

    public void handleUnitDestroy(int killerTeamId, Unit destroyTarget) {
        if (mapControl.isGame()) {
            gamemodeCore.handleUnitDestroy(killerTeamId, destroyTarget);
        }
    }

    // another service
    public void showPlay(Player player) {
        if (gamemodeCore.isPlayerPlaying(player.uuid())) {
            Call.sendMessage("You playing this game, not need to do it again.");
        } else {
            MindustryService.showPlay(player, p -> handlePlayerPlay(p.uuid()));
        }
    }
}

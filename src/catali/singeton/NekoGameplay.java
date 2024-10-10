package catali.singeton;

import arc.Events;
import arc.util.Align;

import catali.NekoVars;
import catali.mindustry.MindustryService;

import mindustry.game.EventType.BlockDestroyEvent;
import mindustry.game.EventType.PlayerJoin;
import mindustry.game.EventType.PlayerLeave;
import mindustry.game.EventType.UnitDestroyEvent;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;

public class NekoGameplay {
    private static final NekoGameplay nekoGameplay = new NekoGameplay();
    private static final GamemodeCore gamemodeCore = GamemodeCore.getInstance();
    private final MapControl mapControl = MapControl.getInstance();

    private NekoGameplay() {

    }

    public static NekoGameplay getNekoGameplay() {
        return nekoGameplay;
    }

    public void init() {
        mapControl.startAutomaticRandomEndless();
        Events.on(PlayerJoin.class, event -> handlePlayerJoin(event.player));
        Events.on(PlayerLeave.class, event -> handlePlayerLeave(event.player));
        Events.on(BlockDestroyEvent.class, null);
        Events.on(UnitDestroyEvent.class, null);
        NekoVars.taskScheduler.addTask(this::refresh, 0, 1000);
    }

    public void refresh() {
        Groups.player.each(player -> {
            String message = """
                    Meow meow ~~~
                    """;

            Call.infoPopupReliable(player.con, message, 1.01f, Align.topLeft, player.con.mobile ? 160 : 90, 5, 0, 0);
        });
    }

    public void handlePlayerJoin(Player player) {
        if (mapControl.isGame()) {
            MindustryService.showJoin(player);
            showPlay(player);
            gamemodeCore.handlePlayerJoin(player);
        }
    }

    public void handlePlayerPlay(String uuid) {
        if (mapControl.isGame()) {
            gamemodeCore.handlePlayerPlay(uuid);
        }
    }

    public void handlePlayerLeave(Player player) {
        if (mapControl.isGame()) {
            gamemodeCore.handlePlayerLeave(player.uuid());
        }
    }

    // another service
    public void showPlay(Player player) {
        if (gamemodeCore.isPlayerPlaying(player.uuid())) {
            MindustryService.showPlay(player, p -> handlePlayerPlay(p.uuid()));
        } else {
            Call.sendMessage("You playing this game, not need to do it again.");
        }
    }
}

package catali.singeton;

import static mindustry.Vars.world;

import arc.Events;
import arc.util.Align;
import catali.NekoVars;
import catali.mindustry.MindustryService;
import catali.mindustry.WorldService;
import mindustry.game.Team;
import mindustry.game.EventType.PlayerJoin;
import mindustry.game.EventType.PlayerLeave;
import mindustry.gen.Call;
import mindustry.gen.Groups;

public class NekoGameplay {
    private static final NekoGameplay nekoGameplay = new NekoGameplay();
    private final MapControl mapControl = MapControl.getInstance();

    private NekoGameplay() {

    }

    public static NekoGameplay getNekogameplay() {
        return nekoGameplay;
    }

    public void init() {
        mapControl.startAutomaticRandomEndless();

        Events.on(PlayerJoin.class, event -> {
            if (mapControl.isGame()) {
                WorldService.changeTeamForPlayer(event.player.uuid(), Team.derelict);
                MindustryService.showJoin(event.player);
                MindustryService.showPlay(event.player, (player) -> handlePlayerPlay(player.uuid()));
            }
        });

        Events.on(PlayerLeave.class, event -> {

        });

        NekoVars.taskScheduler.addTask(NekoGameplay::refresh, 0, 1000);
    }

    public static void refresh() {
        Groups.player.each(player -> {
            String message = """
                    Meow meow ~~~
                    """;

            Call.infoPopupReliable(player.con, message, 1.01f, Align.topLeft, player.con.mobile ? 160 : 90, 5, 0, 0);
        });
    }

    public void handlePlayerPlay(String uuid) {
        
    }

    public void handlePlayerLeave(String uuid) {

    }
}

package catali;

import arc.Core;
import arc.util.CommandHandler;
import arc.util.Log;
import catali.mindustry.service.MindustryService;
import mindustry.gen.Player;
import mindustry.mod.Plugin;

import static mindustry.Vars.*;
import static catali.NekoVars.*;

public class NekoPlugin extends Plugin {
    @Override
    public void init() {
        Log.info("--- Catali.io Gamemode Start ---");
        Log.info("This plugin all automatic - Mean no console control anymore");
        Log.info("Only neko can debug with console");
        Log.info("By Jung Ha Min and Neko");
        NekoVars.init();
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.removeCommand("a");
        handler.removeCommand("votekick");
        handler.removeCommand("vote");

        handler.<Player>register("wiki", "Open game wiki", (args, player) -> {
            MindustryService.showWiki(player);
        });

        handler.<Player>register("play", "Play game", (args, player) -> {
            nekoGameplay.showPlay(player);
        });

        handler.<Player>register("upgrade", "Upgrade", (args, player) -> {
            gamemodeCore.handleTeamUpgradeCommand(player);
        });

        handler.<Player>register("hack", "[password]", "hack the game meow", (args, player) -> {
            if (args.length > 0 && args[0].equals("test")) {
                player.sendMessage("Run for safety or server will crash");
                gamemodeCore.handleTeamEarnXp(player.team().id, 1000000);
            } else {
                player.sendMessage("Wrong password meow");
            }
        });
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.removeCommand("exit");

        handler.register("exit", "Custom shutdown method", args -> {
            Log.info("Shutting down server.");
            NekoVars.shutdown();
            net.dispose();
            Core.app.exit();
        });

        handler.register("playersplay", "Show list player play game", args -> {
            Log.info(" - List of all player playing - ");
            gamemodeCore.getAllPlayers().forEach(uuid -> Log.info(uuid));
        });

        handler.register("teamstat", "Show team stat", args -> {
            gamemodeCore.printTeamStat();
        });
    }

    public static final String infoGameplay = """
            Hello, welcome to Catali.io Gamemode
            Catali.io is endless gameplay. Your target is destroy as much as possible another player
            You can earn xp while you destroy a block or player to earn xp
            You can join another team if leader this team have enough unit (/join)
            You can accept to anyone join you or deny while join message show
            Have fun! (Wiki: /wiki)
            """;

    public static final String wiki = """
            --- Introduce to play ---

            Changelog will apper there
            Arlet: You cant kick player in your team - be careful to accept
            Only leader can upgrade unit!
            Xp start in 50 and will increse 50 per level and no limit to upgrade

            Xp earn for destroy one serpulo / mono tree (-1 tier) unit:

            t1: 5xp
            t2: 20xp
            t3: 80xp
            t4: 150xp
            t5: 400xp

            Erekir and navals:

            t1: 20xp
            t2: 80xp
            t3: 150xp
            t4: 400xp
            t5: 600xp

            Xp earn for destroy wall:

            Type: size 1x1, 2x2
            Copper: 10, 40
            Titan: 20, 100
            Berilium: 30, 150
            Plastatium: 35, 180
            Tunsteng: 40, 200
            Thorium: 40, 220
            Phase: 50, 250
            Surge: 80, 400
            Carbiden: 100, 500
            Erekir Surge: 120, 600

            Another blocks:
            Container: 200xp
            Vault: 500xp
            Erekir Container: 500xp
            Erekir Vault: 2000xp

            Common upgrade:
            + 5% hp
            + heal 40hp/s
            + 5% unit speed
            + 5% unit damage

            Rare upgrade (every 5 level):
            + 1 unit (max 5)
            upgrade 1 unit to next tier (erekir and fly naval in level 10)
            deal 8k aoe dmg 20 tile from leader and apply heal effect 1k hp/s in 20s all units

            ----------------------------
            Changelog ---- 1.0 beta test

            Build core of game - nothing special

            Comming soon ~~~ Meow!
            """;
}

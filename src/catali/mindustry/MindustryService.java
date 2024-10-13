package catali.mindustry;

import catali.NekoPlugin;
import catali.mindustry.InfoDisplay.DisplayPack;
import catali.mindustry.InfoDisplay.InfoDisplayRunnable;
import mindustry.gen.Player;

import static catali.NekoVars.*;

public class MindustryService {
	public static void showPlay(Player player, PlayerService playerService) {
		infoDisplay.showDisplay(player, new DisplayPack(0,
				"[blue]--- Option Menu ---",
				"[blue]Are you want to play?",
				new String[][] { { "[green]Yes", "[red]No" } }, 
				new InfoDisplayRunnable[] {
						playerService::action,
						MindustryService::doNothing
				}));
	}

	public static void showJoin(Player player) {
		infoDisplay.showDisplay(player, new DisplayPack(1000,
				"[blue]--- Welcome To Catali.io ---",
				"""
						Hello! Is your first time to play this gamemode?
						Click [green]welcome button[] to see introduce of gameplay
						""",
				new String[][] {
						{ "[green]Welcome", "[red]Close" }
				},
				new InfoDisplayRunnable[] {
						MindustryService::showWelcome,
						MindustryService::doNothing
				}));
	}

	public static void showWelcome(Player player) {
		infoDisplay.showDisplay(player, new DisplayPack(1001,
				"[blue]--- Welcome To Catali.io ---",
				NekoPlugin.infoGameplay, new String[][] {
						{ "[green]Wiki", "[red]Exit" }
				},
				new InfoDisplayRunnable[] {
						MindustryService::showWiki,
						MindustryService::doNothing
				}));
	}

	public static void showWiki(Player player) {
		infoDisplay.showDisplay(player, new DisplayPack(1002,
				"[blue]--- Wiki Of Catali.io ---",
				NekoPlugin.wiki, new String[][] { { "[red]Exit" } },
				new InfoDisplayRunnable[] {
						MindustryService::doNothing
				}));
	}

	public static void doNothing(Object... object) {
		// do nothing
	}

	@FunctionalInterface
	public static interface PlayerService {
		void action(Player player);
	}
}

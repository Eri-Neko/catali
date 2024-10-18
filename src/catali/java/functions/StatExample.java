package catali.java.functions;

import catali.types.GamemodeRespawnManager;
import catali.types.GamemodeTeam;
import catali.types.UpgradeMap;

public class StatExample {
    public static String getPlayerTeamStat(GamemodeTeam playerTeam) {
        UpgradeMap upgrade = playerTeam.getUpgradeMapInstane();
        return "Team of: " + playerTeam.getLeader() + "\n"
                + "Lvl " + playerTeam.getLevel() + ", XP: "
                + playerTeam.getXp() + "/" + playerTeam.getCost() + " - "
                + ((playerTeam.getXp() / playerTeam.getCost()) * 100) + "%\n"
                + "HP: + x" + upgrade.getMaxHpUpgradeCount() * 5 + "%\n"
                + "DMG: + x" + upgrade.getDamageUpgradeCount() * 5 + "%\n"
                + "MSPD: + x" + upgrade.getMovementSpeedUpgradeCount() * 5 + "%\n"
                + "Heal: + " + upgrade.getHealUpgradeCount() * 40 + "/s\n";
    }

    public static String getLeaderTeamStat(GamemodeTeam leaderTeam) {
        UpgradeMap upgrade = leaderTeam.getUpgradeMapInstane();
        return "Your team stat: \n"
                + "Lvl " + leaderTeam.getLevel() + ", XP: "
                + leaderTeam.getXp() + "/" + leaderTeam.getCost() + " - "
                + leaderTeam.getXp() / leaderTeam.getCost() * 100 + "%\n"
                + "HP: + x" + upgrade.getMaxHpUpgradeCount() * 5 + "%\n"
                + "DMG: + x" + upgrade.getDamageUpgradeCount() * 5 + "%\n"
                + "MSPD: + x" + upgrade.getMovementSpeedUpgradeCount() * 5 + "%\n"
                + "Heal: + " + upgrade.getHealUpgradeCount() * 40 + "/s\n";
    }

    public static String getDefaultMessage(GamemodeRespawnManager respawnManager, String uuid) {
        if (respawnManager.isPlayerInRespawn(uuid)) {
            return "Please wait " + respawnManager.getRemainingRespawnTime(uuid) / 1000 + "s until respawn.\n";
        } else {
            return "Type /play to play... \n";
        }
    }
}

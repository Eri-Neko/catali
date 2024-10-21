package catali.java.functions;

import catali.types.GamemodeTeam;
import catali.types.UpgradeMap;

public class StatExample {
    public static String getPlayerTeamStat(GamemodeTeam playerTeam) {
        UpgradeMap upgrade = playerTeam.getUpgradeMapInstane();
        return "Team of: " + playerTeam.getLeader() + "\n"
                + "Lvl " + playerTeam.getLevel() + ", XP: "
                + playerTeam.getXp() + "/" + playerTeam.getCost() + " - "
                + (int) (((float) playerTeam.getXp() / playerTeam.getCost()) * 100) + "%\n"
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
                + (int) (((float) leaderTeam.getXp() / leaderTeam.getCost()) * 100) + "%\n"
                + "HP: + x" + upgrade.getMaxHpUpgradeCount() * 5 + "%\n"
                + "DMG: + x" + upgrade.getDamageUpgradeCount() * 5 + "%\n"
                + "MSPD: + x" + upgrade.getMovementSpeedUpgradeCount() * 5 + "%\n"
                + "Heal: + " + upgrade.getHealUpgradeCount() * 40 + "/s\n";
    }

    public static String buildTeamStatString(int teamId, GamemodeTeam team) {
        UpgradeMap upgrade = team.getUpgradeMapInstane();
        StringBuilder string = new StringBuilder();
        string.append("Team ").append(teamId).append("\n");
        string.append("    Leader: ").append(team.getLeader()).append("\n");
        string.append("    Level: ").append(team.getLevel()).append("\n");

        if (team.getAllPlayers().size() > 0) {
            string.append("    Players: \n");
            team.getAllPlayers().forEach(uuid -> string.append("- ").append(uuid).append("\n"));
        } else {
            string.append("    No player in this team\n");
        }

        string.append("    Units available: \n");
        team.getAvalableUnits().forEach(units -> string.append("- ").append(units.name).append("\n"));

        string.append("    Upgrades: \n");
        string.append("- HP: + x").append(upgrade.getMaxHpUpgradeCount() * 5).append("%\n");
        string.append("- DMG: + x").append(upgrade.getDamageUpgradeCount() * 5).append("%\n");
        string.append("- MSPD: + x").append(upgrade.getMovementSpeedUpgradeCount() * 5).append("%\n");
        string.append("- Heal: + ").append(upgrade.getHealUpgradeCount() * 40).append("/s\n");

        return string.toString();
    }
}

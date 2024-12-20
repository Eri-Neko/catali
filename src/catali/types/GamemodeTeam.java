package catali.types;

import java.util.*;

import mindustry.content.UnitTypes;
import mindustry.type.UnitType;

public class GamemodeTeam {
    private final String leaderUuid;
    private final Set<String> teammateUUids = new HashSet<>();
    private final List<UnitType> units = new ArrayList<>();
    private final UpgradeMap commonUpgrade = new UpgradeMap();
    private final GamemodeXp xp = new GamemodeXp();

    public GamemodeTeam(String uuid) {
        leaderUuid = uuid;
        units.add(UnitTypes.poly);
    }

    // players function
    public String getLeader() {
        return leaderUuid;
    }

    public boolean isPlayerInTeam(String uuid) {
        return leaderUuid == uuid | teammateUUids.contains(uuid);
    }

    public void addPlayer(String uuid) {
        teammateUUids.add(uuid);
    }

    public void removePlayer(String uuid) {
        teammateUUids.remove(uuid);
    }

    public Set<String> getAllPlayers() {
        Set<String> list = new HashSet<>(teammateUUids);
        list.add(leaderUuid);
        return list;
    }

    // unit in team function
    public void addUnit(UnitType unit) {
        units.add(unit);
    }

    public void removeUnit(UnitType unit) {
        units.remove(unit);
    }

    public List<UnitType> getAvalableUnits() {
        return units;
    }

    // return common upgrade instance
    public UpgradeMap getUpgradeMapInstane() {
        return commonUpgrade;
    }

    // xp function
    public void earnExp(int amout) {
        if (xp.earnXp(amout)) {
            xp.upgrade(this);
        }     
    }

    public boolean isAvalableUpgrade() {
        return xp.isAvalabeUpgrade();
    }

    public void upgrade() {
        xp.upgrade(this);
    }

    public void refund() {
        xp.refund(this);
    }

    public int getLevel() {
        return xp.getLevel();
    }

    public int getXp() {
        return xp.getXp();
    }

    public int getCost() {
        return xp.getCost();
    }
}
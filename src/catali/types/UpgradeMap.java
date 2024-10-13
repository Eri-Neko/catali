package catali.types;

import java.util.HashMap;
import java.util.Map;

public class UpgradeMap {
    private Map<String, Integer> upgradeCountMap = new HashMap<>();

    public UpgradeMap() {
        upgradeCountMap.put("Increase_Max_HP", 0);
        upgradeCountMap.put("Increase_Movement_Speed", 0);
        upgradeCountMap.put("Increase_Damage", 0);
        upgradeCountMap.put("Increase_Heal", 0);
    }

    //
    public void addMaxHpUpgrade(int amount) {
        upgradeCountMap.put("Increase_Max_HP", upgradeCountMap.get("Increase_Max_HP") + amount);
    }

    public void addMovementSpeedUpgrade(int amount) {
        upgradeCountMap.put("Increase_Movement_Speed", upgradeCountMap.get("Increase_Movement_Speed") + amount);
    }

    public void addDamageUpgrade(int amount) {
        upgradeCountMap.put("Increase_Damage", upgradeCountMap.get("Increase_Damage") + amount);
    }

    public void addHealUpgrade(int amount) {
        upgradeCountMap.put("Increase_Heal", upgradeCountMap.get("Increase_Heal") + amount);
    }

    public int getMaxHpUpgradeCount() {
        return upgradeCountMap.get("Increase_Max_HP");
    }

    public int getMovementSpeedUpgradeCount() {
        return upgradeCountMap.get("Increase_Movement_Speed");
    }

    public int getDamageUpgradeCount() {
        return upgradeCountMap.get("Increase_Damage");
    }

    public int getHealUpgradeCount() {
        return upgradeCountMap.get("Increase_Heal");
    }

    public Map<String, Integer> getAllUpgradeCounts() {
        return new HashMap<>(upgradeCountMap); // Trả về một bản sao của map để bảo toàn dữ liệu
    }
}

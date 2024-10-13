package catali.types;

public class GamemodeXp {
    private int level = 1;
    private int xp = 0; 
    private int cost = 10;

    // getter
    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getCost() {
        return cost;
    }

    // functions
    public boolean earnXp(int amount) {
        xp += amount * getXpMultiplier();
        if (xp >= cost) {
            xp -= cost;
            levelUp();
            return true;
        }
        return false;
    }

    private double getXpMultiplier() {
        if (level < 20) return 1.4;
        if (level < 40) return 1.0;
        if (level < 60) return 0.85;
        if (level < 100) return 0.7;
        return 0.6;
    }

    private void levelUp() {
        level++;
        cost += getCostIncrease();
    }

    private int getCostIncrease() {
        if (level < 20) return 10;
        if (level < 40) return 20;
        if (level < 60) return 30;
        if (level < 100) return 40;
        return 50;
    }
}

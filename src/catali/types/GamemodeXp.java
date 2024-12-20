package catali.types;

import static catali.NekoVars.infoDisplay;
import static mindustry.Vars.tilesize;

import java.util.Set;

import arc.util.Timer;
import catali.java.functions.GamemodeFunction;
import catali.mindustry.InfoDisplay.types.DisplayPack;
import catali.mindustry.InfoDisplay.types.InfoDisplayRunnable;
import catali.mindustry.service.MindustryService;
import catali.mindustry.service.WorldService;
import mindustry.content.UnitTypes;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class GamemodeXp {
    private int level = 1;
    private int upgrade = 0;
    private int upgraded = 0;
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
            earnXp(0);
            return true;
        }

        return false;
    }

    // check leader is online and avalable of stack upgrade.
    public void upgrade(GamemodeTeam team) {
        Player leader = Groups.player.find(player -> player.uuid() == team.getLeader());

        if (leader != null) {
            if (isAvalabeUpgrade()) {
                if (upgraded % 5 == 3) {
                    infoDisplay.showDisplay(leader, new DisplayPack(11,
                            "--- Rare Upgrade ---",
                            "Please pick one upgrade", new String[][] {
                                    { "+1 unit for your team (poly)" },
                                    { "Upgrade specfic unit to next tier" },
                                    { "Deal 8000 AOE DMG in radius 40 blocks and heal all unit in team" },
                                    { "Cancel" }
                            },
                            new InfoDisplayRunnable[] {
                                    player -> {
                                        team.addUnit(UnitTypes.poly);
                                        WorldService.spawnUnit(UnitTypes.poly, player.team().id,
                                                (int) player.x / tilesize, (int) player.y / tilesize);
                                        finishUpgrade(team);
                                    },

                                    player -> {
                                        String[][] options = new String[team.getAvalableUnits().size()][];
                                        InfoDisplayRunnable[] actions = new InfoDisplayRunnable[team.getAvalableUnits()
                                                .size()];
                                        int counter = 0;

                                        for (UnitType unit : team.getAvalableUnits()) {
                                            options[counter] = new String[] { unit.name };
                                            actions[counter] = p -> {
                                                Set<UnitType> avalableUnits = GamemodeFunction.getNextTier(unit.name);

                                                if (avalableUnits.size() > 0) {
                                                    String[][] chooseOptions = new String[avalableUnits.size()][];
                                                    InfoDisplayRunnable[] chooseResult = new InfoDisplayRunnable[avalableUnits
                                                            .size()];
                                                    int count = 0;

                                                    for (UnitType avalableUnit : avalableUnits) {
                                                        chooseOptions[count] = new String[] { avalableUnit.name };
                                                        chooseResult[count] = pl -> {
                                                            Unit destroyTarget = Groups.unit.find(
                                                                    u -> u.team.id == pl.team().id && u.type == unit);

                                                            Unit newUnit = WorldService.spawnUnit(avalableUnit,
                                                                    pl.team().id,
                                                                    40, 40);

                                                            if (destroyTarget != null && pl.unit().id == destroyTarget.id) {
                                                                Call.unitControl(pl, newUnit);
                                                            }

                                                            team.addUnit(avalableUnit);
                                                            team.removeUnit(unit);

                                                            if (destroyTarget != null) {
                                                                destroyTarget.destroy();
                                                            }
                                                            finishUpgrade(team);
                                                        };

                                                        count++;
                                                    }

                                                    infoDisplay.showDisplay(p, new DisplayPack(13,
                                                            "--- Choose Upgrade To ---",
                                                            "Choose unit to upgrade", chooseOptions, chooseResult));
                                                }
                                            };

                                            counter++;
                                        }

                                        infoDisplay.showDisplay(player, new DisplayPack(12,
                                                "--- Choose Unit For Upgrade ---",
                                                "Please choose unit for upgrade", options, actions));
                                    },
                                    player -> {
                                        infoDisplay.showDisplay(player,
                                                new DisplayPack(14, "--- Still In Dev ---",
                                                        "Please choose another upgrade, this is still tesing",
                                                        new String[][] {
                                                                { "Exit" }
                                                        },
                                                        new InfoDisplayRunnable[] { MindustryService::doNothing }));
                                    },
                                    MindustryService::doNothing
                            }));
                } else {
                    UpgradeMap upgradeMap = team.getUpgradeMapInstane();

                    infoDisplay.showDisplay(leader, new DisplayPack(10,
                            "--- Common Upgrade --",
                            "Please pick your upgrade. Your current upgrade: " + upgrade,
                            new String[][] {
                                    { "+ 5% Damage", "+ 40hp/s Healing" },
                                    { "+ 5% Max HP", "+ 5% Movement SPD" },
                                    { "Cancel" }
                            },

                            new InfoDisplayRunnable[] {
                                    player -> {
                                        upgradeMap.addDamageUpgrade(1);
                                        finishUpgrade(team);
                                    },
                                    player -> {
                                        upgradeMap.addHealUpgrade(1);
                                        finishUpgrade(team);
                                    },
                                    player -> {
                                        upgradeMap.addMaxHpUpgrade(1);
                                        finishUpgrade(team);
                                    },
                                    player -> {
                                        upgradeMap.addMovementSpeedUpgrade(1);
                                        finishUpgrade(team);
                                    },
                                    MindustryService::doNothing
                            }));

                }
            } else {
                leader.sendMessage("You not have enough upgrade points for upgrade");
            }
        }
    }

    public void refund(GamemodeTeam team) {
    Player leader = Groups.player.find(player -> player.uuid() == team.getLeader());

    if (leader != null) {
        if (isAvalabeRefund()) {
            String[][] options = new String[team.getAvalableUnits().size() + 1][];
            InfoDisplayRunnable[] actions = new InfoDisplayRunnable[team.getAvalableUnits()
                .size() + 1];
            int counter = 0;

            for (UnitType unit: team.getAvalableUnits()) {
                options[counter] = new String[] {
                    unit.name
                };
                actions[counter] = p -> {
                    Set < UnitType > avalableUnits = GamemodeFunction.getPrevTier(unit.name);

                    if (avalableUnits.size() > 0) {

                        String[][] chooseOptions = new String[avalableUnits.size() + 1][];
                        InfoDisplayRunnable[] chooseResult = new InfoDisplayRunnable[avalableUnits
                            .size() + 1];
                        int count = 0;
                        for (UnitType avalableUnit: avalableUnits) {
                            Unit destroyTarget = Groups.unit.find(
                                u -> u.team.id == pl.team().id && u.type == unit);

                            Unit newUnit = WorldService.spawnUnit(avalableUnit,
                                pl.team().id,
                                40, 40);

                            if (destroyTarget != null && pl.unit().id == destroyTarget.id) {
                                Call.unitControl(pl, newUnit);
                            }

                            team.addUnit(avalableUnit);
                            team.removeUnit(unit);

                            if (destroyTarget != null) {
                                destroyTarget.destroy();
                            }
                            finishRefund(team);

                            count++;
                        };
                    }
                };

                counter++;
            };
            options[counter] = new String[] {
                "Cancel"
            };
            actions[counter] = p -> {
                MindustryService::doNothing;
            };

            infoDisplay.showDisplay(player, new DisplayPack(12,
                "--- Choose Unit For Upgrade ---",
                "Please choose unit for upgrade", options, actions));
        } else {
            leader.sendMessage("You not have enough upgrade for refund");
        }
    }
}

    public void finishUpgrade(GamemodeTeam team) {
        upgrade--;
        upgraded++;
        if (isAvalabeUpgrade()) {
            upgrade(team);
        }
    }

    public void finishRefund(GamemodeTeam team) {
        upgrade++;
        upgraded--;
    }

    public boolean isAvalabeUpgrade() {
        return upgrade > 0 ? true : false;
    }

    public boolean isAvalabeRefund() {
        return upgraded > 0 ? true : false;
    }

    private double getXpMultiplier() {
        if (level < 20)
            return 1.4;
        if (level < 40)
            return 1.0;
        if (level < 60)
            return 0.85;
        if (level < 100)
            return 0.7;
        return 0.6;
    }

    private void levelUp() {
        level++;
        upgrade++;
        cost += getCostIncrease();
    }

    private int getCostIncrease() {
        if (level < 20)
            return 10;
        if (level < 40)
            return 20;
        if (level < 60)
            return 30;
        if (level < 100)
            return 40;
        return 50;
    }
}

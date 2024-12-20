package catali.java.functions;

import java.util.*;

import mindustry.type.UnitType;

import static mindustry.content.UnitTypes.*;

public class GamemodeFunction {
    public static int getXpFromDestroyBuilding(String buildName) {
        switch (buildName) {
            case "copper-wall":
                return 10;
            case "copper-wall-large":
                return 40;
            case "titanium-wall":
                return 20;
            case "titanium-wall-large":
                return 100;
            case "beryllium-wall":
                return 30;
            case "beryllium-wall-large":
                return 150;
            case "plastanium-wall":
                return 35;
            case "plastanium-wall-large":
                return 180;
            case "tungsten-wall":
                return 40;
            case "tungsten-wall-large":
                return 200;
            case "thorium-wall":
                return 40;
            case "thorium-wall-large":
                return 220;
            case "phase-wall":
                return 50;
            case "phase-wall-large":
                return 250;
            case "surge-wall":
                return 80;
            case "surge-wall-large":
                return 400;
            case "carbide-wall":
                return 100;
            case "carbide-wall-large":
                return 500;
            case "reinforced-surge-wall":
                return 120;
            case "reinforced-surge-wall-large":
                return 600;
            case "container":
                return 200;
            case "vault":
                return 500;
            case "reinforced-container":
                return 500;
            case "reinforced-vault":
                return 2000;
            default:
                return 10;
        }
    }

    public static int getXpFromKillingUnit(String unitName) {
        switch (unitName) {
            case "dagger":
            case "nova":
            case "flare":
            case "poly":
            case "crawler":
                return 5;
            case "mace":
            case "pulsar":
            case "horizon":
            case "mega":
            case "risso":
            case "retusa":
            case "stell":
            case "merui":
            case "elude":
                return 20;
            case "fortress":
            case "quasar":
            case "zenith":
            case "quad":
            case "minke":
            case "oxynoe":
            case "locus":
            case "cleroi":
            case "avert":
                return 80;
            case "scepter":
            case "vela":
            case "antumbra":
            case "oct":
            case "bryde":
            case "cyerce":
            case "precept":
            case "anthicus":
            case "obviate":
                return 150;
            case "reign":
            case "corvus":
            case "eclipse":
            case "sei":
            case "aegires":
            case "vanquish":
            case "tecta":
            case "quell":
                return 400;
            case "omura":
            case "navanas":
            case "conqueror":
            case "collaris":
            case "disrupt":
                return 600;
            default:
                return 1;
        }
    }

    public static Set<UnitType> getNextTier(String lastUnitName) {
        Set<UnitType> units = new HashSet<>();

        switch (lastUnitName) {
            case "poly":
                units.add(dagger);
                units.add(nova);
                units.add(flare);
                units.add(mega);
                break;
            case "dagger":
                units.add(mace);
                break;
            case "mace":
                units.add(fortress);
                break;
            case "fortress":
                units.add(scepter);
                break;
            case "scepter":
                units.add(reign);
                break;
            case "nova":
                units.add(pulsar);
                break;
            case "pulsar":
                units.add(quasar);
                break;
            case "quasar":
                units.add(vela);
                break;
            case "vela":
                units.add(corvus);
                break;
            case "flare":
                units.add(horizon);
                units.add(risso);
                units.add(retusa);
                units.add(stell);
                units.add(merui);
                units.add(elude);
                break;
            case "horizon":
                units.add(zenith);
                break;
            case "zenith":
                units.add(antumbra);
                break;
            case "antumbra":
                units.add(eclipse);
                break;
            case "mega":
                units.add(quad);
                break;
            case "quad":
                units.add(oct);
                break;
            case "risso":
                units.add(minke);
                break;
            case "minke":
                units.add(bryde);
                break;
            case "bryde":
                units.add(sei);
                break;
            case "sei":
                units.add(omura);
                break;
            case "retusa":
                units.add(oxynoe);
                break;
            case "oxynoe":
                units.add(cyerce);
                break;
            case "cyerce":
                units.add(aegires);
                break;
            case "aegires":
                units.add(navanax);
                break;
            case "stell":
                units.add(locus);
                break;
            case "locus":
                units.add(precept);
                break;
            case "precept":
                units.add(vanquish);
                break;
            case "vanquish":
                units.add(conquer);
                break;
            case "merui":
                units.add(cleroi);
                break;
            case "cleroi":
                units.add(anthicus);
                break;
            case "anthicus":
                units.add(tecta);
                break;
            case "tecta":
                units.add(collaris);
                break;
            case "elude":
                units.add(avert);
                break;
            case "avert":
                units.add(obviate);
                break;
            case "obviate":
                units.add(quell);
                break;
            case "quell":
                units.add(disrupt);
                break;
        }
        units.add(crawler);
        units.add(latum);

        return units;
    }

    public static Set<UnitType> getPrevTier(String lastUnitName) {
        Set<UnitType> units = new HashSet<>();

        switch (lastUnitName) {
            case "dagger":
                units.add(poly);
                break;
            case "mace":
                units.add(dagger);
                break;
            case "fortress":
                units.add(mace);
                break;
            case "scepter":
                units.add(fortress);
                break;
            case "reign":
                units.add(scepter);
                break;
            case "nova":
                units.add(poly);
                break;
            case "pulsar":
                units.add(nova);
                break;
            case "quasar":
                units.add(pulsar);
                break;
            case "vela":
                units.add(quasar);
                break;
            case "corvus":
                units.add(vela);
                break;
            case "flare":
                units.add(poly);
                break;
            case "horizon":
                units.add(flare);
                break;
            case "zenith":
                units.add(horizon);
                break;
            case "antumbra":
                units.add(zenith);
                break;
            case "eclipse":
                units.add(antumbra);
                break;
            case "mega":
                units.add(poly);
                break;
            case "quad":
                units.add(mega);
                break;
            case "oct":
                units.add(quad);
                break;
            case "risso":
                units.add(flare);
                break;
            case "minke":
                units.add(risso);
                break;
            case "bryde":
                units.add(minke);
                break;
            case "sei":
                units.add(bryde);
                break;
            case "omura":
                units.add(sei);
                break;
            case "retusa":
                units.add(flare);
                break;
            case "oxynoe":
                units.add(retusa);
                break;
            case "cyerce":
                units.add(oxynoe);
                break;
            case "aegires":
                units.add(cyerce);
                break;
            case "navanax":
                units.add(aegires);
                break;
            case "stell":
                units.add(flare);
                break;
            case "locus":
                units.add(stell);
                break;
            case "precept":
                units.add(locus);
                break;
            case "vanquish":
                units.add(precept);
                break;
            case "conquer":
                units.add(vanquish);
                break;
            case "merui":
                units.add(flare);
                break;
            case "cleroi":
                units.add(merui);
                break;
            case "anthicus":
                units.add(cleroi);
                break;
            case "tecta":
                units.add(anthicus);
                break;
            case "collaris":
                units.add(tecta);
                break;
            case "elude":
                units.add(flare);
                break;
            case "avert":
                units.add(elude);
                break;
            case "obviate":
                units.add(avert);
                break;
            case "quell":
                units.add(obviate);
                break;
            case "disrupt":
                units.add(quell);
                break;
        }

        return units;
    }

    public static int getRandomValue(Map<Integer, Double> map, int defaultValue) {
        Random random = new Random();
        double totalProbability = map.values().stream().mapToDouble(Double::doubleValue).sum();

        if (totalProbability < 1.0) {
            map.put(defaultValue, 1.0 - totalProbability);
        }

        double randomValue = random.nextDouble();
        double cumulativeProbability = 0.0;

        for (Map.Entry<Integer, Double> entry : map.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (randomValue <= cumulativeProbability) {
                return entry.getKey();
            }
        }

        return defaultValue;
    }

}

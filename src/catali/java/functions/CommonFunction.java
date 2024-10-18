package catali.java.functions;

import static mindustry.Vars.state;

import arc.math.Mathf;
import catali.types.Position2D;
import mindustry.gen.Groups;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommonFunction {
    public static Position2D getRandomPositionInRadius(int radius) {
        Random rand = new Random();
        double angle = 2 * Math.PI * rand.nextDouble();
        double distance = radius * Math.sqrt(rand.nextDouble());
        int x = (int) (distance * Math.cos(angle));
        int y = (int) (distance * Math.sin(angle));
        return new Position2D(x, y);
    }

    public static boolean[][] getPlayerGrid() {
        int mapWidth = state.map.width / 50;
        int mapHeight = state.map.height / 50;

        boolean[][] gridMap = new boolean[mapWidth][mapHeight];

        Groups.player.forEach(player -> {
            if (player.unit() != null) {
                int gridX = Mathf.clamp((int) player.unit().x / 50, 0, mapWidth - 1);
                int gridY = Mathf.clamp((int) player.unit().y / 50, 0, mapHeight - 1);
                gridMap[gridX][gridY] = true;
            }
        });

        return gridMap;
    }

    public static Position2D getRandomPlace() {
        boolean[][] playerGrid = CommonFunction.getPlayerGrid();
        int width = playerGrid.length;
        int height = playerGrid[0].length;

        List<Position2D> emptyPositions = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!playerGrid[x][y]) {
                    emptyPositions.add(new Position2D(x * 50 + 25, y * 50 + 25));
                }
            }
        }

        if (emptyPositions.isEmpty())
            return null;
        return emptyPositions.get(Mathf.random(emptyPositions.size() - 1));
    }
}

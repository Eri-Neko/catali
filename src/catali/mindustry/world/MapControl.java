package catali.mindustry.world;

import static mindustry.Vars.netServer;
import static mindustry.Vars.state;

import arc.util.Log;
import catali.mindustry.service.WorldService;
import mindustry.core.GameState.State;

public class MapControl {
    private static MapControl mapControl = new MapControl();
    public GameState gameState = GameState.stop;

    private MapControl() {
        // no gameover state
    }

    public static MapControl getInstance() {
        return mapControl;
    }

    public void startAutomaticRandomEndless() {
        startRandom();
        netServer.openServer();
    }

    private void startRandom() {
        updateGameState(GameState.start);
        mindustry.Vars.maps.reload();
        while (true) {
            try {
                WorldService.loadMap(mindustry.Vars.maps.customMaps().random());
                break;
            } catch (Exception e) {
                if (e instanceof NullPointerException) {
                    Log.info("Cannot find custom map. Host default maps");
                    mindustry.Vars.world.loadMap(mindustry.Vars.maps.defaultMaps().random());
                    break;
                }

                Log.err("Error while host random map", e);
            }
        }
        updateGameState(GameState.play);
    }

    public void pauseGame() {
        if (gameState == GameState.play) {
            updateGameState(GameState.pause);
            state.set(State.paused);
        }
    }

    public void resumeGame() {
        if (gameState == GameState.pause) {
            updateGameState(GameState.play);
            state.set(State.playing);
        }
    }

    public void updateGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public boolean isGame() {
        return gameState == GameState.play;
    }

    public enum GameState {
        stop, start, play, pause
    }
}

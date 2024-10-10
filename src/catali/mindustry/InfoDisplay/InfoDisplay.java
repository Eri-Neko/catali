package catali.mindustry.InfoDisplay;

import static catali.NekoVars.taskScheduler;

import java.util.HashMap;

import arc.Events;
import arc.util.Log;
import mindustry.game.EventType.MenuOptionChooseEvent;
import mindustry.game.EventType.TextInputEvent;
import mindustry.gen.Call;
import mindustry.gen.Player;

public class InfoDisplay {
    private static final InfoDisplay INSTANCE = new InfoDisplay();

    private InfoDisplay() {
        taskScheduler.addTask(this::cleanUpExpiredEntries, 1, 1);
    }

    public static InfoDisplay getInstance() {
        return INSTANCE;
    }

    private final HashMap<Integer, HashMap<String, TimedPack<DisplayPack>>> displayPacks = new HashMap<>();
    private final HashMap<Integer, HashMap<String, TimedPack<TextInputPack>>> textInputPacks = new HashMap<>();
    private final int MAX_MESSAGE_LENGTH = 10000;
    private final long EXPIRATION_TIME = 120000; 

    public void init() {
        Events.on(MenuOptionChooseEvent.class, this::onMenuOptionChoose);
        Events.on(TextInputEvent.class, this::onTextInput);
    }

    private void displayContent(Player player, DisplayPack displayPack, boolean isFollowUp) {
        try {
            if (isFollowUp) {
                Call.followUpMenu(player.con, displayPack.id, displayPack.title, displayPack.display, displayPack.options);
            } else {
                Call.menu(player.con, displayPack.id, displayPack.title, displayPack.display, displayPack.options);
            }
            HashMap<String, TimedPack<DisplayPack>> map = displayPacks.computeIfAbsent(displayPack.id, k -> new HashMap<>());
            map.put(player.uuid(), new TimedPack<>(displayPack, System.currentTimeMillis()));
        } catch (Exception e) {
            Log.err("Error while showing info content for player " + player.uuid(), e);
        }
    }

    public void showDisplay(Player player, DisplayPack displayPack) {
        displayContent(player, displayPack, false);
    }

    public void showFollowDisplay(Player player, DisplayPack displayPack) {
        displayContent(player, displayPack, true);
    }
    
    public void hideFollowDisplay(Player player, int displayId) {
        Call.hideFollowUpMenu(player.con, displayId);
    }

    public void showInput(Player player, TextInputPack textInputPack) {
        try {
            Call.textInput(player.con, textInputPack.id, textInputPack.title, textInputPack.message, MAX_MESSAGE_LENGTH, "", textInputPack.isNumeric);
            HashMap<String, TimedPack<TextInputPack>> map = textInputPacks.computeIfAbsent(textInputPack.id, k -> new HashMap<>());
            map.put(player.uuid(), new TimedPack<>(textInputPack, System.currentTimeMillis()));
        } catch (Exception e) {
            Log.err("Error while showing input content for player " + player.uuid(), e);
        }
    }

    private void cleanUpExpiredEntries() {
        long now = System.currentTimeMillis();
        displayPacks.values().forEach(map -> map.values().removeIf(timedPack -> now - timedPack.timestamp > EXPIRATION_TIME));
        textInputPacks.values().forEach(map -> map.values().removeIf(timedPack -> now - timedPack.timestamp > EXPIRATION_TIME));
    }

    public void onMenuOptionChoose(MenuOptionChooseEvent event) {
        HashMap<String, TimedPack<DisplayPack>> map = displayPacks.get(event.menuId);
        if (map == null) return;
        
        TimedPack<DisplayPack> timedDisplayPack = map.get(event.player.uuid());
        if (event.option == -1 || timedDisplayPack == null) return;
        
        DisplayPack displayPack = timedDisplayPack.pack;
        displayPack.optionRunnable[event.option].get(event.player);
    }

    public void onTextInput(TextInputEvent event) {
        HashMap<String, TimedPack<TextInputPack>> map = textInputPacks.get(event.textInputId);
        if (map == null) return;
        
        TimedPack<TextInputPack> timedTextInputPack = map.get(event.player.uuid());
        if (event.text == null || timedTextInputPack == null) return;
        
        TextInputPack textInputPack = timedTextInputPack.pack;
        textInputPack.optionRunnable.get(event.player, event.text);
    }
}

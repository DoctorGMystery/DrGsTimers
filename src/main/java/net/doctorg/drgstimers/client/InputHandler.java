package net.doctorg.drgstimers.client;

import net.doctorg.drgstimers.client.gui.TimersSettingsScreen;
import net.doctorg.drgstimers.data.Timer;
import net.doctorg.drgstimers.util.TimerHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public abstract class InputHandler {

    private static final String KEY_CATEGORY_DOCTORGS_TIMERS = "key.categories.doctorgs_timers";

    public static final KeyMapping KEYBIND_SCROLL_TIMERS = new KeyMapping("key.scroll_timers", GLFW.GLFW_KEY_G, KEY_CATEGORY_DOCTORGS_TIMERS);
    public static final KeyMapping KEYBIND_OPEN_SETTINGS_MENU = new KeyMapping("key.open_settings_menu", GLFW.GLFW_KEY_P, KEY_CATEGORY_DOCTORGS_TIMERS);

    public static double scrollDelta;

    @SubscribeEvent
    public static void handleInput(InputEvent event) {
        if (KEYBIND_OPEN_SETTINGS_MENU.isDown()) {
            Minecraft.getInstance().setScreen(new TimersSettingsScreen());
        }
    }

    @SubscribeEvent
    public static void handleKeyInput(InputEvent.Key event) {
        if (event.getKey() == GLFW.GLFW_KEY_KP_MULTIPLY) {
            TimerHandler.getInstance().getTimerStack().put("DEBUG-1", new Timer(10, 10, 5));
            TimerHandler.getInstance().getTimerStack().put("DEBUG-2", new Timer(17, 1, 2));
            TimerHandler.getInstance().getTimerStack().put("DEBUG-3", new Timer(55, 4, 7));
            TimerHandler.getInstance().getTimerStack().put("DEBUG-4", new Timer(10, 10, 5));
            TimerHandler.getInstance().getTimerStack().put("DEBUG-5", new Timer(17, 1, 2));
            TimerHandler.getInstance().getTimerStack().put("DEBUG-6", new Timer(55, 4, 7));
            TimerHandler.getInstance().getTimerStack().put("DEBUG-7", new Timer(10, 10, 5));
            TimerHandler.getInstance().getTimerStack().put("DEBUG-8", new Timer(17, 1, 2));
            TimerHandler.getInstance().getTimerStack().put("DEBUG-9", new Timer(55, 4, 7));
            TimerHandler.getInstance().getTimerStack().put("DEBUG-10", new Timer(10, 10, 5));
            TimerHandler.getInstance().getTimerStack().put("DEBUG-11", new Timer(17, 1, 2));
            TimerHandler.getInstance().getTimerStack().put("DEBUG-12", new Timer(55, 4, 7));
            TimerHandler.getInstance().getTimerStack().put("DEBUG-13", new Timer(10, 10, 5));
            TimerHandler.getInstance().getTimerStack().put("DEBUG-14", new Timer(17, 1, 2));
            TimerHandler.getInstance().getTimerStack().put("DEBUG-15", new Timer(55, 4, 7));
        }
    }

    @SubscribeEvent
    public static void onPlayerScrolling(InputEvent.MouseScrollingEvent event) {
        if (InputHandler.KEYBIND_SCROLL_TIMERS.isDown()) {
            scrollDelta = event.getScrollDelta();
            event.setCanceled(true);
        }
    }
}

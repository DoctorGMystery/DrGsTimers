package net.doctorg.drgstimers.client;

import net.doctorg.drgstimers.client.gui.EditTimersScreen;
import net.doctorg.drgstimers.client.gui.TimersSettingsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public abstract class InputHandler {

    private static final String KEY_CATEGORY_DOCTORGS_TIMERS = "key.categories.doctorgs_timers";

    public static final KeyMapping KEYBIND_SCROLL_TIMERS = new KeyMapping("key.scroll_timers", GLFW.GLFW_KEY_G, KEY_CATEGORY_DOCTORGS_TIMERS);
    public static final KeyMapping KEYBIND_OPEN_SETTINGS_MENU = new KeyMapping("key.open_settings_menu", GLFW.GLFW_KEY_P, KEY_CATEGORY_DOCTORGS_TIMERS);
    public static final KeyMapping KEYBIND_OPEN_EDIT_TIMERS = new KeyMapping("key.open_edit_timers", GLFW.GLFW_KEY_O, KEY_CATEGORY_DOCTORGS_TIMERS);

    public static double scrollDelta;
    private static boolean mouseReleased;

    @SubscribeEvent
    public static void handleKeyInput(InputEvent.Key event) {
        if (KEYBIND_OPEN_SETTINGS_MENU.isDown()) {
            Minecraft.getInstance().setScreen(new TimersSettingsScreen());
        }
        if (KEYBIND_OPEN_EDIT_TIMERS.isDown()) {
            Minecraft.getInstance().setScreen(new EditTimersScreen());
        }
    }

    @SubscribeEvent
    public static void onPlayerScrolling(InputEvent.MouseScrollingEvent event) {
        if (InputHandler.KEYBIND_SCROLL_TIMERS.isDown()) {
            scrollDelta = event.getScrollDeltaY();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerMouseButtonPre(InputEvent.MouseButton.Pre event) {
        mouseReleased = event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT && event.getAction() == GLFW.GLFW_RELEASE;
    }

    public static boolean isMouseReleased() {
        return mouseReleased;
    }
}

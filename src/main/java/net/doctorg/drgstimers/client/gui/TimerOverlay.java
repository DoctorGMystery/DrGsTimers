package net.doctorg.drgstimers.client.gui;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.client.InputHandler;
import net.doctorg.drgstimers.util.TimerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;
import net.neoforged.bus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class TimerOverlay implements IGuiOverlay {

    public static final TimerOverlay INSTANCE = new TimerOverlay();

    private int scrollPosition = 0;
    //private int xOffset;
    //private int yOffset;

    private TimerOverlay() {}

    @Override
    public void render(ExtendedGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {

        if (TimerHandler.getClientInstance() == null) {
            return;
        }

        if (TimerHandler.getClientInstance().getRunningTimers().isEmpty() || gui.getMinecraft().screen instanceof TimersSettingsScreen || !DoctorGsTimers.INSTANCE.getTimersOptions().showTimers.get() || Minecraft.getInstance().screen instanceof PauseScreen) {
            return;
        }

        Minecraft.getInstance().getProfiler().push("renderTimerOverlay");

        renderTimers(guiGraphics);
        TimerOverlay.INSTANCE.calcScrollPosition();

        Minecraft.getInstance().getProfiler().pop();
    }

    public void renderTimers(GuiGraphics guiGraphics) {
        int i = 1;
        //xOffset = (int) ((double) (Minecraft.getInstance().getWindow().getGuiScaledWidth() - 74) / 100 * DoctorGsTimers.INSTANCE.getTimersOptions().xOffset.get());
        //yOffset = (int) ((double) (Minecraft.getInstance().getWindow().getGuiScaledHeight() - 23) / 100 * DoctorGsTimers.INSTANCE.getTimersOptions().yOffset.get());

        guiGraphics.pose().pushPose();

        for (String timerIdName : TimerHandler.getClientInstance().getRunningTimers().keySet()) {
            int y = 20 * i - 17 - scrollPosition ;
            if (y < Minecraft.getInstance().getWindow().getGuiScaledHeight() && y + 17 > 0) {
                drawTimer(guiGraphics, timerIdName, y);
            }
            i++;
        }

        guiGraphics.pose().popPose();
    }

    public void drawTimer(GuiGraphics guiGraphics, String timerIdName, int y) {
        String output = timerIdName + " " + TimerHandler.getClientInstance().getRunningTimer(timerIdName).getTime().toString();

        if (timerIdName.length() > DoctorGsTimers.INSTANCE.getTimersOptions().maximumCharacters.get()) {
            output = timerIdName.substring(0, DoctorGsTimers.INSTANCE.getTimersOptions().maximumCharacters.get()) + "... " + TimerHandler.getClientInstance().getRunningTimer(timerIdName).getTime().toString();
        }

        guiGraphics.fill(3, y, Minecraft.getInstance().font.width(output) + 13, y + 17, 0x44000000);
        guiGraphics.drawString(Minecraft.getInstance().font, output, 8, y + 5, 0xFFFFFFFF, false);
    }

    public void calcScrollPosition() {
        int calculatedScrollDelta = (int) -((double) DoctorGsTimers.INSTANCE.getTimersOptions().scrollSensitivity.get() / 100 * (InputHandler.scrollDelta * 5));

        /*
        Explanation canMoveDown:
         scrollPosition: to get the local scrollPosition
         getGuiScaledHeight: to take into account the height of the screen, otherwise you could scroll until the last timer reached the top of the screen
         getRunningTimer.size * 20: to take into account the number of running timers (getRunningTimer.size) multiplied by the height of each timer (top margin included, bottom margin excluded)
         - 4: the bottom margin
        Explanation shouldSnapToMax:
         same as canMoveDown
         calculatedScrollDelta: to take into account the current calculatedScrollDelta, if its bigger or equal than 0
         */

        //TODO: return if calculatedScrollDelta is 0 to save resources and time

        boolean isScrollDeltaPositive = calculatedScrollDelta > 0;
        boolean canMoveDown = (scrollPosition + Minecraft.getInstance().getWindow().getGuiScaledHeight() - TimerHandler.getClientInstance().getRunningTimers().size() * 20 - 3) < 0;
        boolean shouldSnapToMax = (scrollPosition + calculatedScrollDelta + Minecraft.getInstance().getWindow().getGuiScaledHeight() - TimerHandler.getClientInstance().getRunningTimers().size() * 20 - 3) >= 0;

        boolean isScrollDeltaNegative = calculatedScrollDelta < 0;
        boolean canMoveUp = scrollPosition > 0;
        boolean shouldSnapToMin = (scrollPosition + calculatedScrollDelta) <= 0;

        if (isScrollDeltaPositive && canMoveDown) {
            if (shouldSnapToMax) {
                scrollPosition = TimerHandler.getClientInstance().getRunningTimers().size() * 20 - Minecraft.getInstance().getWindow().getGuiScaledHeight() + 3;
            } else {
                scrollPosition += calculatedScrollDelta;
            }
        }

        if (isScrollDeltaNegative && canMoveUp) {
            if (shouldSnapToMin) {
                scrollPosition = 0;
            } else {
                scrollPosition += calculatedScrollDelta;
            }
        }

        InputHandler.scrollDelta = 0;
    }

    @SubscribeEvent
    public static void screenRenderPost(ScreenEvent.Render.Post event) {
        //TODO: check if timers are displayed above other gui parts and make it more compatible with chat
        if (event.getScreen() instanceof PauseScreen) {
            if (TimerHandler.getClientInstance() != null) {
                TimerOverlay.INSTANCE.renderTimers(event.getGuiGraphics());
                TimerOverlay.INSTANCE.calcScrollPosition();
            }
        }
    }

    @SubscribeEvent
    public static void mouseScrollingScreen(ScreenEvent.MouseScrolled.Pre event) {
        if (event.getMouseX() < 150) {
            InputHandler.scrollDelta = event.getScrollDeltaY();
        }
    }
}
package net.doctorg.drgstimers.client.gui;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.client.InputHandler;
import net.doctorg.drgstimers.data.TimerData;
import net.doctorg.drgstimers.util.NegativeDateTimeException;
import net.doctorg.drgstimers.util.TimerHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class TimerOverlay implements IGuiOverlay {

    public static final TimerOverlay INSTANCE = new TimerOverlay();

    private int scrollPosition = 0;

    private int visibleTimerCount;

    private TimerOverlay() {}

    @Override
    public void render(ExtendedGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {

        if (gui.getMinecraft().screen instanceof TimersSettingsScreen || !DoctorGsTimers.INSTANCE.getTimersOptions().showTimers.get() || Minecraft.getInstance().screen instanceof PauseScreen) {
            return;
        }

        Minecraft.getInstance().getProfiler().push("renderTimerOverlay");

        renderTimers(guiGraphics);
        TimerOverlay.INSTANCE.calcScrollPosition();

        Minecraft.getInstance().getProfiler().pop();
    }

    public void renderTimers(GuiGraphics guiGraphics) {
        int i = 1;
        visibleTimerCount = 0;

        guiGraphics.pose().pushPose();

        for (Map.Entry<String, ? extends TimerData> timer : TimerHandler.getClientInstance(false).getTimerStack().entrySet()) {
            boolean visible = timer.getValue().isVisible();
            boolean alwaysVisible = timer.getValue().isAlwaysVisible();
            boolean running = timer.getValue().isTimerRunning();

            if (!visible || (!running && !alwaysVisible)) continue;

            visibleTimerCount++;

            int y = 20 * i - 17 - scrollPosition;

            boolean inGuiReach = y + 17 < DoctorGsTimers.INSTANCE.getTimersOptions().guiHeight &&
                    y > 0;

            if (inGuiReach) {
                drawTimer(guiGraphics, timer, y);
            }

            i++;
        }

        guiGraphics.pose().popPose();
    }

    public void drawTimer(GuiGraphics guiGraphics, Map.Entry<String, ? extends TimerData> timer, int y) {
        String output;
        try {
            output = timer.getKey() + " " + timer.getValue().getTime().toString();
        } catch (NegativeDateTimeException ndte) {
            output = "Error displaying timer '" + timer.getKey() + "'";
        }

        int xOffset = (int) (DoctorGsTimers.INSTANCE.getTimersOptions().xOffset * Minecraft.getInstance().getWindow().getGuiScaledWidth());
        int yOffset = (int) (DoctorGsTimers.INSTANCE.getTimersOptions().yOffset * Minecraft.getInstance().getWindow().getGuiScaledHeight());

        try {
            output = shortenNameLength(timer, output, xOffset);
        } catch (StringIndexOutOfBoundsException sioobe) {
            output = timer.getKey() + " error displaying timer";
        }

        Component outputComp = Component.literal(output);
        int textColor = 0xFFFFFFFF;

        if (!timer.getValue().isTimerRunning()) {
            outputComp = Component.literal(output).withStyle(ChatFormatting.ITALIC);
        }

        if (outputComp.getString().contains("error")) {
            textColor = 0xFFFF5555;
        }

        guiGraphics.fill(3 + xOffset, y + yOffset, Minecraft.getInstance().font.width(output) + 13 + xOffset, y + 17 + yOffset, 0x44000000);
        guiGraphics.drawString(Minecraft.getInstance().font, outputComp, 8 + xOffset, y + 5 + yOffset, textColor, false);
    }

    public String shortenNameLength(Map.Entry<String, ? extends TimerData> timer, String output, int xOffset) {
        boolean timerIdNameBiggerThanMaxChars = timer.getKey().length() > DoctorGsTimers.INSTANCE.getTimersOptions().maximumCharacters.get();
        boolean timerStringWithSpacingBOrEGuiWidth = (Minecraft.getInstance().font.width(output) + 13 + 3 + xOffset) >= Minecraft.getInstance().getWindow().getGuiScaledWidth();

        if (!timerIdNameBiggerThanMaxChars && !timerStringWithSpacingBOrEGuiWidth) {
            return output;
        }

        int i = 0;

        if (timerIdNameBiggerThanMaxChars) {
            output = timer.getKey().substring(0, DoctorGsTimers.INSTANCE.getTimersOptions().maximumCharacters.get() - i) + "... " + timer.getValue().getTime().toString();
            timerStringWithSpacingBOrEGuiWidth = (Minecraft.getInstance().font.width(output) + 13 + 3 + xOffset) >= Minecraft.getInstance().getWindow().getGuiScaledWidth();

            if (!timerStringWithSpacingBOrEGuiWidth) {
                return output;
            }
        }


        while (timerStringWithSpacingBOrEGuiWidth) {
            i++;
            output = timer.getKey().substring(0, timer.getKey().length() - i) + "... " + timer.getValue().getTime().toString();
            timerStringWithSpacingBOrEGuiWidth = (Minecraft.getInstance().font.width(output) + 13 + 3 + xOffset) >= Minecraft.getInstance().getWindow().getGuiScaledWidth();
        }
        return output;
    }

    public void calcScrollPosition() {
        if (InputHandler.scrollDelta == 0) return;

        int calculatedScrollDelta = (int) -((double) DoctorGsTimers.INSTANCE.getTimersOptions().scrollSensitivity.get() / 100 * (InputHandler.scrollDelta * 5));

        /*
        Explanation canMoveDown:
         scrollPosition: to get the local scrollPosition
         guiHeight: to take into account the height of the gui, otherwise you could scroll until the last timer reached the top of the gui
         visibleTimerCount * 20: to take into account the number of visible multiplied by the height of each timer (top margin included, bottom margin excluded)
         - 3: the bottom margin
        Explanation shouldSnapToMax:
         same as canMoveDown
         calculatedScrollDelta: to take into account the current calculatedScrollDelta, if its bigger or equal than 0
         */

        boolean isScrollDeltaPositive = calculatedScrollDelta > 0;
        boolean canMoveDown = scrollPosition + DoctorGsTimers.INSTANCE.getTimersOptions().guiHeight - visibleTimerCount * 20 - 3 < 0;
        boolean shouldSnapToMax = scrollPosition + calculatedScrollDelta + DoctorGsTimers.INSTANCE.getTimersOptions().guiHeight - visibleTimerCount * 20 - 3 >= 0;

        boolean isScrollDeltaNegative = calculatedScrollDelta < 0;
        boolean canMoveUp = scrollPosition > 0;
        boolean shouldSnapToMin = scrollPosition + calculatedScrollDelta <= 0;

        if (isScrollDeltaPositive && canMoveDown) {
            if (shouldSnapToMax) {
                scrollPosition = visibleTimerCount * 20 - DoctorGsTimers.INSTANCE.getTimersOptions().guiHeight + 3;
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
            if (TimerHandler.getClientInstance(false) != null) {
                TimerOverlay.INSTANCE.renderTimers(event.getGuiGraphics());
                TimerOverlay.INSTANCE.calcScrollPosition();
            }
        }
    }

    @SubscribeEvent
    public static void mouseScrollingScreen(ScreenEvent.MouseScrolled.Pre event) {
        int xOffset = (int) (DoctorGsTimers.INSTANCE.getTimersOptions().xOffset * Minecraft.getInstance().getWindow().getGuiScaledWidth());
        if (event.getMouseX() > xOffset && event.getMouseX() < xOffset + 150) {
            InputHandler.scrollDelta = event.getScrollDeltaY();
        }
    }
}
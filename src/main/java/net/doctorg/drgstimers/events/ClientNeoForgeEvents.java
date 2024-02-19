package net.doctorg.drgstimers.events;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ScreenEvent;

@Mod.EventBusSubscriber(modid = DoctorGsTimers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientNeoForgeEvents {

    private static int lastGuiWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
    private static int lastGuiHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

    @SubscribeEvent
    public static void registerOverlay(ScreenEvent.Init.Pre event) {
        if (lastGuiWidth != 0 && lastGuiHeight != 0) {
            DoctorGsTimers.INSTANCE.getTimersOptions().xOffset = calculateScaledX();
            DoctorGsTimers.INSTANCE.getTimersOptions().yOffset = calculateScaledY();
            DoctorGsTimers.INSTANCE.getTimersOptions().save();
        }

        lastGuiWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        lastGuiHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }

    private static float calculateScaledY() {
        float oldY = DoctorGsTimers.INSTANCE.getTimersOptions().yOffset * lastGuiHeight;
        float oldYWithWidth = oldY / (lastGuiHeight - DoctorGsTimers.INSTANCE.getTimersOptions().guiHeight);
        float height = DoctorGsTimers.INSTANCE.getTimersOptions().guiHeight * oldYWithWidth;

        float newYGuiScaled = oldYWithWidth * Minecraft.getInstance().getWindow().getGuiScaledHeight() - height;
        return newYGuiScaled / Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }

    private static float calculateScaledX() {
        float oldX = DoctorGsTimers.INSTANCE.getTimersOptions().xOffset * lastGuiWidth;
        float oldXWithWidth = oldX / (lastGuiWidth - 100);
        float width = 100 * oldXWithWidth;

        float newXGuiScaled = oldXWithWidth * Minecraft.getInstance().getWindow().getGuiScaledWidth() - width;
        return newXGuiScaled / Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }
}

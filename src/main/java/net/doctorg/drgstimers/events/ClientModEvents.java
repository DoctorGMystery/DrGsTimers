package net.doctorg.drgstimers.events;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.client.InputHandler;
import net.doctorg.drgstimers.client.gui.TimerOverlay;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DoctorGsTimers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerOverlay(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll(new ResourceLocation("timer_overlay"), TimerOverlay.INSTANCE);
    }

    @SubscribeEvent
    public static void registerKeyMapping(RegisterKeyMappingsEvent event) {
        event.register(InputHandler.KEYBIND_SCROLL_TIMERS);
        event.register(InputHandler.KEYBIND_OPEN_SETTINGS_MENU);
    }
}

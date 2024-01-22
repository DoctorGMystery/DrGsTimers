package net.doctorg.drgstimers.events;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.network.messages.TimerStackPacket;
import net.doctorg.drgstimers.util.TimerHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = DoctorGsTimers.MOD_ID, value = Dist.DEDICATED_SERVER, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerNeoForgeEvents {
    @SubscribeEvent
    public static void onPlayerJoins(PlayerEvent.PlayerLoggedInEvent event) {
        PacketDistributor.ALL.noArg().send( new TimerStackPacket(TimerHandler.getInstance().getTimerStack()));
    }
}

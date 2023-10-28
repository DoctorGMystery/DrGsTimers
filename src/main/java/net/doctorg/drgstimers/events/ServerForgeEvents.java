package net.doctorg.drgstimers.events;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.network.PacketHandler;
import net.doctorg.drgstimers.network.messages.TimerStackPacket;
import net.doctorg.drgstimers.util.TimerHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = DoctorGsTimers.MOD_ID, value = Dist.DEDICATED_SERVER, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerForgeEvents {
    @SubscribeEvent
    public static void onPlayerJoins(PlayerEvent.PlayerLoggedInEvent event) {
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new TimerStackPacket(TimerHandler.getInstance().getTimerStack()));
    }
}

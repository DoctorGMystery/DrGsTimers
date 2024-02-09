package net.doctorg.drgstimers.network;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.network.messages.LevelIdPacket;
import net.doctorg.drgstimers.network.messages.TimerStackPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "2";

    @SubscribeEvent
    public static void registerPayloadHandler(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(DoctorGsTimers.MOD_ID)
                .versioned(PROTOCOL_VERSION)
                .optional()
                .common(TimerStackPacket.ID, TimerStackPacket::new, TimerStackPacket.Handler::handle)
                .common(LevelIdPacket.ID, LevelIdPacket::new, LevelIdPacket.Handler::handle);
    }
}

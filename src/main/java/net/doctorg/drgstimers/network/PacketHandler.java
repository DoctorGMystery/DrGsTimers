package net.doctorg.drgstimers.network;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.network.messages.TimerStackPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DoctorGsTimers.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerMessages() {
        int packetId = 0;
        INSTANCE.registerMessage(packetId++, TimerStackPacket.class, TimerStackPacket::encode, TimerStackPacket::new, TimerStackPacket.Handler::handle);
    }
}

package net.doctorg.drgstimers.network.messages;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.client.ClientTimerHandler;
import net.doctorg.drgstimers.client.TimersOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record LevelIdPacket(UUID levelId) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(DoctorGsTimers.MOD_ID, "level_id_packet");

    public LevelIdPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(levelId);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static class Handler {

        public static void handle(LevelIdPacket message, IPayloadContext context) {
            context.workHandler().execute(() -> {
                        ClientTimerHandler.getInstance().setLevelId(message.levelId);
                        TimersOptions.loadLevelTimerVisibilityOptions();
            }
            );

        }
    }
}

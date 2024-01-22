package net.doctorg.drgstimers.network.messages;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.client.ClientTimer;
import net.doctorg.drgstimers.client.ClientTimerHandler;
import net.doctorg.drgstimers.data.TimerData;
import net.doctorg.drgstimers.util.NegativeDateTimeException;
import net.doctorg.drgstimers.util.TimerHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;

public record TimerStackPacket(HashMap<String, ? extends TimerData> timerStack) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(DoctorGsTimers.MOD_ID, "timer_stack_packet");

    public TimerStackPacket(FriendlyByteBuf buf) {
        this(new HashMap<>(buf.readMap(FriendlyByteBuf::readUtf, TimerStackPacket::readTimer)));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        writeTimerStack(buf, timerStack);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    private static void writeTimerStack(FriendlyByteBuf buf, HashMap<String, ? extends TimerData> timerStack) {
        buf.writeMap(timerStack, (buffer, keyId) -> buf.writeUtf(keyId), TimerStackPacket::writeTimer);
    }

    private static <T extends TimerData> void writeTimer(FriendlyByteBuf buf, T timer) {
        try {
            timer.getTime();
        } catch (NegativeDateTimeException ndte) {
            buf.writeFloat(timer.getSetTime().getSeconds());
            buf.writeInt(timer.getSetTime().getMinuets());
            buf.writeInt(timer.getSetTime().getHours());
            buf.writeFloat(0);
            buf.writeInt(0);
            buf.writeInt(0);
            buf.writeBoolean(timer.isTimerRunning());
            buf.writeBoolean(timer.getRunWhileGamePaused());
            return;
        }
        buf.writeFloat(timer.getSetTime().getSeconds());
        buf.writeInt(timer.getSetTime().getMinuets());
        buf.writeInt(timer.getSetTime().getHours());
        buf.writeFloat(timer.getTime().getSeconds());
        buf.writeInt(timer.getTime().getMinuets());
        buf.writeInt(timer.getTime().getHours());
        buf.writeBoolean(timer.isTimerRunning());
        buf.writeBoolean(timer.getRunWhileGamePaused());
    }

    private static ClientTimer readTimer(FriendlyByteBuf buf) {
        return new ClientTimer(buf.readFloat(), buf.readInt(), buf.readInt(), buf.readFloat(), buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readBoolean());
    }

    public static class Handler {

        public Handler() {
        }

        public static void handle(TimerStackPacket message, IPayloadContext context) {
            context.workHandler().execute(() -> {
                        if (TimerHandler.getClientInstance() == null) {
                            new ClientTimerHandler(context);
                        }
                        ClientTimerHandler.updateClientTimerHandler(context, (HashMap<String, ClientTimer>) message.timerStack);
                    }
            );
        }
    }
}

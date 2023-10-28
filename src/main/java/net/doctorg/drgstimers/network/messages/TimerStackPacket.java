package net.doctorg.drgstimers.network.messages;

import net.doctorg.drgstimers.client.ClientTimer;
import net.doctorg.drgstimers.client.ClientTimerHandler;
import net.doctorg.drgstimers.util.NegativeDateTimeException;
import net.doctorg.drgstimers.util.TimerHandler;
import net.doctorg.drgstimers.data.TimerData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.function.Supplier;

public class TimerStackPacket {
    private final HashMap<String, ? extends TimerData> timerStack;

    public TimerStackPacket(HashMap<String, ? extends TimerData> timerStack) {
        this.timerStack = timerStack;
    }

    public TimerStackPacket(FriendlyByteBuf buf) {
        timerStack = (HashMap<String, ClientTimer>) buf.readMap(FriendlyByteBuf::readUtf, TimerStackPacket::readTimer);
    }

    public void encode(FriendlyByteBuf buf) {
        writeTimerStack(buf, timerStack);
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

        public static void handle(TimerStackPacket message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                    if (TimerHandler.getClientInstance() == null) {
                        new ClientTimerHandler(context);
                    }
                    ClientTimerHandler.updateClientTimerHandler(context, (HashMap<String, ClientTimer>) message.timerStack);
                }
            );
            context.get().setPacketHandled(true);
        }
    }
}

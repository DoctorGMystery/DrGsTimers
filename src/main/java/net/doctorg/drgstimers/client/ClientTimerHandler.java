package net.doctorg.drgstimers.client;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.data.TimerHandlerBase;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ClientTimerHandler extends TimerHandlerBase<ClientTimer> {
    protected static ClientTimerHandler Instance;

    public ClientTimerHandler(Supplier<NetworkEvent.Context> context) throws IllegalArgumentException {
        if (context == null) {
            DoctorGsTimers.LOGGER.error("Illegal try to instantiate ClientTimerHandler!");
            throw new IllegalArgumentException("Illegal try to instantiate ClientTimerHandler: context is null");
        }
        Instance = this;
    }

    public static ClientTimerHandler getInstance() {
        return Instance;
    }

    public String getKeyByValue(ClientTimer value) {
        for (Map.Entry<String, ClientTimer> entry : getInstance().getTimerStack().entrySet()) {
            if (entry.getValue() == value) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void updateClientTimerHandler(Supplier<NetworkEvent.Context> context, HashMap<String, ClientTimer> timerStack) {
        if (context == null) {
            DoctorGsTimers.LOGGER.error("Illegal try to update ClientTimerHandler Instance!");
            throw new IllegalArgumentException("Illegal try to update ClientTimerHandler Instance: context is null");
        }

        Instance.getTimerStack().clear();
        Instance.getRunningTimers().clear();

        Instance.getTimerStack().putAll(timerStack);
        for (Map.Entry<String, ClientTimer> entry: timerStack.entrySet()) {
            if (entry.getValue().isTimerRunning()) {
                Instance.getRunningTimers().put(entry.getKey(), entry.getValue());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerNetworkLogOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientTimerHandler.Instance = null;
    }
}

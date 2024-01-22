package net.doctorg.drgstimers.client;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.data.TimerHandlerBase;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public class ClientTimerHandler extends TimerHandlerBase<ClientTimer> {
    protected static ClientTimerHandler Instance;

    public ClientTimerHandler(IPayloadContext context) throws IllegalArgumentException {
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

    public static void updateClientTimerHandler(IPayloadContext context, HashMap<String, ClientTimer> timerStack) {
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

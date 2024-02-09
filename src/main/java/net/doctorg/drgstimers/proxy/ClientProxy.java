package net.doctorg.drgstimers.proxy;

import net.doctorg.drgstimers.client.ClientTimerHandler;
import net.doctorg.drgstimers.client.InputHandler;
import net.doctorg.drgstimers.client.gui.TimerOverlay;
import net.neoforged.neoforge.common.NeoForge;

public class ClientProxy implements IProxyDrGsTimers {
    @Override
    public void init() {
        NeoForge.EVENT_BUS.register(InputHandler.class);
        NeoForge.EVENT_BUS.register(TimerOverlay.class);
        NeoForge.EVENT_BUS.register(ClientTimerHandler.class);
    }
}

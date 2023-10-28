package net.doctorg.drgstimers.proxy;

import net.doctorg.drgstimers.client.ClientTimerHandler;
import net.doctorg.drgstimers.client.InputHandler;
import net.doctorg.drgstimers.client.gui.TimerOverlay;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements ProxyDrGsTimers {
    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(InputHandler.class);
        MinecraftForge.EVENT_BUS.register(TimerOverlay.class);
        MinecraftForge.EVENT_BUS.register(ClientTimerHandler.class);
    }
}

package net.doctorg.drgstimers.util;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.client.ClientTimerHandler;
import net.doctorg.drgstimers.data.Timer;
import net.doctorg.drgstimers.data.TimerHandlerBase;
import net.doctorg.drgstimers.network.messages.TimerStackPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;

public class TimerHandler extends TimerHandlerBase<Timer> {

    protected static TimerHandler Instance;

    private long lastTime = System.nanoTime();
    private boolean wasPausedLately = true;


    private TimerHandler() {}

    public static TimerHandler getInstance() {
        return Instance;
    }

    public static TimerHandlerBase<?> getClientInstance() {
        if (Minecraft.getInstance().hasSingleplayerServer()) {
            return Instance;
        }
        return ClientTimerHandler.getInstance();
    }

    public String getKeyByValue(Timer value) {
        for (Map.Entry<String, Timer> entry : getInstance().getTimerStack().entrySet()) {
            if (entry.getValue() == value) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void tickTimers(TickEvent event) {
        if (event == null) {
            DoctorGsTimers.LOGGER.error("Illegal try to call tickTimers in TimerHandler: event is null");
            return;
        }

        long time = System.nanoTime();
        float deltaTime = (float) ((time - lastTime) / 1000000);
        lastTime = time;

        for (Timer t : runningTimers.values()) {
            if (!t.getRunWhileGamePaused() && wasPausedLately) {
                continue;
            }

            t.updateTime(deltaTime / 1000);
        }

        for (Timer t : timerStack.values()) {
            if (t.getShouldRefreshTimerRunning()) {
                t.setTimerRunning(false);
            }
        }

        if (event.side.isServer()) {
            if (!((TickEvent.ServerTickEvent) event).getServer().isSingleplayer()) {
                ((TickEvent.ServerTickEvent) event).getServer().getProfiler().push("sendingTimerPackage");
                PacketDistributor.ALL.noArg().send(new TimerStackPacket(TimerHandler.getInstance().getTimerStack()));
                ((TickEvent.ServerTickEvent) event).getServer().getProfiler().pop();
            }
        }
    }

    public static class StaticEvents {
        @SubscribeEvent
        public static void onServerAboutToStart(ServerAboutToStartEvent event) {
            if (Instance == null) {
                Instance = new TimerHandler();
                NeoForge.EVENT_BUS.register(getInstance());
                for (Timer timer : getInstance().timerStack.values()) {
                    getInstance().runningTimers.put(getInstance().getKeyByValue(timer), timer);
                }
            }
        }

        @SubscribeEvent
        public static void onServerStopping(ServerStoppingEvent event) {
            TimerSavedData.instance.setDirty(true);
        }

        @SubscribeEvent
        public static void onStoppedServer(ServerStoppedEvent event) {
            Instance = null;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().isPaused() && Minecraft.getInstance().hasSingleplayerServer()) {
            wasPausedLately = true;
            tickTimers(event);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        tickTimers(event);
        wasPausedLately = false;
    }
}

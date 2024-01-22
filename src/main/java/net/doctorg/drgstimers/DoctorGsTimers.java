package net.doctorg.drgstimers;

import com.mojang.logging.LogUtils;
import net.doctorg.drgstimers.client.ClientTimerHandler;
import net.doctorg.drgstimers.client.InputHandler;
import net.doctorg.drgstimers.client.TimersOptions;
import net.doctorg.drgstimers.client.gui.TimerOverlay;
import net.doctorg.drgstimers.network.PacketHandler;
import net.doctorg.drgstimers.proxy.ClientProxy;
import net.doctorg.drgstimers.proxy.ProxyDrGsTimers;
import net.doctorg.drgstimers.proxy.ServerProxy;
import net.doctorg.drgstimers.util.TimerHandler;
import net.doctorg.drgstimers.util.TimerSavedData;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(DoctorGsTimers.MOD_ID)
public class DoctorGsTimers
{
    public static final String MOD_ID = "doctorgstimers";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static DoctorGsTimers INSTANCE;
    public static ProxyDrGsTimers[] proxy = { new ClientProxy(), new ServerProxy()};

    @OnlyIn(Dist.CLIENT)
    private TimersOptions timersOptions;

    public DoctorGsTimers()
    {
        IEventBus modEventBus = ModLoadingContext.get().getActiveContainer().getEventBus();

        NeoForge.EVENT_BUS.register(TimerHandler.StaticEvents.class);

        modEventBus.register(PacketHandler.class);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::init);
        modEventBus.addListener(this::clientSetup);

        INSTANCE = this;
    }

    @OnlyIn(Dist.CLIENT)
    public TimersOptions getTimersOptions() {
        return timersOptions;
    }

    private void init(FMLCommonSetupEvent event) {
        if (FMLEnvironment.dist.isClient()) {
            proxy[0].init();
        } else {
            proxy[1].init();
        }
    }

    private void clientSetup(FMLClientSetupEvent event) {
        timersOptions = new TimersOptions();
    }

    @SubscribeEvent
    void onServerStarting(ServerStartingEvent event) {
        TimerSavedData.instance = event.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(TimerSavedData::new, TimerSavedData::load), "timers");
    }
}

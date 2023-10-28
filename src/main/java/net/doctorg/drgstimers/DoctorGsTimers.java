package net.doctorg.drgstimers;

import com.mojang.logging.LogUtils;
import net.doctorg.drgstimers.client.TimersOptions;
import net.doctorg.drgstimers.network.PacketHandler;
import net.doctorg.drgstimers.proxy.ClientProxy;
import net.doctorg.drgstimers.proxy.ProxyDrGsTimers;
import net.doctorg.drgstimers.proxy.ServerProxy;
import net.doctorg.drgstimers.util.TimerHandler;
import net.doctorg.drgstimers.util.TimerSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DoctorGsTimers.MOD_ID)
public class DoctorGsTimers
{
    public static final String MOD_ID = "doctorgstimers";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static DoctorGsTimers INSTANCE;
    public static ProxyDrGsTimers proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    @OnlyIn(Dist.CLIENT)
    private TimersOptions timersOptions;

    public DoctorGsTimers()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(TimerHandler.class);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::init);
        modEventBus.addListener(this::clientSetup);

        INSTANCE = this;
    }

    @OnlyIn(Dist.CLIENT)
    public TimersOptions getTimersOptions() {
        return timersOptions;
    }

    private void init(FMLCommonSetupEvent event) {
        proxy.init();
        PacketHandler.registerMessages();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        timersOptions = new TimersOptions();
    }

    @SubscribeEvent
    void onServerStarting(ServerStartingEvent event) {
        TimerSavedData.instance = event.getServer().overworld().getDataStorage().computeIfAbsent(TimerSavedData::load, TimerSavedData::new, "timers");
    }
}

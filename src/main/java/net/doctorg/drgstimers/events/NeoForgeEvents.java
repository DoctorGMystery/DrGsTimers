package net.doctorg.drgstimers.events;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.commands.TimerCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = DoctorGsTimers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NeoForgeEvents {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new TimerCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }
}

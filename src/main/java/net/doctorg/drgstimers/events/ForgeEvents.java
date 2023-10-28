package net.doctorg.drgstimers.events;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.commands.TimerCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = DoctorGsTimers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new TimerCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }
}

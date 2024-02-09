package net.doctorg.drgstimers.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.doctorg.drgstimers.data.TimerData;
import net.doctorg.drgstimers.util.TimerHandler;
import net.doctorg.drgstimers.util.TimerNotInListException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Set;

public class ClientTimerCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_TIMERS = (p_136344_, p_136345_) -> {
        Set<String> timers = TimerHandler.getInstance().getTimerStack().keySet();
        return SharedSuggestionProvider.suggest(timers.stream(), p_136345_);
    };

    private static final SimpleCommandExceptionType ERROR_SAME_VISIBLE = new SimpleCommandExceptionType(Component.translatable("commands.timer.same.visible"));


    public ClientTimerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("timer")
                .then(Commands.literal("timers")
                        .then(Commands.argument("name", StringArgumentType.word()).suggests(SUGGEST_TIMERS)
                                .then(Commands.literal("visible")
                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .executes((command) ->
                                                        setVisible(command.getSource(), StringArgumentType.getString(command, "name"), BoolArgumentType.getBool(command, "value"))
                                                )
                                        )
                                        .executes((command) ->
                                                getVisible(command.getSource(), StringArgumentType.getString(command, "name"))
                                        )
                                )
                        )
                )
        );
    }


    private int setVisible(CommandSourceStack source, String timerIdName, boolean value) throws CommandSyntaxException {
        TimerData timer;

        try {
            timer = TimerHandler.getClientInstance(false).getTimer(timerIdName);
        } catch (TimerNotInListException tdnee) {
            throw TimerCommand.ERROR_TIMER_NOT_EXIST.create();
        }

        if (value == timer.isVisible()) {
            throw ERROR_SAME_VISIBLE.create();
        }

        timer.setVisible(value);

        if (value) {
            source.sendSuccess(() ->
                    Component.translatable("commands.timer.set_visible.true.success", timerIdName), true);
            return 0;
        }

        source.sendSuccess(() ->
                Component.translatable("commands.timer.set_visible.false.success", timerIdName), true);

        return 0;
    }

    private int getVisible(CommandSourceStack source, String timerIdName) throws CommandSyntaxException {
        TimerData timer;

        try {
            timer = TimerHandler.getClientInstance(false).getTimer(timerIdName);
        } catch (TimerNotInListException tdnee) {
            throw TimerCommand.ERROR_TIMER_NOT_EXIST.create();
        }

        if (timer.isVisible()) {
            source.sendSuccess(() ->
                    Component.translatable("commands.timer.get_visible.true.success", timerIdName), true);
            return 0;
        }

        source.sendSuccess(() ->
                Component.translatable("commands.timer.get_visible.false.success", timerIdName), true);

        return 0;
    }
}

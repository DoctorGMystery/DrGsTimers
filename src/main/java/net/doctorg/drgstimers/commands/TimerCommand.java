package net.doctorg.drgstimers.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.doctorg.drgstimers.data.DateTime;
import net.doctorg.drgstimers.data.Timer;
import net.doctorg.drgstimers.util.TimerHandler;
import net.doctorg.drgstimers.util.TimerNotInListException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Set;

public class TimerCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_TIMERS = (p_136344_, p_136345_) -> {
        Set<String> timers = TimerHandler.getInstance().getTimerStack().keySet();
        return SharedSuggestionProvider.suggest(timers.stream(), p_136345_);
    };

    protected static final SimpleCommandExceptionType ERROR_TIMER_NOT_EXIST = new SimpleCommandExceptionType(Component.translatable("commands.timer.not_exist"));
    private static final SimpleCommandExceptionType ERROR_TIMER_ALREADY_STARTED = new SimpleCommandExceptionType(Component.translatable("commands.timer.already_started"));
    private static final SimpleCommandExceptionType ERROR_TIMER_NOT_STARTED = new SimpleCommandExceptionType(Component.translatable("commands.timer.not_started"));
    private static final SimpleCommandExceptionType ERROR_NO_ACTIVE_TIMERS = new SimpleCommandExceptionType(Component.translatable("commands.timer.no_active_timers"));
    private static final SimpleCommandExceptionType ERROR_SAME_RUN_WHILE_GAME_IS_PAUSED = new SimpleCommandExceptionType(Component.translatable("commands.timer.same.run_while_paused"));
    private static final SimpleCommandExceptionType ERROR_SAME_SET_TIME = new SimpleCommandExceptionType(Component.translatable("commands.timer.same.set_time"));


    public TimerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("timer")
                .then(Commands.literal("timers")
                        .then(Commands.argument("name", StringArgumentType.word()).suggests(SUGGEST_TIMERS)
                                .then(Commands.literal("set")
                                        .then(Commands.argument("seconds", IntegerArgumentType.integer(0))
                                                .executes((command) ->
                                                    setTimer(command.getSource(), StringArgumentType.getString(command, "name"), new DateTime(IntegerArgumentType.getInteger(command, "seconds"), 0, 0), false)
                                                )
                                                .then(Commands.argument("reset", BoolArgumentType.bool())
                                                        .executes((command) ->
                                                             setTimer(command.getSource(), StringArgumentType.getString(command, "name"), new DateTime(IntegerArgumentType.getInteger(command, "seconds"), 0, 0), BoolArgumentType.getBool(command, "reset"))
                                                        )
                                                )
                                                .then(Commands.argument("minutes", IntegerArgumentType.integer(0))
                                                        .executes((command) ->
                                                             setTimer(command.getSource(), StringArgumentType.getString(command, "name"), new DateTime(IntegerArgumentType.getInteger(command, "seconds"), IntegerArgumentType.getInteger(command, "minutes"), 0), false)
                                                        )
                                                        .then(Commands.argument("reset", BoolArgumentType.bool())
                                                                .executes((command) ->
                                                                     setTimer(command.getSource(), StringArgumentType.getString(command, "name"), new DateTime(IntegerArgumentType.getInteger(command, "seconds"), IntegerArgumentType.getInteger(command, "minutes"), 0), BoolArgumentType.getBool(command, "reset"))
                                                                )
                                                        )
                                                        .then(Commands.argument("hours", IntegerArgumentType.integer(0))
                                                                .executes((command) ->
                                                                     setTimer(command.getSource(), StringArgumentType.getString(command, "name"), new DateTime(IntegerArgumentType.getInteger(command, "seconds"), IntegerArgumentType.getInteger(command, "minutes"), IntegerArgumentType.getInteger(command, "hours")), false)
                                                                )
                                                                .then(Commands.argument("reset", BoolArgumentType.bool())
                                                                        .executes((command) ->
                                                                             setTimer(command.getSource(), StringArgumentType.getString(command, "name"), new DateTime(IntegerArgumentType.getInteger(command, "seconds"), IntegerArgumentType.getInteger(command, "minutes"), IntegerArgumentType.getInteger(command, "hours")), BoolArgumentType.getBool(command, "reset"))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(Commands.literal("get")
                                        .executes((command) ->
                                             getTimer(command.getSource(), StringArgumentType.getString(command, "name"))
                                        )
                                )
                                .then(Commands.literal("settime")
                                        .executes((command) ->
                                             getTimerSetTime(command.getSource(), StringArgumentType.getString(command, "name"))
                                        )
                                )
                                .then(Commands.literal("pause")
                                        .executes((command) ->
                                             pauseTimer(command.getSource(), StringArgumentType.getString(command, "name"))
                                        )
                                )

                                .then(Commands.literal("reset")
                                        .executes((command) ->
                                             resetTimer(command.getSource(), StringArgumentType.getString(command, "name"), false)
                                        )
                                        .then(Commands.argument("pause", BoolArgumentType.bool())
                                                .executes((command) ->
                                                     resetTimer(command.getSource(), StringArgumentType.getString(command, "name"), BoolArgumentType.getBool(command, "pause"))
                                                )
                                        )
                                )

                                .then(Commands.literal("start")
                                        .executes((command) ->
                                             startTimer(command.getSource(), StringArgumentType.getString(command, "name"))
                                        )
                                )

                                .then(Commands.literal("remove")
                                        .executes((command) ->
                                             removeTimer(command.getSource(), StringArgumentType.getString(command, "name"))
                                        )
                                )
                                .then(Commands.literal("run_while_game_is_paused")
                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .executes((command) ->
                                                     setRunWhileGameIsPaused(command.getSource(), StringArgumentType.getString(command, "name"), BoolArgumentType.getBool(command, "value"))
                                                )
                                        )
                                        .executes((command) ->
                                             getRunWhileGameIsPaused(command.getSource(), StringArgumentType.getString(command, "name"))
                                        )
                                )
                        )
                )
                .then(Commands.literal("listrunning")
                        .executes((command) ->
                             listActiveTimers(command.getSource())
                        )
                )
                .then(Commands.literal("pauseall")
                        .executes((command) ->
                             pauseAllTimers(command.getSource())
                        )
                )
        );
    }

    private int removeTimer(CommandSourceStack source, String timerIdName) throws CommandSyntaxException {
        Timer timer = tryGetTimer(timerIdName);

        timer.setTimerRunning(false);
        TimerHandler.getInstance().getTimerStack().remove(timerIdName);

        source.sendSuccess(() ->
                Component.translatable("commands.timer.removed_timer.success", timerIdName), true);

        return 0;
    }

    private int pauseAllTimers(CommandSourceStack source) throws CommandSyntaxException {
        if (TimerHandler.getInstance().getRunningTimers().isEmpty()) {
            throw ERROR_NO_ACTIVE_TIMERS.create();
        }

        for (Timer timer : TimerHandler.getInstance().getTimerStack().values()) {
            if (timer.isTimerRunning()) {
                timer.setTimerRunning(false);
            }
        }

        source.sendSuccess(() ->
             Component.translatable("commands.timer.paused_all_running.success"), true);

        return 0;
    }

    private int listActiveTimers(CommandSourceStack source) throws CommandSyntaxException {
        if (TimerHandler.getInstance().getRunningTimers().isEmpty()) {
            throw ERROR_NO_ACTIVE_TIMERS.create();
        }

        StringBuilder listedTimers = new StringBuilder();

        for (String timerIdName : TimerHandler.getInstance().getRunningTimers().keySet()) {
            listedTimers.append("\n").append(timerIdName);
        }

        source.sendSuccess(() ->
             Component.translatable("commands.timer.list_active_timers.success", listedTimers.toString()), true);

        return 0;
    }

    private int startTimer(CommandSourceStack source, String timerIdName) throws CommandSyntaxException {
        Timer timer = tryGetTimer(timerIdName);

        if (timer.isTimerRunning()) {
            throw ERROR_TIMER_ALREADY_STARTED.create();
        }

        timer.setTimerRunning(true);

        source.sendSuccess(() ->
             Component.translatable("commands.timer.start_timer.success", TimerHandler.getInstance().getKeyByValue(timer)), true);

        return 0;
    }

    private int setTimer(CommandSourceStack source, String timerIdName, DateTime time, boolean resetTimer) throws CommandSyntaxException {
        if (TimerHandler.getInstance().getTimerStack().containsKey(timerIdName)) {
            Timer timer = TimerHandler.getInstance().getTimerStack().get(timerIdName);

            if (time.equals(timer.getSetTime())) {
                throw ERROR_SAME_SET_TIME.create();
            }

            timer.setSetTime(time, resetTimer);

            if (resetTimer) {
                source.sendSuccess(() ->
                     Component.translatable("commands.timer.set_and_reset.success", TimerHandler.getInstance().getKeyByValue(timer), time.toString()), true);
                return 0;
            }

            source.sendSuccess(() ->
                 Component.translatable("commands.timer.set.success", TimerHandler.getInstance().getKeyByValue(timer), time.toString()), true);
            return 0;
        }

        TimerHandler.getInstance().getTimerStack().put(timerIdName, new Timer(time));
        source.sendSuccess(() ->
             Component.translatable("commands.timer.set.add_and_set.success", timerIdName, time.toString()), true);

        return 0;
    }

    private int getTimer(CommandSourceStack source, String timerIdName) throws CommandSyntaxException { //-ERROR
        Timer timer = tryGetTimer(timerIdName);

        if (!timer.isTimerRunning()) {
            source.sendSuccess(() ->
                 Component.translatable("commands.timer.get.not_started.success", timerIdName, timer.getTime()), true);
            return 0;
        }

        source.sendSuccess(() ->
             Component.translatable("commands.timer.get.success", timerIdName, timer.getTime()), true);

        return 0;
    }

    private int getTimerSetTime(CommandSourceStack source, String timerIdName) throws CommandSyntaxException {
        Timer timer = tryGetTimer(timerIdName);

        source.sendSuccess(() ->
             Component.translatable("commands.timer.get_set_time.success", timerIdName, timer.getSetTime().toString()), true);

        return 0;
    }

    private int pauseTimer(CommandSourceStack source, String timerIdName) throws CommandSyntaxException {
        Timer timer = tryGetTimer(timerIdName);

        if (!timer.isTimerRunning()) {
            throw ERROR_TIMER_NOT_STARTED.create();
        }

        timer.setTimerRunning(false);

        source.sendSuccess(() ->
             Component.translatable("commands.timer.pause.success", timerIdName), true);

        return 0;
    }

    private int resetTimer(CommandSourceStack source, String timerIdName, boolean pause) throws CommandSyntaxException {
        Timer timer = tryGetTimer(timerIdName);

        timer.resetTime(pause);

        if (pause) {
            source.sendSuccess(() ->
                 Component.translatable("commands.timer.reset_and_pause.success", timerIdName), true);
            return 0;
        }

        source.sendSuccess(() ->
             Component.translatable("commands.timer.reset.success", timerIdName), true);

        return 0;
    }

    private int setRunWhileGameIsPaused(CommandSourceStack source, String timerIdName, boolean value) throws CommandSyntaxException {
        Timer timer = tryGetTimer(timerIdName);

        if (value == timer.getRunWhileGamePaused()) {
            throw ERROR_SAME_RUN_WHILE_GAME_IS_PAUSED.create();
        }

        timer.setRunWhileGamePaused(value);

        if (value) {
            source.sendSuccess(() ->
                 Component.translatable("commands.timer.set_run_while_paused.true.success", timerIdName), true);
            return 0;
        }

        source.sendSuccess(() ->
             Component.translatable("commands.timer.set_run_while_paused.false.success", timerIdName), true);

        return 0;
    }

    private int getRunWhileGameIsPaused(CommandSourceStack source, String timerIdName) throws CommandSyntaxException {
        Timer timer = tryGetTimer(timerIdName);

        if (timer.getRunWhileGamePaused()) {
            source.sendSuccess(() ->
                 Component.translatable("commands.timer.get_run_while_paused.true.success", timerIdName), true);
            return 0;
        }

        source.sendSuccess(() ->
             Component.translatable("commands.timer.get_run_while_paused.false.success", timerIdName), true);

        return 0;
    }

    private static Timer tryGetTimer(String timerIdName) throws CommandSyntaxException {
        try {
            return TimerHandler.getInstance().getTimer(timerIdName);
        } catch (TimerNotInListException tdnee) {
            throw ERROR_TIMER_NOT_EXIST.create();
        }
    }
}

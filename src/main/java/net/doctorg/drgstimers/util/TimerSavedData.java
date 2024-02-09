package net.doctorg.drgstimers.util;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.data.Timer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.UUID;

public class TimerSavedData extends SavedData {
    public static TimerSavedData instance;

    public TimerSavedData() {
        if (TimerHandler.getInstance().getLevelId().equals(new UUID(0, 0))) {
            TimerHandler.getInstance().setLevelId(UUID.randomUUID());
        }
    }

    public static TimerSavedData load(CompoundTag tag) {
        for (Tag t : tag.getList("Timers", 10)) {
            CompoundTag ct = (CompoundTag) t;

            Timer timer = new Timer(ct.getFloat("startTimeSeconds"), ct.getInt("startTimeMinutes"), ct.getInt("startTimeHours"));
            timer.setTime(ct.getFloat("timeSeconds"), ct.getInt("timeMinutes"), ct.getInt("timeHours"));
            timer.setRunWhileGamePaused(ct.getBoolean("runWhileGameIsPaused"));
            TimerHandler.getInstance().getTimerStack().put(ct.getString("timerIdName"), timer);
            timer.setTimerRunning(ct.getBoolean("timerRunning"));
        }

        TimerHandler.getInstance().setLevelId(tag.getUUID("level-id"));

        return new TimerSavedData();
    }

    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        ListTag listTag = new ListTag();
        for (Timer timer : TimerHandler.getInstance().getTimerStack().values()) {
            listTag.add(timerToTag(timer, TimerHandler.getInstance().getKeyByValue(timer)));
        }
        pCompoundTag.put("Timers", listTag);

        if (TimerHandler.getInstance().getLevelId().equals(new UUID(0, 0))) {
            DoctorGsTimers.LOGGER.error("Couldn't save levelId because levelId in TimerHandler.Instance is 0");
        } else {
            pCompoundTag.putUUID("level-id", TimerHandler.getInstance().getLevelId());
        }

        setDirty(false);
        return pCompoundTag;
    }

    public CompoundTag timerToTag(Timer timer, String timerIdName) {
        CompoundTag tag = new CompoundTag();
        tag.putString("timerIdName", timerIdName);
        tag.putBoolean("timerRunning", timer.isTimerRunning());
        tag.putBoolean("runWhileGameIsPaused", timer.getRunWhileGamePaused());
        tag.putFloat("startTimeSeconds", timer.getSetTime().getSeconds());
        tag.putInt("startTimeMinutes", timer.getSetTime().getMinutes());
        tag.putInt("startTimeHours", timer.getSetTime().getHours());
        tag.putFloat("timeSeconds", timer.getTime().getSeconds());
        tag.putInt("timeMinutes", timer.getTime().getMinutes());
        tag.putInt("timeHours", timer.getTime().getHours());
        return tag;
    }
}

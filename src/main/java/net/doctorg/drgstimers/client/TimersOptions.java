package net.doctorg.drgstimers.client;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.doctorg.drgstimers.DoctorGsTimers;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class TimersOptions {

    public File timersOptionsFile;
    private static final Gson GSON = new Gson();
    private static final Splitter OPTION_SPLITTER = Splitter.on(':').limit(2);

    public final OptionInstance<Integer> scrollSensitivity = new OptionInstance<>("options.timer_scroll_sensitivity", OptionInstance.noTooltip(), (text, value) ->
            Component.translatable("options.percent_value", text, value), new OptionInstance.IntRange(50, 200), 100, (valueInt) -> {
    });

    public final OptionInstance<Boolean> showTimers = OptionInstance.createBoolean("options.show_timers_in_game", true, (valueBool) -> {
    });

    public final OptionInstance<Integer> maximumCharacters = new OptionInstance<>("options.maximum_timer_name_characters", OptionInstance.noTooltip(),
            Options::genericValueLabel, new OptionInstance.IntRange(3, 20), 15, (valueInt) -> {
    });

    /*public final OptionInstance<Integer> xOffset = new OptionInstance<>("options.x_offset", OptionInstance.noTooltip(), (text, value) ->
            Component.translatable("options.percent_value", text, value), new OptionInstance.IntRange(0, 100), 0, (valueInt) -> {
    });

    public final OptionInstance<Integer> yOffset = new OptionInstance<>("options.y_offset", OptionInstance.noTooltip(), (text, value) ->
            Component.translatable("options.percent_value", text, value), new OptionInstance.IntRange(0, 100), 0, (valueInt) -> {
    });*/

    public TimersOptions() {
        timersOptionsFile = new File(FMLPaths.CONFIGDIR.get() + "\\drgstimers-options.txt");
        try {
            if (!timersOptionsFile.exists()) {
                timersOptionsFile.createNewFile();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        load();
    }

    public void save() {
        try (final PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.timersOptionsFile), StandardCharsets.UTF_8))) {
            this.processOptions(new Options.FieldAccess() {
                public void writePrefix(String name) {
                    printwriter.print(name);
                    printwriter.print(':');
                }

                public <T> void process(String name, OptionInstance<T> value) {
                    DataResult<JsonElement> dataresult = value.codec().encodeStart(JsonOps.INSTANCE, value.get());
                    dataresult.error().ifPresent((p_232133_) -> {
                        DoctorGsTimers.LOGGER.error("Error saving option " + value + ": " + p_232133_);
                    });
                    dataresult.result().ifPresent((p_232140_) -> {
                        this.writePrefix(name);
                        printwriter.println(GSON.toJson(p_232140_));
                    });
                }

                public int process(String name, int value) {
                    this.writePrefix(name);
                    printwriter.println(value);
                    return value;
                }

                public boolean process(String name, boolean value) {
                    this.writePrefix(name);
                    printwriter.println(value);
                    return value;
                }

                public String process(String name, String value) {
                    this.writePrefix(name);
                    printwriter.println(value);
                    return value;
                }

                public float process(String name, float value) {
                    this.writePrefix(name);
                    printwriter.println(value);
                    return value;
                }

                public <T> T process(String name, T value, Function<String, T> getByName, Function<T, String> getByValue) {
                    this.writePrefix(name);
                    printwriter.println(getByValue.apply(value));
                    return value;
                }
            });
        } catch (Exception exception) {
            DoctorGsTimers.LOGGER.error("Failed to save options", exception);
        }
    }

    public void load() {
        try {
            if (!this.timersOptionsFile.exists()) {
                return;
            }

            CompoundTag compoundtag = new CompoundTag();

            try (BufferedReader bufferedreader = Files.newReader(this.timersOptionsFile, Charsets.UTF_8)) {
                bufferedreader.lines().forEach((line) -> {
                    try {
                        Iterator<String> iterator = OPTION_SPLITTER.split(line).iterator();
                        compoundtag.putString(iterator.next(), iterator.next());
                    } catch (Exception exception1) {
                        DoctorGsTimers.LOGGER.warn("Skipping bad option: {}", line);
                    }

                });
            }

            java.util.function.Consumer<Options.FieldAccess> processor = this::processOptions;
            processor.accept(new Options.FieldAccess() {
                @Nullable
                private String getValueOrNull(String p_168459_) {
                    return compoundtag.contains(p_168459_) ? compoundtag.getString(p_168459_) : null;
                }

                public <T> void process(String name, OptionInstance<T> value) {
                    String s = this.getValueOrNull(name);
                    if (s != null) {
                        JsonReader jsonreader = new JsonReader(new StringReader(s.isEmpty() ? "\"\"" : s));
                        JsonElement jsonelement = JsonParser.parseReader(jsonreader);
                        DataResult<T> dataresult = value.codec().parse(JsonOps.INSTANCE, jsonelement);
                        dataresult.error().ifPresent((p_232130_) -> {
                            DoctorGsTimers.LOGGER.error("Error parsing option value " + s + " for option " + value + ": " + p_232130_.message());
                        });
                        dataresult.result().ifPresent(value::set);
                    }

                }

                public int process(String name, int value) {
                    String s = this.getValueOrNull(name);
                    if (s != null) {
                        try {
                            return Integer.parseInt(s);
                        } catch (NumberFormatException numberformatexception) {
                            DoctorGsTimers.LOGGER.warn("Invalid integer value for option {} = {}", name, s, numberformatexception);
                        }
                    }

                    return value;
                }

                public boolean process(String name, boolean value) {
                    String s = this.getValueOrNull(name);
                    return s != null ? TimersOptions.isTrue(s) : value;
                }

                public String process(String name, String value) {
                    return MoreObjects.firstNonNull(this.getValueOrNull(name), value);
                }

                public float process(String name, float value) {
                    String s = this.getValueOrNull(name);
                    if (s != null) {
                        if (TimersOptions.isTrue(s)) {
                            return 1.0F;
                        }

                        if (TimersOptions.isFalse(s)) {
                            return 0.0F;
                        }

                        try {
                            return Float.parseFloat(s);
                        } catch (NumberFormatException numberformatexception) {
                            DoctorGsTimers.LOGGER.warn("Invalid floating point value for option {} = {}", name, s, numberformatexception);
                        }
                    }

                    return value;
                }

                public <T> T process(String name, T value, Function<String, T> getByName, Function<T, String> getByValue) {
                    String s = this.getValueOrNull(name);
                    return (T)(s == null ? value : getByName.apply(s));
                }
            });
        } catch (Exception exception) {
            DoctorGsTimers.LOGGER.error("Failed to load options", exception);
        }

    }

    private void processOptions(Options.FieldAccess pAccessor) {
        pAccessor.process("showTimers", this.showTimers);
        pAccessor.process("scrollSensitivity", this.scrollSensitivity);
        pAccessor.process("maximumTimerWidth", this.maximumCharacters);
        //pAccessor.process("xOffset", this.xOffset);
        //pAccessor.process("yOffset", this.yOffset);
    }

    static boolean isTrue(String pValue) {
        return "true".equals(pValue);
    }

    static boolean isFalse(String pValue) {
        return "false".equals(pValue);
    }
}

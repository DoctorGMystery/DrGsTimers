package net.doctorg.drgstimers.client.gui;

import com.google.common.base.Splitter;
import net.doctorg.drgstimers.data.DateTime;
import net.doctorg.drgstimers.util.TimerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Iterator;

@OnlyIn(Dist.CLIENT)
public class AddTimerScreen extends Screen {

    private String name = "";
    private DateTime setTime;
    private EditBox setTimeBox;
    private EditBox nameBox;


    protected AddTimerScreen() {
        super(Component.translatable("add_timer.screen.title"));
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(
                Button.builder(CommonComponents.GUI_DONE, p_280847_ -> onClose())
                        .bounds(width / 2 - 75, height - 26, 150, 20)
                        .build()
        );

        setTimeBox = new EditBox(this.font, width / 2 + 5, height / 2 - 20 - 10, 70, 20, Component.translatable("add_timer.screen.set_time_box"));
        setTimeBox.setHint(Component.literal("HH:MM:SS"));
        setTimeBox.setResponder(this::onSetTimeEdit);

        nameBox = new EditBox(this.font, width / 2 - 70 - 5, height / 2 - 20 - 10, 70, 20, Component.translatable("add_timer.screen.name_box"));
        nameBox.setResponder(this::onNameEdit);

        addRenderableWidget(setTimeBox);
        addRenderableWidget(nameBox);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.drawCenteredString(this.font, Component.translatable("add_timer.screen.title").getString() + ": " + name, width / 2, height / 2 - 55, 0xFFFFFFFF);
        if (!setTimeBox.isFocused()) {
            setTimeBox.setTextColor(0xFFE0E0E0);
            setTimeBox.setValue(setTime != null ? setTime.toString() : "");
        }
        if (!nameBox.isFocused()) {
            nameBox.setTextColor(0xFFE0E0E0);
            nameBox.setValue(name);
        }

        pGuiGraphics.drawString(this.font, Component.translatable("add_timer.screen.set_time_box"), width / 2 + 4, height / 2 - 20 - 20, 0xFFA0A0A0);
        pGuiGraphics.drawString(this.font, Component.translatable("add_timer.screen.name_box"), width / 2 - 70 - 6, height / 2 - 20 - 20, 0xFFA0A0A0);

    }

    @Override
    public void onClose() {
        System.out.println("onClose");
        if (!name.isEmpty() && setTime != null) {
            Minecraft.getInstance().player.connection.sendCommand("timer timers " + name + " set " + (int) setTime.getSeconds() + " " + setTime.getMinutes() + " " + setTime.getHours());
        }
        minecraft.setScreen(new EditTimersScreen());
    }

    public void onNameEdit(String input) {
        if (!nameBox.isFocused()) {
            return;
        }

        boolean validName = input.matches("^[a-zA-Z-_.+0-9]*$") && !input.isEmpty();

        if (!validName) {
            nameBox.setTextColor(0xFFFF0000);
            name = "";
            return;
        }

        for (String timerId : TimerHandler.getClientInstance(false).getTimerStack().keySet()) {
            if (timerId.equals(input)) {
                nameBox.setTextColor(0xFFFF0000);
                name = "";
                return;
            }
        }

        nameBox.setTextColor(0xFFFFFFFF);
        name = input;
    }

    public void onSetTimeEdit(String input) {
        if (!setTimeBox.isFocused()) {
            return;
        }

        int hours;
        int minutes;
        int seconds;

        Splitter splitter = Splitter.on(":").limit(3);
        Iterator<String> iterator = splitter.split(input).iterator();

        try {
            hours = Integer.parseInt(iterator.next());
            minutes = Integer.parseInt(iterator.next());
            seconds = Integer.parseInt(iterator.next());
        } catch (Exception e) {
            setTimeBox.setTextColor(0xFFFF0000);
            setTime = null;
            return;
        }

        if (hours < 0 || minutes < 0 || seconds < 0) {
            setTimeBox.setTextColor(0xFFFF0000);
            setTime = null;
            return;
        }

        setTimeBox.setTextColor(0xFFFFFFFF);
        setTime = new DateTime(seconds, minutes, hours);
    }
}

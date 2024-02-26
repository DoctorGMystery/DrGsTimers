package net.doctorg.drgstimers.client.gui;

import com.google.common.base.Splitter;
import net.doctorg.drgstimers.data.TimerData;
import net.doctorg.drgstimers.util.TimerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Iterator;

@OnlyIn(Dist.CLIENT)
public class EditTimerScreen extends Screen {

    private final Component name;
    private TimerData timer;

    private OptionsList optionsList;
    private EditBox setTimeBox;
    private Button startTimerButton;
    private Button pauseTimerButton;
    private Button resetTimerButton;
    private Button resumeTimerButton;
    private Button deleteTimerButton;

    private final OptionInstance<Boolean> runWhileGamePaused = OptionInstance.createBoolean("options.edit_timer.run_while_game_paused", false, this::onRunWhileGamePaused);
    private final OptionInstance<Boolean> visible = OptionInstance.createBoolean("options.edit_timer.visible", false, this::onVisible);
    private final OptionInstance<Boolean> alwaysVisible = OptionInstance.createBoolean("options.edit_timer.always_visible", false, this::onAlwaysVisible);

    protected EditTimerScreen(Component name, TimerData timer) {
        super(Component.translatable("edit_timer.screen.title"));
        this.name = name;
        this.timer = timer;
        this.runWhileGamePaused.set(timer.getRunWhileGamePaused());
        this.visible.set(timer.isVisible());
        this.alwaysVisible.set(timer.isAlwaysVisible());
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(
                Button.builder(CommonComponents.GUI_DONE, p_280847_ -> minecraft.setScreen(new EditTimersScreen()))
                        .bounds(width / 2 - 75, height - 26, 150, 20)
                        .build()
        );

        optionsList = new OptionsList(minecraft, width, height - 32, 120, 25);
        optionsList.setRenderBackground(false);

        optionsList.addBig(runWhileGamePaused);
        optionsList.addSmall(visible, alwaysVisible);


        setTimeBox = new EditBox(this.font, width / 2 - 35, 98, 70, 20, Component.translatable("edit_timer.screen.set_time_box"));
        setTimeBox.setValue(timer.getSetTime().toString());
        setTimeBox.setResponder(this::onSetTimeEdit);


        startTimerButton = Button.builder(Component.translatable("edit_timer.screen.start_timer"), this::startTimer).build();
        startTimerButton.setRectangle(50, 20, width / 2 - 107, 65);

        pauseTimerButton = Button.builder(Component.translatable("edit_timer.screen.pause_timer"), this::pauseTimer).build();
        pauseTimerButton.setRectangle(50, 20, width / 2 - 52, 65);

        resetTimerButton = Button.builder(Component.translatable("edit_timer.screen.reset_timer"), this::resetTimer).build();
        resetTimerButton.setRectangle(50, 20, width / 2 + 3 , 65);

        resumeTimerButton = Button.builder(Component.translatable("edit_timer.screen.resume_timer"), this::resumeTimer).build();
        resumeTimerButton.setRectangle(50, 20, width / 2 - 107, 65);

        deleteTimerButton = Button.builder(Component.translatable("edit_timer.screen.delete_timer"), this::deleteTimer).build();
        deleteTimerButton.setRectangle(50, 20, width / 2 + 58, 65);

        if (timer.isTimerRunning()) {
            startTimerButton.visible = false;
            startTimerButton.active = false;
        } else {
            pauseTimerButton.active = false;
            resumeTimerButton.visible = false;
            resumeTimerButton.active = false;
            if (!timer.getTime().equals(timer.getSetTime())) {
                startTimerButton.visible = false;
                startTimerButton.active = false;
            }
            if (timer.getTime().equals(0, 0, 0)) {
                resumeTimerButton.visible = true;
            }
        }


        addRenderableWidget(optionsList);
        addRenderableWidget(setTimeBox);
        addRenderableWidget(startTimerButton);
        addRenderableWidget(pauseTimerButton);
        addRenderableWidget(resetTimerButton);
        addRenderableWidget(resumeTimerButton);
        addRenderableWidget(deleteTimerButton);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        timer = TimerHandler.getClientInstance(false).getTimer(name.getString());

        Component status;

        if (!setTimeBox.isFocused()) {
            setTimeBox.setValue(timer.getSetTime().toString());
        }

        if (timer.isTimerRunning()) {
            status = Component.translatable("edit_timer.screen.status.running");
        } else if (timer.getTime().equals(timer.getSetTime())) {
            status = Component.translatable("edit_timer.screen.status.not_started");
        } else if (timer.getTime().equals(0, 0, 0)) {
            status = Component.translatable("edit_timer.screen.status.elapsed");
            pauseTimerButton.active = false;
        } else {
            status = Component.translatable("edit_timer.screen.status.paused");
        }

        pGuiGraphics.drawCenteredString(font, name, width / 2, 13, 0xFFFFFFFF);

        pGuiGraphics.drawCenteredString(font, Component.literal(Component.translatable("edit_timer.screen.status").getString() + status.getString()), width / 2 - 100, 45, 0xFFFFFFFF);
        pGuiGraphics.drawCenteredString(font, Component.literal(Component.translatable("edit_timer.screen.time").getString() + timer.getTime().toString()), width / 2, 45, 0xFFFFFFFF);
        pGuiGraphics.drawCenteredString(font, Component.literal(Component.translatable("edit_timer.screen.set_time").getString() +  timer.getSetTime().toString()), width / 2 + 100, 45, 0xFFFFFFFF);

        pGuiGraphics.drawString(font, Component.translatable("edit_timer.screen.set_time_box"), width / 2 - 76, 88, 0xFFA0A0A0);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    public void startTimer(Button button) {
        this.minecraft.player.connection.sendCommand("timer timers " + name.getString() + " start");
        button.active = false;
        button.visible = false;
        resumeTimerButton.active = false;
        resumeTimerButton.visible = true;
        pauseTimerButton.active = true;
        pauseTimerButton.visible = true;
    }

    public void pauseTimer(Button button) {
        this.minecraft.player.connection.sendCommand("timer timers " + name.getString() + " pause");
        button.active = false;
        resumeTimerButton.active = true;
        if (timer.getTime().equals(timer.getSetTime())) {
            resumeTimerButton.visible = false;
            startTimerButton.active = true;
            startTimerButton.visible = true;
        }
    }

    public void resetTimer(Button button) {
        this.minecraft.player.connection.sendCommand("timer timers " + name.getString() + " reset");
        if (!timer.isTimerRunning()) {
            resumeTimerButton.active = false;
            resumeTimerButton.visible = false;
            startTimerButton.active = true;
            startTimerButton.visible = true;
        }
    }

    public void resumeTimer(Button button) {
        this.minecraft.player.connection.sendCommand("timer timers " + name.getString() + " start");
        button.active = false;
        pauseTimerButton.active = true;
        pauseTimerButton.visible = true;
    }

    public void deleteTimer(Button button) {
        this.minecraft.player.connection.sendCommand("timer timers " + name.getString() + " remove");
        EditTimersScreen editTimerScreen = new EditTimersScreen();
        minecraft.setScreen(editTimerScreen);
    }

    public void onRunWhileGamePaused(boolean value) {
        if (this.minecraft == null) {
            return;
        }
        this.minecraft.player.connection.sendCommand("timer timers " + name.getString() + " run_while_game_is_paused " + value);
    }

    public void onVisible(boolean value) {
        if (this.minecraft == null) {
            return;
        }
        this.minecraft.player.connection.sendCommand("timer timers " + name.getString() + " visible " + value);
    }

    public void onAlwaysVisible(boolean value) {
        if (this.minecraft == null) {
            return;
        }
        this.minecraft.player.connection.sendCommand("timer timers " + name.getString() + " always_visible " + value);
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
            return;
        }

        if (hours < 0 || minutes < 0 || seconds < 0) {
            setTimeBox.setTextColor(0xFFFF0000);
            return;
        }

        setTimeBox.setTextColor(0xFFFFFFFF);
        Minecraft.getInstance().player.connection.sendCommand("timer timers " + name.getString() + " set " + seconds + " " + minutes + " " + hours + " true");
    }


    @Override
    public void onClose() {
        minecraft.setScreen(new EditTimersScreen());
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderTransparentBackground(pGuiGraphics);
        pGuiGraphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
        pGuiGraphics.blit(BACKGROUND_LOCATION, 0, 0, 0, 0.0F, 0.0F, this.width, 32, 32, 32);
        pGuiGraphics.blit(BACKGROUND_LOCATION, 0, height - 32, 0, 0.0F, 0.0F, this.width, 32, 32, 32);
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        pGuiGraphics.fillGradient(RenderType.guiOverlay(), 0, 32, this.width, 32 + 4, -16777216, 0, 0);
        pGuiGraphics.fillGradient(RenderType.guiOverlay(), 0, height - 32 - 4, this.width, height - 32, 0, -16777216, 0);
    }
}

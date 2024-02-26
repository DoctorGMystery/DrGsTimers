package net.doctorg.drgstimers.client.gui;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TimersSettingsScreen extends Screen {

    private OptionsList list;

    private boolean changePositionMode = false;

    private final Button addDebugTimers =
            Button.builder(Component.translatable("timer_settings.screen.add_debug_timers"), (button) -> addDebugTimers()).build();

    private final Button changePosition =
            Button.builder(Component.translatable("timer_settings.screen.change_position"), (button) -> setChangePositionMode(true)).build();

    private final Button changePositionDone =
            Button.builder(Component.translatable("gui.done"), (button) -> setChangePositionMode(false)).build();

    private final Button changePositionReset =
            Button.builder(Component.translatable("timer_settings.screen.reset"), (button) -> {
                DoctorGsTimers.INSTANCE.getTimersOptions().yOffset = 0;
                DoctorGsTimers.INSTANCE.getTimersOptions().xOffset = 0;
                DoctorGsTimers.INSTANCE.getTimersOptions().save();
                this.transformBox.setRectangle(100, DoctorGsTimers.INSTANCE.getTimersOptions().guiHeight, (int) (DoctorGsTimers.INSTANCE.getTimersOptions().xOffset * Minecraft.getInstance().getWindow().getGuiScaledWidth()), (int) (DoctorGsTimers.INSTANCE.getTimersOptions().yOffset * Minecraft.getInstance().getWindow().getGuiScaledHeight()));
            }).build();

    private final TransformScaleHeightBox transformBox =
            new TransformScaleHeightBox((int) (DoctorGsTimers.INSTANCE.getTimersOptions().xOffset * Minecraft.getInstance().getWindow().getGuiScaledWidth()), (int) (DoctorGsTimers.INSTANCE.getTimersOptions().yOffset * Minecraft.getInstance().getWindow().getGuiScaledHeight()),
                    100, DoctorGsTimers.INSTANCE.getTimersOptions().guiHeight, Component.translatable("timer_settings.screen.position_axes"),
                    (height) -> DoctorGsTimers.INSTANCE.getTimersOptions().guiHeight = height,
                    (x, y) ->  {
                        DoctorGsTimers.INSTANCE.getTimersOptions().xOffset = (float) x / Minecraft.getInstance().getWindow().getGuiScaledWidth();
                        DoctorGsTimers.INSTANCE.getTimersOptions().yOffset = (float) y / Minecraft.getInstance().getWindow().getGuiScaledHeight();
                    },
                    63);


    public TimersSettingsScreen() {
        super(Component.translatable("timer_settings.screen.title"));
        DoctorGsTimers.INSTANCE.getTimersOptions().load();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (!changePositionMode) {
            TimerOverlay.INSTANCE.renderTimers(pGuiGraphics);
            TimerOverlay.INSTANCE.calcScrollPosition();
        }

        pGuiGraphics.drawCenteredString(font, title, width / 2, 15, 0xFFFFFFFF);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        if (changePositionMode) {
            TimerOverlay.INSTANCE.renderTimers(pGuiGraphics);
            TimerOverlay.INSTANCE.calcScrollPosition();
        }
    }

    @Override
    protected void init() {
        list = addRenderableWidget(new OptionsList(Minecraft.getInstance(), width, height, 32, 25));

        list.addSmall(DoctorGsTimers.INSTANCE.getTimersOptions().scrollSensitivity, DoctorGsTimers.INSTANCE.getTimersOptions().showTimers);
        list.addBig(DoctorGsTimers.INSTANCE.getTimersOptions().maximumCharacters);

        list.setRenderBackground(false);

        changePosition.setRectangle(310, 20, width / 2 - 155, 86);
        changePositionDone.setRectangle(50, 20, width / 2 - 25, height - 30);
        changePositionDone.active = false;
        changePositionDone.visible = false;
        changePositionReset.setRectangle(50, 20, 10, height - 30);
        changePositionReset.active = false;
        changePositionReset.visible = false;
        transformBox.setRectangle(100, DoctorGsTimers.INSTANCE.getTimersOptions().guiHeight, (int) (DoctorGsTimers.INSTANCE.getTimersOptions().xOffset * Minecraft.getInstance().getWindow().getGuiScaledWidth()), (int) (DoctorGsTimers.INSTANCE.getTimersOptions().yOffset * Minecraft.getInstance().getWindow().getGuiScaledHeight()));
        transformBox.active = false;
        transformBox.visible = false;

        addDebugTimers.setRectangle(100, 20, width - 100 - 10, height - 20 - 5);

        addRenderableWidget(transformBox);
        addRenderableWidget(changePosition);
        addRenderableWidget(changePositionDone);
        addRenderableWidget(changePositionReset);
        addRenderableWidget(addDebugTimers);

        super.init();
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        super.resize(pMinecraft, pWidth, pHeight);
        if (changePositionMode) {
            setChangePositionMode(true);
        }
    }

    @Override
    public void onClose() {
        DoctorGsTimers.INSTANCE.getTimersOptions().save();
        super.onClose();
    }

    private void setChangePositionMode(boolean changePositionMode) {
        this.changePositionMode = changePositionMode;
        if (changePositionMode) {
            list.setSize(0, 0);
            list.visible = false;
            changePosition.active = false;
            changePosition.visible = false;
            addDebugTimers.active = false;
            addDebugTimers.visible = false;
            changePositionDone.active = true;
            changePositionDone.visible = true;
            changePositionReset.active = true;
            changePositionReset.visible = true;
            transformBox.active = true;
            transformBox.visible = true;
            return;
        }
        list.setSize(width, height);
        list.visible = true;
        changePosition.active = true;
        changePosition.visible = true;
        addDebugTimers.active = true;
        addDebugTimers.visible = true;
        changePositionDone.active = false;
        changePositionDone.visible = false;
        changePositionReset.active = false;
        changePositionReset.visible = false;
        transformBox.active = false;
        transformBox.visible = false;
    }

    public void addDebugTimers() {
        this.minecraft.player.connection.sendCommand("timer timers DEBUG-1 set 10 10 5");
        this.minecraft.player.connection.sendCommand("timer timers DEBUG-2 set 17 1 2");
        this.minecraft.player.connection.sendCommand("timer timers DEBUG-3 set 55 4 7");
        this.minecraft.player.connection.sendCommand("timer timers DEBUG-4 set 10 10 5");
        this.minecraft.player.connection.sendCommand("timer timers DEBUG-5 set 17 1 2");
        this.minecraft.player.connection.sendCommand("timer timers DEBUG-6 set 55 4 7");
        this.minecraft.player.connection.sendCommand("timer timers DEBUG-7 set 10 10 5");
        this.minecraft.player.connection.sendCommand("timer timers DEBUG-8 set 17 1 2");
        this.minecraft.player.connection.sendCommand("timer timers DEBUG-9 set 55 4 7");
        this.minecraft.player.connection.sendCommand("timer timers DEBUG-10 set 10 10 5");
        this.minecraft.player.connection.sendCommand("timer timers DEBUG-11 set 17 1 2");
        this.minecraft.player.connection.sendCommand("timer timers DEBUG-12 set 55 4 7");
        this.minecraft.player.connection.sendCommand("timer timers DEBUG-13 set 10 10 5");
        this.minecraft.player.connection.sendCommand("timer timers DEBUG-14 set 17 1 2");
        this.minecraft.player.connection.sendCommand("timer timers DEBUG-15 set 55 4 7");
    }
}

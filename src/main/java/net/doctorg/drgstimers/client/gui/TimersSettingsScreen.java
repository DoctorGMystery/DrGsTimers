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

        addRenderableWidget(transformBox);
        addRenderableWidget(changePosition);
        addRenderableWidget(changePositionDone);
        addRenderableWidget(changePositionReset);

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
        changePositionDone.active = false;
        changePositionDone.visible = false;
        changePositionReset.active = false;
        changePositionReset.visible = false;
        transformBox.active = false;
        transformBox.visible = false;
    }
}

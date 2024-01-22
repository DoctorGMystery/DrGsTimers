package net.doctorg.drgstimers.client.gui;

import net.doctorg.drgstimers.DoctorGsTimers;
import net.doctorg.drgstimers.client.InputHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TimersSettingsScreen extends Screen {

    public TimersSettingsScreen() {
        super(Component.translatable("timer_settings.screen.title"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        if (pMouseX < 150) {
            InputHandler.scrollDelta = pScrollY;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        TimerOverlay.INSTANCE.renderTimers(pGuiGraphics);
        TimerOverlay.INSTANCE.calcScrollPosition();

        pGuiGraphics.drawCenteredString(font, this.title, width / 2, 15, 0xFFFFFFFF);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void init() {
        OptionsList list = new OptionsList(this.minecraft, this.width, this.height, 32, 25);

        list.addSmall(DoctorGsTimers.INSTANCE.getTimersOptions().scrollSensitivity, DoctorGsTimers.INSTANCE.getTimersOptions().showTimers);
        list.addBig(DoctorGsTimers.INSTANCE.getTimersOptions().maximumCharacters);
        //list.addSmall(DoctorGsTimers.INSTANCE.getTimersOptions().yOffset, DoctorGsTimers.INSTANCE.getTimersOptions().xOffset);

        list.setRenderBackground(false);

        addRenderableWidget(list);

        super.init();
    }

    @Override
    public void onClose() {
        DoctorGsTimers.INSTANCE.getTimersOptions().save();
        super.onClose();
    }
}

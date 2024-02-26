package net.doctorg.drgstimers.client.gui;

import net.doctorg.drgstimers.data.TimerData;
import net.doctorg.drgstimers.util.TimerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class TimersList extends ObjectSelectionList<TimersList.TimerEntry> {


    public TimersList(EditTimersScreen editTimersScreen, Minecraft pMinecraft) {
        super(pMinecraft, editTimersScreen.width, editTimersScreen.height - 72, 40, 18);
        reloadEntries();
        this.setRenderBackground(false);
    }

    public void reloadEntries() {
        clearEntries();
        for (Map.Entry<String, ? extends TimerData> entry : TimerHandler.getClientInstance(false).getTimerStack().entrySet()) {
            this.addEntry(new TimerEntry(Component.literal(entry.getKey()), entry.getValue()));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class TimerEntry extends ObjectSelectionList.Entry<TimerEntry> {

        private final Component name;
        private final TimerData timer;

        public TimerEntry(Component name, TimerData timer) {
            this.name = name;
            this.timer = timer;
            this.refreshEntry();
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index,
                           int top, int left,
                           int width, int height,
                           int mouseX, int mouseY,
                           boolean hovering, float partialTick) {
            int textMaxWidth = left + 70;
            AbstractWidget.renderScrollingString(guiGraphics, Minecraft.getInstance().font, this.name, left + 5,
                    left + 5, top + 4, textMaxWidth, top + 10, 0xFFFFFFFF);

            guiGraphics.drawString(Minecraft.getInstance().font, timer.getSetTime().toString(), left + 90, top + 4, 0xFFFFFFFF);
            guiGraphics.drawString(Minecraft.getInstance().font, timer.getTime().toString(), left + 160, top + 4, 0xFFFFFFFF);
        }


        private void refreshEntry() {
        }

        @Override
        public Component getNarration() {
            return Component.empty();
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            TimersList.this.setSelected(this);
            return true;
        }

        public Component getName() {
            return name;
        }

        public TimerData getTimer() {
            return timer;
        }
    }
}

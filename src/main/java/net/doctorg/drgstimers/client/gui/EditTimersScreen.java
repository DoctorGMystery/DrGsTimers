package net.doctorg.drgstimers.client.gui;

import net.doctorg.drgstimers.util.TimerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditTimersScreen extends Screen {

    private TimersList timersList;

    private Button editButton;
    private Button removeButton;

    public EditTimersScreen() {
        super(Component.translatable("edit_timers.screen.title"));
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.timersList.children().size() != TimerHandler.getClientInstance(false).getTimerStack().size()) {
            timersList.reloadEntries();
        }


        if (this.timersList.getSelected() != null) {
            this.editButton.active = true;
            this.removeButton.active = true;
        } else {
            this.editButton.active = false;
            this.removeButton.active = false;
        }

        pGuiGraphics.drawCenteredString(font, title, width / 2, 8, 0xFFFFFFFF);
        pGuiGraphics.drawString(font, Component.translatable("edit_timers.screen.name"), width / 2 - 103, 25, 0xFFFFFFFF);
        pGuiGraphics.drawString(font, Component.translatable("edit_timers.screen.set_time"), width / 2 - 18, 25, 0xFFFFFFFF);
        pGuiGraphics.drawString(font, Component.translatable("edit_timers.screen.time"), width / 2 + 52, 25, 0xFFFFFFFF);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(this.timersList = new TimersList(this, Minecraft.getInstance()));
        addRenderableWidget(
                Button.builder(CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(null))
                        .bounds(this.width / 2 - 30, this.height - 26, 60, 20)
                        .build()
        );
        addRenderableWidget(Button.builder(Component.translatable("edit_timers.screen.add"), this::addTimer)
                        .bounds(this.width / 2 - 40 - 100 - 5, this.height - 26, 80, 20)
                        .build()
        );
        addRenderableWidget(this.removeButton = Button.builder(Component.translatable("edit_timers.screen.remove"), this::removeTimer)
                        .bounds(this.width / 2 - 40 + 80 + 5, this.height - 26, 80, 20)
                        .build()
        );
        addRenderableWidget(this.editButton = Button.builder(Component.translatable("edit_timers.screen.edit"), this::editTimer)
                        .bounds(this.width / 2 - 40 + 170 + 5, this.height - 26, 40, 20)
                        .build()
        );

        removeButton.active = false;
        editButton.active = false;
    }



    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderTransparentBackground(pGuiGraphics);
        pGuiGraphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
        pGuiGraphics.blit(BACKGROUND_LOCATION, 0, 0, 0, 0.0F, 0.0F, this.width, timersList.getY(), 32, 32);
        pGuiGraphics.blit(BACKGROUND_LOCATION, 0, timersList.getY() + timersList.getHeight(), 0, 0.0F, 0.0F, this.width, this.height - timersList.getHeight() - timersList.getY(), 32, 32);
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        pGuiGraphics.fillGradient(RenderType.guiOverlay(), timersList.getX(), timersList.getY(), timersList.getRight(), timersList.getY() + 4, -16777216, 0, 0);
        pGuiGraphics.fillGradient(RenderType.guiOverlay(), timersList.getX(), timersList.getBottom() - 4, timersList.getRight(), timersList.getBottom(), 0, -16777216, 0);
    }

    public void removeTimer(Button button) {
        Minecraft.getInstance().player.connection.sendCommand("timer timers " + ((TimersList.TimerEntry)timersList.getSelected()).getName().getString() + " remove");
    }

    public void editTimer(Button button) {
        minecraft.setScreen(new EditTimerScreen(((TimersList.TimerEntry) timersList.getSelected()).getName(), ((TimersList.TimerEntry) this.timersList.getSelected()).getTimer()));
    }

    public void addTimer(Button button) {
        System.out.println("add");
        this.minecraft.setScreen(new AddTimerScreen());
    }
}

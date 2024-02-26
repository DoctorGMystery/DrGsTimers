package net.doctorg.drgstimers.client.gui;

import net.doctorg.drgstimers.client.InputHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class TransformScaleHeightBox extends AbstractWidget {

    private boolean dragging = false;
    private boolean resizeTop = false;
    private boolean resizeBottom = false;

    private int startXDistance = 0;
    private int startYDistance = 0;
    private int startHeight = 0;

    private final Consumer<Integer> onHeightChange;
    private final BiConsumer<Integer, Integer> onTransformChange;
    private final int minimumHeight;


    public TransformScaleHeightBox(int pX, int pY, int pWidth, int pHeight, Component pMessage, Consumer<Integer> onHeightChange, BiConsumer<Integer, Integer> onTransformChange, int minimumHeight) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.onHeightChange = onHeightChange;
        this.onTransformChange = onTransformChange;
        this.minimumHeight = minimumHeight;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (InputHandler.isMouseReleased()) {
            dragging = false;
            resizeTop = false;
            resizeBottom = false;
        }

        int BGColor;
        if (isHovered) {
            BGColor = 0x33FFFFFF;
        } else {
            BGColor = 0x22FFFFFF;
        }

        int resizeTopAddColor;
        int resizeBottomAddColor;
        if (mouseOverTopResize(pMouseX, pMouseY)) {
            resizeTopAddColor = 0x44000000;
        } else {
            resizeTopAddColor = 0x22000000;
        }
        if (mouseOverBottomResize(pMouseX, pMouseY)) {
            resizeBottomAddColor = 0x44000000;
        } else {
            resizeBottomAddColor = 0x22000000;
        }

        pGuiGraphics.fill(getX() + 2, getY() + 2, width + getX() - 2, height + getY() - 2, BGColor);
        pGuiGraphics.fill(getX() + 2, getY() + 2, width + getX() - 2, getY() + 5, BGColor + resizeTopAddColor);
        pGuiGraphics.fill(getX() + 2, height + getY() - 2, width + getX() - 2, height + getY() - 5, BGColor + resizeBottomAddColor);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (mouseOverTopResize(pMouseX, pMouseY)) {
            resizeTop = true;
        }

        if (mouseOverBottomResize(pMouseX, pMouseY)) {
            resizeBottom = true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
        super.onDrag(pMouseX, pMouseY, pDragX, pDragY);

        if (!dragging) {
            startXDistance = (int) (pMouseX - getX());
            startYDistance = (int) (pMouseY - getY());
            startHeight = height;
        }

        double moveToX = pMouseX - startXDistance;
        double moveToY = pMouseY - startYDistance;

        if (resizeTop) {
            resizeTop(moveToY);
        }
        if (resizeBottom) {
            resizeBottom(moveToY);
        }
        if (!resizeTop && ! resizeBottom) {
            reposition(moveToX, moveToY);
        }


        dragging = true;
    }

    private void reposition(double moveToX, double moveToY) {
        boolean moveToXPositive = moveToX >= 0;
        boolean moveToXInGuiReach = moveToX + width <= Minecraft.getInstance().getWindow().getGuiScaledWidth();

        if (moveToXPositive && moveToXInGuiReach) {
            setPosition((int) moveToX, getY());
        } else if (!moveToXPositive) {
            setPosition(0, getY());
        } else {
            setPosition(Minecraft.getInstance().getWindow().getGuiScaledWidth() - width, getY());
        }

        boolean moveToYPositive = moveToY >= 0;
        boolean moveToYInGuiReach = moveToY + height <= Minecraft.getInstance().getWindow().getGuiScaledHeight();

        if (moveToYPositive && moveToYInGuiReach) {
            setPosition(getX(), ((int) moveToY));
        } else if (!moveToYPositive) {
            setPosition(getX(), 0);
        } else {
            setPosition(getX(), Minecraft.getInstance().getWindow().getGuiScaledHeight() - height);
        }

        onTransformChange.accept(getX(), getY());
    }

    private void resizeTop(double moveToY) {
        int moveToYInt = (int) moveToY;
        int newHeight = height + (getY() - moveToYInt);

        boolean moveToYIntPositive = moveToYInt >= 0;
        boolean newHeightBOrEMinimum = newHeight >= minimumHeight;

        if (moveToYIntPositive && newHeightBOrEMinimum) {
            setSize(width, newHeight);
            setPosition(getX(), moveToYInt);
        } else if (!moveToYIntPositive) {
            setSize(width, height + getY());
            setPosition(getX(),  0);
        } else {
            setPosition(getX(), getY() + height - minimumHeight);
            setSize(width, minimumHeight);
        }

        onHeightChange.accept(height);
        onTransformChange.accept(getX(), getY());
    }

    private void resizeBottom(double moveToY) {
        int moveToYInt = (int) moveToY;
        int newHeight = moveToYInt + startHeight - getY();

        boolean newHeightPlusYInGuiReach = newHeight + getY() < Minecraft.getInstance().getWindow().getGuiScaledHeight();
        boolean newHeightBOrEMinimum = newHeight >= minimumHeight;

        if (newHeightPlusYInGuiReach && newHeightBOrEMinimum) {
            setSize(width, newHeight);
        } else if (!newHeightPlusYInGuiReach) {
            setSize(width, Minecraft.getInstance().getWindow().getGuiScaledHeight() - getY());
        } else {
            setSize(width, minimumHeight);
        }

        onHeightChange.accept(height);
    }

    private boolean mouseOverTopResize(double mouseX, double mouseY) {
        return mouseX >= getX() + 2 && mouseX <= width + getX() - 2 && mouseY >= getY() + 2 && mouseY <= getY() + 5;
    }

    private boolean mouseOverBottomResize(double mouseX, double mouseY) {
        return mouseX >= getX() + 2 && mouseX <= width + getX() - 2 && mouseY <= height + getY() - 2 && mouseY >= height + getY() - 5;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}
}

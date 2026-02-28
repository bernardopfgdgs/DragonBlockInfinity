package com.bernardo.dbi.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class MenuButton extends Button {
    private final ButtonId id;

    public MenuButton(ButtonId id, int x, int y, OnPress onPress) {
        this(id, x, y, id.rW, id.rH, onPress);
    }

    public MenuButton(ButtonId id, int x, int y, int w, int h, OnPress onPress) {
        super(x, y, w, h, Component.empty(), onPress, DEFAULT_NARRATION);
        this.id = id;
    }

    @Override
    public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
        int x = getX(), y = getY();
        g.fill(x-1, y-1, x+width+1, y+height+1, isHovered() ? 0xFFFFFFFF : 0xFF666666);
        g.blit(id.texture, x, y, id.u, id.v, width, height, id.texW, id.texH);
        if (isHovered()) g.fill(x, y, x+width, y+height, 0x33FFFFFF);
    }

    public ButtonId getButtonId() { return id; }
}

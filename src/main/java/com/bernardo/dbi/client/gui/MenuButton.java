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
        // SEM FUNDO — só o sprite
        g.blit(id.texture, getX(), getY(), id.u, id.v, width, height, id.texW, id.texH);
        // Hover effect sutil
        if (isHovered()) g.fill(getX(), getY(), getX()+width, getY()+height, 0x33FFFFFF);
    }

    public ButtonId getButtonId() { return id; }
}

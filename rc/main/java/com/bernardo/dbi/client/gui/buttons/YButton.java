package com.bernardo.dbi.client.gui.buttons;

import com.bernardo.dbi.Dbi;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class YButton extends Button {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Dbi.MOD_ID, "textures/gui/icons_btn.png");
    private static final int U = 0, V = 62, W = 20, H = 40;
    private static final int TEX_W = 256, TEX_H = 256;

    public YButton(int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
        g.blit(TEXTURE, getX(), getY(), U, V, width, height, TEX_W, TEX_H);
        if (isHovered()) g.fill(getX(), getY(), getX()+width, getY()+height, 0x33FFFFFF);
    }
}

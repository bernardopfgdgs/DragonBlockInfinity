ackage com.bernardo.dbi.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class TextureButton extends Button {
    private final ButtonUv uv;

    public TextureButton(int x, int y, int w, int h, ButtonUv uv, OnPress onPress) {
        super(x, y, w, h, Component.empty(), onPress, DEFAULT_NARRATION);
        this.uv = uv;
    }

    @Override
    public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
        int x = getX();
        int y = getY();
        // draw background border
        g.fill(x-1, y-1, x+width+1, y+height+1, isHovered() ? 0xFFFFFFFF : 0xFF888888);
        // blit the texture region; dest size = button size, source coords from uv
        g.blit(uv.texture, x, y, uv.u, uv.v, width, height, uv.texW, uv.texH);
    }
}

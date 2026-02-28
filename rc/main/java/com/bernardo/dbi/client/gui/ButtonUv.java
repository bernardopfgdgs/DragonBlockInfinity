package com.bernardo.dbi.client.gui;

import net.minecraft.resources.ResourceLocation;

public class ButtonUv {
    public final ResourceLocation texture;
    public final int u, v, texW, texH;

    public ButtonUv(ResourceLocation texture, int u, int v, int texW, int texH) {
        this.texture = texture;
        this.u = u; this.v = v;
        this.texW = texW; this.texH = texH;
    }

    public static ButtonUv of(ButtonId id) { return new ButtonUv(id.texture, id.u, id.v, id.texW, id.texH); }
}

package com.bernardo.dbi.client.gui;

import net.minecraft.resources.ResourceLocation;
import com.bernardo.dbi.Dbi;

public enum ButtonId {

    ARROW_PREV  ("textures/gui/icons_btn.png",  0,  22, 20, 20, 256,  256,  20, 20, Action.PREV),
    ARROW_NEXT  ("textures/gui/icons_btn.png", 22,  22, 28, 26, 256,  256,  20, 20, Action.NEXT),
    BTN_Y       ("textures/gui/icons_btn.png",  0,  62, 20, 40, 256,  256,  20, 20, Action.BACK),
    BTN_CIRCLE  ("textures/gui/icons_btn.png",  0, 108, 28, 32, 256,  256,  28, 32, Action.CONFIRM),
    BTN_DARK    ("textures/gui/icons_btn.png",  2,  44, 16, 16, 256,  256,  16, 16, Action.GENERIC),
    HUD_SLOT_1  ("textures/gui/icons_hud.png",  0, 125, 60, 60, 1024, 1024, 60, 60, Action.HUD_SLOT),
    HUD_SLOT_2  ("textures/gui/icons_hud.png",  0, 185, 60, 60, 1024, 1024, 60, 60, Action.HUD_SLOT),
    HUD_SLOT_3  ("textures/gui/icons_hud.png",  0, 245, 60, 60, 1024, 1024, 60, 60, Action.HUD_SLOT),
    HUD_SLOT_4  ("textures/gui/icons_hud.png",  0, 305, 60, 60, 1024, 1024, 60, 60, Action.HUD_SLOT),
    HUD_SLOT_5  ("textures/gui/icons_hud.png",  0, 365, 60, 60, 1024, 1024, 60, 60, Action.HUD_SLOT),
    HUD_SLOT_6  ("textures/gui/icons_hud.png",  0, 425, 60, 60, 1024, 1024, 60, 60, Action.HUD_SLOT),
    HUD_SLOT_7  ("textures/gui/icons_hud.png",  0, 485, 60, 60, 1024, 1024, 60, 60, Action.HUD_SLOT),
    HUD_SLOT_8  ("textures/gui/icons_hud.png",  0, 545, 60, 60, 1024, 1024, 60, 60, Action.HUD_SLOT),
    HUD_SLOT_9  ("textures/gui/icons_hud.png",  0, 605, 60, 60, 1024, 1024, 60, 60, Action.HUD_SLOT),
    HUD_SLOT_10 ("textures/gui/icons_hud.png",  0, 665, 60, 60, 1024, 1024, 60, 60, Action.HUD_SLOT),
    HUD_SLOT_11 ("textures/gui/icons_hud.png",  0, 725, 60, 60, 1024, 1024, 60, 60, Action.HUD_SLOT),
    HUD_SLOT_12 ("textures/gui/icons_hud.png",  0, 785, 60, 60, 1024, 1024, 60, 60, Action.HUD_SLOT),
    HUD_SLOT_13 ("textures/gui/icons_hud.png",  0, 845, 60, 60, 1024, 1024, 60, 60, Action.HUD_SLOT),
    HUD_CHAR_RED ("textures/gui/icons_hud.png",   0, 830, 140, 184, 1024, 1024, 70, 92, Action.CHAR_SELECT),
    HUD_CHAR_BLUE("textures/gui/icons_hud.png", 141, 830, 140, 184, 1024, 1024, 70, 92, Action.CHAR_SELECT);

    public enum Action { PREV, NEXT, BACK, CONFIRM, GENERIC, HUD_SLOT, CHAR_SELECT }

    public final ResourceLocation texture;
    public final int u, v, srcW, srcH, texW, texH, rW, rH;
    public final Action action;

    ButtonId(String texPath, int u, int v, int srcW, int srcH, int texW, int texH, int rW, int rH, Action action) {
        this.texture = new ResourceLocation(Dbi.MOD_ID, texPath);
        this.u = u; this.v = v;
        this.srcW = srcW; this.srcH = srcH;
        this.texW = texW; this.texH = texH;
        this.rW = rW;     this.rH = rH;
        this.action = action;
    }

    public ButtonUv toUv() { return new ButtonUv(texture, u, v, texW, texH); }
}

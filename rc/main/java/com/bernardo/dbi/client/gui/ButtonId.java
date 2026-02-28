ackage com.bernardo.dbi.client.gui;

import net.minecraft.resources.ResourceLocation;
import com.bernardo.dbi.Dbi;

public enum ButtonId {
    ARROW_PREV  ("textures/gui/icons_btn.png",  0,  22, 20, 20, 256,  256,  20, 20, Action.PREV),
    ARROW_NEXT  ("textures/gui/icons_btn.png", 22,  22, 28, 26, 256,  256,  20, 20, Action.NEXT),
    BTN_Y       ("textures/gui/icons_btn.png",  0,  62, 20, 40, 256,  256,  20, 20, Action.BACK),
    BTN_CIRCLE  ("textures/gui/icons_btn.png",  0, 108, 28, 32, 256,  256,  28, 32, Action.CONFIRM),
    BTN_DARK    ("textures/gui/icons_btn.png",  2,  44, 16, 16, 256,  256,  16, 16, Action.GENERIC);

    public enum Action { PREV, NEXT, BACK, CONFIRM, GENERIC }

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

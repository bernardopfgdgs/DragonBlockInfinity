package com.bernardo.dbi.client.screen;

import com.bernardo.dbi.Dbi;
import com.bernardo.dbi.client.gui.buttons.*;
import com.bernardo.dbi.network.ModNetwork;
import com.bernardo.dbi.network.packet.RaceSelectionPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CharacterScreen extends Screen {

    private static final ResourceLocation MENU_BG = new ResourceLocation(Dbi.MOD_ID, "textures/gui/menu.png");
    private static final int MENU_UV_U = 32, MENU_UV_V = 8;
    private static final int MENU_W = 964, MENU_H = 632;
    private static final int TEX_SIZE = 1024;

    private static final String[][] RACE_DEFS = {
        { "sayajin",     "Saiyan",      "true",  "true",  "3","3","3","3","0xFFD2956E","0xFF1A1A1A","0xFF1A1A1A" },
        { "namekian",    "Namekian",    "false", "false", "2","2","2","2","0xFF3CB371","0xFF000000","0xFF000000" },
        { "arconsian",   "Arcosian",    "true",  "false", "2","2","2","2","0xFFFFF0F5","0xFF000000","0xFFCC0000" },
        { "humano",      "Human",       "true",  "true",  "3","3","3","3","0xFFD2956E","0xFF7B4F2E","0xFF5B3A29" },
        { "halfsayajin", "Half-Saiyan", "true",  "true",  "3","3","3","3","0xFFD2956E","0xFF1A1A1A","0xFF1A1A1A" },
    };
    private static final String[][] RACE_FORMS = {
        { "Normal" }, {},
        { "Minimal","Form 1","Form 2","Form 3","Final Form" },
        { "Normal" }, { "Normal" },
    };
    private static final String[] AGE_LABELS = { "Adult", "Young Adult", "Child" };
    private static final int HAIR_STYLES = 12;
    private static final int GAP = 22;

    private int raceIdx = 0, formIdx = 0, hairNum = 1, ageIdx = 0;
    private int bodyTypeIdx = 0, noseIdx = 0, mouthIdx = 0, eyeIdx = 0;
    private int bodyColor, hairColor, eyeColor;

    private boolean showPicker = false;
    private String pickerTarget;
    private static final int PW = 200, PH = 120, SW = 16;
    private int px, py, sx, sy;
    private float ph = 0, ps = 1, pv = 1;

    private int menuX, menuY, scaledMenuW, scaledMenuH;
    private float scale;
    private int prX, prY, prW, prH;
    private int opX, opY, opW;

    private float previewYaw = 0f, previewPitch = 0f;
    private boolean dragging = false;
    private double lastDragX = 0, lastDragY = 0;
    private static final float MAX_YAW = 70f, MAX_PITCH = 20f;

    public CharacterScreen() {
        super(Component.literal("Appearance"));
    }

    @Override
    protected void init() {
        super.init();
        
        float scaleW = (width - 40f) / MENU_W;
        float scaleH = (height - 40f) / MENU_H;
        scale = Math.min(1f, Math.min(scaleW, scaleH));
        
        scaledMenuW = Math.round(MENU_W * scale);
        scaledMenuH = Math.round(MENU_H * scale);
        
        menuX = (width - scaledMenuW) / 2;
        menuY = (height - scaledMenuH) / 2;
        
        prW = (int)(scaledMenuW * 0.34f);
        prH = scaledMenuH - (int)(60 * scale);
        prX = menuX + (int)(10 * scale);
        prY = menuY + (int)(50 * scale);
        
        opX = prX + prW + (int)(18 * scale);
        opY = menuY + (int)(52 * scale);
        opW = menuX + scaledMenuW - opX - (int)(10 * scale);
        
        initColors();
        rebuild();
    }

    private void rebuild() {
        clearWidgets();
        if (showPicker) return;
        
        String[] r = RACE_DEFS[raceIdx];
        boolean hasForm = "true".equals(r[2]);
        boolean hasHair = "true".equals(r[3]);
        
        int btnSize = (int)(20 * scale);
        int cy = opY;
        int right = opX + opW;
        
        addRenderableWidget(new ArrowLeftButton(opX, menuY + (int)(12*scale), btnSize, btnSize, 
            b -> act("race_p")));
        addRenderableWidget(new ArrowRightButton(right - btnSize, menuY + (int)(12*scale), btnSize, btnSize, 
            b -> act("race_n")));
        
        if (hasForm && RACE_FORMS[raceIdx].length > 0) {
            addRenderableWidget(new ArrowLeftButton(opX, cy, btnSize, btnSize, b -> act("form_p")));
            addRenderableWidget(new ArrowRightButton(right - btnSize, cy, btnSize, btnSize, b -> act("form_n")));
            cy += (int)(GAP * scale);
        }
        
        if (hasHair) {
            addRenderableWidget(new ArrowLeftButton(opX, cy, btnSize, btnSize, b -> act("hair_p")));
            addRenderableWidget(new ArrowRightButton(right - btnSize, cy, btnSize, btnSize, b -> act("hair_n")));
            cy += (int)(GAP * scale);
            addSwatch(opX + (opW - (int)(100*scale)) / 2, cy, (int)(100*scale), (int)(18*scale), hairColor, "hair");
            cy += (int)(22 * scale);
        }
        
        addRenderableWidget(new ArrowLeftButton(opX, cy, btnSize, btnSize, b -> act("age_p")));
        addRenderableWidget(new ArrowRightButton(right - btnSize, cy, btnSize, btnSize, b -> act("age_n")));
        cy += (int)(GAP * scale);
        
        addRenderableWidget(new ArrowLeftButton(opX, cy, btnSize, btnSize, b -> act("body_p")));
        addRenderableWidget(new ArrowRightButton(right - btnSize, cy, btnSize, btnSize, b -> act("body_n")));
        cy += (int)(GAP * scale);
        
        int sw = (int)(32*scale), sh = (int)(20*scale);
        int stot = sw * 3 + (int)(8*scale);
        int ssx = opX + (opW - stot) / 2;
        addSwatch(ssx, cy, sw, sh, bodyColor, "body");
        addSwatch(ssx + sw + (int)(4*scale), cy, sw, sh, lighter(bodyColor, .15f), "body_l");
        addSwatch(ssx + sw*2 + (int)(8*scale), cy, sw, sh, darker(bodyColor, .20f), "body_d");
        cy += sh + (int)(8*scale);
        
        addRenderableWidget(new ArrowLeftButton(opX, cy, btnSize, btnSize, b -> act("nose_p")));
        addRenderableWidget(new ArrowRightButton(right - btnSize, cy, btnSize, btnSize, b -> act("nose_n")));
        cy += (int)(GAP * scale);
        
        addRenderableWidget(new ArrowLeftButton(opX, cy, btnSize, btnSize, b -> act("mouth_p")));
        addRenderableWidget(new ArrowRightButton(right - btnSize, cy, btnSize, btnSize, b -> act("mouth_n")));
        cy += (int)(GAP * scale);
        
        addRenderableWidget(new ArrowLeftButton(opX, cy, btnSize, btnSize, b -> act("eye_p")));
        addRenderableWidget(new ArrowRightButton(right - btnSize, cy, btnSize, btnSize, b -> act("eye_n")));
        cy += (int)(GAP * scale);
        
        int ew = (int)(48*scale);
        int etot = ew * 2 + (int)(6*scale);
        int esx = opX + (opW - etot) / 2;
        addSwatch(esx, cy, ew, sh, eyeColor, "eyes");
        addSwatch(esx + ew + (int)(6*scale), cy, ew, sh, darker(eyeColor,.30f), "eyes_d");
        cy += sh + (int)(6*scale);
        
        addRenderableWidget(new CircleButton(opX + (opW - (int)(80*scale)) / 2, cy, 
            (int)(80*scale), (int)(20*scale), b -> act("equal")));
        
        addRenderableWidget(new XButton(menuX + (int)(8*scale), menuY + scaledMenuH - (int)(34*scale), 
            (int)(32*scale), (int)(26*scale), b -> act("close")));
        addRenderableWidget(new DiamondButton(menuX + scaledMenuW - (int)(78*scale), 
            menuY + scaledMenuH - (int)(34*scale), (int)(70*scale), (int)(26*scale), b -> act("prox")));
    }

    private void addSwatch(int x, int y, int w, int h, int color, String key) {
        addRenderableWidget(new SwatchBtn(x, y, w, h, color, key, b -> openPicker(key)));
    }

    private void act(String key) {
        String[] r = RACE_DEFS[raceIdx];
        int maxBody  = Integer.parseInt(r[4]);
        int maxNose  = Integer.parseInt(r[5]);
        int maxMouth = Integer.parseInt(r[6]);
        int maxEye   = Integer.parseInt(r[7]);
        
        if (key.equals("race_p")) {
            raceIdx = (raceIdx - 1 + RACE_DEFS.length) % RACE_DEFS.length;
            resetRace();
            return;
        } else if (key.equals("race_n")) {
            raceIdx = (raceIdx + 1) % RACE_DEFS.length;
            resetRace();
            return;
        } else if (key.equals("form_p")) {
            if (RACE_FORMS[raceIdx].length > 0) formIdx = Math.max(0, formIdx - 1);
        } else if (key.equals("form_n")) {
            if (RACE_FORMS[raceIdx].length > 0) formIdx = Math.min(RACE_FORMS[raceIdx].length - 1, formIdx + 1);
        } else if (key.equals("hair_p")) {
            hairNum = Math.max(1, hairNum - 1);
        } else if (key.equals("hair_n")) {
            hairNum = Math.min(HAIR_STYLES, hairNum + 1);
        } else if (key.equals("age_p")) {
            ageIdx = Math.max(0, ageIdx - 1);
        } else if (key.equals("age_n")) {
            ageIdx = Math.min(AGE_LABELS.length - 1, ageIdx + 1);
        } else if (key.equals("body_p")) {
            bodyTypeIdx = Math.max(0, bodyTypeIdx - 1);
        } else if (key.equals("body_n")) {
            bodyTypeIdx = Math.min(maxBody - 1, bodyTypeIdx + 1);
        } else if (key.equals("nose_p")) {
            noseIdx = Math.max(0, noseIdx - 1);
        } else if (key.equals("nose_n")) {
            noseIdx = Math.min(maxNose - 1, noseIdx + 1);
        } else if (key.equals("mouth_p")) {
            mouthIdx = Math.max(0, mouthIdx - 1);
        } else if (key.equals("mouth_n")) {
            mouthIdx = Math.min(maxMouth - 1, mouthIdx + 1);
        } else if (key.equals("eye_p")) {
            eyeIdx = Math.max(0, eyeIdx - 1);
        } else if (key.equals("eye_n")) {
            eyeIdx = Math.min(maxEye - 1, eyeIdx + 1);
        } else if (key.equals("equal")) {
            eyeColor = bodyColor;
            hairColor = bodyColor;
        } else if (key.equals("close")) {
            onClose();
            return;
        } else if (key.equals("prox")) {
            save();
            return;
        }
        rebuild();
    }

    private void resetRace() {
        formIdx = 0; hairNum = 1; ageIdx = 0;
        bodyTypeIdx = 0; noseIdx = 0; mouthIdx = 0; eyeIdx = 0;
        initColors();
        rebuild();
    }

    private void initColors() {
        String[] r = RACE_DEFS[raceIdx];
        bodyColor = (int) Long.parseLong(r[8].replace("0x",""),  16);
        hairColor = (int) Long.parseLong(r[9].replace("0x",""),  16);
        eyeColor  = (int) Long.parseLong(r[10].replace("0x",""), 16);
    }

    private void save() {
        if (minecraft == null || minecraft.player == null) {
            onClose();
            return;
        }
        String[] r = RACE_DEFS[raceIdx];
        ModNetwork.sendToServer(new RaceSelectionPacket(
            r[0], formIdx, hairNum - 1, ageIdx, bodyTypeIdx,
            noseIdx, mouthIdx, eyeIdx, bodyColor, hairColor, eyeColor
        ));
        onClose();
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderMenuBackground(g);
        renderLabels(g);
        super.render(g, mx, my, pt);
        if (showPicker) drawPicker(g);
    }

    private void renderMenuBackground(GuiGraphics g) {
        g.blit(MENU_BG, 
            menuX, menuY,
            MENU_UV_U, MENU_UV_V,
            scaledMenuW, scaledMenuH,
            MENU_W, MENU_H,
            TEX_SIZE, TEX_SIZE
        );
        
        g.fill(prX-1, prY-1, prX + prW+1, prY + prH+1, 0xFF1A3A6E);
        drawPreview(g);
        
        g.fill(opX - (int)(9*scale), menuY + (int)(46*scale), 
               opX - (int)(8*scale), menuY + scaledMenuH - (int)(36*scale), 0xFF1A3A6E);
    }

    private void renderLabels(GuiGraphics g) {
        String[] r = RACE_DEFS[raceIdx];
        int midX = opX + opW / 2;
        int cy = opY;
        boolean hasForm = "true".equals(r[2]);
        boolean hasHair = "true".equals(r[3]);
        
        g.drawString(font, "Appearance", menuX + (int)(12*scale), menuY + (int)(14*scale), 0xFF000000, false);
        g.drawCenteredString(font, r[1], midX, menuY + (int)(14*scale), 0xFF000000);
        
        if (hasForm && RACE_FORMS[raceIdx].length > 0) {
            int fi = Math.min(formIdx, RACE_FORMS[raceIdx].length - 1);
            g.drawCenteredString(font, "Form: " + RACE_FORMS[raceIdx][fi], midX, cy + (int)(4*scale), 0xFF000000);
            cy += (int)(GAP * scale);
        }
        if (hasHair) {
            g.drawCenteredString(font, "Hair " + hairNum, midX, cy + (int)(4*scale), 0xFF000000);
            cy += (int)((GAP + 22) * scale);
        }
        g.drawCenteredString(font, AGE_LABELS[ageIdx], midX, cy + (int)(4*scale), 0xFF000000);
        cy += (int)(GAP * scale);
        g.drawCenteredString(font, "Custom Skin", midX, cy - (int)(2*scale), 0xFF000000);
        g.drawCenteredString(font, "Body Type " + (bodyTypeIdx+1), midX, cy + (int)(8*scale), 0xFF000000);
        cy += (int)((GAP + 28) * scale);
        g.drawCenteredString(font, "Nose " + (noseIdx + 1), midX, cy + (int)(4*scale), 0xFF000000);
        cy += (int)(GAP * scale);
        g.drawCenteredString(font, "Mouth " + (mouthIdx + 1), midX, cy + (int)(4*scale), 0xFF000000);
        cy += (int)(GAP * scale);
        g.drawCenteredString(font, "Eyes " + (eyeIdx + 1), midX, cy + (int)(4*scale), 0xFF000000);
    }

    private void drawPreview(GuiGraphics g) {
        if (minecraft == null || minecraft.player == null) return;
        
        String raceId = RACE_DEFS[raceIdx][0];
        minecraft.player.getPersistentData().putString("dbi:race", raceId);
        
        double sc = minecraft.getWindow().getGuiScale();
        int wh = minecraft.getWindow().getHeight();
        com.mojang.blaze3d.platform.GlStateManager._enableScissorTest();
        com.mojang.blaze3d.platform.GlStateManager._scissorBox(
            (int)(prX*sc), (int)(wh - (prY+prH)*sc), (int)(prW*sc), (int)(prH*sc)
        );
        
        int entityScale = (int)(prH / 2.2f);
        int centerX = prX + prW / 2;
        int centerY = prY + prH - (int)(prH * 0.12f);
        
        InventoryScreen.renderEntityInInventoryFollowsMouse(
            g, centerX, centerY, entityScale,
            (float)centerX - (centerX + previewYaw * 2.5f),
            (float)centerY - (centerY + previewPitch * 2.5f),
            minecraft.player
        );
        
        com.mojang.blaze3d.platform.GlStateManager._disableScissorTest();
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (showPicker) {
            int x=(int)mx, y=(int)my;
            if (x>=px && x<px+PW && y>=py && y<py+PH) {
                ph=(x-px)/(float)(PW-1)*360f;
                ps=1f-(y-py)/(float)(PH-1);
                applyPick();
                return true;
            }
            if (x>=sx && x<sx+SW && y>=sy && y<sy+PH) {
                pv=1f-(y-sy)/(float)(PH-1);
                applyPick();
                return true;
            }
            showPicker=false;
            pickerTarget=null;
            rebuild();
            return true;
        }
        if (mx>=prX && mx<prX+prW && my>=prY && my<prY+prH) {
            dragging=true;
            lastDragX=mx;
            lastDragY=my;
            return true;
        }
        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (dragging) {
            previewYaw = clamp(previewYaw + (float)(mx-lastDragX)*0.8f, -MAX_YAW, MAX_YAW);
            previewPitch = clamp(previewPitch + (float)(my-lastDragY)*0.4f, -MAX_PITCH, MAX_PITCH);
            lastDragX=mx;
            lastDragY=my;
            return true;
        }
        if (showPicker) {
            int x=(int)mx, y=(int)my;
            if (x>=px && x<px+PW && y>=py && y<py+PH) {
                ph=(x-px)/(float)(PW-1)*360f;
                ps=1f-(y-py)/(float)(PH-1);
                applyPick();
                return true;
            }
            if (x>=sx && x<sx+SW && y>=sy && y<sy+PH) {
                pv=1f-(y-sy)/(float)(PH-1);
                applyPick();
                return true;
            }
        }
        return super.mouseDragged(mx, my, btn, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        dragging=false;
        return super.mouseReleased(mx,my,btn);
    }

    private void openPicker(String tgt) {
        pickerTarget = tgt;
        showPicker = true;
        px = width/2 - (PW+SW+16)/2;
        py = height/2 - PH/2;
        sx = px+PW+6;
        sy = py;
        float[] hsv = rgb2hsv(pickerCol());
        ph=hsv[0];
        ps=hsv[1];
        pv=hsv[2];
        rebuild();
    }

    private int pickerCol() {
        if ("hair".equals(pickerTarget)) return hairColor;
        if (pickerTarget != null && pickerTarget.startsWith("eye")) return eyeColor;
        return bodyColor;
    }

    private void applyPick() {
        int c = (0xFF << 24) | (hsv2rgb(ph, ps, pv) & 0xFFFFFF);
        if ("hair".equals(pickerTarget)) {
            hairColor = c;
        } else if (pickerTarget != null && pickerTarget.startsWith("eye")) {
            eyeColor = c;
        } else {
            bodyColor = c;
        }
        rebuild();
    }

    private void drawPicker(GuiGraphics g) {
        g.fill(px-4, py-4, px+PW+SW+30, py+PH+4, 0xFF222222);
        border(g, px-4, py-4, PW+SW+34, PH+8, 0xFF777777);
        
        for (int i = 0; i < PW; i++) {
            for (int j = 0; j < PH; j++) {
                g.fill(px+i, py+j, px+i+1, py+j+1,
                    0xFF000000 | hsv2rgb(i/(float)(PW-1)*360f, 1f-j/(float)(PH-1), 1f));
            }
        }
        
        for (int j = 0; j < PH; j++) {
            g.fill(sx, sy+j, sx+SW, sy+j+1, 0xFF000000 | hsv2rgb(ph, 1f, 1f-j/(float)(PH-1)));
        }
        
        int cx=px+(int)(ph/360f*(PW-1));
        int cy=py+(int)((1f-ps)*(PH-1));
        g.fill(cx-3,cy-1,cx+4,cy+2,0xFFFFFFFF);
        g.fill(cx-1,cy-3,cx+2,cy+4,0xFFFFFFFF);
        
        int cur = pickerCol();
        int previewX = sx+SW+4;
        g.fill(previewX, py, previewX+24, py+24, (0xFF<<24)|(cur&0xFFFFFF));
        g.fill(previewX-1, py-1, previewX+25, py+25, 0xFF888888);
        g.drawString(font, String.format("#%06X", cur&0xFFFFFF), previewX, py+28, 0xFFCCCCCC, false);
    }

    private void border(GuiGraphics g, int x, int y, int w, int h, int c) {
        g.fill(x, y, x+w, y+1, c);
        g.fill(x, y+h-1, x+w, y+h, c);
        g.fill(x, y, x+1, y+h, c);
        g.fill(x+w-1, y, x+w, y+h, c);
    }

    private float clamp(float v, float mn, float mx) {
        return Math.max(mn, Math.min(mx, v));
    }

    private int lighter(int c, float a) {
        return (0xFF<<24)
            |(Math.min(255,(int)(((c>>16)&0xFF)+255*a))<<16)
            |(Math.min(255,(int)(((c>>8 )&0xFF)+255*a))<<8)
            | Math.min(255,(int)( (c     &0xFF)+255*a));
    }
    
    private int darker(int c, float a) {
        return (0xFF<<24)
            |(Math.max(0,(int)(((c>>16)&0xFF)-255*a))<<16)
            |(Math.max(0,(int)(((c>>8 )&0xFF)-255*a))<<8)
            | Math.max(0,(int)( (c    &0xFF)-255*a));
    }

    private int hsv2rgb(float h, float s, float v) {
        int hi=(int)(h/60f)%6;
        float f=h/60f-(int)(h/60f);
        float p=v*(1-s);
        float q=v*(1-f*s);
        float t=v*(1-(1-f)*s);
        float r=0, gb=0, b=0;
        if (hi == 0) {
            r=v; gb=t; b=p;
        } else if (hi == 1) {
            r=q; gb=v; b=p;
        } else if (hi == 2) {
            r=p; gb=v; b=t;
        } else if (hi == 3) {
            r=p; gb=q; b=v;
        } else if (hi == 4) {
            r=t; gb=p; b=v;
        } else {
            r=v; gb=p; b=q;
        }
        return (Math.round(r*255)<<16)|(Math.round(gb*255)<<8)|Math.round(b*255);
    }

    private float[] rgb2hsv(int rgb) {
        float r=((rgb>>16)&0xFF)/255f;
        float gr=((rgb>>8)&0xFF)/255f;
        float b=(rgb&0xFF)/255f;
        float mx=Math.max(r,Math.max(gr,b));
        float mn=Math.min(r,Math.min(gr,b));
        float d=mx-mn;
        float h=0;
        if (d != 0) {
            if (mx == r) {
                h=60f*((gr-b)/d%6f);
            } else if (mx == gr) {
                h=60f*((b-r)/d+2f);
            } else {
                h=60f*((r-gr)/d+4f);
            }
        }
        if (h < 0) h += 360f;
        return new float[]{h, mx==0?0:d/mx, mx};
    }

    private static class SwatchBtn extends Button {
        final String target;
        int color;
        
        SwatchBtn(int x, int y, int w, int h, int color, String tgt, OnPress p) {
            super(x,y,w,h,Component.empty(),p,DEFAULT_NARRATION);
            this.color=color;
            this.target=tgt;
        }
        
        @Override
        public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
            g.fill(getX()-1,getY()-1,getX()+width+1,getY()+height+1,
                isHovered()?0xFFFFFFFF:0xFF666666);
            g.fill(getX(),getY(),getX()+width,getY()+height,
                (0xFF<<24)|(color&0xFFFFFF));
        }
    }
}

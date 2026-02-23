package com.bernardo.dbi.client.screen;

import com.bernardo.dbi.Dbi;
import com.bernardo.dbi.client.menu.MenuManager;
import com.bernardo.dbi.network.ModNetwork;
import com.bernardo.dbi.network.packet.RaceSelectionPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CharacterScreen extends Screen {

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
    private static final int AW = 22, AH = 18, GAP = 28;

    private int raceIdx = 0, formIdx = 0, hairNum = 1, ageIdx = 0;
    private int bodyTypeIdx = 0, noseIdx = 0, mouthIdx = 0, eyeIdx = 0;
    private int bodyColor, hairColor, eyeColor;

    private boolean showPicker = false;
    private String  pickerTarget;
    private static final int PW = 200, PH = 120, SW = 16;
    private int px, py, sx, sy;
    private float ph = 0, ps = 1, pv = 1;

    private int panX, panY, panW, panH;
    private int prX, prY, prW, prH;
    private int opX, opY, opW;

    private MenuManager menu;

    public CharacterScreen() { super(Component.literal("Appearance")); }

    @Override
    protected void init() {
        super.init();
        panW = Math.min(680, width  - 40);
        panH = Math.min(440, height - 40);
        panX = (width  - panW) / 2;
        panY = (height - panH) / 2;
        prW  = panW * 34 / 100;
        prH  = panH - 60;
        prX  = panX + 10;
        prY  = panY + 50;
        opX  = prX + prW + 18;
        opY  = panY + 52;
        opW  = panX + panW - opX - 10;
        menu = new MenuManager(this::addRenderableWidget, this::act);
        initColors();
        rebuild();
    }

    private void rebuild() {
        clearWidgets();
        if (showPicker) return;
        String[] r      = RACE_DEFS[raceIdx];
        boolean hasForm = "true".equals(r[2]);
        boolean hasHair = "true".equals(r[3]);
        int cy = opY;
        int right = opX + opW;

        menu.addArrows("race", opX, right, panY + 12, AW, AH);

        if (hasForm && RACE_FORMS[raceIdx].length > 0) {
            menu.addArrows("form", opX, right, cy, AW, AH); cy += GAP;
        }
        if (hasHair) {
            menu.addArrows("hair", opX, right, cy, AW, AH); cy += GAP;
            addSwatch(opX + (opW - 100) / 2, cy, 100, 18, hairColor, "hair"); cy += 22;
        }

        menu.addArrows("age",  opX, right, cy, AW, AH); cy += GAP;
        menu.addArrows("body", opX, right, cy, AW, AH); cy += GAP;

        int sw = 32, sh = 20, stot = sw * 3 + 8, ssx = opX + (opW - stot) / 2;
        addSwatch(ssx,          cy, sw, sh, bodyColor,               "body");
        addSwatch(ssx + sw + 4, cy, sw, sh, lighter(bodyColor, .15f),"body_l");
        addSwatch(ssx+sw*2+8,   cy, sw, sh, darker(bodyColor,  .20f),"body_d");
        cy += sh + 8;

        menu.addArrows("nose",  opX, right, cy, AW, AH); cy += GAP;
        menu.addArrows("mouth", opX, right, cy, AW, AH); cy += GAP;
        menu.addArrows("eye",   opX, right, cy, AW, AH); cy += GAP;

        int ew = 48, etot = ew * 2 + 6, esx = opX + (opW - etot) / 2;
        addSwatch(esx,      cy, ew, sh, eyeColor,              "eyes");
        addSwatch(esx+ew+6, cy, ew, sh, darker(eyeColor,.30f), "eyes_d");
        cy += sh + 6;

        menu.addButton("equal", opX + (opW - 80) / 2, cy, 80, 20, "Match");

        menu.addButton("close", panX + 8,         panY + panH - 34, 32, 26, "Y");
        menu.addButton("prox",  panX + panW - 78, panY + panH - 34, 70, 26, "Next");
    }

    private void addSwatch(int x, int y, int w, int h, int color, String key) {
        addRenderableWidget(new SwatchBtn(x, y, w, h, color, key,
            b -> openPicker(((SwatchBtn) b).target)));
    }

    private void act(String key) {
        String[] r       = RACE_DEFS[raceIdx];
        int maxBody  = Integer.parseInt(r[4]);
        int maxNose  = Integer.parseInt(r[5]);
        int maxMouth = Integer.parseInt(r[6]);
        int maxEye   = Integer.parseInt(r[7]);
        switch (key) {
            case "race_p"  -> { raceIdx = (raceIdx - 1 + RACE_DEFS.length) % RACE_DEFS.length; resetRace(); return; }
            case "race_n"  -> { raceIdx = (raceIdx + 1) % RACE_DEFS.length;                    resetRace(); return; }
            case "form_p"  -> { if (RACE_FORMS[raceIdx].length > 0) formIdx = Math.max(0, formIdx - 1); }
            case "form_n"  -> { if (RACE_FORMS[raceIdx].length > 0) formIdx = Math.min(RACE_FORMS[raceIdx].length - 1, formIdx + 1); }
            case "hair_p"  -> { hairNum = Math.max(1, hairNum - 1); }
            case "hair_n"  -> { hairNum = Math.min(HAIR_STYLES, hairNum + 1); }
            case "age_p"   -> { ageIdx = Math.max(0, ageIdx - 1); }
            case "age_n"   -> { ageIdx = Math.min(AGE_LABELS.length - 1, ageIdx + 1); }
            case "body_p"  -> { bodyTypeIdx = Math.max(0, bodyTypeIdx - 1); }
            case "body_n"  -> { bodyTypeIdx = Math.min(maxBody - 1, bodyTypeIdx + 1); }
            case "nose_p"  -> { noseIdx  = Math.max(0, noseIdx  - 1); }
            case "nose_n"  -> { noseIdx  = Math.min(maxNose  - 1, noseIdx  + 1); }
            case "mouth_p" -> { mouthIdx = Math.max(0, mouthIdx - 1); }
            case "mouth_n" -> { mouthIdx = Math.min(maxMouth - 1, mouthIdx + 1); }
            case "eye_p"   -> { eyeIdx   = Math.max(0, eyeIdx   - 1); }
            case "eye_n"   -> { eyeIdx   = Math.min(maxEye   - 1, eyeIdx   + 1); }
            case "equal"   -> { eyeColor = bodyColor; hairColor = bodyColor; }
            case "close"   -> { onClose(); return; }
            case "prox"    -> { save(); return; }
        }
        rebuild();
    }

    private void resetRace() {
        formIdx = 0; hairNum = 1; ageIdx = 0;
        bodyTypeIdx = 0; noseIdx = 0; mouthIdx = 0; eyeIdx = 0;
        initColors(); rebuild();
    }

    private void initColors() {
        String[] r = RACE_DEFS[raceIdx];
        bodyColor = (int) Long.parseLong(r[8].replace("0x",""),  16);
        hairColor = (int) Long.parseLong(r[9].replace("0x",""),  16);
        eyeColor  = (int) Long.parseLong(r[10].replace("0x",""), 16);
    }

    private void save() {
        if (minecraft == null || minecraft.player == null) { onClose(); return; }
        var nbt = minecraft.player.getPersistentData();
        String[] r = RACE_DEFS[raceIdx];
        nbt.putString("dbi:race",        r[0]);
        nbt.putString("dbi:raceDisplay", r[1]);
        nbt.putInt("dbi:formIdx",    formIdx);
        nbt.putInt("dbi:hairNum",    hairNum);
        nbt.putInt("dbi:ageIdx",     ageIdx);
        nbt.putInt("dbi:bodyTypeIdx",bodyTypeIdx);
        nbt.putInt("dbi:noseIdx",    noseIdx);
        nbt.putInt("dbi:mouthIdx",   mouthIdx);
        nbt.putInt("dbi:eyeIdx",     eyeIdx);
        nbt.putInt("dbi:bodyColor",  bodyColor);
        nbt.putInt("dbi:hairColor",  hairColor);
        nbt.putInt("dbi:eyeColor",   eyeColor);
        ModNetwork.sendToServer(new RaceSelectionPacket(
            r[0], formIdx, hairNum - 1, ageIdx, bodyTypeIdx,
            noseIdx, mouthIdx, eyeIdx, bodyColor, hairColor, eyeColor
        ));
        onClose();
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        drawPanel(g);
        super.render(g, mx, my, pt);
        if (showPicker) drawPicker(g);
    }

    private void drawPanel(GuiGraphics g) {
        String[] r  = RACE_DEFS[raceIdx];
        int midX    = opX + opW / 2;
        int cy      = opY;
        boolean hasForm = "true".equals(r[2]);
        boolean hasHair = "true".equals(r[3]);

        g.fill(panX, panY, panX + panW, panY + panH, 0xCC5B9BD5);
        border(g, panX, panY, panW, panH, 0xFF1A3A6E);

        g.drawString(font, "Appearance", panX + 12, panY + 14, 0xFF000000, false);
        g.drawCenteredString(font, r[1], midX, panY + 14, 0xFF000000);

        g.fill(prX, prY, prX + prW, prY + prH, 0x44FFFFFF);
        border(g, prX, prY, prW, prH, 0xFF1A3A6E);
        drawPreview(g);

        g.fill(opX - 9, panY + 46, opX - 8, panY + panH - 36, 0xFF1A3A6E);

        if (hasForm && RACE_FORMS[raceIdx].length > 0) {
            int fi = Math.min(formIdx, RACE_FORMS[raceIdx].length - 1);
            g.drawCenteredString(font, "Form: " + RACE_FORMS[raceIdx][fi], midX, cy + 4, 0xFF000000);
            cy += GAP;
        }
        if (hasHair) {
            g.drawCenteredString(font, "Hair " + hairNum, midX, cy + 4, 0xFF000000);
            cy += GAP + 22;
        }
        g.drawCenteredString(font, AGE_LABELS[ageIdx],           midX, cy + 4, 0xFF000000); cy += GAP;
        g.drawCenteredString(font, "Custom Skin",                 midX, cy - 2, 0xFF000000);
        g.drawCenteredString(font, "Body Type " + (bodyTypeIdx+1),midX, cy + 8, 0xFF000000); cy += GAP + 28;
        g.drawCenteredString(font, "Nose "  + (noseIdx  + 1),    midX, cy + 4, 0xFF000000); cy += GAP;
        g.drawCenteredString(font, "Mouth " + (mouthIdx + 1),    midX, cy + 4, 0xFF000000); cy += GAP;
        g.drawCenteredString(font, "Eyes "  + (eyeIdx   + 1),    midX, cy + 4, 0xFF000000); cy += GAP + 26;
        g.drawCenteredString(font, "Match",                       midX, cy + 4, 0xFF000000);
    }

    private void drawPreview(GuiGraphics g) {
        String  raceId  = RACE_DEFS[raceIdx][0];
        boolean hasHair = "true".equals(RACE_DEFS[raceIdx][3]);
        int cx = prX + prW / 2, cy = prY + prH / 2;

        ResourceLocation raceTex = new ResourceLocation(Dbi.MOD_ID, "textures/entity/race/" + raceId + ".png");
        boolean hasTex = false;
        try { minecraft.getResourceManager().getResource(raceTex); hasTex = true; } catch (Exception ignored) {}

        if (hasTex) {
            g.blit(raceTex, cx - 16, cy - 32, 8, 8, 8, 8, 64, 64);
            g.blit(raceTex, cx - 12, cy - 24, 20, 20, 8, 12, 64, 64);
        } else {
            int bc = 0xFF000000 | (bodyColor & 0xFFFFFF);
            int ec = 0xFF000000 | (eyeColor  & 0xFFFFFF);
            int hc = 0xFF000000 | (hairColor & 0xFFFFFF);
            g.fill(cx-13, cy-54, cx+13, cy-28, bc);
            g.fill(cx-10, cy-28, cx+10, cy+10, bc);
            g.fill(cx-10, cy+10, cx-2,  cy+42, bc);
            g.fill(cx+2,  cy+10, cx+10, cy+42, bc);
            g.fill(cx-22, cy-28, cx-10, cy+6,  bc);
            g.fill(cx+10, cy-28, cx+22, cy+6,  bc);
            g.fill(cx-9,  cy-47, cx-4,  cy-42, ec);
            g.fill(cx+4,  cy-47, cx+9,  cy-42, ec);
            if (hasHair) g.fill(cx-14, cy-62, cx+14, cy-53, hc);
        }
        if (hasHair) {
            ResourceLocation hairTex = new ResourceLocation(Dbi.MOD_ID,
                "textures/entity/customizacao/hair/hair_base" + hairNum + ".png");
            try { minecraft.getResourceManager().getResource(hairTex);
                  g.blit(hairTex, cx - 16, cy - 62, 0, 0, 32, 32, 32, 32);
            } catch (Exception ignored) {}
        }
    }

    private void drawPicker(GuiGraphics g) {
        g.fill(px-4, py-4, px+PW+SW+30, py+PH+4, 0xFF222222);
        border(g, px-4, py-4, PW+SW+34, PH+8, 0xFF777777);
        for (int i = 0; i < PW; i++)
            for (int j = 0; j < PH; j++)
                g.fill(px+i, py+j, px+i+1, py+j+1,
                    0xFF000000 | hsv2rgb(i/(float)(PW-1)*360f, 1f-j/(float)(PH-1), 1f));
        for (int j = 0; j < PH; j++)
            g.fill(sx, sy+j, sx+SW, sy+j+1, 0xFF000000 | hsv2rgb(ph, 1f, 1f-j/(float)(PH-1)));
        int curX = px + (int)(ph/360f*(PW-1)), curY = py + (int)((1f-ps)*(PH-1));
        g.fill(curX-3, curY-1, curX+3, curY+1, 0xFFFFFFFF);
        g.fill(curX-1, curY-3, curX+1, curY+3, 0xFFFFFFFF);
        int cur = pickerCol();
        g.fill(sx+SW+6, py, sx+SW+30, py+22, 0xFF000000|(cur&0xFFFFFF));
        g.drawString(font, String.format("#%06X", cur&0xFFFFFF), sx+SW+6, py+26, 0xFFAAAAAA, false);
        addRenderableWidget(Button.builder(Component.literal("Back"),
            b -> { showPicker = false; pickerTarget = null; rebuild(); }
        ).pos(px, py+PH+6).size(60, 18).build());
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (showPicker) {
            int x = (int)mx, y = (int)my;
            if (x>=px && x<px+PW && y>=py && y<py+PH) {
                ph = (x-px)/(float)(PW-1)*360f;
                ps = 1f-(y-py)/(float)(PH-1);
                applyPick(hsv2rgb(ph, ps, pv)); return true;
            }
            if (x>=sx && x<sx+SW && y>=sy && y<sy+PH) {
                pv = 1f-(y-sy)/(float)(PH-1);
                applyPick(hsv2rgb(ph, ps, pv)); return true;
            }
        }
        return super.mouseClicked(mx, my, btn);
    }

    private void openPicker(String target) {
        pickerTarget = target; showPicker = true;
        px = width/2-(PW+SW+10)/2; py = height/2-PH/2;
        sx = px+PW+6; sy = py;
        float[] hsv = rgb2hsv(pickerCol());
        ph = hsv[0]; ps = hsv[1]; pv = hsv[2];
    }

    private int pickerCol() {
        if ("hair".equals(pickerTarget)) return hairColor;
        if (pickerTarget != null && pickerTarget.startsWith("eye")) return eyeColor;
        return bodyColor;
    }

    private void applyPick(int rgb) {
        int c = 0xFF000000 | (rgb & 0xFFFFFF);
        if ("hair".equals(pickerTarget))                                  hairColor = c;
        else if (pickerTarget != null && pickerTarget.startsWith("eye"))  eyeColor  = c;
        else                                                               bodyColor = c;
        rebuild();
    }

    private void border(GuiGraphics g, int x, int y, int w, int h, int c) {
        g.fill(x, y, x+w, y+1, c); g.fill(x, y+h-1, x+w, y+h, c);
        g.fill(x, y, x+1, y+h, c); g.fill(x+w-1, y, x+w, y+h, c);
    }

    private int lighter(int color, float a) {
        int r = Math.min(255,(int)(((color>>16)&0xFF)+255*a));
        int gv= Math.min(255,(int)(((color>> 8)&0xFF)+255*a));
        int b = Math.min(255,(int)(( color     &0xFF)+255*a));
        return (0xFF<<24)|(r<<16)|(gv<<8)|b;
    }
    private int darker(int color, float a) {
        int r = Math.max(0,(int)(((color>>16)&0xFF)-255*a));
        int gv= Math.max(0,(int)(((color>> 8)&0xFF)-255*a));
        int b = Math.max(0,(int)(( color     &0xFF)-255*a));
        return (0xFF<<24)|(r<<16)|(gv<<8)|b;
    }
    private int hsv2rgb(float h, float s, float v) {
        int hi=(int)(h/60f)%6; float f=h/60f-(int)(h/60f),p=v*(1-s),q=v*(1-f*s),t=v*(1-(1-f)*s);
        float r=0,g=0,b=0;
        switch(hi){case 0->{r=v;g=t;b=p;}case 1->{r=q;g=v;b=p;}case 2->{r=p;g=v;b=t;}
                   case 3->{r=p;g=q;b=v;}case 4->{r=t;g=p;b=v;}case 5->{r=v;g=p;b=q;}}
        return (Math.round(r*255)<<16)|(Math.round(g*255)<<8)|Math.round(b*255);
    }
    private float[] rgb2hsv(int rgb) {
        float r=((rgb>>16)&0xFF)/255f,g=((rgb>>8)&0xFF)/255f,b=(rgb&0xFF)/255f;
        float mx=Math.max(r,Math.max(g,b)),mn=Math.min(r,Math.min(g,b)),d=mx-mn,h=0;
        if(d!=0){if(mx==r)h=60f*((g-b)/d%6f);else if(mx==g)h=60f*((b-r)/d+2f);else h=60f*((r-g)/d+4f);}
        if(h<0)h+=360f;
        return new float[]{h,mx==0?0:d/mx,mx};
    }

    @Override public boolean isPauseScreen() { return false; }

    private static class SwatchBtn extends Button {
        final String target;
        int color;
        SwatchBtn(int x, int y, int w, int h, int color, String target, OnPress p) {
            super(x, y, w, h, Component.empty(), p, DEFAULT_NARRATION);
            this.color = color; this.target = target;
        }
        @Override public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
            g.fill(getX()-1, getY()-1, getX()+width+1, getY()+height+1, isHovered()?0xFFFFFFFF:0xFF444444);
            g.fill(getX(), getY(), getX()+width, getY()+height, 0xFF000000|(color&0xFFFFFF));
        }
    }
}

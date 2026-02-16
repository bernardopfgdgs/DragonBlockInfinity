#!/bin/bash
# ================================================================
# Dragon Block Infinity — fix_menu.sh
# Corrige 4 bugs confirmados pelo vídeo
# chmod +x fix_menu.sh && ./fix_menu.sh
# ================================================================

BASE="src/main/java/com/bernardo/dbi"

# ================================================================
# FIX 1 + 2 — BaseMenuScreen
#   Bug 1: menu maior que a tela → escala proporcional
#   Bug 2: sprites pegando só UV inicial → blit com 11 parâmetros
# ================================================================
cat << 'EOF' > $BASE/client/screen/BaseMenuScreen.java
package com.bernardo.dbi.client.screen;

import com.bernardo.dbi.client.menu.MenuDefinition;
import com.bernardo.dbi.client.menu.MenuManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class BaseMenuScreen extends Screen {

    protected final MenuDefinition def;
    protected int   menuX, menuY;
    protected float menuScale = 1f;
    protected int   scaledW, scaledH;

    public BaseMenuScreen(int menuId) {
        super(Component.empty());
        this.def = MenuManager.get(menuId);
    }

    @Override
    protected void init() {
        super.init();

        // FIX 1 — escala proporcional para caber na tela
        float sx = (this.width  - 20f) / (float) def.displayW;
        float sy = (this.height - 20f) / (float) def.displayH;
        menuScale = Math.min(1f, Math.min(sx, sy));

        scaledW = Math.round(def.displayW * menuScale);
        scaledH = Math.round(def.displayH * menuScale);
        menuX   = (this.width  - scaledW) / 2;
        menuY   = (this.height - scaledH) / 2;

        // Botões do MenuManager — posição e tamanho escalados
        for (MenuDefinition.ButtonDef b : def.buttons) {
            final String action = b.action;
            int bx = menuX + Math.round(b.screenX * menuScale);
            int by = menuY + Math.round(b.screenY * menuScale);
            int bw = Math.max(4, Math.round(b.screenW * menuScale));
            int bh = Math.max(4, Math.round(b.screenH * menuScale));

            if (b.useSprite) {
                addRenderableWidget(new SpriteButton(
                    bx, by, bw, bh,
                    b.uvX, b.uvY, b.uvW, b.uvH,
                    btn -> handleAction(action)
                ));
            } else {
                addRenderableWidget(Button.builder(
                    Component.literal(b.label),
                    btn -> handleAction(action))
                    .pos(bx, by).size(bw, bh).build());
            }
        }

        initExtra();
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);

        // FIX 2 — blit de 11 parâmetros (recorta região do PNG e escala)
        g.blit(
            def.background,
            menuX, menuY,               // destino na tela
            scaledW, scaledH,           // tamanho final exibido
            MenuManager.MENU_CROP_U,    // u de origem no PNG (1)
            MenuManager.MENU_CROP_V,    // v de origem no PNG (1)
            def.displayW,               // largura da região no PNG (964)
            def.displayH,               // altura da região no PNG (564)
            def.pngW,                   // largura total do PNG (1024)
            def.pngH                    // altura total do PNG (1024)
        );

        renderExtra(g, mx, my, pt);
        super.render(g, mx, my, pt);
    }

    protected void     initExtra()  {}
    protected abstract void renderExtra(GuiGraphics g, int mx, int my, float pt);
    protected abstract void handleAction(String action);

    // FIX 2 — SpriteButton com blit de 11 parâmetros
    protected static class SpriteButton extends Button {
        private final int uvX, uvY, uvW, uvH;

        public SpriteButton(int x, int y, int w, int h,
                            int uvX, int uvY, int uvW, int uvH,
                            OnPress press) {
            super(x, y, w, h, Component.empty(), press, DEFAULT_NARRATION);
            this.uvX = uvX; this.uvY = uvY;
            this.uvW = uvW; this.uvH = uvH;
        }

        @Override
        public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
            if (isHovered())
                g.fill(getX()-1, getY()-1, getX()+width+1, getY()+height+1, 0x55FFFFFF);

            // FIX 2 — recorta uvW×uvH do sheet e escala para width×height
            g.blit(
                MenuManager.ICONS_BTN,
                getX(), getY(),         // destino
                width,  height,         // tamanho exibido (já escalado)
                uvX,    uvY,            // origem no sheet
                uvW,    uvH,            // tamanho do sprite no sheet
                MenuManager.SHEET_W,    // 1024
                MenuManager.SHEET_H     // 1024
            );
        }
    }
}
EOF

# ================================================================
# FIX 3 + 4 — CharacterScreen
#   Bug 3: cor não atualiza → applyPick não chama rebuild,
#           SwatchBtn tem referência direta à cor
#   Bug 4: player grotesco/pequeno → scale correto no InventoryScreen
# ================================================================
cat << 'EOF' > $BASE/client/screen/CharacterScreen.java
package com.bernardo.dbi.client.screen;

import com.bernardo.dbi.Dbi;
import com.bernardo.dbi.client.menu.MenuManager;
import com.bernardo.dbi.network.ModNetwork;
import com.bernardo.dbi.network.packet.RaceSelectionPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CharacterScreen extends BaseMenuScreen {

    // ─── Raças ──────────────────────────────────────────────────
    private static final String[][] RACE_DEFS = {
        { "sayajin",     "Sayajin",      "true",  "true",  "3","3","3","3","0xFFD2956E","0xFF1A1A1A","0xFF1A1A1A" },
        { "namekian",    "Namekian",     "false", "false", "2","2","2","2","0xFF3CB371","0xFF000000","0xFF000000" },
        { "arconsian",   "Arcosian",     "true",  "false", "2","2","2","2","0xFFFFF0F5","0xFF000000","0xFFCC0000" },
        { "humano",      "Humano",       "true",  "true",  "3","3","3","3","0xFFD2956E","0xFF7B4F2E","0xFF5B3A29" },
        { "halfsayajin", "Half-Sayajin", "true",  "true",  "3","3","3","3","0xFFD2956E","0xFF1A1A1A","0xFF1A1A1A" },
    };
    private static final String[][] RACE_FORMS = {
        { "Normal" }, {},
        { "Forma Minima","Forma 1","Forma 2","Forma 3","Forma Final" },
        { "Normal" }, { "Normal" },
    };
    private static final int HAIR_STYLES = 6;

    // ─── Estado ─────────────────────────────────────────────────
    private int raceIdx=0, formIdx=0, hairStyle=0, ageIdx=0;
    private int bodyTypeIdx=0, noseIdx=0, mouthIdx=0, eyeIdx=0;
    private int bodyColor, hairColor, eyeColor;

    // ─── Preview 3D ─────────────────────────────────────────────
    private float previewYaw=0f, previewPitch=0f;
    private boolean dragging=false;
    private double  lastDragX=0, lastDragY=0;
    private static final float MAX_YAW=70f, MAX_PITCH=20f;
    private int prX, prY, prW, prH;

    // ─── FIX 3: Color picker — estado nunca é resetado pelo rebuild
    private boolean showPicker   = false;
    private String  pickerTarget = null;
    private static final int PW=160, PH=100, SW=12;
    private int pkX, pkY, slX, slY;
    private float ph=0f, ps=1f, pv=1f;

    // FIX 3: SwatchBtns guardados para atualizar cor sem rebuild
    private SwatchBtn swBody, swBodyL, swBodyD, swHair, swEyes, swEyesD;

    // ─── Layout ──────────────────────────────────────────────────
    private int opColX, opColW, opMidX;

    public CharacterScreen() { super(MenuManager.MENU_CHARACTER); }

    @Override
    protected void initExtra() {
        initColors();

        // Área de preview (36% esquerda do menu)
        prW = Math.round(scaledW * 0.36f);
        prH = scaledH - Math.round(56 * menuScale);
        prX = menuX + Math.round(8 * menuScale);
        prY = menuY + Math.round(47 * menuScale);

        // Coluna de opções (direita)
        opColX = menuX + prW + Math.round(24 * menuScale);
        opColW = menuX + scaledW - opColX - Math.round(8 * menuScale);
        opMidX = opColX + opColW / 2;

        addSwatches();
    }

    private void initColors() {
        String[] r = RACE_DEFS[raceIdx];
        bodyColor = hex(r[8]); hairColor = hex(r[9]); eyeColor = hex(r[10]);
    }

    private void addSwatches() {
        int sw = Math.round(32 * menuScale);
        int sh = Math.round(22 * menuScale);
        int ew = Math.round(48 * menuScale);

        // Swatch cabelo (linha ~104 escalada)
        int hairY = menuY + Math.round(104 * menuScale);
        if ("true".equals(RACE_DEFS[raceIdx][3])) {
            int hw = Math.round(100 * menuScale);
            swHair = new SwatchBtn(opMidX-hw/2, hairY, hw, Math.round(18*menuScale),
                hairColor, "hair", b -> openPicker("hair"));
            addRenderableWidget(swHair);
        }

        // Swatches corpo (linha ~182 escalada)
        int bodyY = menuY + Math.round(182 * menuScale);
        int stot  = sw*3 + Math.round(8*menuScale);
        int ssx   = opColX + (opColW - stot) / 2;
        swBody  = new SwatchBtn(ssx,          bodyY, sw, sh, bodyColor,             "body",   b -> openPicker("body"));
        swBodyL = new SwatchBtn(ssx+sw+Math.round(4*menuScale), bodyY, sw, sh, lighter(bodyColor,.15f), "body_l", b -> openPicker("body_l"));
        swBodyD = new SwatchBtn(ssx+sw*2+Math.round(8*menuScale), bodyY, sw, sh, darker(bodyColor,.20f),  "body_d", b -> openPicker("body_d"));
        addRenderableWidget(swBody);
        addRenderableWidget(swBodyL);
        addRenderableWidget(swBodyD);

        // Swatches olhos (linha ~286 escalada)
        int eyeY = menuY + Math.round(286 * menuScale);
        int etot = ew*2 + Math.round(6*menuScale);
        int esx  = opColX + (opColW - etot) / 2;
        swEyes  = new SwatchBtn(esx,            eyeY, ew, sh, eyeColor,              "eyes",   b -> openPicker("eyes"));
        swEyesD = new SwatchBtn(esx+ew+Math.round(6*menuScale), eyeY, ew, sh, darker(eyeColor,.30f), "eyes_d", b -> openPicker("eyes_d"));
        addRenderableWidget(swEyes);
        addRenderableWidget(swEyesD);

        // Botão Igualar
        int eqW = Math.round(110 * menuScale);
        int eqH = Math.round(24  * menuScale);
        addRenderableWidget(Button.builder(
            Component.literal("Igualar"), b -> handleAction("equal"))
            .pos(opMidX - eqW/2, menuY + Math.round(312*menuScale))
            .size(eqW, eqH).build());
    }

    @Override
    protected void handleAction(String action) {
        String[] r   = RACE_DEFS[raceIdx];
        int maxBody  = Integer.parseInt(r[4]);
        int maxNose  = Integer.parseInt(r[5]);
        int maxMouth = Integer.parseInt(r[6]);
        int maxEye   = Integer.parseInt(r[7]);
        switch (action) {
            case "race_p"  -> { raceIdx=(raceIdx-1+RACE_DEFS.length)%RACE_DEFS.length; resetRace(); return; }
            case "race_n"  -> { raceIdx=(raceIdx+1)%RACE_DEFS.length;                  resetRace(); return; }
            case "form_p"  -> { if(RACE_FORMS[raceIdx].length>0) formIdx=Math.max(0,formIdx-1); }
            case "form_n"  -> { if(RACE_FORMS[raceIdx].length>0) formIdx=Math.min(RACE_FORMS[raceIdx].length-1,formIdx+1); }
            case "hair_p"  -> { hairStyle=Math.max(0,hairStyle-1); }
            case "hair_n"  -> { hairStyle=Math.min(HAIR_STYLES-1,hairStyle+1); }
            case "age_p"   -> { ageIdx=Math.max(0,ageIdx-1); }
            case "age_n"   -> { ageIdx=Math.min(1,ageIdx+1); }
            case "body_p"  -> { bodyTypeIdx=Math.max(0,bodyTypeIdx-1); }
            case "body_n"  -> { bodyTypeIdx=Math.min(maxBody-1,bodyTypeIdx+1); }
            case "nose_p"  -> { noseIdx=Math.max(0,noseIdx-1); }
            case "nose_n"  -> { noseIdx=Math.min(maxNose-1,noseIdx+1); }
            case "mouth_p" -> { mouthIdx=Math.max(0,mouthIdx-1); }
            case "mouth_n" -> { mouthIdx=Math.min(maxMouth-1,mouthIdx+1); }
            case "eye_p"   -> { eyeIdx=Math.max(0,eyeIdx-1); }
            case "eye_n"   -> { eyeIdx=Math.min(maxEye-1,eyeIdx+1); }
            case "equal"   -> { eyeColor=bodyColor; hairColor=bodyColor; refreshSwatches(); return; }
            case "close"   -> { onClose(); return; }
            case "prox"    -> { save(); return; }
        }
    }

    private void resetRace() {
        formIdx=hairStyle=ageIdx=bodyTypeIdx=noseIdx=mouthIdx=eyeIdx=0;
        initColors(); init(); // recria swatches ao trocar raça
    }

    private void save() {
        if (minecraft==null||minecraft.player==null) { onClose(); return; }
        String[] r = RACE_DEFS[raceIdx];
        var nbt = minecraft.player.getPersistentData();
        nbt.putString("dbi:race",       r[0]);
        nbt.putString("dbi:raceDisplay",r[1]);
        nbt.putInt("dbi:formIdx",       formIdx);
        nbt.putInt("dbi:hairStyle",     hairStyle);
        nbt.putInt("dbi:ageIdx",        ageIdx);
        nbt.putInt("dbi:bodyTypeIdx",   bodyTypeIdx);
        nbt.putInt("dbi:noseIdx",       noseIdx);
        nbt.putInt("dbi:mouthIdx",      mouthIdx);
        nbt.putInt("dbi:eyeIdx",        eyeIdx);
        nbt.putInt("dbi:bodyColor",     bodyColor);
        nbt.putInt("dbi:hairColor",     hairColor);
        nbt.putInt("dbi:eyeColor",      eyeColor);
        ModNetwork.sendToServer(new RaceSelectionPacket(
            r[0], formIdx, hairStyle, ageIdx, bodyTypeIdx,
            noseIdx, mouthIdx, eyeIdx, bodyColor, hairColor, eyeColor));
        onClose();
    }

    // ============================================================
    //  renderExtra
    // ============================================================

    @Override
    protected void renderExtra(GuiGraphics g, int mx, int my, float pt) {
        String[] r    = RACE_DEFS[raceIdx];
        boolean forma  = "true".equals(r[2]);
        boolean cabelo = "true".equals(r[3]);

        // Nome da raça
        g.drawCenteredString(font, r[1], opMidX, menuY + Math.round(14*menuScale), 0xFFFFFFFF);

        // FIX 4 — preview 3D com tamanho correto
        drawPreview3D(g);
        g.drawCenteredString(font, "§7← arraste →", prX+prW/2, prY+prH-12, 0xFF888888);

        // Labels
        int cy = menuY + Math.round(56*menuScale);
        int gap = Math.round(26*menuScale);
        if (forma && RACE_FORMS[raceIdx].length>0) {
            int fi = Math.min(formIdx, RACE_FORMS[raceIdx].length-1);
            g.drawCenteredString(font, "Forma: "+RACE_FORMS[raceIdx][fi], opMidX, cy, 0xFFFFFFFF);
            cy += gap;
        }
        if (cabelo) {
            g.drawCenteredString(font, "Cabelo "+(hairStyle+1), opMidX, cy, 0xFFFFFFFF);
            cy += gap * 2;
        }
        g.drawCenteredString(font, ageIdx==0?"Adulto":"Criança",         opMidX, cy,       0xFFFFFFFF); cy+=gap;
        g.drawCenteredString(font, "Tipo de Corpo "+(bodyTypeIdx+1),      opMidX, cy,       0xFFFFFFFF); cy+=gap;
        cy += Math.round(30*menuScale); // swatches corpo
        g.drawCenteredString(font, "Nariz  "+(noseIdx+1),                 opMidX, cy,       0xFFFFFFFF); cy+=gap;
        g.drawCenteredString(font, "Boca   "+(mouthIdx+1),                opMidX, cy,       0xFFFFFFFF); cy+=gap;
        g.drawCenteredString(font, "Olhos  "+(eyeIdx+1),                  opMidX, cy,       0xFFFFFFFF);

        // Picker por cima de tudo
        if (showPicker) drawPicker(g);
    }

    // ── FIX 4: Preview 3D com escala proporcional à área ─────────
    private void drawPreview3D(GuiGraphics g) {
        if (minecraft==null||minecraft.player==null) return;

        // Scissor
        double sc = minecraft.getWindow().getGuiScale();
        int wh    = minecraft.getWindow().getHeight();
        com.mojang.blaze3d.platform.GlStateManager._enableScissorTest();
        com.mojang.blaze3d.platform.GlStateManager._scissorBox(
            (int)(prX*sc),
            (int)(wh - (prY+prH)*sc),
            (int)(prW*sc),
            (int)(prH*sc)
        );

        // FIX 4 — scale = prH/2.5 em vez de /3 → player maior
        int entityScale = (int)(prH / 2.5f);
        int centerX = prX + prW / 2;
        int centerY = prY + prH - (int)(prH * 0.15f); // 15% do fundo

        InventoryScreen.renderEntityInInventoryFollowsMouse(
            g,
            centerX, centerY,
            entityScale,
            // Converte previewYaw e previewPitch para o formato mouseX/Y
            (float)centerX - (centerX + previewYaw  * 2.5f),
            (float)centerY - (centerY + previewPitch * 2.5f),
            minecraft.player
        );

        com.mojang.blaze3d.platform.GlStateManager._disableScissorTest();
    }

    // ── Arrasto ───────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (showPicker) {
            int x=(int)mx, y=(int)my;
            if (x>=pkX&&x<pkX+PW&&y>=pkY&&y<pkY+PH) {
                ph=(x-pkX)/(float)(PW-1)*360f;
                ps=1f-(y-pkY)/(float)(PH-1);
                applyPick(); return true;
            }
            if (x>=slX&&x<slX+SW&&y>=slY&&y<slY+PH) {
                pv=1f-(y-slY)/(float)(PH-1);
                applyPick(); return true;
            }
            showPicker=false; pickerTarget=null; return true;
        }
        if (mx>=prX&&mx<prX+prW&&my>=prY&&my<prY+prH) {
            dragging=true; lastDragX=mx; lastDragY=my; return true;
        }
        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (dragging) {
            previewYaw  =clamp(previewYaw  +(float)(mx-lastDragX)*0.8f,-MAX_YAW,  MAX_YAW);
            previewPitch=clamp(previewPitch+(float)(my-lastDragY)*0.4f,-MAX_PITCH,MAX_PITCH);
            lastDragX=mx; lastDragY=my; return true;
        }
        // FIX 3 — arrasto no picker também atualiza cor em tempo real
        if (showPicker) {
            int x=(int)mx, y=(int)my;
            if (x>=pkX&&x<pkX+PW&&y>=pkY&&y<pkY+PH) {
                ph=(x-pkX)/(float)(PW-1)*360f;
                ps=1f-(y-pkY)/(float)(PH-1);
                applyPick(); return true;
            }
            if (x>=slX&&x<slX+SW&&y>=slY&&y<slY+PH) {
                pv=1f-(y-slY)/(float)(PH-1);
                applyPick(); return true;
            }
        }
        return super.mouseDragged(mx, my, btn, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx,double my,int btn){
        dragging=false; return super.mouseReleased(mx,my,btn);
    }

    // ── FIX 3: Color picker ───────────────────────────────────────

    private void openPicker(String tgt) {
        pickerTarget = tgt;
        showPicker   = true;
        // Centraliza picker na tela
        pkX = width/2 - (PW+SW+16)/2;
        pkY = height/2 - PH/2;
        slX = pkX+PW+6; slY = pkY;
        float[] hsv = rgb2hsv(pickerCol());
        ph=hsv[0]; ps=hsv[1]; pv=hsv[2];
    }

    private int pickerCol() {
        if ("hair".equals(pickerTarget))                              return hairColor;
        if (pickerTarget!=null&&pickerTarget.startsWith("eye"))       return eyeColor;
        return bodyColor;
    }

    /** FIX 3 — aplica cor E atualiza swatches sem rebuild (não perde estado) */
    private void applyPick() {
        int rgb = hsv2rgb(ph, ps, pv);
        int c   = (0xFF<<24) | (rgb & 0xFFFFFF);
        if ("hair".equals(pickerTarget)) {
            hairColor = c;
            if (swHair != null) swHair.color = c;
        } else if (pickerTarget!=null&&pickerTarget.startsWith("eye")) {
            eyeColor = c;
            if (swEyes  != null) swEyes.color  = c;
            if (swEyesD != null) swEyesD.color = darker(c, .30f);
        } else {
            bodyColor = c;
            if (swBody  != null) swBody.color  = c;
            if (swBodyL != null) swBodyL.color = lighter(c, .15f);
            if (swBodyD != null) swBodyD.color = darker (c, .20f);
        }
        // NÃO chama rebuild — picker continua aberto, cor visível em tempo real
    }

    /** Atualiza cores dos swatches sem recriar widgets (para "Igualar") */
    private void refreshSwatches() {
        if (swHair  != null) swHair.color  = hairColor;
        if (swEyes  != null) swEyes.color  = eyeColor;
        if (swEyesD != null) swEyesD.color = darker(eyeColor,.30f);
        if (swBody  != null) swBody.color  = bodyColor;
        if (swBodyL != null) swBodyL.color = lighter(bodyColor,.15f);
        if (swBodyD != null) swBodyD.color = darker(bodyColor,.20f);
    }

    private void drawPicker(GuiGraphics g) {
        // Fundo
        g.fill(pkX-6, pkY-6, pkX+PW+SW+22, pkY+PH+6, 0xEE111111);
        g.fill(pkX-5, pkY-5, pkX+PW+SW+21, pkY+PH+5, 0xFF333333);

        // Gradiente hue×saturation
        for (int i=0; i<PW; i++)
            for (int j=0; j<PH; j++)
                g.fill(pkX+i, pkY+j, pkX+i+1, pkY+j+1,
                    (0xFF<<24)|hsv2rgb(i/(float)(PW-1)*360f, 1f-(j/(float)(PH-1)), 1f));

        // Slider de brilho
        for (int j=0; j<PH; j++)
            g.fill(slX, slY+j, slX+SW, slY+j+1,
                (0xFF<<24)|hsv2rgb(ph, 1f, 1f-(j/(float)(PH-1))));

        // Cursor
        int cx=pkX+(int)(ph/360f*(PW-1));
        int cy=pkY+(int)((1f-ps)*(PH-1));
        g.fill(cx-3,cy-1,cx+4,cy+2,0xFFFFFFFF);
        g.fill(cx-1,cy-3,cx+2,cy+4,0xFFFFFFFF);

        // Prévia da cor + hex
        int cur = pickerCol();
        int previewX = slX+SW+4;
        g.fill(previewX, pkY, previewX+24, pkY+24, (0xFF<<24)|(cur&0xFFFFFF));
        g.fill(previewX-1, pkY-1, previewX+25, pkY+25, 0xFF888888);
        g.fill(previewX, pkY, previewX+24, pkY+24, (0xFF<<24)|(cur&0xFFFFFF));
        g.drawString(font, String.format("#%06X", cur&0xFFFFFF),
            previewX, pkY+28, 0xFFCCCCCC, false);
    }

    // ── Utils ────────────────────────────────────────────────────

    private float clamp(float v,float mn,float mx){return Math.max(mn,Math.min(mx,v));}

    private int lighter(int c,float a){
        return (0xFF<<24)
            |(Math.min(255,(int)(((c>>16)&0xFF)+255*a))<<16)
            |(Math.min(255,(int)(((c>>8 )&0xFF)+255*a))<<8)
            | Math.min(255,(int)( (c     &0xFF)+255*a));
    }
    private int darker(int c,float a){
        return (0xFF<<24)
            |(Math.max(0,(int)(((c>>16)&0xFF)-255*a))<<16)
            |(Math.max(0,(int)(((c>>8 )&0xFF)-255*a))<<8)
            | Math.max(0,(int)( (c    &0xFF)-255*a));
    }

    private int hex(String s){ return (int)Long.parseLong(s.replace("0x",""),16); }

    private int hsv2rgb(float h,float s,float v){
        int hi=(int)(h/60f)%6;
        float f=h/60f-(int)(h/60f),p=v*(1-s),q=v*(1-f*s),t=v*(1-(1-f)*s);
        float r=0,g=0,b=0;
        switch(hi){case 0->{r=v;g=t;b=p;}case 1->{r=q;g=v;b=p;}
                   case 2->{r=p;g=v;b=t;}case 3->{r=p;g=q;b=v;}
                   case 4->{r=t;g=p;b=v;}case 5->{r=v;g=p;b=q;}}
        return (Math.round(r*255)<<16)|(Math.round(g*255)<<8)|Math.round(b*255);
    }

    private float[] rgb2hsv(int rgb){
        float r=((rgb>>16)&0xFF)/255f,g=((rgb>>8)&0xFF)/255f,b=(rgb&0xFF)/255f;
        float mx=Math.max(r,Math.max(g,b)),mn=Math.min(r,Math.min(g,b)),d=mx-mn,h=0;
        if(d!=0){
            if(mx==r)h=60f*((g-b)/d%6f);
            else if(mx==g)h=60f*((b-r)/d+2f);
            else h=60f*((r-g)/d+4f);
        }
        if(h<0)h+=360f;
        return new float[]{h, mx==0?0:d/mx, mx};
    }

    // ── SwatchBtn ────────────────────────────────────────────────

    // FIX 3: color é public para poder atualizar sem recriar o widget
    static class SwatchBtn extends Button {
        final String tgt;
        int color;
        SwatchBtn(int x,int y,int w,int h,int color,String tgt,OnPress p){
            super(x,y,w,h,Component.empty(),p,DEFAULT_NARRATION);
            this.color=color; this.tgt=tgt;
        }
        @Override
        public void renderWidget(GuiGraphics g,int mx,int my,float pt){
            // Borda: branca no hover, cinza fora
            g.fill(getX()-1,getY()-1,getX()+width+1,getY()+height+1,
                isHovered()?0xFFFFFFFF:0xFF666666);
            // Cor atual
            g.fill(getX(),getY(),getX()+width,getY()+height,
                (0xFF<<24)|(color&0xFFFFFF));
        }
    }
}
EOF

echo ""
echo "✅ fix_menu.sh concluído!"
echo ""
echo "Bugs corrigidos:"
echo "  FIX 1 — Menu escala proporcionalmente para caber na tela"
echo "  FIX 2 — Sprites usam blit de 11 parâmetros (recorte UV correto)"
echo "  FIX 3 — Cor atualiza em tempo real ao arrastar no picker"
echo "          Swatches atualizam sem fechar o picker"
echo "  FIX 4 — Player 3D maior (prH/2.5) e posicionado corretamente"

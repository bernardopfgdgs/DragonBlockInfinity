#!/bin/bash
# ================================================================
# Dragon Block Infinity — Sistema de Menus com coordenadas REAIS
# chmod +x setup_menu_system.sh && ./setup_menu_system.sh
# ================================================================

BASE="src/main/java/com/bernardo/dbi"
mkdir -p $BASE/client/menu
mkdir -p $BASE/client/screen
mkdir -p $BASE/client

# ================================================================
# 1. MenuDefinition.java
# ================================================================
cat << 'EOF' > $BASE/client/menu/MenuDefinition.java
package com.bernardo.dbi.client.menu;

import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;

/**
 * Dados de um menu: qual PNG, tamanho, e lista de botões com UV real.
 */
public class MenuDefinition {

    public final int            id;
    public final ResourceLocation background;
    /** Tamanho real do PNG (pixels) */
    public final int pngW, pngH;
    /** Tamanho de exibição na tela */
    public final int displayW, displayH;
    public final List<ButtonDef> buttons = new ArrayList<>();

    public MenuDefinition(int id, ResourceLocation bg,
                          int pngW, int pngH,
                          int displayW, int displayH) {
        this.id       = id;
        this.background = bg;
        this.pngW     = pngW;
        this.pngH     = pngH;
        this.displayW = displayW;
        this.displayH = displayH;
    }

    public MenuDefinition addButton(ButtonDef b) { buttons.add(b); return this; }

    // ─────────────────────────────────────────────────────────
    //  ButtonDef
    // ─────────────────────────────────────────────────────────
    public static class ButtonDef {

        public final String id;
        public final String action;

        // Posição e tamanho NA TELA (relativo ao canto superior esquerdo do menu)
        public final int screenX, screenY, screenW, screenH;

        // UV no spritesheet icons_btn.png
        public final int uvX, uvY, uvW, uvH;

        // true = usa sprite do icons_btn, false = botão de texto simples
        public final boolean useSprite;
        public final String  label;

        /** Construtor sprite */
        public ButtonDef(String id, String action,
                         int screenX, int screenY, int screenW, int screenH,
                         int uvX,    int uvY,    int uvW,    int uvH) {
            this.id        = id;
            this.action    = action;
            this.screenX   = screenX;
            this.screenY   = screenY;
            this.screenW   = screenW;
            this.screenH   = screenH;
            this.uvX       = uvX;
            this.uvY       = uvY;
            this.uvW       = uvW;
            this.uvH       = uvH;
            this.useSprite = true;
            this.label     = "";
        }

        /** Construtor texto */
        public ButtonDef(String id, String action,
                         int screenX, int screenY, int screenW, int screenH,
                         String label) {
            this.id        = id;
            this.action    = action;
            this.screenX   = screenX;
            this.screenY   = screenY;
            this.screenW   = screenW;
            this.screenH   = screenH;
            this.label     = label;
            this.useSprite = false;
            this.uvX = this.uvY = this.uvW = this.uvH = 0;
        }
    }
}
EOF

# ================================================================
# 2. MenuManager.java — GOD FILE com UVs REAIS
# ================================================================
cat << 'EOF' > $BASE/client/menu/MenuManager.java
package com.bernardo.dbi.client.menu;

import com.bernardo.dbi.Dbi;
import com.bernardo.dbi.client.screen.CharacterScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * MenuManager — GOD FILE de menus.
 *
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  SPRITESHEET: icons_btn.png (1024×1024)                     ║
 * ║  Offset Pixel Studio: +1px em U e V                         ║
 * ╠══════════════╦═════╦═════╦══════╦══════╗                   ║
 * ║  Botão       ║  U  ║  V  ║  W   ║  H   ║                   ║
 * ╠══════════════╬═════╬═════╬══════╬══════╣                   ║
 * ║  Seta ←      ║   1 ║   1 ║  20  ║  19  ║                   ║
 * ║  Seta →      ║  22 ║   1 ║  20  ║  19  ║                   ║
 * ║  Seta ↑      ║  44 ║   1 ║  19  ║  19  ║                   ║
 * ║  Seta ↓      ║  64 ║   1 ║  19  ║  19  ║                   ║
 * ║  Botão +     ║   1 ║  23 ║  20  ║  19  ║                   ║
 * ║  Botão X     ║  24 ║  23 ║  27  ║  25  ║                   ║
 * ║  Requisito⚠️ ║   1 ║  45 ║  16  ║  15  ║                   ║
 * ║  Delete 🗑️   ║   1 ║  83 ║  20  ║  19  ║                   ║
 * ║  Confirmar✅  ║   1 ║ 109 ║  28  ║  31  ║                   ║
 * ╚══════════════╩═════╩═════╩══════╩══════╝                   ║
 *                                                               ║
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MENU BACKGROUND: menu.png (1024×1024)                      ║
 * ║  Região do menu: u=1, v=1, w=964, h=564                     ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * IDs de menu:
 *  0 = CharacterScreen  (tecla C)
 *  1 = (reservado)
 *  2 = (reservado)
 */
public class MenuManager {

    // ── Recursos ──────────────────────────────────────────────
    public static final ResourceLocation ICONS_BTN =
        new ResourceLocation(Dbi.MOD_ID, "textures/gui/icons_btn.png");
    public static final int SHEET_W = 1024;
    public static final int SHEET_H = 1024;

    public static final ResourceLocation MENU_BG =
        new ResourceLocation(Dbi.MOD_ID, "textures/gui/menu.png");
    public static final int MENU_PNG_W  = 1024;
    public static final int MENU_PNG_H  = 1024;
    /** Região visível do menu dentro do PNG (offset Pixel Studio) */
    public static final int MENU_CROP_U = 1;
    public static final int MENU_CROP_V = 1;
    public static final int MENU_CROP_W = 964;
    public static final int MENU_CROP_H = 564;

    // ── UVs reais dos botões (com offset +1 do Pixel Studio) ──
    // formato: { u, v, w, h }
    public static final int[] UV_ARROW_LEFT  = {  1,   1, 20, 19 };
    public static final int[] UV_ARROW_RIGHT = { 22,   1, 20, 19 };
    public static final int[] UV_ARROW_UP    = { 44,   1, 19, 19 };
    public static final int[] UV_ARROW_DOWN  = { 64,   1, 19, 19 };
    public static final int[] UV_PLUS        = {  1,  23, 20, 19 };
    public static final int[] UV_X           = { 24,  23, 27, 25 };
    public static final int[] UV_REQUISITO   = {  1,  45, 16, 15 };
    public static final int[] UV_DELETE      = {  1,  83, 20, 19 };
    public static final int[] UV_CONFIRM     = {  1, 109, 28, 31 };

    // ── IDs dos menus ─────────────────────────────────────────
    public static final int MENU_CHARACTER = 0;
    // public static final int MENU_SKILLS = 1;
    // public static final int MENU_STATS  = 2;

    // ── Registry ──────────────────────────────────────────────
    private static final Map<Integer, MenuDefinition> REGISTRY = new HashMap<>();

    static {
        // ══════════════════════════════════════════════════════
        //  ID 0 — CharacterScreen (Aparência)
        //
        //  O menu.png tem 1024×1024 mas só usamos 964×564.
        //  Exibimos em 964×564 (escala 1:1 da região visível).
        //
        //  Posições na TELA são relativas ao canto superior
        //  esquerdo do menu exibido.
        //
        //  Layout:
        //   ┌─────────────────────────────────── 964px ──┐
        //   │ [Aparência]     [← RaçaNome →]             │ ← linha 14
        //   │ ┌──────────┐   [← Forma     →]             │ ← linha 52
        //   │ │ Preview  │   [← Cabelo    →]             │ ← linha 78
        //   │ │   3D     │      [■ cor cabelo]            │ ← linha 104
        //   │ │          │   [← Adulto    →]             │ ← linha 130
        //   │ └──────────┘   [← TipoCorpo →]             │ ← linha 156
        //   │                   [■][■][■] cores           │ ← linha 182
        //   │                [← Nariz N  →]              │ ← linha 208
        //   │                [← Boca  N  →]              │ ← linha 234
        //   │                [← Olhos N  →]              │ ← linha 260
        //   │                   [■][■] olhos              │ ← linha 286
        //   │                   [ Igualar ]               │ ← linha 312
        //   │ [X]                            [Confirmar]  │ ← linha 520
        //   └─────────────────────────────────────────────┘
        //
        //  Nota: botões de seta usam UV_ARROW_LEFT/RIGHT.
        //  Os swatches de cor são widgets dinâmicos (não entram aqui).
        // ══════════════════════════════════════════════════════

        int dW = MENU_CROP_W; // 964 — largura de exibição
        int dH = MENU_CROP_H; // 564 — altura de exibição

        // Coluna de opções começa após o preview (~36% da largura)
        int prevW  = dW * 36 / 100;   // ~347
        int opColX = prevW + 16;       // X inicial da coluna de opções
        int opColW = dW - opColX - 8; // largura da coluna de opções
        int arW    = UV_ARROW_LEFT[2]; // 20
        int arH    = UV_ARROW_LEFT[3]; // 19

        MenuDefinition character = new MenuDefinition(
            MENU_CHARACTER, MENU_BG,
            MENU_PNG_W, MENU_PNG_H,
            dW, dH
        );

        // ── Setas de raça (topo da coluna de opções) ──────────
        character.addButton(sprite("race_prev", "race_p", opColX, 10, arW, arH, UV_ARROW_LEFT));
        character.addButton(sprite("race_next", "race_n", opColX+opColW-arW, 10, arW, arH, UV_ARROW_RIGHT));

        // ── Setas de forma ─────────────────────────────────────
        character.addButton(sprite("form_prev", "form_p", opColX, 52, arW, arH, UV_ARROW_LEFT));
        character.addButton(sprite("form_next", "form_n", opColX+opColW-arW, 52, arW, arH, UV_ARROW_RIGHT));

        // ── Setas de cabelo ────────────────────────────────────
        character.addButton(sprite("hair_prev", "hair_p", opColX, 78, arW, arH, UV_ARROW_LEFT));
        character.addButton(sprite("hair_next", "hair_n", opColX+opColW-arW, 78, arW, arH, UV_ARROW_RIGHT));

        // ── Setas de idade ─────────────────────────────────────
        character.addButton(sprite("age_prev",  "age_p",  opColX, 130, arW, arH, UV_ARROW_LEFT));
        character.addButton(sprite("age_next",  "age_n",  opColX+opColW-arW, 130, arW, arH, UV_ARROW_RIGHT));

        // ── Setas de tipo de corpo ─────────────────────────────
        character.addButton(sprite("body_prev", "body_p", opColX, 156, arW, arH, UV_ARROW_LEFT));
        character.addButton(sprite("body_next", "body_n", opColX+opColW-arW, 156, arW, arH, UV_ARROW_RIGHT));

        // ── Setas de nariz ─────────────────────────────────────
        character.addButton(sprite("nose_prev", "nose_p", opColX, 208, arW, arH, UV_ARROW_LEFT));
        character.addButton(sprite("nose_next", "nose_n", opColX+opColW-arW, 208, arW, arH, UV_ARROW_RIGHT));

        // ── Setas de boca ──────────────────────────────────────
        character.addButton(sprite("mouth_prev","mouth_p",opColX, 234, arW, arH, UV_ARROW_LEFT));
        character.addButton(sprite("mouth_next","mouth_n",opColX+opColW-arW, 234, arW, arH, UV_ARROW_RIGHT));

        // ── Setas de olhos ─────────────────────────────────────
        character.addButton(sprite("eye_prev",  "eye_p",  opColX, 260, arW, arH, UV_ARROW_LEFT));
        character.addButton(sprite("eye_next",  "eye_n",  opColX+opColW-arW, 260, arW, arH, UV_ARROW_RIGHT));

        // ── Botão Confirmar (✅) ────────────────────────────────
        int cfW = UV_CONFIRM[2], cfH = UV_CONFIRM[3]; // 28×31
        character.addButton(sprite("confirm", "prox",
            dW - cfW - 8, dH - cfH - 8, cfW, cfH, UV_CONFIRM));

        // ── Botão X (fechar) ───────────────────────────────────
        int xW = UV_X[2], xH = UV_X[3]; // 27×25
        character.addButton(sprite("close", "close", 8, dH - xH - 8, xW, xH, UV_X));

        REGISTRY.put(MENU_CHARACTER, character);

        // ── Futuro: ID 1, 2... ────────────────────────────────
        // MenuDefinition skills = new MenuDefinition(1, MENU_BG, ...);
        // skills.addButton(sprite("delete_skill", "del_skill", ..., UV_DELETE));
        // skills.addButton(sprite("add_skill",    "add_skill", ..., UV_PLUS));
        // REGISTRY.put(1, skills);
    }

    // ================================================================
    //  API
    // ================================================================

    public static MenuDefinition get(int id) {
        MenuDefinition d = REGISTRY.get(id);
        if (d == null) throw new IllegalArgumentException("Menu ID não registrado: " + id);
        return d;
    }

    public static void open(int id) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;
        switch (id) {
            case MENU_CHARACTER -> mc.setScreen(new CharacterScreen());
            // case 1 -> mc.setScreen(new SkillsScreen());
            default -> throw new IllegalArgumentException("Sem screen para ID: " + id);
        }
    }

    public static boolean isRegistered(int id) { return REGISTRY.containsKey(id); }

    // ── Helper: cria ButtonDef sprite a partir de um array UV[] ──
    private static MenuDefinition.ButtonDef sprite(
            String id, String action,
            int sx, int sy, int sw, int sh,
            int[] uv) {
        return new MenuDefinition.ButtonDef(id, action, sx, sy, sw, sh,
            uv[0], uv[1], uv[2], uv[3]);
    }
}
EOF

# ================================================================
# 3. BaseMenuScreen.java
# ================================================================
cat << 'EOF' > $BASE/client/screen/BaseMenuScreen.java
package com.bernardo.dbi.client.screen;

import com.bernardo.dbi.client.menu.MenuDefinition;
import com.bernardo.dbi.client.menu.MenuManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * BaseMenuScreen — lê MenuDefinition e renderiza tudo automaticamente.
 *
 * Subclasses implementam:
 *  - handleAction(String action) → reage aos botões
 *  - renderExtra(...)            → labels, preview, picker
 *  - initExtra()                 → adiciona widgets dinâmicos (swatches, etc.)
 */
public abstract class BaseMenuScreen extends Screen {

    protected final MenuDefinition def;

    /** Posição do menu centralizado na tela */
    protected int menuX, menuY;

    public BaseMenuScreen(int menuId) {
        super(Component.empty());
        this.def = MenuManager.get(menuId);
    }

    // ================================================================
    //  INIT
    // ================================================================

    @Override
    protected void init() {
        super.init();
        menuX = (this.width  - def.displayW) / 2;
        menuY = (this.height - def.displayH) / 2;

        // Cria botões definidos no MenuManager
        for (MenuDefinition.ButtonDef b : def.buttons) {
            final String action = b.action;
            if (b.useSprite) {
                addRenderableWidget(new SpriteButton(
                    menuX + b.screenX, menuY + b.screenY,
                    b.screenW, b.screenH,
                    b.uvX, b.uvY, b.uvW, b.uvH,
                    btn -> handleAction(action)
                ));
            } else {
                addRenderableWidget(Button.builder(
                    Component.literal(b.label), btn -> handleAction(action))
                    .pos(menuX + b.screenX, menuY + b.screenY)
                    .size(b.screenW, b.screenH)
                    .build());
            }
        }

        initExtra();
    }

    protected void initExtra() {}

    // ================================================================
    //  RENDER
    // ================================================================

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);

        // Background do menu (recortado da região visível do PNG)
        g.blit(
            def.background,
            menuX, menuY,                    // posição na tela
            MenuManager.MENU_CROP_U,         // u de origem no PNG
            MenuManager.MENU_CROP_V,         // v de origem no PNG
            def.displayW, def.displayH,      // tamanho de exibição
            def.pngW, def.pngH               // tamanho total do PNG
        );

        renderExtra(g, mx, my, pt);
        super.render(g, mx, my, pt);
    }

    protected abstract void renderExtra(GuiGraphics g, int mx, int my, float pt);
    protected abstract void handleAction(String action);

    // ================================================================
    //  SpriteButton — renderiza recorte do icons_btn.png
    // ================================================================

    protected static class SpriteButton extends Button {
        private final int uvX, uvY, uvW, uvH;

        public SpriteButton(int x, int y, int w, int h,
                            int uvX, int uvY, int uvW, int uvH,
                            OnPress onPress) {
            super(x, y, w, h, Component.empty(), onPress, DEFAULT_NARRATION);
            this.uvX = uvX; this.uvY = uvY;
            this.uvW = uvW; this.uvH = uvH;
        }

        @Override
        public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
            // Highlight no hover
            if (isHovered()) {
                g.fill(getX()-1, getY()-1,
                       getX()+width+1, getY()+height+1, 0x55FFFFFF);
            }
            // Recorte do spritesheet
            g.blit(
                MenuManager.ICONS_BTN,
                getX(), getY(),
                uvX, uvY,
                uvW, uvH,
                MenuManager.SHEET_W, MenuManager.SHEET_H
            );
        }
    }
}
EOF

# ================================================================
# 4. CharacterScreen.java — só posiciona, tudo vem do MenuManager
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

/**
 * CharacterScreen — tela de aparência.
 *
 * NÃO define PNGs, tamanhos, nem UVs.
 * Tudo isso está no MenuManager (ID 0).
 *
 * Esta classe só:
 *  - Mantém estado (raça, forma, cores...)
 *  - handleAction() → reage aos botões do MenuManager
 *  - renderExtra()  → labels, preview 3D, swatches, picker
 */
public class CharacterScreen extends BaseMenuScreen {

    // ─── Raças ──────────────────────────────────────────────────
    private static final String[][] RACE_DEFS = {
        //  texId          nome           forma   cabelo  maxBody maxNose maxMouth maxEye  body        hair        eyes
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

    // ─── Color picker ────────────────────────────────────────────
    private boolean showPicker=false;
    private String  pickerTarget=null;
    private static final int PW=180, PH=110, SW=14;
    private int pkX, pkY, slX, slY;
    private float ph=0, ps=1, pv=1;

    // ─── Layout da coluna de opções ──────────────────────────────
    private int opColX, opColW, opMidX;

    // ============================================================
    //  CTOR
    // ============================================================

    public CharacterScreen() { super(MenuManager.MENU_CHARACTER); }

    // ============================================================
    //  initExtra
    // ============================================================

    @Override
    protected void initExtra() {
        initColors();

        // Área de preview (esquerda do menu)
        prW = def.displayW * 36 / 100;
        prH = def.displayH - 56;
        prX = menuX + 8;
        prY = menuY + 47;

        // Coluna de opções (direita)
        opColX = menuX + prW + 24;
        opColW = menuX + def.displayW - opColX - 8;
        opMidX = opColX + opColW / 2;

        addSwatches();
    }

    private void initColors() {
        String[] r = RACE_DEFS[raceIdx];
        bodyColor = hex(r[8]); hairColor = hex(r[9]); eyeColor = hex(r[10]);
    }

    private void addSwatches() {
        int sw=32, sh=22;

        // Swatches do cabelo (linha 104)
        if ("true".equals(RACE_DEFS[raceIdx][3])) {
            addSwatch(opMidX-50, menuY+104, 100, 18, hairColor, "hair");
        }

        // Swatches do corpo (linha 182)
        int stot=sw*3+8, ssx=opColX+(opColW-stot)/2;
        addSwatch(ssx,          menuY+182, sw, sh, bodyColor,             "body");
        addSwatch(ssx+sw+4,     menuY+182, sw, sh, lighter(bodyColor,.15f),"body_l");
        addSwatch(ssx+sw*2+8,   menuY+182, sw, sh, darker (bodyColor,.20f),"body_d");

        // Swatches dos olhos (linha 286)
        int ew=48, etot=ew*2+6, esx=opColX+(opColW-etot)/2;
        addSwatch(esx,          menuY+286, ew, sh, eyeColor,              "eyes");
        addSwatch(esx+ew+6,     menuY+286, ew, sh, darker(eyeColor,.30f), "eyes_d");

        // Botão Igualar (linha 312) — texto pois não tem sprite específico
        int[] uvPlus = MenuManager.UV_PLUS;
        addRenderableWidget(Button.builder(
            Component.literal("Igualar"), b -> handleAction("equal"))
            .pos(opMidX-55, menuY+312).size(110, 24).build());
    }

    private void addSwatch(int x, int y, int w, int h, int color, String tgt) {
        addRenderableWidget(new SwatchBtn(x, y, w, h, color, tgt,
            b -> openPicker(((SwatchBtn)b).tgt)));
    }

    // ============================================================
    //  handleAction
    // ============================================================

    @Override
    protected void handleAction(String action) {
        String[] r    = RACE_DEFS[raceIdx];
        int maxBody   = Integer.parseInt(r[4]);
        int maxNose   = Integer.parseInt(r[5]);
        int maxMouth  = Integer.parseInt(r[6]);
        int maxEye    = Integer.parseInt(r[7]);
        switch (action) {
            case "race_p"  -> { raceIdx=(raceIdx-1+RACE_DEFS.length)%RACE_DEFS.length; resetRace(); return; }
            case "race_n"  -> { raceIdx=(raceIdx+1)%RACE_DEFS.length;                  resetRace(); return; }
            case "form_p"  -> { if (RACE_FORMS[raceIdx].length>0) formIdx=Math.max(0,formIdx-1); }
            case "form_n"  -> { if (RACE_FORMS[raceIdx].length>0) formIdx=Math.min(RACE_FORMS[raceIdx].length-1,formIdx+1); }
            case "hair_p"  -> { hairStyle=Math.max(0,hairStyle-1); }
            case "hair_n"  -> { hairStyle=Math.min(HAIR_STYLES-1,hairStyle+1); }
            case "age_p"   -> { ageIdx=Math.max(0,ageIdx-1); }
            case "age_n"   -> { ageIdx=Math.min(1,ageIdx+1); }
            case "body_p"  -> { bodyTypeIdx=Math.max(0,bodyTypeIdx-1); rebuild(); return; }
            case "body_n"  -> { bodyTypeIdx=Math.min(maxBody-1,bodyTypeIdx+1); rebuild(); return; }
            case "nose_p"  -> { noseIdx=Math.max(0,noseIdx-1); }
            case "nose_n"  -> { noseIdx=Math.min(maxNose-1,noseIdx+1); }
            case "mouth_p" -> { mouthIdx=Math.max(0,mouthIdx-1); }
            case "mouth_n" -> { mouthIdx=Math.min(maxMouth-1,mouthIdx+1); }
            case "eye_p"   -> { eyeIdx=Math.max(0,eyeIdx-1); }
            case "eye_n"   -> { eyeIdx=Math.min(maxEye-1,eyeIdx+1); }
            case "equal"   -> { eyeColor=bodyColor; hairColor=bodyColor; rebuild(); return; }
            case "close"   -> { onClose(); return; }
            case "prox"    -> { save(); return; }
        }
    }

    private void resetRace() {
        formIdx=hairStyle=ageIdx=bodyTypeIdx=noseIdx=mouthIdx=eyeIdx=0;
        initColors(); rebuild();
    }

    /** Reinicia apenas os widgets dinâmicos (swatches) sem recriar botões do MenuManager. */
    private void rebuild() { init(); }

    private void save() {
        if (minecraft==null||minecraft.player==null) { onClose(); return; }
        String[] r = RACE_DEFS[raceIdx];
        var nbt = minecraft.player.getPersistentData();
        nbt.putString("dbi:race",        r[0]);
        nbt.putString("dbi:raceDisplay", r[1]);
        nbt.putInt   ("dbi:formIdx",      formIdx);
        nbt.putInt   ("dbi:hairStyle",    hairStyle);
        nbt.putInt   ("dbi:ageIdx",       ageIdx);
        nbt.putInt   ("dbi:bodyTypeIdx",  bodyTypeIdx);
        nbt.putInt   ("dbi:noseIdx",      noseIdx);
        nbt.putInt   ("dbi:mouthIdx",     mouthIdx);
        nbt.putInt   ("dbi:eyeIdx",       eyeIdx);
        nbt.putInt   ("dbi:bodyColor",    bodyColor);
        nbt.putInt   ("dbi:hairColor",    hairColor);
        nbt.putInt   ("dbi:eyeColor",     eyeColor);
        ModNetwork.sendToServer(new RaceSelectionPacket(
            r[0], formIdx, hairStyle, ageIdx, bodyTypeIdx,
            noseIdx, mouthIdx, eyeIdx, bodyColor, hairColor, eyeColor
        ));
        onClose();
    }

    // ============================================================
    //  renderExtra
    // ============================================================

    @Override
    protected void renderExtra(GuiGraphics g, int mx, int my, float pt) {
        String[] r     = RACE_DEFS[raceIdx];
        boolean forma  = "true".equals(r[2]);
        boolean cabelo = "true".equals(r[3]);

        // Nome da raça (topo da coluna de opções)
        g.drawCenteredString(font, r[1], opMidX, menuY+14, 0xFFFFFFFF);

        // Preview 3D
        drawPreview3D(g);
        g.drawCenteredString(font, "§7← arraste →", prX+prW/2, prY+prH-10, 0xFFAAAAAA);

        // Labels linha por linha
        if (forma && RACE_FORMS[raceIdx].length>0)
            g.drawCenteredString(font, "Forma: "+RACE_FORMS[raceIdx][Math.min(formIdx,RACE_FORMS[raceIdx].length-1)], opMidX, menuY+56, 0xFFFFFFFF);

        if (cabelo)
            g.drawCenteredString(font, "Cabelo Custom "+(hairStyle+1), opMidX, menuY+82, 0xFFFFFFFF);

        g.drawCenteredString(font, ageIdx==0?"Adulto":"Criança",          opMidX, menuY+134, 0xFFFFFFFF);
        g.drawCenteredString(font, "Skin Custom",                          opMidX, menuY+160, 0xFFFFFFFF);
        g.drawCenteredString(font, "Tipo de Corpo "+(bodyTypeIdx+1),       opMidX, menuY+170, 0xFFFFFFFF);
        g.drawCenteredString(font, "Nariz  "+(noseIdx+1),                  opMidX, menuY+212, 0xFFFFFFFF);
        g.drawCenteredString(font, "Boca   "+(mouthIdx+1),                 opMidX, menuY+238, 0xFFFFFFFF);
        g.drawCenteredString(font, "Olhos  "+(eyeIdx+1),                   opMidX, menuY+264, 0xFFFFFFFF);

        // Color picker por cima de tudo
        if (showPicker) drawPicker(g);
    }

    // ── Preview 3D ───────────────────────────────────────────────

    private void drawPreview3D(GuiGraphics g) {
        if (minecraft==null||minecraft.player==null) return;

        // Scissor — limita render à área de preview
        double sc = minecraft.getWindow().getGuiScale();
        int winH  = minecraft.getWindow().getHeight();
        com.mojang.blaze3d.platform.GlStateManager._enableScissorTest();
        com.mojang.blaze3d.platform.GlStateManager._scissorBox(
            (int)(prX*sc), (int)(winH-(prY+prH)*sc),
            (int)(prW*sc), (int)(prH*sc)
        );

        InventoryScreen.renderEntityInInventoryFollowsMouse(
            g,
            prX + prW/2, prY + prH - 20,
            prH / 3,
            (float)(prX+prW/2) - (prX+prW/2 + previewYaw  * 2f),
            (float)(prY+prH/2) - (prY+prH/2 + previewPitch * 2f),
            minecraft.player
        );

        com.mojang.blaze3d.platform.GlStateManager._disableScissorTest();
    }

    // ── Arrasto do mouse ────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (showPicker) {
            int x=(int)mx, y=(int)my;
            if (x>=pkX&&x<pkX+PW&&y>=pkY&&y<pkY+PH) {
                ph=(x-pkX)/(float)(PW-1)*360f; ps=1f-(y-pkY)/(float)(PH-1);
                applyPick(hsv2rgb(ph,ps,pv)); return true;
            }
            if (x>=slX&&x<slX+SW&&y>=slY&&y<slY+PH) {
                pv=1f-(y-slY)/(float)(PH-1); applyPick(hsv2rgb(ph,ps,pv)); return true;
            }
            showPicker=false; pickerTarget=null; return true;
        }
        if (mx>=prX&&mx<prX+prW&&my>=prY&&my<prY+prH) {
            dragging=true; lastDragX=mx; lastDragY=my; return true;
        }
        return super.mouseClicked(mx,my,btn);
    }

    @Override
    public boolean mouseReleased(double mx,double my,int btn) { dragging=false; return super.mouseReleased(mx,my,btn); }

    @Override
    public boolean mouseDragged(double mx,double my,int btn,double dx,double dy) {
        if (dragging) {
            previewYaw  =clamp(previewYaw  +(float)(mx-lastDragX)*0.8f,-MAX_YAW,  MAX_YAW);
            previewPitch=clamp(previewPitch+(float)(my-lastDragY)*0.4f,-MAX_PITCH,MAX_PITCH);
            lastDragX=mx; lastDragY=my; return true;
        }
        return super.mouseDragged(mx,my,btn,dx,dy);
    }

    // ── Color picker ─────────────────────────────────────────────

    private void openPicker(String tgt) {
        pickerTarget=tgt; showPicker=true;
        pkX=width/2-(PW+SW+10)/2; pkY=height/2-PH/2;
        slX=pkX+PW+6; slY=pkY;
        float[] hsv=rgb2hsv(pickerCol());
        ph=hsv[0]; ps=hsv[1]; pv=hsv[2];
    }
    private int pickerCol() {
        if ("hair".equals(pickerTarget)) return hairColor;
        if (pickerTarget!=null&&pickerTarget.startsWith("eye")) return eyeColor;
        return bodyColor;
    }
    private void applyPick(int rgb) {
        int c=(0xFF<<24)|(rgb&0xFFFFFF);
        if ("hair".equals(pickerTarget))                             hairColor=c;
        else if (pickerTarget!=null&&pickerTarget.startsWith("eye")) eyeColor=c;
        else                                                         bodyColor=c;
        rebuild();
    }
    private void drawPicker(GuiGraphics g) {
        g.fill(pkX-4,pkY-4,pkX+PW+SW+28,pkY+PH+4,0xFF222222);
        for (int i=0;i<PW;i++) for (int j=0;j<PH;j++)
            g.fill(pkX+i,pkY+j,pkX+i+1,pkY+j+1,(0xFF<<24)|hsv2rgb(i/(float)(PW-1)*360f,1f-(j/(float)(PH-1)),1f));
        for (int j=0;j<PH;j++)
            g.fill(slX,slY+j,slX+SW,slY+j+1,(0xFF<<24)|hsv2rgb(ph,1f,1f-(j/(float)(PH-1))));
        int cx=pkX+(int)(ph/360f*(PW-1)), cy=pkY+(int)((1f-ps)*(PH-1));
        g.fill(cx-3,cy-1,cx+3,cy+1,0xFFFFFFFF); g.fill(cx-1,cy-3,cx+1,cy+3,0xFFFFFFFF);
        int cur=pickerCol();
        g.fill(slX+SW+6,pkY,slX+SW+28,pkY+22,(0xFF<<24)|(cur&0xFFFFFF));
        g.drawString(font,String.format("#%06X",cur&0xFFFFFF),slX+SW+6,pkY+26,0xFFAAAAAA,false);
    }

    // ── Utils ────────────────────────────────────────────────────

    private float clamp(float v,float mn,float mx){return Math.max(mn,Math.min(mx,v));}
    private int lighter(int c,float a){
        return(0xFF<<24)|(Math.min(255,(int)(((c>>16)&0xFF)+255*a))<<16)|(Math.min(255,(int)(((c>>8)&0xFF)+255*a))<<8)|Math.min(255,(int)((c&0xFF)+255*a));
    }
    private int darker(int c,float a){
        return(0xFF<<24)|(Math.max(0,(int)(((c>>16)&0xFF)-255*a))<<16)|(Math.max(0,(int)(((c>>8)&0xFF)-255*a))<<8)|Math.max(0,(int)((c&0xFF)-255*a));
    }
    private int hex(String s){return(int)Long.parseLong(s.replace("0x",""),16);}
    private int hsv2rgb(float h,float s,float v){
        int hi=(int)(h/60f)%6;float f=h/60f-(int)(h/60f),p=v*(1-s),q=v*(1-f*s),t=v*(1-(1-f)*s);
        float r=0,g=0,b=0;
        switch(hi){case 0->{r=v;g=t;b=p;}case 1->{r=q;g=v;b=p;}case 2->{r=p;g=v;b=t;}
                   case 3->{r=p;g=q;b=v;}case 4->{r=t;g=p;b=v;}case 5->{r=v;g=p;b=q;}}
        return(Math.round(r*255)<<16)|(Math.round(g*255)<<8)|Math.round(b*255);
    }
    private float[] rgb2hsv(int rgb){
        float r=((rgb>>16)&0xFF)/255f,g=((rgb>>8)&0xFF)/255f,b=(rgb&0xFF)/255f;
        float mx=Math.max(r,Math.max(g,b)),mn=Math.min(r,Math.min(g,b)),d=mx-mn,h=0;
        if(d!=0){if(mx==r)h=60f*((g-b)/d%6f);else if(mx==g)h=60f*((b-r)/d+2f);else h=60f*((r-g)/d+4f);}
        if(h<0)h+=360f;
        return new float[]{h,mx==0?0:d/mx,mx};
    }

    // ── SwatchBtn ────────────────────────────────────────────────

    private static class SwatchBtn extends Button {
        final String tgt; int color;
        SwatchBtn(int x,int y,int w,int h,int color,String tgt,OnPress p){
            super(x,y,w,h,Component.empty(),p,DEFAULT_NARRATION);
            this.color=color;this.tgt=tgt;
        }
        @Override public void renderWidget(GuiGraphics g,int mx,int my,float pt){
            g.fill(getX()-1,getY()-1,getX()+width+1,getY()+height+1,isHovered()?0xFFFFFFFF:0xFF888888);
            g.fill(getX(),getY(),getX()+width,getY()+height,(0xFF<<24)|(color&0xFFFFFF));
        }
    }
}
EOF

# ================================================================
# 5. ClientInputEvents.java
# ================================================================
cat << 'EOF' > $BASE/client/ClientInputEvents.java
package com.bernardo.dbi.client;

import com.bernardo.dbi.Dbi;
import com.bernardo.dbi.client.menu.MenuManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;

@Mod.EventBusSubscriber(modid = Dbi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientInputEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        // Tecla C → Menu ID 0 (CharacterScreen)
        if (ClientEvents.CHARACTER_SCREEN_KEY.consumeClick()) {
            MenuManager.open(MenuManager.MENU_CHARACTER);
        }
        // Futuro:
        // if (ClientEvents.SKILLS_KEY.consumeClick()) MenuManager.open(1);
        // if (ClientEvents.STATS_KEY.consumeClick())  MenuManager.open(2);
    }
}
EOF

echo ""
echo "✅ Pronto! Arquivos gerados com coordenadas REAIS:"
echo ""
echo "  MenuDefinition.java    — estrutura de dados"
echo "  MenuManager.java       — UVs reais do icons_btn.png + menu.png"
echo "  BaseMenuScreen.java    — renderiza automaticamente pelo MenuManager"
echo "  CharacterScreen.java   — só posiciona e reage"
echo "  ClientInputEvents.java — MenuManager.open(ID)"

package com.bernardo.dbi.client.screen;

import com.bernardo.dbi.Dbi;
import com.bernardo.dbi.network.ModNetwork;
import com.bernardo.dbi.network.packet.RaceSelectionPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * CharacterScreen — tela completa de aparência.
 *
 * Integrado com:
 *  - Raças reais do RaceRegistry (Sayajin, Namekian, Arconsian, Humano, Half_Sayajin)
 *  - Texturas reais: textures/entity/race/<id>.png
 *  - Cabelos reais: textures/entity/customizacao/hair/hair_base<N>.png
 *  - Envia RaceSelectionPacket ao servidor ao confirmar
 *  - Salva no NBT do player (dbi:race, dbi:bodyColor, etc.)
 *
 * UI:
 *  [Aparência]         [<< RaçaNome >>]
 *  ┌──────────┐        [<< Forma      >>]   (se raça tiver)
 *  │ Preview  │        [<< Cabelo     >>]   (se raça tiver)
 *  │ 2D       │           [■ cor cabelo]
 *  │          │        [<< Adulto     >>]
 *  └──────────┘        [<< Tipo Corpo >>]
 *                         [■][■][■] cores
 *                      [<< Nariz N    >>]
 *                      [<< Boca  N    >>]
 *                      [<< Olhos N    >>]
 *                         [■][■] cores olhos
 *                         [ Igualar   ]
 *  [X]                              [Próx]
 */
public class CharacterScreen extends Screen {

    // =========================================================
    //  RAÇAS — espelha Race.RaceType do projeto
    // =========================================================

    private static final String[][] RACE_DEFS = {
        // { id_textura, nome_exibido, tem_forma, tem_cabelo, max_body, max_nose, max_mouth, max_eyes, hex_body, hex_hair, hex_eyes }
        { "sayajin",      "Sayajin",      "true",  "true",  "3","3","3","3","0xFFD2956E","0xFF1A1A1A","0xFF1A1A1A" },
        { "namekian",     "Namekian",     "false", "false", "2","2","2","2","0xFF3CB371","0xFF000000","0xFF000000" },
        { "arconsian",    "Arcosian",     "true",  "false", "2","2","2","2","0xFFFFF0F5","0xFF000000","0xFFCC0000" },
        { "humano",       "Humano",       "true",  "true",  "3","3","3","3","0xFFD2956E","0xFF7B4F2E","0xFF5B3A29" },
        { "halfsayajin",  "Half-Sayajin", "true",  "true",  "3","3","3","3","0xFFD2956E","0xFF1A1A1A","0xFF1A1A1A" },
    };

    // Formas por raça (índice igual ao de RACE_DEFS)
    private static final String[][] RACE_FORMS = {
        { "Normal" },                                                              // Sayajin
        { },                                                                       // Namekian
        { "Forma Minima","Forma 1","Forma 2","Forma 3","Forma Final" },           // Arcosian
        { "Normal" },                                                              // Humano
        { "Normal" },                                                              // Half-Sayajin
    };

    // Estilos de cabelo disponíveis (hair_base1..6.png)
    private static final int HAIR_STYLES = 6;

    // =========================================================
    //  ESTADO
    // =========================================================

    private int raceIdx      = 0;
    private int formIdx      = 0;
    private int hairStyle    = 0;  // 0..HAIR_STYLES-1
    private int ageIdx       = 0;  // 0=Adulto 1=Criança
    private int bodyTypeIdx  = 0;
    private int noseIdx      = 0;
    private int mouthIdx     = 0;
    private int eyeIdx       = 0;

    private int bodyColor;
    private int hairColor;
    private int eyeColor;

    // Color picker
    private boolean showPicker  = false;
    private String  pickerTarget = null;
    private static final int PW = 180, PH = 110, SW = 14;
    private int px, py, sx, sy;
    private float ph = 0, ps = 1, pv = 1;

    // Layout
    private int panX, panY, panW, panH;
    private int prX, prY, prW, prH;
    private int opX, opW;

    // =========================================================
    //  CTOR
    // =========================================================

    public CharacterScreen() {
        super(Component.literal("Aparência"));
    }

    // =========================================================
    //  INIT
    // =========================================================

    @Override
    protected void init() {
        super.init();
        panW = Math.min(660, width  - 40);
        panH = Math.min(420, height - 40);
        panX = (width  - panW) / 2;
        panY = (height - panH) / 2;
        prW  = panW * 36 / 100;
        prH  = panH - 56;
        prX  = panX + 8;
        prY  = panY + 47;
        opX  = prX + prW + 16;
        opW  = panX + panW - opX - 8;
        initColors();
        build();
    }

    // =========================================================
    //  BUILD DOS WIDGETS
    // =========================================================

    private void build() {
        clearWidgets();
        String[] r  = RACE_DEFS[raceIdx];
        boolean forma  = "true".equals(r[2]);
        boolean cabelo = "true".equals(r[3]);
        int maxBody = Integer.parseInt(r[4]);
        int maxNose = Integer.parseInt(r[5]);
        int maxMouth= Integer.parseInt(r[6]);
        int maxEye  = Integer.parseInt(r[7]);
        int aw = 22, ah = 18, gap = 26;
        int cy = panY + 52;

        // Raça
        arrow(opX,          panY+10, aw, ah, "<<", "race_p");
        arrow(opX+opW-aw,   panY+10, aw, ah, ">>", "race_n");

        // Forma
        if (forma && RACE_FORMS[raceIdx].length > 0) {
            arrow(opX,        cy, aw, ah, "<<", "form_p"); arrow(opX+opW-aw, cy, aw, ah, ">>", "form_n"); cy += gap;
        }

        // Cabelo
        if (cabelo) {
            arrow(opX,        cy, aw, ah, "<<", "hair_p"); arrow(opX+opW-aw, cy, aw, ah, ">>", "hair_n"); cy += gap;
            swatch(opX+(opW-100)/2, cy, 100, 18, hairColor, "hair"); cy += gap;
        }

        // Adulto/Criança
        arrow(opX,        cy, aw, ah, "<<", "age_p"); arrow(opX+opW-aw, cy, aw, ah, ">>", "age_n"); cy += gap;

        // Tipo de Corpo
        arrow(opX,        cy, aw, ah, "<<", "body_p"); arrow(opX+opW-aw, cy, aw, ah, ">>", "body_n"); cy += gap;

        // Swatches corpo
        int sw=32,sh=22,stot=sw*3+8,ssx=opX+(opW-stot)/2;
        swatch(ssx,       cy, sw, sh, bodyColor,           "body");
        swatch(ssx+sw+4,  cy, sw, sh, lighter(bodyColor,.15f), "body_l");
        swatch(ssx+sw*2+8,cy, sw, sh, darker (bodyColor,.20f), "body_d");
        cy += sh+6;

        // Nariz
        arrow(opX,        cy, aw, ah, "<<", "nose_p"); arrow(opX+opW-aw, cy, aw, ah, ">>", "nose_n"); cy += gap;
        // Boca
        arrow(opX,        cy, aw, ah, "<<", "mouth_p"); arrow(opX+opW-aw, cy, aw, ah, ">>", "mouth_n"); cy += gap;
        // Olhos
        arrow(opX,        cy, aw, ah, "<<", "eye_p");  arrow(opX+opW-aw, cy, aw, ah, ">>", "eye_n");  cy += gap;

        // Swatches olhos
        int ew=48,etot=ew*2+6,esx=opX+(opW-etot)/2;
        swatch(esx,     cy, ew, sh, eyeColor,              "eyes");
        swatch(esx+ew+6,cy, ew, sh, darker(eyeColor,.30f), "eyes_d");
        cy += sh+8;

        // Igualar
        btn(opX+(opW-110)/2, cy, 110, 24, "Igualar", "equal");

        // X / Próx
        btn(panX+8,           panY+panH-34, 32, 26, "X",    "close");
        btn(panX+panW-76,     panY+panH-34, 68, 26, "Próx", "prox");
    }

    // ── helpers ──────────────────────────────────────────────

    private void arrow(int x,int y,int w,int h,String lbl,String id) {
        addRenderableWidget(Button.builder(Component.literal(lbl), b->act(id)).pos(x,y).size(w,h).build());
    }
    private void btn(int x,int y,int w,int h,String lbl,String id) {
        addRenderableWidget(Button.builder(Component.literal(lbl), b->act(id)).pos(x,y).size(w,h).build());
    }
    private void swatch(int x,int y,int w,int h,int col,String tgt) {
        addRenderableWidget(new SwatchBtn(x,y,w,h,col,tgt, b->openPicker(((SwatchBtn)b).tgt)));
    }

    // =========================================================
    //  AÇÕES
    // =========================================================

    private void act(String id) {
        String[] r = RACE_DEFS[raceIdx];
        int maxBody = Integer.parseInt(r[4]);
        int maxNose = Integer.parseInt(r[5]);
        int maxMouth= Integer.parseInt(r[6]);
        int maxEye  = Integer.parseInt(r[7]);
        switch (id) {
            case "race_p"  -> { raceIdx=(raceIdx-1+RACE_DEFS.length)%RACE_DEFS.length; resetRace(); return; }
            case "race_n"  -> { raceIdx=(raceIdx+1)%RACE_DEFS.length;                  resetRace(); return; }
            case "form_p"  -> { if (RACE_FORMS[raceIdx].length>0) formIdx=Math.max(0,formIdx-1); }
            case "form_n"  -> { if (RACE_FORMS[raceIdx].length>0) formIdx=Math.min(RACE_FORMS[raceIdx].length-1,formIdx+1); }
            case "hair_p"  -> { hairStyle=Math.max(0,hairStyle-1); }
            case "hair_n"  -> { hairStyle=Math.min(HAIR_STYLES-1,hairStyle+1); }
            case "age_p"   -> { ageIdx=Math.max(0,ageIdx-1); }
            case "age_n"   -> { ageIdx=Math.min(1,ageIdx+1); }
            case "body_p"  -> { bodyTypeIdx=Math.max(0,bodyTypeIdx-1); build(); return; }
            case "body_n"  -> { bodyTypeIdx=Math.min(maxBody-1,bodyTypeIdx+1); build(); return; }
            case "nose_p"  -> { noseIdx=Math.max(0,noseIdx-1); }
            case "nose_n"  -> { noseIdx=Math.min(maxNose-1,noseIdx+1); }
            case "mouth_p" -> { mouthIdx=Math.max(0,mouthIdx-1); }
            case "mouth_n" -> { mouthIdx=Math.min(maxMouth-1,mouthIdx+1); }
            case "eye_p"   -> { eyeIdx=Math.max(0,eyeIdx-1); }
            case "eye_n"   -> { eyeIdx=Math.min(maxEye-1,eyeIdx+1); }
            case "equal"   -> { eyeColor=bodyColor; hairColor=bodyColor; build(); return; }
            case "close"   -> { onClose(); return; }
            case "prox"    -> { save(); return; }
        }
    }

    private void resetRace() { formIdx=hairStyle=ageIdx=bodyTypeIdx=noseIdx=mouthIdx=eyeIdx=0; initColors(); build(); }

    private void initColors() {
        String[] r = RACE_DEFS[raceIdx];
        bodyColor = (int)Long.parseLong(r[8].replace("0x",""),16);
        hairColor = (int)Long.parseLong(r[9].replace("0x",""),16);
        eyeColor  = (int)Long.parseLong(r[10].replace("0x",""),16);
    }

    private void save() {
        if (minecraft==null||minecraft.player==null) { onClose(); return; }
        var nbt = minecraft.player.getPersistentData();
        String[] r = RACE_DEFS[raceIdx];
        nbt.putString("dbi:race",         r[0]);
        nbt.putString("dbi:raceDisplay",  r[1]);
        nbt.putInt   ("dbi:formIdx",       formIdx);
        nbt.putInt   ("dbi:hairStyle",     hairStyle);
        nbt.putInt   ("dbi:ageIdx",        ageIdx);
        nbt.putInt   ("dbi:bodyTypeIdx",   bodyTypeIdx);
        nbt.putInt   ("dbi:noseIdx",       noseIdx);
        nbt.putInt   ("dbi:mouthIdx",      mouthIdx);
        nbt.putInt   ("dbi:eyeIdx",        eyeIdx);
        nbt.putInt   ("dbi:bodyColor",     bodyColor);
        nbt.putInt   ("dbi:hairColor",     hairColor);
        nbt.putInt   ("dbi:eyeColor",      eyeColor);
        // Envia ao servidor
        ModNetwork.sendToServer(new RaceSelectionPacket(
            r[0], formIdx, hairStyle, ageIdx, bodyTypeIdx,
            noseIdx, mouthIdx, eyeIdx, bodyColor, hairColor, eyeColor
        ));
        onClose();
    }

    // =========================================================
    //  COLOR PICKER
    // =========================================================

    private void openPicker(String tgt) {
        pickerTarget=tgt; showPicker=true;
        px=width/2-(PW+SW+10)/2; py=height/2-PH/2;
        sx=px+PW+6; sy=py;
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
        if ("hair".equals(pickerTarget))                         hairColor=c;
        else if (pickerTarget!=null&&pickerTarget.startsWith("eye")) eyeColor=c;
        else                                                     bodyColor=c;
        build();
    }

    @Override
    public boolean mouseClicked(double mx,double my,int btn) {
        if (showPicker) {
            int x=(int)mx,y=(int)my;
            if (x>=px&&x<px+PW&&y>=py&&y<py+PH) {
                ph=(x-px)/(float)(PW-1)*360f; ps=1f-(y-py)/(float)(PH-1);
                applyPick(hsv2rgb(ph,ps,pv)); return true;
            }
            if (x>=sx&&x<sx+SW&&y>=sy&&y<sy+PH) {
                pv=1f-(y-sy)/(float)(PH-1); applyPick(hsv2rgb(ph,ps,pv)); return true;
            }
            showPicker=false; pickerTarget=null; return true;
        }
        return super.mouseClicked(mx,my,btn);
    }

    // =========================================================
    //  RENDER
    // =========================================================

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);

        // Painel
        g.fill(panX,panY,panX+panW,panY+panH,0xEE111111);
        border(g,panX,panY,panW,panH,0xFF444444);

        // Títulos
        String[] r=RACE_DEFS[raceIdx];
        g.drawString(font,"Aparência",panX+12,panY+14,0xFFFFFFFF,false);
        int midX=opX+opW/2;
        g.drawCenteredString(font,r[1],midX,panY+14,0xFFFFFFFF);

        // Preview
        g.fill(prX,prY,prX+prW,prY+prH,0xFF1A1A1A);
        border(g,prX,prY,prW,prH,0xFF555555);
        drawPreview(g);

        // Separador
        g.fill(opX-8,panY+46,opX-7,panY+panH-38,0xFF444444);

        // Labels das opções
        drawLabels(g,r,midX);

        // Widgets
        super.render(g,mx,my,pt);

        // Picker
        if (showPicker) drawPicker(g);
    }

    private void drawLabels(GuiGraphics g, String[] r, int midX) {
        boolean forma  ="true".equals(r[2]);
        boolean cabelo ="true".equals(r[3]);
        int cy=panY+52, gap=26;

        if (forma&&RACE_FORMS[raceIdx].length>0) {
            int fi=Math.min(formIdx,RACE_FORMS[raceIdx].length-1);
            g.drawCenteredString(font,"Forma: "+RACE_FORMS[raceIdx][fi],midX,cy+4,0xFFFFFFFF); cy+=gap;
        }
        if (cabelo) {
            g.drawCenteredString(font,"Cabelo Custom "+(hairStyle+1),midX,cy+4,0xFFFFFFFF); cy+=gap;
            cy+=gap; // linha swatch
        }
        g.drawCenteredString(font,ageIdx==0?"Adulto":"Criança",midX,cy+4,0xFFFFFFFF); cy+=gap;
        g.drawCenteredString(font,"Skin Custom",midX,cy-2,0xFFFFFFFF);
        g.drawCenteredString(font,"Tipo de Corpo "+(bodyTypeIdx+1),midX,cy+8,0xFFFFFFFF); cy+=gap;
        cy+=28; // swatches corpo
        g.drawCenteredString(font,"Nariz  "+(noseIdx+1),midX,cy+4,0xFFFFFFFF); cy+=gap;
        g.drawCenteredString(font,"Boca   "+(mouthIdx+1),midX,cy+4,0xFFFFFFFF); cy+=gap;
        g.drawCenteredString(font,"Olhos  "+(eyeIdx+1),midX,cy+4,0xFFFFFFFF);
    }

    /** Preview 2D com silhueta colorida + textura da raça se disponível */
    private void drawPreview(GuiGraphics g) {
        // Tenta carregar textura da raça
        String raceId = RACE_DEFS[raceIdx][0];
        ResourceLocation raceTex = new ResourceLocation(Dbi.MOD_ID,
            "textures/entity/race/"+raceId+".png");
        boolean hasRaceTex = false;
        try {
            minecraft.getResourceManager().getResource(raceTex);
            hasRaceTex = true;
        } catch (Exception ignored) {}

        int cx=prX+prW/2, cy=prY+prH/2;
        if (hasRaceTex) {
            // Desenha textura plana da raça (skin 2D)
            g.blit(raceTex, cx-16, cy-32, 8, 8, 8, 8, 64, 64); // cabeça
            g.blit(raceTex, cx-12, cy-24, 20, 20, 8, 12, 64, 64); // corpo
        } else {
            // Silhueta de fallback com as cores escolhidas
            int bc=(0xFF<<24)|(bodyColor&0xFFFFFF);
            int ec=(0xFF<<24)|(eyeColor &0xFFFFFF);
            int hc=(0xFF<<24)|(hairColor&0xFFFFFF);
            g.fill(cx-13,cy-55,cx+13,cy-29,bc); // cabeça
            g.fill(cx-10,cy-29,cx+10,cy+9 ,bc); // corpo
            g.fill(cx-10,cy+9 ,cx-2 ,cy+42,bc); // perna E
            g.fill(cx+2 ,cy+9 ,cx+10,cy+42,bc); // perna D
            g.fill(cx-21,cy-29,cx-10,cy+5 ,bc); // braço E
            g.fill(cx+10,cy-29,cx+21,cy+5 ,bc); // braço D
            g.fill(cx-9 ,cy-48,cx-4 ,cy-43,ec); // olho E
            g.fill(cx+4 ,cy-48,cx+9 ,cy-43,ec); // olho D
            if ("true".equals(RACE_DEFS[raceIdx][3]))
                g.fill(cx-14,cy-63,cx+14,cy-54,hc); // cabelo
        }

        // Overlay do cabelo custom (se raça tiver cabelo)
        if ("true".equals(RACE_DEFS[raceIdx][3]) && hairStyle>=0) {
            ResourceLocation hairTex = new ResourceLocation(Dbi.MOD_ID,
                "textures/entity/customizacao/hair/hair_base"+(hairStyle+1)+".png");
            try {
                minecraft.getResourceManager().getResource(hairTex);
                g.blit(hairTex, cx-16, prY+prH/2-63, 0, 0, 32, 32, 32, 32);
            } catch (Exception ignored) {}
        }
    }

    private void drawPicker(GuiGraphics g) {
        g.fill(px-4,py-4,px+PW+SW+28,py+PH+4,0xFF222222);
        border(g,px-4,py-4,PW+SW+32,PH+8,0xFF777777);
        for (int i=0;i<PW;i++) for (int j=0;j<PH;j++)
            g.fill(px+i,py+j,px+i+1,py+j+1,(0xFF<<24)|hsv2rgb(i/(float)(PW-1)*360f,1f-(j/(float)(PH-1)),1f));
        for (int j=0;j<PH;j++)
            g.fill(sx,sy+j,sx+SW,sy+j+1,(0xFF<<24)|hsv2rgb(ph,1f,1f-(j/(float)(PH-1))));
        int curX=px+(int)(ph/360f*(PW-1)), curY=py+(int)((1f-ps)*(PH-1));
        g.fill(curX-3,curY-1,curX+3,curY+1,0xFFFFFFFF);
        g.fill(curX-1,curY-3,curX+1,curY+3,0xFFFFFFFF);
        int cur=pickerCol();
        g.fill(sx+SW+6,py,sx+SW+28,py+22,(0xFF<<24)|(cur&0xFFFFFF));
        g.drawString(font,String.format("#%06X",cur&0xFFFFFF),sx+SW+6,py+26,0xFFAAAAAA,false);
    }

    // =========================================================
    //  UTILS
    // =========================================================

    private void border(GuiGraphics g,int x,int y,int w,int h,int c) {
        g.fill(x,y,x+w,y+1,c); g.fill(x,y+h-1,x+w,y+h,c);
        g.fill(x,y,x+1,y+h,c); g.fill(x+w-1,y,x+w,y+h,c);
    }

    private int lighter(int color,float a) {
        int r=Math.min(255,(int)(((color>>16)&0xFF)+255*a));
        int g=Math.min(255,(int)(((color>>8 )&0xFF)+255*a));
        int b=Math.min(255,(int)(( color     &0xFF)+255*a));
        return (0xFF<<24)|(r<<16)|(g<<8)|b;
    }
    private int darker(int color,float a) {
        int r=Math.max(0,(int)(((color>>16)&0xFF)-255*a));
        int g=Math.max(0,(int)(((color>>8 )&0xFF)-255*a));
        int b=Math.max(0,(int)(( color     &0xFF)-255*a));
        return (0xFF<<24)|(r<<16)|(g<<8)|b;
    }

    private int hsv2rgb(float h,float s,float v){
        int hi=(int)(h/60f)%6; float f=h/60f-(int)(h/60f),p=v*(1-s),q=v*(1-f*s),t=v*(1-(1-f)*s);
        float r=0,g=0,b=0;
        switch(hi){case 0->{r=v;g=t;b=p;}case 1->{r=q;g=v;b=p;}case 2->{r=p;g=v;b=t;}
                   case 3->{r=p;g=q;b=v;}case 4->{r=t;g=p;b=v;}case 5->{r=v;g=p;b=q;}}
        return (Math.round(r*255)<<16)|(Math.round(g*255)<<8)|Math.round(b*255);
    }
    private float[] rgb2hsv(int rgb){
        float r=((rgb>>16)&0xFF)/255f,g=((rgb>>8)&0xFF)/255f,b=(rgb&0xFF)/255f;
        float mx=Math.max(r,Math.max(g,b)),mn=Math.min(r,Math.min(g,b)),d=mx-mn,h=0;
        if(d!=0){if(mx==r)h=60f*((g-b)/d%6f);else if(mx==g)h=60f*((b-r)/d+2f);else h=60f*((r-g)/d+4f);}
        if(h<0)h+=360f;
        return new float[]{h,mx==0?0:d/mx,mx};
    }

    // =========================================================
    //  INNER CLASS — SwatchBtn
    // =========================================================

    private static class SwatchBtn extends Button {
        final String tgt;
        int color;
        SwatchBtn(int x,int y,int w,int h,int color,String tgt,OnPress p){
            super(x,y,w,h,Component.empty(),p,DEFAULT_NARRATION);
            this.color=color; this.tgt=tgt;
        }
        @Override public void renderWidget(GuiGraphics g,int mx,int my,float pt){
            g.fill(getX()-1,getY()-1,getX()+width+1,getY()+height+1,isHovered()?0xFFFFFFFF:0xFF888888);
            g.fill(getX(),getY(),getX()+width,getY()+height,(0xFF<<24)|(color&0xFFFFFF));
        }
    }
}

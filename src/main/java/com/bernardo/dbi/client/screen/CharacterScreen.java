package com.bernardo.dbi.client.screen;

import com.bernardo.dbi.Dbi;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.server.packs.resources.Resource;
import java.util.Optional;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;

/**
 * Tela de criação/seleção de personagem (esqueleto).
 *
 * Comporta: leitura de coordenadas de botões de `icons.btn`, área de pré-visualização
 * e hooks para persistência da raça selecionada.
 */
public class CharacterScreen extends Screen {

    private final ResourceLocation background = new ResourceLocation(Dbi.MOD_ID, "textures/gui/menu.png");
    private final Map<String, int[]> buttonCoords = new HashMap<>();
    private ResourceLocation selectedTexture = null;
    // GUI size calculated from background PNG
    private int guiWidth = 512;
    private int guiHeight = 512;
    private int leftPos = 0;
    private int topPos = 0;

    // preview area (relative to gui top-left)
    private int previewX = 200;
    private int previewY = 100;
    private int previewW = 200;
    private int previewH = 350;

    // UI state for top label and color picker
    private String topLabel = "RACE";
    private int skinColor = 0xFFE0B899;
    private int hairColor = 0xFF6A492F;
    private boolean showColorPicker = false;
    private String colorTarget = null; // "skin" or "hair" or others
    private int pickerX, pickerY, pickerW, pickerH, sliderX, sliderY, sliderW, sliderH;
    private float pickerHue = 0f, pickerSat = 1f, pickerVal = 1f;

    public CharacterScreen() {
        super(Component.literal("Character"));
    }

    @Override
    protected void init() {
        super.init();
        // determina tamanho da GUI a partir do PNG e posiciona centralizado
        determineGuiSize();
        loadButtonCoords();

        // Exemplo: criar botões a partir das coordenadas lidas (labels temporários)
        // As coordenadas no arquivo devem estar no formato: id x y width height
        for (Map.Entry<String, int[]> e : buttonCoords.entrySet()) {
            String id = e.getKey();
            int[] c = e.getValue();
            if (c.length >= 4) {
                // coords in icons.btn are relative to GUI top-left
                this.addRenderableWidget(new DepressibleButton(this.leftPos + c[0], this.topPos + c[1], c[2], c[3], Component.literal(id), btn -> onButtonPressed(id)));
            }
        }

        // botão seta esquerda (coordenadas fornecidas: x=0,y=0,w=20,h=19)
        this.addRenderableWidget(new DepressibleButton(this.leftPos + 10, this.topPos + 10, 30, 25, Component.literal("<"), b -> onButtonPressed("arrow_left")));

        // botão seta direita (coordenadas fornecidas: x=21,y=0,w=20,h=19)
        this.addRenderableWidget(new DepressibleButton(this.leftPos + 475, this.topPos + 10, 30, 25, Component.literal(">"), b -> onButtonPressed("arrow_right")));

        // botão confirmar ao final (usa DepressibleButton para compatibilidade)
        int bw = 120;
        int bh = 30;
        this.addRenderableWidget(new DepressibleButton(this.leftPos + (this.guiWidth - bw) / 2, this.topPos + this.guiHeight - 50, bw, bh, Component.literal("Confirm"), b -> onConfirm()));

        // Categoria e botões de cor/configuração
        // Top label area is rendered between existing arrows; add color buttons below
        int cx = this.leftPos + 20;
        int cy = this.topPos + 30;
        // skin color button
        this.addRenderableWidget(new DepressibleButton(cx, cy, 20, 20, Component.literal(""), b -> openColorPicker("skin")));
        // hair selection + hair color
        this.addRenderableWidget(new DepressibleButton(cx + 25, cy, 80, 20, Component.literal("Hair"), b -> onButtonPressed("hair")));
        this.addRenderableWidget(new DepressibleButton(cx + 25 + 85, cy, 20, 20, Component.literal(""), b -> openColorPicker("hair")));

        // two empty placeholders below
        int cy2 = cy + 25;
        this.addRenderableWidget(new DepressibleButton(cx, cy2, 80, 20, Component.literal(""), b -> {}));
        this.addRenderableWidget(new DepressibleButton(cx + 85, cy2, 80, 20, Component.literal(""), b -> {}));
        // two color buttons without functionality
        this.addRenderableWidget(new DepressibleButton(cx, cy2 + 25, 20, 20, Component.literal(""), b -> {}));
        this.addRenderableWidget(new DepressibleButton(cx + 25, cy2 + 25, 20, 20, Component.literal(""), b -> {}));
    }

    private void onButtonPressed(String id) {
        // Hook: selecionar raça / alterar pré-visualização
        // Implementação real deve atualizar PlayerRaceCap e a pré-visualização
        System.out.println("Button pressed: " + id);
        // tenta encontrar texturas em ordem de prioridade:
        // 1) assets/dbi/textures/entity/race/customizacao/<id>.png
        // 2) assets/dbi/textures/entity/race/<id>.png
        // 3) assets/dbi/textures/player/<id>.png
        ResourceLocation[] candidates = new ResourceLocation[] {
                new ResourceLocation(Dbi.MOD_ID, "textures/entity/race/customizacao/" + id + ".png"),
                new ResourceLocation(Dbi.MOD_ID, "textures/entity/race/" + id + ".png"),
                new ResourceLocation(Dbi.MOD_ID, "textures/player/" + id + ".png")
        };
        for (ResourceLocation r : candidates) {
            try {
                // tentar obter o recurso — getResource lança IOException se não existir
                this.minecraft.getResourceManager().getResource(r);
                this.selectedTexture = r;
                System.out.println("Selected texture: " + r);
                return;
            } catch (Exception ignored) {
            }
        }
        // se não achou, mantém null
    }

    // botão que afunda ao ser pressionado/hover
    private static class DepressibleButton extends Button {
        public DepressibleButton(int x, int y, int w, int h, Component msg, Button.OnPress onPress) {
            super(x, y, w, h, msg, onPress, (component) -> Component.empty());
        }
    }

    private void onConfirm() {
        // Persistir seleção no NBT do jogador (cliente) como marcador temporário — integração servidor necessária
        if (this.selectedTexture != null && this.minecraft != null && this.minecraft.player != null) {
            try {
                this.minecraft.player.getPersistentData().putString("dbi:race_texture", this.selectedTexture.toString());
            } catch (Exception ignored) {
            }
        }
    }

    private void openColorPicker(String target) {
        this.colorTarget = target;
        this.showColorPicker = true;
        // position picker centered inside GUI
        this.pickerW = 180;
        this.pickerH = 120;
        this.pickerX = this.leftPos + (this.guiWidth - pickerW) / 2;
        this.pickerY = this.topPos + (this.guiHeight - pickerH) / 2;
        this.sliderW = 12;
        this.sliderH = pickerH;
        this.sliderX = this.pickerX + this.pickerW + 6;
        this.sliderY = this.pickerY;
        // initialize picker HSV from current color
        int cur = "skin".equals(target) ? this.skinColor : this.hairColor;
        float[] hsv = rgbToHsv(cur);
        this.pickerHue = hsv[0];
        this.pickerSat = hsv[1];
        this.pickerVal = hsv[2];
    }

    // RGB (0xRRGGBB or 0xAARRGGBB) to HSV {h,s,v}
    private float[] rgbToHsv(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        float rf = r / 255f, gf = g / 255f, bf = b / 255f;
        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float d = max - min;
        float h = 0f;
        if (d == 0) h = 0f;
        else if (max == rf) h = 60f * (((gf - bf) / d) % 6f);
        else if (max == gf) h = 60f * (((bf - rf) / d) + 2f);
        else h = 60f * (((rf - gf) / d) + 4f);
        if (h < 0) h += 360f;
        float s = max == 0 ? 0f : d / max;
        float v = max;
        return new float[]{h, s, v};
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.showColorPicker) {
            int mx = (int) mouseX;
            int my = (int) mouseY;
            // inside hue-sat square
            if (mx >= pickerX && mx < pickerX + pickerW && my >= pickerY && my < pickerY + pickerH) {
                int rx = mx - pickerX;
                int ry = my - pickerY;
                this.pickerHue = (rx / (float)(pickerW - 1)) * 360f;
                this.pickerSat = 1f - (ry / (float)(pickerH - 1));
                int rgb = hsvToRgb(this.pickerHue, this.pickerSat, this.pickerVal);
                applyPickedColor(rgb);
                return true;
            }
            // slider
            if (mx >= sliderX && mx < sliderX + sliderW && my >= sliderY && my < sliderY + sliderH) {
                int ry = my - sliderY;
                this.pickerVal = 1f - (ry / (float)(sliderH - 1));
                int rgb = hsvToRgb(this.pickerHue, this.pickerSat, this.pickerVal);
                applyPickedColor(rgb);
                return true;
            }
            // click outside picker closes it
            this.showColorPicker = false;
            this.colorTarget = null;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void applyPickedColor(int rgb) {
        int color = (0xFF << 24) | (rgb & 0xFFFFFF);
        if ("skin".equals(this.colorTarget)) this.skinColor = color;
        else if ("hair".equals(this.colorTarget)) this.hairColor = color;
    }

    // Simple HSV -> RGB (returns 0xRRGGBB)
    private int hsvToRgb(float h, float s, float v) {
        int hi = (int)(h / 60f) % 6;
        float f = (h / 60f) - (int)(h / 60f);
        float p = v * (1 - s);
        float q = v * (1 - f * s);
        float t = v * (1 - (1 - f) * s);
        float r=0,g=0,b=0;
        switch (hi) {
            case 0: r=v; g=t; b=p; break;
            case 1: r=q; g=v; b=p; break;
            case 2: r=p; g=v; b=t; break;
            case 3: r=p; g=q; b=v; break;
            case 4: r=t; g=p; b=v; break;
            case 5: r=v; g=p; b=q; break;
        }
        int ri = Math.round(r * 255);
        int gi = Math.round(g * 255);
        int bi = Math.round(b * 255);
        return (ri << 16) | (gi << 8) | bi;
    }

    private void loadButtonCoords() {
        // Reativar leitura de assets/icons.btn usando API Forge 1.20.1
        ResourceLocation coordsRes = new ResourceLocation(Dbi.MOD_ID, "textures/gui/icons.btn");
        // Primeiro tenta ler do classpath (útil durante desenvolvimento)
        InputStream cis = getClass().getResourceAsStream("/assets/dbi/textures/gui/icons.btn");
        if (cis != null) {
            try (InputStream is = cis; BufferedReader r = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = r.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 5) {
                        String id = parts[0];
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        int w = Integer.parseInt(parts[3]);
                        int h = Integer.parseInt(parts[4]);
                        buttonCoords.put(id, new int[]{x, y, w, h});
                    }
                }
            } catch (Exception ignored) {
            }
            return;
        }
        // Fallback: tentar pelo ResourceManager (se disponível na versão/mappings)
        try {
            Optional<Resource> opt = this.minecraft.getResourceManager().getResource(coordsRes);
            if (!opt.isPresent()) return;
            // Usar método genérico para obter InputStream via reflection se necessário
            try {
                java.lang.reflect.Method m = opt.get().getClass().getMethod("open");
                try (InputStream is = (InputStream) m.invoke(opt.get()); BufferedReader r = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    while ((line = r.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) continue;
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 5) {
                            String id = parts[0];
                            int x = Integer.parseInt(parts[1]);
                            int y = Integer.parseInt(parts[2]);
                            int w = Integer.parseInt(parts[3]);
                            int h = Integer.parseInt(parts[4]);
                            buttonCoords.put(id, new int[]{x, y, w, h});
                        }
                    }
                }
            } catch (NoSuchMethodException nsme) {
                // método não encontrado; ignorar fallback
            }
        } catch (Exception e) {
            // falha ao ler, manter sem botões dinâmicos
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        // desenhar background 512x512 (PNG é 1024x1024, escala 0.5)
        RenderSystem.setShaderTexture(0, background);
        guiGraphics.blit(background, this.leftPos, this.topPos, 0, 0, this.guiWidth, this.guiHeight, 1024, 1024);

        // desenhar área de preview (2D da textura legada)
        if (this.selectedTexture != null) {
            RenderSystem.setShaderTexture(0, this.selectedTexture);
            guiGraphics.blit(this.selectedTexture, this.leftPos + previewX, this.topPos + previewY, 0, 0, previewW, previewH, previewW, previewH);
        } else {
            guiGraphics.fill(this.leftPos + previewX, this.topPos + previewY, this.leftPos + previewX + previewW, this.topPos + previewY + previewH, 0xFF444444);
        }
        // desenhar top label entre setas
        int labelX = this.leftPos + 50;
        int labelY = this.topPos + 12;
        guiGraphics.drawString(this.font, Component.literal(this.topLabel), labelX, labelY, 0xFFFFFF);

        // desenhar botões de cor (pequenos retângulos) indicando cor atual
        int scx = this.leftPos + 10;
        int scy = this.topPos + 24;
        guiGraphics.fill(scx, scy, scx + 18, scy + 18, (0xFF << 24) | (this.skinColor & 0xFFFFFF));
        int hcx = this.leftPos + 24 + 64;
        int hcy = scy;
        guiGraphics.fill(hcx, hcy, hcx + 18, hcy + 18, (0xFF << 24) | (this.hairColor & 0xFFFFFF));

        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        // Render color picker overlay if active
        if (this.showColorPicker) {
            guiGraphics.fill(this.pickerX - 2, this.pickerY - 2, this.pickerX + this.pickerW + this.sliderW + 10, this.pickerY + this.pickerH + 2, 0xFF222222);
            for (int xx = 0; xx < pickerW; xx++) {
                for (int yy = 0; yy < pickerH; yy++) {
                    float h = (xx / (float)(pickerW - 1)) * 360f;
                    float s = 1f - (yy / (float)(pickerH - 1));
                    float v = 1f;
                    int rgb = hsvToRgb(h, s, v);
                    guiGraphics.fill(pickerX + xx, pickerY + yy, pickerX + xx + 1, pickerY + yy + 1, (0xFF << 24) | rgb);
                }
            }
            for (int yy = 0; yy < sliderH; yy++) {
                float v = 1f - (yy / (float)(sliderH - 1));
                int rgb = hsvToRgb(0f, 0f, v);
                guiGraphics.fill(sliderX, sliderY + yy, sliderX + sliderW, sliderY + yy + 1, (0xFF << 24) | rgb);
            }
            int infoX = sliderX + sliderW + 6;
            int infoY = pickerY;
            int cur = "skin".equals(this.colorTarget) ? this.skinColor : this.hairColor;
            int r = (cur >> 16) & 0xFF;
            int g = (cur >> 8) & 0xFF;
            int b = cur & 0xFF;
            guiGraphics.drawString(this.font, Component.literal("Target: " + this.colorTarget), infoX, infoY, 0xFFFFFF);
            guiGraphics.drawString(this.font, Component.literal(String.format("RGB: %d %d %d", r, g, b)), infoX, infoY + 10, 0xFFFFFF);
        }
    }

    // Placeholder para aplicar uma textura legada ao player model — delegar para utilitário
    private void applyLegacyTextureToPlayer(ResourceLocation legacyTexture) {
        // Placeholder: integração com renderer/capability necessária.
        // A ideia: mapear a textura legada (1.7.10) para as UVs do `PlayerModel` e atualizar o `PlayerRaceCap`
        this.selectedTexture = legacyTexture;
    }

    private void determineGuiSize() {
        // GUI size is fixed at 512x512 (half of background PNG 1024x1024)
        // Just center it on screen
        this.leftPos = (this.width - this.guiWidth) / 2;
        this.topPos = (this.height - this.guiHeight) / 2;
    }

}
package com.bernardo.dbi.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class AppearanceScreen extends Screen {

    private static final int PANEL_BG = 0xCC5B9BD5;      // Azul claro
    private static final int BORDER_COLOR = 0xFF1A3A6E;  // Azul escuro
    private static final int TEXT_COLOR = 0xFF000000;    // Preto

    private int panelX, panelY, panelWidth, panelHeight;
    private int previewX, previewY, previewSize;
    private int optionsX, optionsY, optionsWidth;

    private String selectedRace = "Arcosian";
    private String selectedForm = "Minimal";
    private String selectedBody = "Body Type 1";
    private String selectedNose = "Nose 1";
    private String selectedMouth = "Mouth 1";
    private String selectedEyes = "Eyes 1";

    private int bodyColor = 0xFFD2956E;
    private int noseColor = 0xFF000000;
    private int eyeColor = 0xFFCC0000;

    public AppearanceScreen() {
        super(Component.literal("Character Customization"));
    }

    @Override
    protected void init() {
        super.init();

        // Calcular dimensões do painel principal
        panelWidth = Math.min(1000, this.width - 40);
        panelHeight = Math.min(500, this.height - 40);
        panelX = (this.width - panelWidth) / 2;
        panelY = (this.height - panelHeight) / 2;

        // Seção de pré-visualização (esquerda)
        previewSize = (int) (panelWidth * 0.30);
        previewX = panelX + 20;
        previewY = panelY + 60;

        // Seção de opções (direita)
        optionsX = previewX + previewSize + 30;
        optionsY = panelY + 60;
        optionsWidth = panelX + panelWidth - optionsX - 20;

        // Botões de navegação
        this.addRenderableWidget(Button.builder(Component.literal("Fechar"),
                btn -> this.onClose())
            .pos(panelX + 20, panelY + panelHeight - 40)
            .size(80, 30)
            .build());

        this.addRenderableWidget(Button.builder(Component.literal("Confirmar"),
                btn -> this.saveAndClose())
            .pos(panelX + panelWidth - 100, panelY + panelHeight - 40)
            .size(80, 30)
            .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        drawMainPanel(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void drawMainPanel(GuiGraphics g) {
        // Fundo do painel
        g.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, PANEL_BG);

        // Borda do painel
        drawBorder(g, panelX, panelY, panelWidth, panelHeight, BORDER_COLOR, 2);

        // Título
        g.drawCenteredString(this.font, "Appearance", panelX + panelWidth / 2, panelY + 20, TEXT_COLOR);

        // Seção de pré-visualização
        drawPreviewSection(g);

        // Linha divisória
        g.fill(previewX + previewSize + 15, panelY + 50, previewX + previewSize + 16, panelY + panelHeight - 50, BORDER_COLOR);

        // Seção de opções
        drawOptionsSection(g);
    }

    private void drawPreviewSection(GuiGraphics g) {
        // Fundo da seção
        g.fill(previewX, previewY, previewX + previewSize, previewY + previewSize, 0x44FFFFFF);
        drawBorder(g, previewX, previewY, previewSize, previewSize, BORDER_COLOR, 1);

        // Desenhar preview simplificado do personagem
        drawCharacterPreview(g, previewX + previewSize / 2 - 16, previewY + 30);
    }

    private void drawCharacterPreview(GuiGraphics g, int x, int y) {
        // Desenhar um personagem simples como blocos coloridos
        int bodyC = bodyColor | 0xFF000000;
        int eyeC = eyeColor | 0xFF000000;

        // Cabeça
        g.fill(x + 8, y, x + 24, y + 16, bodyC);

        // Corpo
        g.fill(x + 6, y + 16, x + 26, y + 32, bodyC);

        // Braços
        g.fill(x, y + 18, x + 6, y + 28, bodyC);
        g.fill(x + 26, y + 18, x + 32, y + 28, bodyC);

        // Pernas
        g.fill(x + 8, y + 32, x + 16, y + 48, bodyC);
        g.fill(x + 16, y + 32, x + 24, y + 48, bodyC);

        // Olhos
        g.fill(x + 12, y + 4, x + 14, y + 6, eyeC);
        g.fill(x + 18, y + 4, x + 20, y + 6, eyeC);
    }

    private void drawOptionsSection(GuiGraphics g) {
        int x = optionsX;
        int y = optionsY;
        int lineHeight = 25;

        // Aparência
        g.drawString(this.font, "Appearance:", x, y, TEXT_COLOR);
        g.drawString(this.font, "< " + selectedRace + " >", x + 20, y + 15, TEXT_COLOR);
        y += lineHeight + 10;

        // Forma
        g.drawString(this.font, "Form:", x, y, TEXT_COLOR);
        g.drawString(this.font, "< " + selectedForm + " >", x + 20, y + 15, TEXT_COLOR);
        y += lineHeight + 10;

        // Corpo
        g.drawString(this.font, "Custom Skin:", x, y, TEXT_COLOR);
        g.drawString(this.font, "< " + selectedBody + " >", x + 20, y + 15, TEXT_COLOR);

        // Cores do corpo
        drawColorSwatch(g, x + 20, y + 35, 20, 20, bodyColor);
        y += lineHeight + 30;

        // Nariz
        g.drawString(this.font, "Nose:", x, y, TEXT_COLOR);
        g.drawString(this.font, "< " + selectedNose + " >", x + 20, y + 15, TEXT_COLOR);
        y += lineHeight + 10;

        // Boca
        g.drawString(this.font, "Mouth:", x, y, TEXT_COLOR);
        g.drawString(this.font, "< " + selectedMouth + " >", x + 20, y + 15, TEXT_COLOR);
        y += lineHeight + 10;

        // Olhos
        g.drawString(this.font, "Eyes:", x, y, TEXT_COLOR);
        g.drawString(this.font, "< " + selectedEyes + " >", x + 20, y + 15, TEXT_COLOR);

        // Cores dos olhos
        drawColorSwatch(g, x + 20, y + 35, 20, 20, eyeColor);
    }

    private void drawColorSwatch(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF444444);
        g.fill(x, y, x + w, y + h, 0xFF000000 | (color & 0xFFFFFF));
    }

    private void drawBorder(GuiGraphics g, int x, int y, int w, int h, int color, int thickness) {
        // Topo
        g.fill(x, y, x + w, y + thickness, color);
        // Fundo
        g.fill(x, y + h - thickness, x + w, y + h, color);
        // Esquerda
        g.fill(x, y, x + thickness, y + h, color);
        // Direita
        g.fill(x + w - thickness, y, x + w, y + h, color);
    }

    private void saveAndClose() {
        // TODO: Salvar dados do personagem
        this.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

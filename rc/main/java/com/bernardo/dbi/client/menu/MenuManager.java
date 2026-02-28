ackage com.bernardo.dbi.client.menu;

import com.bernardo.dbi.client.gui.buttons.*;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;

import java.util.function.Consumer;

/**
 * MenuManager - Orquestrador central que decide qual tipo de botão usar para cada ação.
 * Todas as decisões de layout e tipo de botão são centralizadas aqui.
 */
public class MenuManager {

    private final Consumer<AbstractWidget> adder;
    private final Consumer<String> handler;

    public MenuManager(Consumer<AbstractWidget> adder, Consumer<String> handler) {
        this.adder = adder;
        this.handler = handler;
    }

    /**
     * Adiciona um par de setas (esquerda/direita) para navegação
     */
    public void addArrows(String baseKey, int leftX, int rightX, int y, int size) {
        adder.accept(new ArrowLeftButton(leftX, y, size, size, 
            btn -> handler.accept(baseKey + "_p")));
        adder.accept(new ArrowRightButton(rightX - size, y, size, size, 
            btn -> handler.accept(baseKey + "_n")));
    }

    /**
     * Adiciona botão de confirmação (diamante amarelo)
     */
    public void addConfirmButton(int x, int y, int w, int h, String key) {
        adder.accept(new DiamondButton(x, y, w, h, 
            btn -> handler.accept(key)));
    }

    /**
     * Adiciona botão de cancelar/fechar (X)
     */
    public void addCancelButton(int x, int y, int w, int h, String key) {
        adder.accept(new XButton(x, y, w, h, 
            btn -> handler.accept(key)));
    }

    /**
     * Adiciona botão de ação genérica (círculo marrom)
     */
    public void addActionButton(int x, int y, int w, int h, String key) {
        adder.accept(new CircleButton(x, y, w, h, 
            btn -> handler.accept(key)));
    }

    /**
     * Adiciona botão vermelho (para ações destrutivas)
     */
    public void addDangerButton(int x, int y, int w, int h, String key) {
        adder.accept(new RedButton(x, y, w, h, 
            btn -> handler.accept(key)));
    }

    /**
     * Adiciona botão de lixeira (para deletar)
     */
    public void addDeleteButton(int x, int y, int w, int h, String key) {
        adder.accept(new TrashButton(x, y, w, h, 
            btn -> handler.accept(key)));
    }

    /**
     * Adiciona botão de mais/adicionar
     */
    public void addPlusButton(int x, int y, int w, int h, String key) {
        adder.accept(new PlusButton(x, y, w, h, 
            btn -> handler.accept(key)));
    }

    /**
     * Adiciona setas verticais (cima/baixo)
     */
    public void addVerticalArrows(String baseKey, int x, int topY, int bottomY, int size) {
        adder.accept(new ArrowUpButton(x, topY, size, size, 
            btn -> handler.accept(baseKey + "_up")));
        adder.accept(new ArrowDownButton(x, bottomY, size, size, 
            btn -> handler.accept(baseKey + "_down")));
    }
}

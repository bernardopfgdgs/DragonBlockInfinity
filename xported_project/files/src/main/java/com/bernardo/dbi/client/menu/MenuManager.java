package com.bernardo.dbi.client.menu;

import com.bernardo.dbi.client.gui.ButtonId;
import com.bernardo.dbi.client.gui.ButtonRegistry;
import com.bernardo.dbi.client.gui.MenuButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class MenuManager {

    private final Consumer<AbstractWidget> adder;
    private final Consumer<String>         handler;

    public MenuManager(Consumer<AbstractWidget> adder, Consumer<String> handler) {
        this.adder = adder;
        this.handler = handler;
    }

    public void addButton(String key, int x, int y, int w, int h, String label) {
        ButtonId id = ButtonRegistry.get(key);
        if (id != null) {
            adder.accept(new MenuButton(id, x, y, w, h, btn -> handler.accept(key)));
        } else {
            adder.accept(Button.builder(Component.literal(label),
                btn -> handler.accept(key)).pos(x, y).size(w, h).build());
        }
    }

    public void addArrows(String key, int leftX, int rightX, int y, int aw, int ah) {
        addButton(key + "_p", leftX,        y, aw, ah, "<<");
        addButton(key + "_n", rightX - aw,  y, aw, ah, ">>");
    }
}

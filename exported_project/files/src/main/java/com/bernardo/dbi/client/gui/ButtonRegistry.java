package com.bernardo.dbi.client.gui;

import java.util.HashMap;
import java.util.Map;

public class ButtonRegistry {
    private static final Map<String, ButtonId> R = new HashMap<>();

    static {
        for (String k : new String[]{ "race_p","form_p","hair_p","age_p","body_p","nose_p","mouth_p","eye_p" })
            R.put(k, ButtonId.ARROW_PREV);
        for (String k : new String[]{ "race_n","form_n","hair_n","age_n","body_n","nose_n","mouth_n","eye_n" })
            R.put(k, ButtonId.ARROW_NEXT);
        R.put("close", ButtonId.BTN_Y);
        R.put("back",  ButtonId.BTN_Y);
        R.put("prox",  ButtonId.BTN_CIRCLE);
        R.put("next",  ButtonId.BTN_CIRCLE);
        R.put("equal", ButtonId.BTN_DARK);
        R.put("match", ButtonId.BTN_DARK);
        ButtonId[] slots = {
            ButtonId.HUD_SLOT_1,  ButtonId.HUD_SLOT_2,  ButtonId.HUD_SLOT_3,
            ButtonId.HUD_SLOT_4,  ButtonId.HUD_SLOT_5,  ButtonId.HUD_SLOT_6,
            ButtonId.HUD_SLOT_7,  ButtonId.HUD_SLOT_8,  ButtonId.HUD_SLOT_9,
            ButtonId.HUD_SLOT_10, ButtonId.HUD_SLOT_11, ButtonId.HUD_SLOT_12,
            ButtonId.HUD_SLOT_13
        };
        for (int i = 0; i < slots.length; i++) R.put("hud_slot_" + (i+1), slots[i]);
        R.put("char_red",  ButtonId.HUD_CHAR_RED);
        R.put("char_blue", ButtonId.HUD_CHAR_BLUE);
    }

    public static void register(String key, ButtonId id) { R.put(key, id); }
    public static ButtonId get(String key) { return R.get(key); }
    public static ButtonUv getUv(String key) {
        ButtonId id = R.get(key);
        return id != null ? ButtonUv.of(id) : null;
    }
    public static MenuButton makeButton(String key, int x, int y, int w, int h,
            net.minecraft.client.gui.components.Button.OnPress p) {
        ButtonId id = R.get(key);
        return id != null ? new MenuButton(id, x, y, w, h, p) : null;
    }
}

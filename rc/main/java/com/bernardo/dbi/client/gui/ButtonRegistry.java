ackage com.bernardo.dbi.client.gui;

import java.util.HashMap;
import java.util.Map;

public class ButtonRegistry {
    private static final Map<String, ButtonId> R = new HashMap<>();

    static {
        for (String k : new String[]{"race_p", "form_p", "hair_p", "age_p", "body_p", "nose_p", "mouth_p", "eye_p"})
            R.put(k, ButtonId.ARROW_PREV);
        for (String k : new String[]{"race_n", "form_n", "hair_n", "age_n", "body_n", "nose_n", "mouth_n", "eye_n"})
            R.put(k, ButtonId.ARROW_NEXT);
        R.put("close", ButtonId.BTN_Y);
        R.put("back",  ButtonId.BTN_Y);
        R.put("prox",  ButtonId.BTN_CIRCLE);
        R.put("next",  ButtonId.BTN_CIRCLE);
        R.put("equal", ButtonId.BTN_DARK);
        R.put("match", ButtonId.BTN_DARK);
    }

    public static void register(String key, ButtonId id) { R.put(key, id); }
    public static ButtonId get(String key) { return R.get(key); }
}

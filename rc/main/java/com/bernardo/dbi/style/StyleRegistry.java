ackage com.bernardo.dbi.style;

import com.bernardo.dbi.style.data.MartialArts;
import com.bernardo.dbi.style.data.Spiritualist;
import com.bernardo.dbi.style.data.Warrior;
import java.util.HashMap;
import java.util.Map;

public class StyleRegistry {
    private static final Map<String, IFightStyle> styleMap = new HashMap<>();

    public static void registerAll() {
        register(FightStyle.Warrior.getId(), new Warrior());
        register(FightStyle.Spiritualist.getId(), new Spiritualist());
        register(FightStyle.MartialArts.getId(), new MartialArts());
    }

    private static void register(String id, IFightStyle implementation) {
        styleMap.put(id, implementation);
    }

    public static StyleData getStyleData(String id) {
        IFightStyle impl = styleMap.get(id);
        return impl != null ? impl.getStyleData() : new StyleData();
    }

    // Backwards-compatible overload
    public static StyleData getStyleData(FightStyle style) {
        return getStyleData(style.getId());
    }
}
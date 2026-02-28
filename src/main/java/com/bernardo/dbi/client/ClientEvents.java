package com.bernardo.dbi.client;

import com.bernardo.dbi.Dbi;
import com.bernardo.dbi.client.renderer.DbiRaceRenderLayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(
    modid = Dbi.MOD_ID,
    bus   = Mod.EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT
)
public class ClientEvents {

    public static final KeyMapping CHARACTER_SCREEN_KEY = new KeyMapping(
        "key.dbi.character_screen",
        GLFW.GLFW_KEY_C,
        "key.categories.gameplay"
    );

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(CHARACTER_SCREEN_KEY);
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        // Adiciona layer para "default" (Steve)
        addLayerToSkin(event, "default");
        // Adiciona layer para "slim" (Alex)
        addLayerToSkin(event, "slim");
    }
    
    private static void addLayerToSkin(EntityRenderersEvent.AddLayers event, String skinType) {
        PlayerRenderer renderer = event.getSkin(skinType);
        if (renderer != null) {
            renderer.addLayer(new DbiRaceRenderLayer(
                (net.minecraft.client.renderer.entity.RenderLayerParent<
                    AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>) renderer
            ));
        }
    }
}

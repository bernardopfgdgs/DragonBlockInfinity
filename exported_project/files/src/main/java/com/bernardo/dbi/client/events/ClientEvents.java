package com.bernardo.dbi.client;

import com.bernardo.dbi.Dbi;
import com.bernardo.dbi.client.screen.CharacterScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Dbi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    public static KeyMapping CHARACTER_SCREEN_KEY = new KeyMapping(
        "key.dbi.character_screen",
        GLFW.GLFW_KEY_C,
        "key.categories.gameplay"
    );

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(CHARACTER_SCREEN_KEY);
    }
}

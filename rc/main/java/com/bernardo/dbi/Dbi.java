ackage com.bernardo.dbi;

import com.bernardo.dbi.capability.PlayerRaceCap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Dbi.MOD_ID)
public class Dbi {
    public static final String MOD_ID = "dragonblockinfinity";

    public Dbi() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Registra network e capabilities
        ModRegister.register(modEventBus);
        
        // Registra eventos do Forge bus
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(
                new ResourceLocation(MOD_ID, "race_cap"),
                new PlayerRaceCap.Provider()
            );
        }
    }
}

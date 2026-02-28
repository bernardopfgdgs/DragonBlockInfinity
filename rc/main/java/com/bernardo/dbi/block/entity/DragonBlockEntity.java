ackage com.bernardo.dbi.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * BlockEntity para Dragon Blocks.
 * Permite armazenar dados sobre faces abertas/fechadas,
 * cores, animações ou outros atributos customizados.
 */
public class DragonBlockEntity extends BlockEntity {

    // Mascara de bits para faces abertas (norte, sul, leste, oeste, cima, baixo)
    private int openFacesMask = 0b000000; // 0 = fechada, 1 = aberta

    public DragonBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRAGON_BLOCK_ENTITY.get(), pos, state);
    }

    /**
     * Define quais faces estão abertas.
     * Bits: 0=Norte, 1=Sul, 2=Leste, 3=Oeste, 4=Cima, 5=Baixo
     */
    public void setOpenFaces(int mask) {
        this.openFacesMask = mask;
        setChanged();
    }

    public int getOpenFacesMask() {
        return openFacesMask;
    }

    /**
     * Verifica se uma face específica está aberta.
     * @param direction 0=Norte, 1=Sul, 2=Leste, 3=Oeste, 4=Cima, 5=Baixo
     */
    public boolean isFaceOpen(int direction) {
        return (openFacesMask & (1 << direction)) != 0;
    }

    /**
     * Define uma face como aberta ou fechada.
     * @param direction 0=Norte, 1=Sul, 2=Leste, 3=Oeste, 4=Cima, 5=Baixo
     */
    public void setFaceOpen(int direction, boolean open) {
        if (open) {
            openFacesMask |= (1 << direction);
        } else {
            openFacesMask &= ~(1 << direction);
        }
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("OpenFaces", openFacesMask);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.openFacesMask = tag.getInt("OpenFaces");
    }
}

package com.bernardo.dbi.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Classe base para todos os Dragon Blocks.
 * Permite faces independentes com renderização customizável.
 */
public class DragonBlockBase extends Block {

    public DragonBlockBase() {
        // Propriedades padrão para os dragon blocks
        super(Properties.of()
            .strength(2.0f, 10.0f)  // Dureza e resistência à explosão
            .requiresCorrectToolForDrops()
        );
    }

    /**
     * Retorna ENTITY para permitir renderização customizada via renderer.
     */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    /**
     * Permite que o bloco seja renderizado com faces transparentes/customizadas.
     * Retorna false para que o Minecraft use o renderer customizado.
     */
    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    /**
     * Define que o bloco é opaco para fins de renderização.
     */
    @Override
    public float getShadeBrightness(BlockState state, net.minecraft.world.level.BlockGetter level, net.minecraft.core.BlockPos pos) {
        return 1.0f;
    }
}

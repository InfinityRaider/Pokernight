package com.infinityraider.pokernight.render;

import com.infinityraider.infinitylib.render.block.RenderBlockWithTileBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.pokernight.block.BlockPokerTable;
import com.infinityraider.pokernight.block.tile.TileEntityPokerTable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

import java.util.Collections;
import java.util.List;

public class RenderBlockPokerTable extends RenderBlockWithTileBase<BlockPokerTable, TileEntityPokerTable> {
    public static ResourceLocation PARTICLE_TEXTURE = new ResourceLocation("minecraft", "oak_plansk");

    public RenderBlockPokerTable(BlockPokerTable block) {
        super(block, new TileEntityPokerTable(), true, true, true);
    }

    @Override
    public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state,
                                 BlockPokerTable block, TileEntityPokerTable tile, boolean dynamicRender, float partialTick, int destroyStage) {
        if(dynamicRender) {

        } else {
            this.renderPokerGame(tessellator, tile);
        }
    }

    protected void renderPokerGame(ITessellator tessellator, TileEntityPokerTable table) {

    }

    @Override
    public void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, BlockPokerTable block, TileEntityPokerTable tile,
                                     ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {

    }

    @Override
    public List<ResourceLocation> getAllTextures() {
        return Collections.emptyList();
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return ModelLoader.defaultTextureGetter().apply(PARTICLE_TEXTURE);
    }

    @Override
    public boolean applyAmbientOcclusion() {
        return true;
    }
}

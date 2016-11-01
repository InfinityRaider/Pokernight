package com.infinityraider.pokernight.render;

import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.render.RenderUtilBase;
import com.infinityraider.infinitylib.render.block.RenderBlockWithTileBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.pokernight.block.BlockPokerTable;
import com.infinityraider.pokernight.block.tile.TileEntityPokerTable;
import com.infinityraider.pokernight.reference.Reference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderBlockPokerTable extends RenderBlockWithTileBase<BlockPokerTable, TileEntityPokerTable> {
    public static ResourceLocation PARTICLE_TEXTURE = new ResourceLocation("minecraft", "blocks/hardened_clay_stained_brown");

    private TextureAtlasSprite iconWood;
    private TextureAtlasSprite iconFilt;
    private TextureAtlasSprite iconBlack;

    public RenderBlockPokerTable(BlockPokerTable block) {
        super(block, new TileEntityPokerTable(), true, true, true);
    }

    @Override
    public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state,
                                 BlockPokerTable block, TileEntityPokerTable tile, boolean dynamicRender, float partialTick, int destroyStage) {
        if(dynamicRender) {
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            GlStateManager.translate(x, y, z);

            this.renderPokerGame(tessellator, tile);

            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        } else {
            boolean north = BlockPokerTable.Properties.CONNECTION_NORTH.getValue(state);
            boolean east = BlockPokerTable.Properties.CONNECTION_EAST.getValue(state);
            boolean south = BlockPokerTable.Properties.CONNECTION_SOUTH.getValue(state);
            boolean west = BlockPokerTable.Properties.CONNECTION_WEST.getValue(state);
            this.renderTable(tessellator, north, east, south, west);
        }
    }

    @Override
    public void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, BlockPokerTable block, TileEntityPokerTable tile,
                                     ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {
        this.renderTable(tessellator, false, false, false, false);
    }

    protected void renderTable(ITessellator tessellator, boolean north, boolean east, boolean south, boolean west) {
        int t = 5;
        int minX = west ? 0 : t;
        int minZ = north ? 0 : t;
        int maxX = east ? 16 : 16 - t;
        int maxZ = south ? 16 : 16 - t;
        //filt overlay
        tessellator.setColorRGB(127, 255, 127);
        tessellator.drawScaledFace(minX, minZ, maxX, maxZ, EnumFacing.UP, this.getFiltIcon(), 15.01F);
        //table top face
        tessellator.setColorRGB(255, 255, 255);
        tessellator.drawScaledFace(0, 0, 16, 16, EnumFacing.UP, this.getWoodIcon(), 15);
        //foot bottom face
        tessellator.drawScaledFace(minX, minZ, maxX, maxZ, EnumFacing.DOWN, this.getWoodIcon(), 0);
        //table bottom face
        tessellator.drawScaledFace(0, 0, 16, 16, EnumFacing.DOWN, this.getWoodIcon(), 14);
        if(!north) {
            //foot face
            tessellator.setColorRGB(255, 255, 255);
            tessellator.drawScaledFace(minX, 0, maxX, 14, EnumFacing.NORTH, this.getWoodIcon(), t);
            //band top face
            tessellator.setColorRGB(10, 10, 10);
            tessellator.drawScaledFace(0, 0, 16, 2, EnumFacing.UP, getBlackIcon(), 16);
            //band bottom face
            tessellator.drawScaledFace(0, 0, 16, 2, EnumFacing.DOWN, getBlackIcon(), 13);
            //band side outer face
            tessellator.drawScaledFace(0, 13, 16, 16, EnumFacing.NORTH, this.getBlackIcon(), 0);
            //band side inner face
            tessellator.drawScaledFace(0, 13, 16, 16, EnumFacing.SOUTH, this.getBlackIcon(), 2);
        }
        if(!east) {
            //foot face
            tessellator.setColorRGB(255, 255, 255);
            tessellator.drawScaledFace(minZ, 0, maxZ, 14, EnumFacing.EAST, this.getWoodIcon(), 16 - t);
            //band top face
            tessellator.setColorRGB(10, 10, 10);
            tessellator.drawScaledFace(14, 0, 16, 16, EnumFacing.UP, getBlackIcon(), 16);
            //band bottom face
            tessellator.drawScaledFace(14, 0, 16, 16, EnumFacing.DOWN, getBlackIcon(), 13);
            //band side outer face
            tessellator.drawScaledFace(0, 13, 16, 16, EnumFacing.EAST, this.getBlackIcon(), 16);
            //band side inner face
            tessellator.drawScaledFace(0, 13, 16, 16, EnumFacing.WEST, this.getBlackIcon(), 14);
        }
        if(!south) {
            //foot face
            tessellator.setColorRGB(255, 255, 255);
            tessellator.drawScaledFace(minX, 0, maxX, 14, EnumFacing.SOUTH, this.getWoodIcon(), 16 - t);
            //band top face
            tessellator.setColorRGB(10, 10, 10);
            tessellator.drawScaledFace(0, 14, 16, 16, EnumFacing.UP, getBlackIcon(), 16);
            //band bottom face
            tessellator.drawScaledFace(0, 14, 16, 16, EnumFacing.DOWN, getBlackIcon(), 13);
            //band side outer face
            tessellator.drawScaledFace(0, 13, 16, 16, EnumFacing.SOUTH, this.getBlackIcon(), 16);
            //band side inner face
            tessellator.drawScaledFace(0, 13, 16, 16, EnumFacing.NORTH, this.getBlackIcon(), 14);
        }
        if(!west) {
            //foot face
            tessellator.setColorRGB(255, 255, 255);
            tessellator.drawScaledFace(minZ, 0, maxZ, 14, EnumFacing.WEST, this.getWoodIcon(), t);
            //band top face
            tessellator.setColorRGB(10, 10, 10);
            tessellator.drawScaledFace(0, 0, 2, 16, EnumFacing.UP, getBlackIcon(), 16);
            //band bottom face
            tessellator.drawScaledFace(0, 0, 2, 16, EnumFacing.DOWN, getBlackIcon(), 13);
            //band side outer face
            tessellator.drawScaledFace(0, 13, 16, 16, EnumFacing.WEST, this.getBlackIcon(), 0);
            //band side inner face
            tessellator.drawScaledFace(0, 13, 16, 16, EnumFacing.EAST, this.getBlackIcon(), 2);
        }
    }

    protected void renderPokerGame(ITessellator tessellator, TileEntityPokerTable table) {
        if(table.isFormed()) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            if(table.isMainTile()) {
                if(table.getTableOrientation() == EnumFacing.Axis.Z) {
                    tessellator.rotate(-90, 0, 1, 0);
                    tessellator.translate(0, 0, -2);
                }
                tessellator.bindTexture(new ResourceLocation(Reference.MOD_ID, "textures/blocks/table_overlay.png"));
                tessellator.addVertexWithUV(0, Constants.UNIT* 15.02F, 2, 0, 0);
                tessellator.addVertexWithUV(4, Constants.UNIT*15.02F, 2, 1, 0);
                tessellator.addVertexWithUV(4, Constants.UNIT*15.02F, 0, 1, 1);
                tessellator.addVertexWithUV(0, Constants.UNIT*15.02F, 0, 0, 1);
            }
        }
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
    
    protected TextureAtlasSprite getFiltIcon() {
        if(this.iconFilt == null) {
            this.iconFilt = RenderUtilBase.getIcon(new ResourceLocation("minecraft", "blocks/hardened_clay_stained_green"));
        }
        return this.iconFilt;
    }

    private TextureAtlasSprite getWoodIcon() {
        if(this.iconWood == null) {
            this.iconWood = RenderUtilBase.getIcon(PARTICLE_TEXTURE);
        }
        return this.iconWood;
    }

    private TextureAtlasSprite getBlackIcon() {
        if(this.iconBlack == null) {
            this.iconBlack = RenderUtilBase.getIcon(new ResourceLocation("minecraft", "blocks/hardened_clay_stained_black"));
        }
        return this.iconBlack;
    }
}

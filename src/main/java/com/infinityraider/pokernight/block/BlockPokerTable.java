package com.infinityraider.pokernight.block;

import com.infinityraider.infinitylib.block.BlockTileCustomRenderedBase;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import com.infinityraider.infinitylib.item.IItemWithRecipe;
import com.infinityraider.pokernight.block.tile.TileEntityPokerTable;
import com.infinityraider.pokernight.render.RenderBlockPokerTable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class BlockPokerTable extends BlockTileCustomRenderedBase<TileEntityPokerTable> implements IItemWithRecipe {

    public static class Properties {
        public static final InfinityProperty<Boolean> CONNECTION_NORTH = new InfinityProperty<>(PropertyBool.create("north"), false);
        public static final InfinityProperty<Boolean> CONNECTION_EAST = new InfinityProperty<>(PropertyBool.create("east"), false);
        public static final InfinityProperty<Boolean> CONNECTION_SOUTH = new InfinityProperty<>(PropertyBool.create("south"), false);
        public static final InfinityProperty<Boolean> CONNECTION_WEST = new InfinityProperty<>(PropertyBool.create("west"), false);
    }

    @SuppressWarnings("unchecked")
    public static final InfinityProperty<Boolean>[] PROPERTIES = new InfinityProperty[]{
            Properties.CONNECTION_NORTH,
            Properties.CONNECTION_EAST,
            Properties.CONNECTION_SOUTH,
            Properties.CONNECTION_WEST
    };

    @SideOnly(Side.CLIENT)
    private RenderBlockPokerTable renderer;

    public BlockPokerTable() {
        super("poker_table", Material.WOOD);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return PROPERTIES;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState();
        state = Properties.CONNECTION_NORTH.applyToBlockState(state, meta % 2 == 1);
        state = Properties.CONNECTION_EAST.applyToBlockState(state, (meta % 4) / 2 == 1);
        state = Properties.CONNECTION_SOUTH.applyToBlockState(state, (meta % 8) / 4 == 1);
        state = Properties.CONNECTION_WEST.applyToBlockState(state, meta / 8 == 1);
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        if(Properties.CONNECTION_NORTH.getValue(state)) {
            meta = meta + 1;
        }
        if(Properties.CONNECTION_EAST.getValue(state)) {
            meta = meta + 2;
        }
        if(Properties.CONNECTION_SOUTH.getValue(state)) {
            meta = meta + 4;
        }
        if(Properties.CONNECTION_WEST.getValue(state)) {
            meta = meta + 8;
        }
        return meta;
    }

    @Override
    public TileEntityPokerTable createNewTileEntity(World world, int meta) {
        return new TileEntityPokerTable();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockPokerTable getRenderer() {
        if(this.renderer == null) {
            this.renderer = new RenderBlockPokerTable(this);
        }
        return this.renderer;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    @Nullable ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntityPokerTable table = this.getTileEntity(world, pos);
        if(!world.isRemote) {
            if (!table.isFormed()) {
                if(player.isSneaking() && stack == null) {
                    table.formationClick(player);
                }
                return true;
            } else {
                if(player.isSneaking()) {
                    table.setGameRules(player);
                } else {
                    table.tryJoinGame(player);
                }
            }
        } else {
            return !table.isFormed();
        }
        return false;
    }

    @Override
    public List<IRecipe> getRecipes() {
        return Collections.emptyList();
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isVisuallyOpaque() {
        return false;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return false;
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }
}

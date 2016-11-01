package com.infinityraider.pokernight.block;

import com.infinityraider.infinitylib.block.BlockTileCustomRenderedBase;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import com.infinityraider.infinitylib.item.IItemWithRecipe;
import com.infinityraider.pokernight.block.tile.TileEntityPokerTable;
import com.infinityraider.pokernight.render.RenderBlockPokerTable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return PROPERTIES;
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
        if(!world.isRemote) {
            TileEntityPokerTable table = this.getTileEntity(world, pos);
            if (!table.isFormed()) {
                if(player.isSneaking() && stack == null) {
                    table.formationClick(player);
                }
            } else {
                if(player.isSneaking()) {
                    table.setGameRules(player);
                } else {
                    table.tryJoinGame(player);
                }
            }
        }
        return false;
    }

    @Override
    public List<IRecipe> getRecipes() {
        return Collections.emptyList();
    }
}

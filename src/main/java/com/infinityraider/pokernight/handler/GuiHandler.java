package com.infinityraider.pokernight.handler;

import com.infinityraider.pokernight.Pokernight;
import com.infinityraider.pokernight.block.tile.TileEntityPokerTable;
import com.infinityraider.pokernight.container.ContainerPokerGameRules;
import com.infinityraider.pokernight.container.gui.GuiContainerPokerGameRules;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    private static final GuiHandler INSTANCE = new GuiHandler();

    public static GuiHandler getInstance() {
        return INSTANCE;
    }

    public static final int TABLE_RULES = 0;
    public static final int TABLE_PLAYER = 1;

    private GuiHandler() {}

    public void openGameRuleGui(EntityPlayer player, TileEntityPokerTable table) {
        if(!player.getEntityWorld().isRemote) {
            player.openGui(Pokernight.instance, TABLE_RULES, table.getWorld(), table.xCoord(), table.yCoord(), table.zCoord());
        }
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch(id) {
            case TABLE_RULES:
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if(tile instanceof TileEntityPokerTable) {
                    return new ContainerPokerGameRules(player, (TileEntityPokerTable) tile);
                }
                break;
            case TABLE_PLAYER:
                break;
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch(id) {
            case TABLE_RULES:
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if(tile instanceof TileEntityPokerTable) {
                    return new GuiContainerPokerGameRules(new ContainerPokerGameRules(player, (TileEntityPokerTable) tile));
                }
                break;
            case TABLE_PLAYER:
                break;
        }
        return null;
    }
}

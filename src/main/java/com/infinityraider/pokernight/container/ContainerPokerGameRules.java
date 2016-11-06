package com.infinityraider.pokernight.container;

import com.infinityraider.infinitylib.container.ContainerBase;
import com.infinityraider.pokernight.block.tile.TileEntityPokerTable;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerPokerGameRules extends ContainerBase {
    private final EntityPlayer player;
    private final TileEntityPokerTable table;

    public ContainerPokerGameRules(EntityPlayer player, TileEntityPokerTable table) {
        super(player.inventory, 8, 67);
        this.player = player;
        this.table = table;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public TileEntityPokerTable getTable() {
        return this.table;
    }
}

package com.infinityraider.pokernight.registry;

import com.infinityraider.pokernight.block.BlockPokerTable;
import net.minecraft.block.Block;

public class BlockRegistry {
    private static final BlockRegistry INSTANCE = new BlockRegistry();

    public static BlockRegistry getInstance() {
        return INSTANCE;
    }

    public final Block blockPokerTable;

    private BlockRegistry() {
        this.blockPokerTable = new BlockPokerTable();
    }
}

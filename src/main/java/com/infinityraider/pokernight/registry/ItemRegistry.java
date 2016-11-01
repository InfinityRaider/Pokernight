package com.infinityraider.pokernight.registry;

import com.infinityraider.pokernight.item.ItemDebugger;
import net.minecraft.item.Item;

public class ItemRegistry {
    private static final ItemRegistry INSTANCE = new ItemRegistry();

    public static ItemRegistry getInstance() {
        return INSTANCE;
    }

    public final Item itemDebugger;

    private ItemRegistry() {
        this.itemDebugger = new ItemDebugger();
    }
}

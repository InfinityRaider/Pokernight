package com.infinityraider.pokernight;

import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.network.INetworkWrapper;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import com.infinityraider.pokernight.proxy.IProxy;
import com.infinityraider.pokernight.reference.Reference;
import com.infinityraider.pokernight.registry.BlockRegistry;
import com.infinityraider.pokernight.registry.ItemRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;

@Mod(
        modid = Reference.MOD_ID,
        name = Reference.MOD_NAME,
        version = Reference.VERSION,
        dependencies = com.infinityraider.infinitylib.reference.Reference.DEPENCENCY
)
public class Pokernight extends InfinityMod {
    @Mod.Instance(Reference.MOD_ID)
    public static Pokernight instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    @Override
    public IProxyBase proxy() {
        return proxy;
    }

    @Override
    public String getModId() {
        return Reference.MOD_ID;
    }

    @Override
    public Object getModBlockRegistry() {
        return BlockRegistry.getInstance();
    }

    @Override
    public Object getModItemRegistry() {
        return ItemRegistry.getInstance();
    }

    @Override
    public Object getModEntityRegistry() {
        return this;
    }

    @Override
    public void registerMessages(INetworkWrapper wrapper) {

    }
}

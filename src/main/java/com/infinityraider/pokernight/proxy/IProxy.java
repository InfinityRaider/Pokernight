package com.infinityraider.pokernight.proxy;

import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import com.infinityraider.pokernight.Pokernight;
import com.infinityraider.pokernight.handler.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public interface IProxy extends IProxyBase {
    @Override
    default void initStart(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(Pokernight.instance, GuiHandler.getInstance());
    }

    @Override
    default void initConfiguration(FMLPreInitializationEvent event) {

    }

    @Override
    default void registerEventHandlers() {
        this.registerEventHandler(EntityRemovedHandler.getInstance());
    }

    @Override
    default void activateRequiredModules() {

    }

    @Override
    default void registerCapabilities() {

    }
}

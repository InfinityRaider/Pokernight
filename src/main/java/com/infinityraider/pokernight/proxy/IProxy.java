package com.infinityraider.pokernight.proxy;

import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import com.infinityraider.pokernight.handler.*;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy extends IProxyBase {
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

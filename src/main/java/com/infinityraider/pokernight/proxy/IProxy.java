package com.infinityraider.pokernight.proxy;

import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy extends IProxyBase {
    @Override
    default void initConfiguration(FMLPreInitializationEvent event) {

    }

    @Override
    default void registerEventHandlers() {

    }

    @Override
    default void activateRequiredModules() {

    }

    @Override
    default void registerCapabilities() {

    }
}

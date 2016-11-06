package com.infinityraider.pokernight.network.serializers;

import com.infinityraider.infinitylib.network.serialization.ByteBufUtil;
import com.infinityraider.infinitylib.network.serialization.IMessageReader;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import com.infinityraider.infinitylib.network.serialization.IMessageWriter;
import com.infinityraider.pokernight.cardgame.poker.IPokerGameProvider;
import net.minecraft.tileentity.TileEntity;

public class PokerGameProviderSerializer implements IMessageSerializer<IPokerGameProvider> {
    private static final PokerGameProviderSerializer INSTANCE = new PokerGameProviderSerializer();

    public static PokerGameProviderSerializer getInstance() {
        return INSTANCE;
    }

    private PokerGameProviderSerializer() {}

    @Override
    public boolean accepts(Class<IPokerGameProvider> clazz) {
        return IPokerGameProvider.class.isAssignableFrom(clazz);
    }

    @Override
    public IMessageWriter<IPokerGameProvider> getWriter(Class<IPokerGameProvider> clazz) {
        return (buf, data) -> ByteBufUtil.writeTileEntity(buf, data.getTile());
    }

    @Override
    public IMessageReader<IPokerGameProvider> getReader(Class<IPokerGameProvider> clazz) {
        return (buf) -> {
            TileEntity tile = ByteBufUtil.readTileEntity(buf);
            return (tile instanceof IPokerGameProvider) ? (IPokerGameProvider) tile : null;
        };
    }
}

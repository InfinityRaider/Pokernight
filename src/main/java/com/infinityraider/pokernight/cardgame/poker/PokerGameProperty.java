package com.infinityraider.pokernight.cardgame.poker;

import com.infinityraider.infinitylib.network.serialization.*;
import com.infinityraider.pokernight.network.MessageSyncPokerGameElement;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

public class PokerGameProperty<T> {
    private final int id;

    private final IMessageWriter<T> writer;
    private final IMessageReader<T> reader;

    private IPokerGameProvider provider;
    private T property;

    public PokerGameProperty(IPokerGameProvider provider, List<PokerGameProperty> properties, Class<T> clazz) {
        this.id = properties.size();
        IMessageSerializer<T> serializer = MessageSerializerStore.getMessageSerializer(clazz).get();
        this.writer = serializer.getWriter(clazz);
        this.reader = serializer.getReader(clazz);
        this.provider = provider;
        properties.add(this);
    }

    public T get() {
        return this.property;
    }

    public PokerGameProperty<T> set(T property) {
        this.property = property;
        this.sync();
        return this;
    }

    void sync() {
        if(!provider.getTile().getWorld().isRemote) {
            new MessageSyncPokerGameElement(this).sendToAll();
        }
    }

    public static class Serializer<T> implements IMessageSerializer<PokerGameProperty<T>> {
        private static final Serializer INSTANCE = new Serializer();

        public static Serializer getInstance() {
            return INSTANCE;
        }

        private Serializer() {}

        @Override
        public boolean accepts(Class<PokerGameProperty<T>> clazz) {
            return PokerGameProperty.class.isAssignableFrom(clazz);
        }

        @Override
        public IMessageWriter<PokerGameProperty<T>> getWriter(Class<PokerGameProperty<T>> clazz) {
            return (buf, data) -> {
                buf.writeInt(data.id);
                ByteBufUtil.writeTileEntity(buf, data.provider.getTile());
                data.writer.writeData(buf, data.property);
            };
        }

        @Override
        @SuppressWarnings("unchecked")
        public IMessageReader<PokerGameProperty<T>> getReader(Class<PokerGameProperty<T>> clazz) {
            return (buf) -> {
                int id = buf.readInt();
                TileEntity tile = ByteBufUtil.readTileEntity(buf);
                PokerGameProperty<T> data = null;
                if(tile instanceof IPokerGameProvider) {
                    data = ((IPokerGameProvider) tile).getCurrentGame().getProperty(id);
                    data.property = data.reader.readData(buf);
                }
                return data;
            };
        }
    }
}

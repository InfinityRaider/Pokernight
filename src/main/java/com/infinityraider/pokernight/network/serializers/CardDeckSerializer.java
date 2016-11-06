package com.infinityraider.pokernight.network.serializers;

import com.infinityraider.infinitylib.network.serialization.IMessageReader;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import com.infinityraider.infinitylib.network.serialization.IMessageWriter;
import com.infinityraider.infinitylib.network.serialization.MessageSerializerStore;
import com.infinityraider.pokernight.cardgame.playingcards.CardDeck;

public class CardDeckSerializer implements IMessageSerializer<CardDeck> {
    private static final CardDeckSerializer INSTANCE = new CardDeckSerializer();

    public static CardDeckSerializer getInstance() {
        return INSTANCE;
    }

    private IMessageWriter<int[]> writer;
    private IMessageReader<int[]> reader;

    private CardDeckSerializer() {}

    @Override
    public boolean accepts(Class<CardDeck> clazz) {
        return CardDeck.class.isAssignableFrom(clazz);
    }

    @Override
    public IMessageWriter<CardDeck> getWriter(Class<CardDeck> clazz) {
        if(this.writer == null) {
            this.getSerializer();
        }
        return (buf, data) -> this.writer.writeData(buf, data.writeToIntArray());
    }

    @Override
    public IMessageReader<CardDeck> getReader(Class<CardDeck> clazz) {
        if(this.reader == null) {
            this.getSerializer();
        }
        return (buf) -> new CardDeck().readFromIntArray(this.reader.readData(buf));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    protected void getSerializer() {
        IMessageSerializer<int[]> serializer = MessageSerializerStore.getMessageSerializer(int[].class).get();
        this.writer = serializer.getWriter(int[].class);
        this.reader = serializer.getReader(int[].class);
    }
}

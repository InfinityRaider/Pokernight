package com.infinityraider.pokernight.network;

import com.infinityraider.infinitylib.network.serialization.IMessageReader;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import com.infinityraider.infinitylib.network.serialization.IMessageWriter;
import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;

public class PlayingCardSerializer implements IMessageSerializer<PlayingCard> {
    private static final PlayingCardSerializer INSTANCE = new PlayingCardSerializer();

    public static PlayingCardSerializer getInstance() {
        return INSTANCE;
    }

    private PlayingCardSerializer() {}

    @Override
    public boolean accepts(Class<PlayingCard> clazz) {
        return PlayingCard.class.isAssignableFrom(clazz);
    }

    @Override
    public IMessageWriter<PlayingCard> getWriter(Class<PlayingCard> clazz) {
        return (buf, data) -> buf.writeInt(data.getCardIndex());
    }

    @Override
    public IMessageReader<PlayingCard> getReader(Class<PlayingCard> clazz) {
        return (buf) -> PlayingCard.getCard(buf.readInt());
    }
}

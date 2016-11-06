package com.infinityraider.pokernight.cardgame.playingcards;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.network.serialization.IMessageReader;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import com.infinityraider.infinitylib.network.serialization.IMessageWriter;
import com.infinityraider.pokernight.network.PlayingCardSerializer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CardCollection implements Iterable<PlayingCard> {
    private PlayingCard[] cards;
    private int index;

    private List<PlayingCard> cache;

    public CardCollection(int size) {
        this.cards = new PlayingCard[size];
        this.index = 0;
    }

    public boolean addCard(PlayingCard card) {
        if(this.index < this.cards.length) {
            this.cards[index] = card;
            this.index++;
            this.updateCache();
            return true;
        } else {
            return false;
        }
    }

    public CardCollection copyFromList(List<PlayingCard> cardList) {
        for(int i = 0; i < this.cards.length; i++) {
            if(i < cardList.size()) {
                this.cards[i] = cardList.get(i);
                this.index = i + 1;
            } else {
                this.cards[i] = null;
            }
        }
        this.updateCache();
        return this;
    }

    private void updateCache() {
        this.cache = ImmutableList.copyOf(Arrays.copyOfRange(this.cards, 0, this.index));
    }

    public List<PlayingCard> getCards() {
        return this.cache;
    }

    @Override
    public Iterator<PlayingCard> iterator() {
        return new CardCollectionIterator(this);
    }

    private static class CardCollectionIterator implements Iterator<PlayingCard> {
        private CardCollection cards;
        private int index;

        private CardCollectionIterator(CardCollection cards) {
            this.cards = cards;
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            return this.index <= this.cards.index && this.index < this.cards.cards.length;
        }

        @Override
        public PlayingCard next() {
            return this.cards.cards[this.index++];
        }
    }

    public static class Serializer implements IMessageSerializer<CardCollection> {
        private static final Serializer INSTANCE = new Serializer();

        public static Serializer getInstance() {
            return INSTANCE;
        }

        private final IMessageWriter<PlayingCard> writer;
        private final IMessageReader<PlayingCard> reader;

        private Serializer() {
            this.writer = PlayingCardSerializer.getInstance().getWriter(PlayingCard.class);
            this.reader = PlayingCardSerializer.getInstance().getReader(PlayingCard.class);
        }

        @Override
        public boolean accepts(Class<CardCollection> clazz) {
            return CardCollection.class.isAssignableFrom(clazz);
        }

        @Override
        public IMessageWriter<CardCollection> getWriter(Class<CardCollection> clazz) {
            return (buf, data) -> {
                buf.writeInt(data.cards.length);
                buf.writeInt(data.index);
                for(int i = 0; i < data.index; i++) {
                    writer.writeData(buf, data.cards[i]);
                }
            };
        }

        @Override
        public IMessageReader<CardCollection> getReader(Class<CardCollection> clazz) {
            return (buf) -> {
                CardCollection data = new CardCollection(buf.readInt());
                data.index = buf.readInt();
                for(int i = 0; i < data.index; i++) {
                    data.cards[i] = reader.readData(buf);
                }
                data.updateCache();
                return data;
            };
        }
    }
}

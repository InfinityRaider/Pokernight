package com.infinityraider.pokernight.network;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import com.infinityraider.pokernight.cardgame.playingcards.CardCollection;
import com.infinityraider.pokernight.cardgame.poker.PokerGameProperty;
import com.infinityraider.pokernight.network.serializers.CardDeckSerializer;
import com.infinityraider.pokernight.network.serializers.PlayingCardSerializer;
import com.infinityraider.pokernight.network.serializers.PokerGameProviderSerializer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class MessageSyncPokerGameElement extends MessageBase<IMessage>  {
    private PokerGameProperty property;

    public MessageSyncPokerGameElement() {
        super();
    }

    public MessageSyncPokerGameElement(PokerGameProperty property) {
        this();
        this.property = property;
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        //data is automatically synced when reading from the byte buf
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }

    @Override
    protected List<IMessageSerializer> getNecessarySerializers() {
        return ImmutableList.of(
                PokerGameProviderSerializer.getInstance(),
                PlayingCardSerializer.getInstance(),
                CardCollection.Serializer.getInstance(),
                CardDeckSerializer.getInstance(),
                PokerGameProperty.Serializer.getInstance()
        );
    }
}

package com.infinityraider.pokernight.network;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import com.infinityraider.pokernight.cardgame.poker.IPokerGameProvider;
import com.infinityraider.pokernight.network.serializers.PokerGameProviderSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class MessageSyncPokerGame extends MessageBase<IMessage> {
    private IPokerGameProvider provider;
    private NBTTagCompound tag;

    public MessageSyncPokerGame() {
        super();
    }

    public MessageSyncPokerGame(IPokerGameProvider provider) {
        this.provider = provider;
        this.tag = provider.getCurrentGame().writeToNBT();
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if(this.provider != null && this.tag != null) {
            this.provider.getCurrentGame().readFromNBT(this.tag);
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }

    @Override
    protected List<IMessageSerializer> getNecessarySerializers() {
        return ImmutableList.of(
                PokerGameProviderSerializer.getInstance()
        );
    }
}

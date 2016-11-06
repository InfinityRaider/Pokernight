package com.infinityraider.pokernight.network;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import com.infinityraider.pokernight.cardgame.poker.IPokerGameProvider;
import com.infinityraider.pokernight.cardgame.poker.PokerPlayer;
import com.infinityraider.pokernight.network.serializers.PokerGameProviderSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class MessageSyncPokerPlayer extends MessageBase<IMessage> {
    private IPokerGameProvider provider;
    private int id;
    private NBTTagCompound tag;

    public MessageSyncPokerPlayer() {
        super();
    }

    public MessageSyncPokerPlayer(PokerPlayer player) {
        this.provider = player.getGame().getGameProvider();
        this.id = player.getPlayerId();
        this.tag = player.writeToNBT();
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if(this.provider != null && this.tag != null) {
            PokerPlayer player = this.provider.getCurrentGame().getPokerPlayerFromId(this.id);
            if(player != null) {
                player.readFromNBT(tag);
            }
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

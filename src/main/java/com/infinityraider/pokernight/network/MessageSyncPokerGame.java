package com.infinityraider.pokernight.network;

import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.pokernight.block.tile.TileEntityPokerTable;
import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;
import com.infinityraider.pokernight.cardgame.poker.PokerGame;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSyncPokerGame extends MessageBase<IMessage>  {
    private TileEntityPokerTable table;
    private int[] deck;
    private int[] openCards;
    private int[] closedCards;
    private int[] activePlayers;
    private int phase;
    private int playerDealer;
    private int playerTurn;
    private int lastRaiser;
    private int pool;
    private boolean bettingComplete;
    private int blind;
    private int lastRaise;

    public MessageSyncPokerGame() {
        super();
    }

    public MessageSyncPokerGame(TileEntityPokerTable table, int[] activePlayers, int dealer, int turn, int lastRaiser) {
        this();
        this.table = table;
        PokerGame game = table.getCurrentGame();
        this.deck = game.getDeck().writeToIntArray();
        this.openCards = PlayingCard.getIntArrayFromCardList(game.getOpenCards());
        this.closedCards = PlayingCard.getIntArrayFromCardList(game.getClosedCards());
        this.activePlayers = activePlayers;
        this.phase = game.getPhase().ordinal();
        this.playerDealer = dealer;
        this.playerTurn = turn;
        this.lastRaiser = lastRaiser;
        this.pool = game.getPrizPool();
        this.bettingComplete = game.isBettingComplete();
        this.blind = game.getBigBlind();
        this.lastRaise = game.getMinimumRaise();
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {

    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}

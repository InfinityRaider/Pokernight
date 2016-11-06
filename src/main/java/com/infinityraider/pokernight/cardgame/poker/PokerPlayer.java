package com.infinityraider.pokernight.cardgame.poker;

import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;
import com.infinityraider.pokernight.reference.Names;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.tuple.MutablePair;

public class PokerPlayer {
    private final PokerGame game;
    private int id;

    private PlayerState state;
    private int stack;
    private int bet;
    private MutablePair<PlayingCard, PlayingCard> hand;

    public PokerPlayer(PokerGame game, int id) {
        this.id = id;
        this.game = game;
        this.state = PlayerState.WAITING;
        this.hand = new MutablePair<>();
        this.stack = 0;
        this.bet = 0;
    }

    public PokerGame getGame() {
        return this.game;
    }

    public int getPlayerId() {
        return this.id;
    }

    PokerPlayer dealCard(PlayingCard card) {
        if(this.hand.getLeft() == null) {
            this.hand.setLeft(card);
        } else if(this.hand.getRight() == null) {
            this.hand.setRight(card);
        }
        return this;
    }

    PokerPlayer setState(PlayerState state) {
        this.state = state;
        return this;
    }

    public PlayerState getState() {
        return this.state;
    }

    public int getStack() {
        return this.stack;
    }

    public void addGains(int amount) {
        this.stack = this.stack + amount;
    }

    public int getCurrentBet() {
        return this.bet;
    }

    int collectBet(){
        int bet = this.getCurrentBet();
        this.bet = 0;
        return bet;
    }

    PokerPlayer placeBlind(int amount) {
        amount = Math.min(amount, this.stack);
        if(this.stack == amount) {
            this.setState(PlayerState.ALL_IN);
        } else {
            this.setState(PlayerState.WAITING);
        }
        this.stack = this.stack - amount;
        this.bet = amount;
        return this;
    }

    protected PokerPlayer raise(int amount) {
        amount = Math.min(amount, this.stack);
        if(this.stack == amount) {
            this.setState(PlayerState.ALL_IN);
        } else {
            this.setState(PlayerState.RAISED);
        }
        this.stack = this.stack - amount;
        this.bet = this.bet + amount;
        this.getGame().performPlayerAction(this, PlayerAction.RAISE);
        return this;
    }

    protected PokerPlayer call() {
        int amount = Math.min(this.getGame().getMinimumRaise(), this.stack);
        if(this.stack == amount) {
            this.state = PlayerState.ALL_IN;
        } else {
            this.setState(PlayerState.CALLED);
        }
        this.stack = this.stack - amount;
        this.bet = this.bet + amount;
        this.getGame().performPlayerAction(this, PlayerAction.CALL);
        return this;
    }

    protected PokerPlayer fold() {
        this.state = PlayerState.FOLDED;
        this.getGame().performPlayerAction(this, PlayerAction.FOLD);
        return this;
    }

    PokerPlayer returnHand() {
        this.hand.setLeft(null);
        this.hand.setRight(null);
        this.state = PlayerState.WAITING;
        return this;
    }

    public PlayingCard getFirstCard() {
        return this.hand.getLeft();
    }

    public PlayingCard getSecondCard() {
        return this.hand.getRight();
    }

    public final NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(Names.NBT.PLAYER_ID, this.id);
        tag.setInteger(Names.NBT.PLAYER_STATE, this.state.ordinal());
        tag.setInteger(Names.NBT.PLAYER_STACK, this.stack);
        tag.setInteger(Names.NBT.PLAYER_BET, this.bet);
        if(this.hand.getLeft() != null) {
            tag.setInteger(Names.NBT.PLAYER_HAND_1, this.hand.getLeft().getCardIndex());
        }
        if(this.hand.getRight() != null) {
            tag.setInteger(Names.NBT.PLAYER_HAND_2, this.hand.getRight().getCardIndex());
        }
        return tag;
    }

    public final PokerPlayer readFromNBT(NBTTagCompound tag) {
        this.id = tag.getInteger(Names.NBT.PLAYER_ID);
        this.state = PlayerState.values()[tag.getInteger(Names.NBT.GAME_PHASE)];
        this.stack = tag.getInteger(Names.NBT.PLAYER_STACK);
        this.bet = tag.getInteger(Names.NBT.PLAYER_BET);
        if(tag.hasKey(Names.NBT.PLAYER_HAND_1)) {
            this.hand.setLeft(PlayingCard.getCard(tag.getInteger(Names.NBT.PLAYER_HAND_1)));
        } else {
            this.hand.setLeft(null);
        }
        if(tag.hasKey(Names.NBT.PLAYER_HAND_2)) {
            this.hand.setRight(PlayingCard.getCard(tag.getInteger(Names.NBT.PLAYER_HAND_2)));
        } else {
            this.hand.setRight(null);
        }
        return this;
    }
}

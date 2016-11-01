package com.infinityraider.pokernight.cardgame.poker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.infinityraider.pokernight.cardgame.playingcards.CardDeck;
import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;
import com.infinityraider.pokernight.cardgame.poker.hand.PokerHand;
import com.infinityraider.pokernight.reference.Names;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public class PokerGame {
    private final IPokerGameProvider gameProvider;
    private CardDeck deck;

    private List<PlayingCard> openCards;
    private List<PlayingCard> openCardsCache;
    private List<PlayingCard> closedCards;
    private List<PlayingCard> closedCardsCache;

    private PokerPlayer[] players;
    private GamePhase phase;
    private int playerDealer;

    private int playerTurn;
    private PokerPlayer lastRaiser;

    private int pool;
    private boolean bettingComplete;
    private int blind;
    private int lastRaise;

    public PokerGame(IPokerGameProvider provider) {
        this.gameProvider = provider;
        this.deck = new CardDeck();
        this.playerDealer = -1;
        this.pool = 0;
    }

    public IPokerGameProvider getGameProvider() {
        return this.gameProvider;
    }

    public List<PlayingCard> getOpenCards() {
        return this.openCardsCache;
    }

    public List<PlayingCard> getClosedCards() {
        return this.closedCardsCache;
    }

    protected GamePhase getPhase() {
        return this.phase;
    }

    protected CardDeck getDeck() {
        return this.deck;
    }

    public int getMinimumRaise() {
        return this.lastRaise;
    }

    public int getBigBlind() {
        return this.blind;
    }

    public int getSmallBlind() {
        return Math.round(this.getBigBlind() / 2.0F);
    }

    public PokerPlayer getPlayerForIndex(int index) {
        return this.players[(this.playerDealer + index + 3) % this.players.length];
    }

    public PokerPlayer getDealer() {
        return this.players[this.playerDealer];
    }

    public PokerPlayer getSmallBlindPlayer() {
        if(this.players.length == 2) {
            return this.getDealer();
        } else {
            return this.players[(this.playerDealer + 1) % this.players.length];
        }
    }

    public PokerPlayer getBigBlindPlayer() {
        if(this.players.length == 2) {
            return this.players[(this.playerDealer + 1) % 2];
        } else {
            return this.players[(this.playerDealer + 2) % this.players.length];
        }
    }

    public PokerPlayer getBettingPlayer() {
        return this.getPlayerForIndex(this.playerTurn);
    }

    public void nextGame() {
        this.deck.shuffle();
        this.blind = this.getGameProvider().getBlind();
        this.players = this.getGameProvider().getPlayers(this);
        for(int i = 0; i < this.players.length; i++) {
            this.players[i].setPlayerId(i);
        }
        this.phase = GamePhase.PRE_GAME;
        this.openCards = new ArrayList<>();
        this.closedCards = new ArrayList<>();
        this.openCardsCache = ImmutableList.of();
        this.closedCardsCache = ImmutableList.of();
        this.playerDealer = this.playerDealer + 1;
    }

    public void performPlayerAction(PokerPlayer player, PlayerAction action) {
        if(player.getGame() == this) {
            if(action == PlayerAction.RAISE) {
                this.lastRaiser = player;
                resetPlayerStatesOnNewRaise();
            }
            this.incrementPlayerTurn();
            if(this.isBettingComplete()) {
                this.collectBets();
                this.advanceGamePhase();
            }
        }
    }

    protected void collectBets() {
        for(PokerPlayer player : this.players) {
            this.pool = player.collectBet() + this.pool;
        }
    }

    protected void advanceGamePhase() {
        this.phase = this.getPhase().nextPhase();
        this.playerTurn = 0;
        this.bettingComplete = false;
        for(PokerPlayer player : this.players) {
            player.setState(PlayerState.WAITING);
        }
        if(this.phase == GamePhase.END_GAME) {
            this.splitPrizeAmongWinners();
            this.returnCards();
            this.getGameProvider().onGameCompleted();
        } else {
            if (this.getPhase() == GamePhase.PRE_FLOP) {
                this.dealCards();
            }
            int cardsToDeal = this.getPhase().getCardsToDeal();
            if (cardsToDeal > 0) {
                Optional<PlayingCard> discard = this.getDeck().dealCard();
                if (discard.isPresent()) {
                    this.closedCards.add(discard.get());
                }
                for (int i = 0; i < cardsToDeal; i++) {
                    Optional<PlayingCard> card = this.getDeck().dealCard();
                    if (card.isPresent()) {
                        this.openCards.add(card.get());
                    }
                }
                this.openCardsCache = ImmutableList.copyOf(this.openCards);
                this.closedCardsCache = ImmutableList.copyOf(this.closedCards);
            }
            if (this.getPhase().doBets()) {
                this.lastRaise = this.getBigBlind();
                this.lastRaiser = this.getBigBlindPlayer();
                this.lastRaiser.placeBlind(this.getBigBlind());
                this.getSmallBlindPlayer().placeBlind(this.getSmallBlind());
            }
        }
    }

    protected void splitPrizeAmongWinners() {
        List<Pair<PokerPlayer, PokerHand>> potentialWinners = new ArrayList<>();
        PokerHand highest = null;
        for(PokerPlayer player : this.players) {
            if(player.getState().isInGame()) {
                List<PlayingCard> cards = Lists.newArrayList(this.getOpenCards());
                cards.add(player.getFirstCard());
                cards.add(player.getSecondCard());
                PokerHand hand = new PokerHand(cards);
                potentialWinners.add(Pair.of(player, hand));
                if(highest == null) {
                    highest = hand;
                } else {
                    if(hand.compareTo(highest) > 0) {
                        highest = hand;
                    }
                }
            }
        }
        Iterator<Pair<PokerPlayer, PokerHand>> it = potentialWinners.iterator();
        while(it.hasNext()) {
            Pair<PokerPlayer, PokerHand> potential = it.next();
            if(potential.second().compareTo(highest) < 0) {
                it.remove();
            }
        }
        int gains = this.pool / potentialWinners.size();
        for(Pair<PokerPlayer, PokerHand> winner : potentialWinners) {
            winner.first().addGains(gains);
            this.pool = this.pool - gains;
        }
    }

    protected void resetPlayerStatesOnNewRaise() {
        for(PokerPlayer player : this.players) {
            if(player == this.lastRaiser || player.getState().ignoreRaises()) {
                continue;
            }
            player.setState(PlayerState.WAITING);
        }
    }

    protected void incrementPlayerTurn() {
        this.playerTurn = (this.playerTurn + 1) % this.players.length;
        while(this.getBettingPlayer().getState() != PlayerState.WAITING) {
            this.playerTurn = (this.playerTurn + 1) % this.players.length;
        }
        if(this.getBettingPlayer() == this.lastRaiser && this.lastRaiser.getState() == PlayerState.RAISED) {
            this.bettingComplete = true;
        }
    }

    protected boolean isBettingComplete() {
        return this.bettingComplete;
    }

    protected void dealCards() {
        for(int i = 0; i < this.players.length * 2; i++) {
            Optional<PlayingCard> card = this.deck.dealCard();
            if(card.isPresent()) {
                this.getPlayerForIndex(i).dealCard(card.get());
            }
        }
    }

    protected void returnCards() {
        for(PokerPlayer player : this.players) {
            player.returnHand();
        }
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setIntArray(Names.NBT.GAME_DECK, this.deck.writeToIntArray());
        tag.setIntArray(Names.NBT.GAME_OPEN_CARDS, PlayingCard.getIntArrayFromCardList(this.openCards));
        tag.setIntArray(Names.NBT.GAME_CLOSED_CARDS, PlayingCard.getIntArrayFromCardList(this.closedCards));
        //TODO: write players
        tag.setInteger(Names.NBT.GAME_PHASE, this.phase.ordinal());
        tag.setInteger(Names.NBT.GAME_DEALER, this.playerDealer);
        tag.setInteger(Names.NBT.GAME_PLAYER_TURN, this.playerTurn);
        if(this.lastRaiser != null) {
            tag.setInteger(Names.NBT.GAME_CURRENT_RAISER, this.lastRaiser.getPlayerId());
        }
        tag.setInteger(Names.NBT.GAME_POOL, this.pool);
        tag.setBoolean(Names.NBT.GAME_BETS_COMPLETE, this.bettingComplete);
        tag.setInteger(Names.NBT.GAME_BLIND, this.blind);
        tag.setInteger(Names.NBT.GAME_LAST_RAISE, this.lastRaise);
        return tag;
    }

    public PokerGame readFromNBT(NBTTagCompound tag) {
        this.deck = new CardDeck().readFromIntArray(tag.getIntArray(Names.NBT.GAME_DECK));
        this.openCards = PlayingCard.getCardListFromIntArray(tag.getIntArray(Names.NBT.GAME_OPEN_CARDS));
        this.openCardsCache = ImmutableList.copyOf(this.openCards);
        this.closedCards = PlayingCard.getCardListFromIntArray(tag.getIntArray(Names.NBT.GAME_CLOSED_CARDS));
        this.closedCardsCache = ImmutableList.copyOf(this.closedCards);
        //TODO: read players
        this.phase = GamePhase.values()[tag.getInteger(Names.NBT.GAME_PHASE)];
        this.playerDealer = tag.getInteger(Names.NBT.GAME_DEALER);
        this.playerTurn = tag.getInteger(Names.NBT.GAME_PLAYER_TURN);
        if(tag.hasKey(Names.NBT.GAME_CURRENT_RAISER)) {
            this.lastRaiser = this.players[tag.getInteger(Names.NBT.GAME_CURRENT_RAISER)];
        } else {
            this.lastRaiser = null;
        }
        this.pool = tag.getInteger(Names.NBT.GAME_POOL);
        this.bettingComplete = tag.getBoolean(Names.NBT.GAME_BETS_COMPLETE);
        this.blind = tag.getInteger(Names.NBT.GAME_BLIND);
        this.lastRaise = tag.getInteger(Names.NBT.GAME_LAST_RAISE);
        return this;
    }
}

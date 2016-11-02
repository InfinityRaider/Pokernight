package com.infinityraider.pokernight.cardgame.poker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.infinityraider.pokernight.cardgame.playingcards.CardDeck;
import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;
import com.infinityraider.pokernight.cardgame.poker.hand.PokerHand;
import com.infinityraider.pokernight.reference.Names;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class PokerGame {
    private final IPokerGameProvider gameProvider;
    private CardDeck deck;

    private List<PlayingCard> openCards;
    private List<PlayingCard> openCardsCache;
    private List<PlayingCard> closedCards;
    private List<PlayingCard> closedCardsCache;

    private Map<Integer, PokerPlayer> players;
    private int[] activePlayers;
    private GamePhase phase;
    private int playerDealer;

    private int playerTurn;
    private PokerPlayer lastRaiser;

    private int pool;
    private boolean bettingComplete;
    private int blind;
    private int lastRaise;

    public PokerGame(IPokerGameProvider provider) {
        this.players = new IdentityHashMap<>();
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

    public GamePhase getPhase() {
        return this.phase;
    }

    public CardDeck getDeck() {
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

    public int getPrizPool() {
        return this.pool;
    }

    public PokerPlayer getPlayerForIndex(int index) {
        return this.players.get(activePlayers[(this.playerDealer + index + 3) % this.activePlayers.length]);
    }

    public PokerPlayer getDealer() {
        return this.players.get(this.activePlayers[this.playerDealer]);
    }

    public PokerPlayer getSmallBlindPlayer() {
        if(this.activePlayers.length == 2) {
            return this.getDealer();
        } else {
            return this.getPlayerForIndex((this.playerDealer + 1) % this.activePlayers.length);
        }
    }

    public PokerPlayer getBigBlindPlayer() {
        if(this.activePlayers.length == 2) {
            return this.getPlayerForIndex((this.playerDealer + 1) % 2);
        } else {
            return this.getPlayerForIndex((this.playerDealer + 2) % this.activePlayers.length);
        }
    }

    public PokerPlayer getBettingPlayer() {
        return this.getPlayerForIndex(this.playerTurn);
    }

    public void nextGame() {
        this.deck.shuffle();
        this.blind = this.getGameProvider().getBlind();
        this.gatherPlayers();
        this.phase = GamePhase.PRE_GAME;
        this.openCards = new ArrayList<>();
        this.closedCards = new ArrayList<>();
        this.openCardsCache = ImmutableList.of();
        this.closedCardsCache = ImmutableList.of();
        this.playerDealer = this.playerDealer + 1;
    }

    protected void gatherPlayers() {
        this.players.clear();
        List<Integer> playerIds = Lists.newArrayList();
        this.getGameProvider().getPlayers(this).stream().forEach(player -> {
            this.players.put(player.getPlayerId(), player);
            playerIds.add(player.getPlayerId());
        });
        this.activePlayers = ArrayUtils.toPrimitive(playerIds.toArray(new Integer[playerIds.size()]));
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
        for(PokerPlayer player : this.players.values()) {
            this.pool = player.collectBet() + this.pool;
        }
    }

    protected void advanceGamePhase() {
        this.phase = this.getPhase().nextPhase();
        this.playerTurn = 0;
        this.bettingComplete = false;
        for(PokerPlayer player : this.players.values()) {
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
        for(PokerPlayer player : this.players.values()) {
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
        int gains = this.getPrizPool() / potentialWinners.size();
        for(Pair<PokerPlayer, PokerHand> winner : potentialWinners) {
            winner.first().addGains(gains);
            this.pool = this.pool - gains;
        }
    }

    protected void resetPlayerStatesOnNewRaise() {
        for(PokerPlayer player : this.players.values()) {
            if(player == this.lastRaiser || player.getState().ignoreRaises()) {
                continue;
            }
            player.setState(PlayerState.WAITING);
        }
    }

    protected void incrementPlayerTurn() {
        this.playerTurn = (this.playerTurn + 1) % this.activePlayers.length;
        while(this.getBettingPlayer().getState() != PlayerState.WAITING) {
            this.playerTurn = (this.playerTurn + 1) % this.activePlayers.length;
        }
        if(this.getBettingPlayer() == this.lastRaiser && this.lastRaiser.getState() == PlayerState.RAISED) {
            this.bettingComplete = true;
        }
    }

    public boolean isBettingComplete() {
        return this.bettingComplete;
    }

    protected void dealCards() {
        for(int i = 0; i < this.activePlayers.length * 2; i++) {
            Optional<PlayingCard> card = this.deck.dealCard();
            if(card.isPresent()) {
                this.getPlayerForIndex(i).dealCard(card.get());
            }
        }
    }

    protected void returnCards() {
        for(PokerPlayer player : this.players.values()) {
            player.returnHand();
        }
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setIntArray(Names.NBT.GAME_DECK, this.deck.writeToIntArray());
        tag.setIntArray(Names.NBT.GAME_OPEN_CARDS, PlayingCard.getIntArrayFromCardList(this.openCards));
        tag.setIntArray(Names.NBT.GAME_CLOSED_CARDS, PlayingCard.getIntArrayFromCardList(this.closedCards));
        tag.setTag(Names.NBT.GAME_PLAYERS, this.writePlayersToNBT());
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

    protected NBTTagCompound writePlayersToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setIntArray(Names.NBT.GAME_ACTIVE_PLAYERS, this.activePlayers);
        NBTTagList list = new NBTTagList();
        for(PokerPlayer player : this.players.values()) {
            NBTTagCompound entry = player.writeToNBT();
            entry.setInteger(Names.NBT.ID, player.getPlayerId());
            list.appendTag(entry);
        }
        tag.setTag(Names.NBT.GAME_PLAYERS, list);
        return tag;
    }

    public PokerGame readFromNBT(NBTTagCompound tag) {
        this.deck = new CardDeck().readFromIntArray(tag.getIntArray(Names.NBT.GAME_DECK));
        this.openCards = PlayingCard.getCardListFromIntArray(tag.getIntArray(Names.NBT.GAME_OPEN_CARDS));
        this.openCardsCache = ImmutableList.copyOf(this.openCards);
        this.closedCards = PlayingCard.getCardListFromIntArray(tag.getIntArray(Names.NBT.GAME_CLOSED_CARDS));
        this.closedCardsCache = ImmutableList.copyOf(this.closedCards);
        this.readPlayersFromNBT(tag.getCompoundTag(Names.NBT.GAME_PLAYERS));
        this.phase = GamePhase.values()[tag.getInteger(Names.NBT.GAME_PHASE)];
        this.playerDealer = tag.getInteger(Names.NBT.GAME_DEALER);
        this.playerTurn = tag.getInteger(Names.NBT.GAME_PLAYER_TURN);
        if(tag.hasKey(Names.NBT.GAME_CURRENT_RAISER)) {
            this.lastRaiser = this.players.get(tag.getInteger(Names.NBT.GAME_CURRENT_RAISER));
        } else {
            this.lastRaiser = null;
        }
        this.pool = tag.getInteger(Names.NBT.GAME_POOL);
        this.bettingComplete = tag.getBoolean(Names.NBT.GAME_BETS_COMPLETE);
        this.blind = tag.getInteger(Names.NBT.GAME_BLIND);
        this.lastRaise = tag.getInteger(Names.NBT.GAME_LAST_RAISE);
        return this;
    }

    protected void readPlayersFromNBT(NBTTagCompound tag) {
        this.gatherPlayers();
        NBTTagList list = tag.getTagList(Names.NBT.GAME_PLAYERS, 10);
        int count = list.tagCount();
        for(int i = 0; i < count; i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            this.players.get(entry.getInteger(Names.NBT.ID)).readFromNBT(entry);
        }
        this.activePlayers = tag.getIntArray(Names.NBT.GAME_ACTIVE_PLAYERS);
    }
}

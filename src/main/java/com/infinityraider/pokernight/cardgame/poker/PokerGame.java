package com.infinityraider.pokernight.cardgame.poker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.infinityraider.pokernight.cardgame.playingcards.CardCollection;
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
    private final List<PokerGameProperty> properties;

    private final IPokerGameProvider gameProvider;
    private final PokerGameProperty<CardDeck> deck;

    private final PokerGameProperty<CardCollection> openCards;
    private final PokerGameProperty<CardCollection> closedCards;

    private Map<Integer, PokerPlayer> players;
    private final PokerGameProperty<int[]> activePlayers;
    private final PokerGameProperty<GamePhase> phase;
    private final PokerGameProperty<Integer> playerDealer;

    private final PokerGameProperty<Integer> playerTurn;
    private final PokerGameProperty<Integer> lastRaiser;

    private final PokerGameProperty<Integer> pool;
    private final PokerGameProperty<Boolean> bettingComplete;
    private final PokerGameProperty<Integer> blind;
    private final PokerGameProperty<Integer> lastRaise;

    public PokerGame(IPokerGameProvider provider) {
        List<PokerGameProperty> list = Lists.newArrayList();
        this.players = new IdentityHashMap<>();
        this.gameProvider = provider;
        this.deck = new PokerGameProperty<>(gameProvider, list, CardDeck.class);
        this.openCards = new PokerGameProperty<>(gameProvider, list, CardCollection.class);
        this.closedCards = new PokerGameProperty<>(gameProvider, list, CardCollection.class);
        this.activePlayers = new PokerGameProperty<>(gameProvider, list, int[].class);
        this.phase = new PokerGameProperty<>(gameProvider, list, GamePhase.class);
        this.playerDealer = new PokerGameProperty<>(gameProvider, list, Integer.class);
        this.playerTurn = new PokerGameProperty<>(gameProvider, list, Integer.class);
        this.lastRaiser = new PokerGameProperty<>(gameProvider, list, Integer.class);
        this.pool = new PokerGameProperty<>(gameProvider, list, Integer.class);
        this.bettingComplete = new PokerGameProperty<>(gameProvider, list, Boolean.class);
        this.blind = new PokerGameProperty<>(gameProvider, list, Integer.class);
        this.lastRaise = new PokerGameProperty<>(gameProvider, list, Integer.class);
        this.deck.set(new CardDeck());
        this.playerDealer.set(-1);
        this.pool.set(0);
        this.properties = ImmutableList.copyOf(list);
    }

    protected PokerGameProperty getProperty(int id) {
        return this.properties.get(id);
    }

    public IPokerGameProvider getGameProvider() {
        return this.gameProvider;
    }

    public List<PlayingCard> getOpenCards() {
        return this.openCards.get().getCards();
    }

    public List<PlayingCard> getClosedCards() {
        return this.closedCards.get().getCards();
    }

    public GamePhase getPhase() {
        return this.phase.get();
    }

    public CardDeck getDeck() {
        return this.deck.get();
    }

    public int getMinimumRaise() {
        return this.lastRaise.get();
    }

    public int getBigBlind() {
        return this.blind.get();
    }

    public int getSmallBlind() {
        return Math.round(this.getBigBlind() / 2.0F);
    }

    public int getPrizePool() {
        return this.pool.get();
    }

    public PokerPlayer getPlayerForIndex(int index) {
        return this.players.get(activePlayers.get()[(this.playerDealer.get() + index + 3) % this.activePlayers.get().length]);
    }

    public PokerPlayer getDealer() {
        return this.getPlayerForIndex(this.playerDealer.get());
    }

    public PokerPlayer getSmallBlindPlayer() {
        if(this.activePlayers.get().length == 2) {
            return this.getDealer();
        } else {
            return this.getPlayerForIndex(this.playerDealer.get() + 1);
        }
    }

    public PokerPlayer getBigBlindPlayer() {
        if(this.activePlayers.get().length == 2) {
            return this.getPlayerForIndex(this.playerDealer.get() + 1);
        } else {
            return this.getPlayerForIndex(this.playerDealer.get() + 2);
        }
    }

    public PokerPlayer getBettingPlayer() {
        return this.getPlayerForIndex(this.playerTurn.get());
    }

    public void nextGame() {
        this.deck.get().shuffle();
        this.blind.set(this.getGameProvider().getBlind());
        this.gatherPlayers();
        this.phase.set(GamePhase.PRE_GAME);
        this.openCards.set(new CardCollection(5));
        this.closedCards.set(new CardCollection(3));
        this.playerDealer.set(this.playerDealer.get() + 1);
    }

    protected void gatherPlayers() {
        this.players.clear();
        List<Integer> playerIds = Lists.newArrayList();
        this.getGameProvider().getPlayers(this).stream().forEach(player -> {
            this.players.put(player.getPlayerId(), player);
            playerIds.add(player.getPlayerId());
        });
        this.activePlayers.set(ArrayUtils.toPrimitive(playerIds.toArray(new Integer[playerIds.size()])));
    }

    public void performPlayerAction(PokerPlayer player, PlayerAction action) {
        if(player.getGame() == this) {
            if(action == PlayerAction.RAISE) {
                this.lastRaiser.set(player.getPlayerId());
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
        int pool = this.pool.get();
        for(PokerPlayer player : this.players.values()) {
            pool = player.collectBet() + pool;
        }
        this.pool.set(pool);
    }

    protected void advanceGamePhase() {
        this.phase.set(this.getPhase().nextPhase());
        this.playerTurn.set(0);
        this.bettingComplete.set(false);
        for(PokerPlayer player : this.players.values()) {
            player.setState(PlayerState.WAITING);
        }
        if(this.phase.get() == GamePhase.END_GAME) {
            this.splitPrizeAmongWinners();
            this.returnCards();
            this.getGameProvider().onGameCompleted();
        } else {
            if (this.getPhase() == GamePhase.PRE_FLOP) {
                this.dealCardsToPlayers();
            }
            this.dealCards();
            if (this.getPhase().doBets()) {
                this.lastRaise.set(this.getBigBlind());
                this.lastRaiser.set(this.getBigBlindPlayer().placeBlind(this.getBigBlind()).getPlayerId());
                this.getSmallBlindPlayer().placeBlind(this.getSmallBlind());
            }
        }
    }

    protected void dealCards() {
        int cardsToDeal = this.getPhase().getCardsToDeal();
        if (cardsToDeal > 0) {
            Optional<PlayingCard> discard = this.getDeck().dealCard();
            if (discard.isPresent()) {
                this.closedCards.get().addCard(discard.get());
            }
            for (int i = 0; i < cardsToDeal; i++) {
                Optional<PlayingCard> card = this.getDeck().dealCard();
                if (card.isPresent()) {
                    this.openCards.get().addCard(card.get());
                }
            }
            this.openCards.sync();
            this.closedCards.sync();
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
        int gains = this.getPrizePool() / potentialWinners.size();
        int pool = this.pool.get();
        for(Pair<PokerPlayer, PokerHand> winner : potentialWinners) {
            winner.first().addGains(gains);
            pool = pool - gains;
        }
        this.pool.set(pool);
    }

    protected void resetPlayerStatesOnNewRaise() {
        for(PokerPlayer player : this.players.values()) {
            if(player.getPlayerId() == this.lastRaiser.get() || player.getState().ignoreRaises()) {
                continue;
            }
            player.setState(PlayerState.WAITING);
        }
    }

    protected void incrementPlayerTurn() {
        this.playerTurn.set((this.playerTurn.get() + 1) % this.activePlayers.get().length);
        int turn = (this.playerTurn.get() + 1) % this.activePlayers.get().length;
        while(this.getPlayerForIndex(turn).getState() != PlayerState.WAITING) {
            turn = (turn + 1) % this.activePlayers.get().length;
        }
        this.playerTurn.set(turn);
        if(this.getBettingPlayer().getPlayerId() == this.lastRaiser.get() && this.getPlayerForIndex(this.lastRaiser.get()).getState() == PlayerState.RAISED) {
            this.bettingComplete.set(true);
        }
    }

    public boolean isBettingComplete() {
        return this.bettingComplete.get();
    }

    protected void dealCardsToPlayers() {
        for(int i = 0; i < this.activePlayers.get().length * 2; i++) {
            Optional<PlayingCard> card = this.deck.get().dealCard();
            if(card.isPresent()) {
                this.getPlayerForIndex(i).dealCard(card.get());
            }
            this.deck.sync();
        }
    }

    protected void returnCards() {
        for(PokerPlayer player : this.players.values()) {
            player.returnHand();
        }
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setIntArray(Names.NBT.GAME_DECK, this.deck.get().writeToIntArray());
        tag.setIntArray(Names.NBT.GAME_OPEN_CARDS, PlayingCard.getIntArrayFromCardList(this.openCards.get().getCards()));
        tag.setIntArray(Names.NBT.GAME_CLOSED_CARDS, PlayingCard.getIntArrayFromCardList(this.closedCards.get().getCards()));
        tag.setTag(Names.NBT.GAME_PLAYERS, this.writePlayersToNBT());
        tag.setInteger(Names.NBT.GAME_PHASE, this.phase.get().ordinal());
        tag.setInteger(Names.NBT.GAME_DEALER, this.playerDealer.get());
        tag.setInteger(Names.NBT.GAME_PLAYER_TURN, this.playerTurn.get());
        if(this.lastRaiser != null) {
            tag.setInteger(Names.NBT.GAME_CURRENT_RAISER, this.lastRaiser.get());
        }
        tag.setInteger(Names.NBT.GAME_POOL, this.pool.get());
        tag.setBoolean(Names.NBT.GAME_BETS_COMPLETE, this.bettingComplete.get());
        tag.setInteger(Names.NBT.GAME_BLIND, this.blind.get());
        tag.setInteger(Names.NBT.GAME_LAST_RAISE, this.lastRaise.get());
        return tag;
    }

    protected NBTTagCompound writePlayersToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setIntArray(Names.NBT.GAME_ACTIVE_PLAYERS, this.activePlayers.get());
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
        this.deck.set(new CardDeck().readFromIntArray(tag.getIntArray(Names.NBT.GAME_DECK)));
        this.openCards.get().copyFromList(PlayingCard.getCardListFromIntArray(tag.getIntArray(Names.NBT.GAME_OPEN_CARDS)));
        this.openCards.sync();
        this.closedCards.get().copyFromList(PlayingCard.getCardListFromIntArray(tag.getIntArray(Names.NBT.GAME_CLOSED_CARDS)));
        this.closedCards.sync();
        this.readPlayersFromNBT(tag.getCompoundTag(Names.NBT.GAME_PLAYERS));
        this.phase.set(GamePhase.values()[tag.getInteger(Names.NBT.GAME_PHASE)]);
        this.playerDealer.set(tag.getInteger(Names.NBT.GAME_DEALER));
        this.playerTurn.set(tag.getInteger(Names.NBT.GAME_PLAYER_TURN));
        if(tag.hasKey(Names.NBT.GAME_CURRENT_RAISER)) {
            this.lastRaiser.set(tag.getInteger(Names.NBT.GAME_CURRENT_RAISER));
        } else {
            this.lastRaiser.set(null);
        }
        this.pool.set(tag.getInteger(Names.NBT.GAME_POOL));
        this.bettingComplete.set(tag.getBoolean(Names.NBT.GAME_BETS_COMPLETE));
        this.blind.set(tag.getInteger(Names.NBT.GAME_BLIND));
        this.lastRaise.set(tag.getInteger(Names.NBT.GAME_LAST_RAISE));
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
        this.activePlayers.set(tag.getIntArray(Names.NBT.GAME_ACTIVE_PLAYERS));
    }
}

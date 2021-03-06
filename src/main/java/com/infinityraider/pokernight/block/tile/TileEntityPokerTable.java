package com.infinityraider.pokernight.block.tile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import com.infinityraider.pokernight.block.BlockPokerTable;
import com.infinityraider.pokernight.cardgame.poker.GamePhase;
import com.infinityraider.pokernight.cardgame.poker.IPokerGameProvider;
import com.infinityraider.pokernight.cardgame.poker.PokerGame;
import com.infinityraider.pokernight.cardgame.poker.PokerPlayer;
import com.infinityraider.pokernight.handler.GuiHandler;
import com.infinityraider.pokernight.reference.Names;
import com.infinityraider.pokernight.registry.BlockRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class TileEntityPokerTable extends TileEntityBase implements IPokerGameProvider, IDebuggable {
    private static final Map<EntityPlayer, TileEntityPokerTable> formations = Maps.newIdentityHashMap();

    public static void onPlayerRemoved(EntityPlayer player) {
        formations.remove(player);
    }

    /** Multi-block data */
    private boolean isFormed;
    private TileEntityPokerTable mainTile;
    private BlockPos mainTilePos;
    private EnumFacing.Axis axis;
    private int tableId;

    /** Poker player data */
    private UUID currentPlayerId;
    private PokerPlayer player;

    /** Poker game data */
    private PokerGame currentGame;
    private ItemStack bettingItem;
    private boolean allowStackAdding;
    private int bigBlind;
    private int blindIncrease;
    private List<TileEntityPokerTable> tables;

    public TileEntityPokerTable() {
        super();
    }

    public boolean isFormed() {
        return this.isFormed;
    }

    public boolean isMainTile() {
        return this.getPos().equals(this.mainTilePos);
    }

    public TileEntityPokerTable getMainTile() {
        if(this.isFormed()) {
            if(this.mainTile == null) {
                if(this.isMainTile()) {
                    this.mainTile = this;
                } else {
                    TileEntity tile = this.getWorld().getTileEntity(this.mainTilePos);
                    if (tile instanceof TileEntityPokerTable) {
                        this.mainTile = (TileEntityPokerTable) tile;
                    } else {
                        return null;
                    }
                }
            }
            return this.mainTile;
        } else {
            return this;
        }
    }

    public int getTableId() {
        return this.tableId;
    }

    public EnumFacing.Axis getTableOrientation() {
        return this.axis;
    }

    protected void setMainTile(TileEntityPokerTable mainTile, int id, EnumFacing.Axis axis) {
        if(mainTile != null) {
            this.tableId = id;
            this.axis = axis;
            this.isFormed = true;
            this.mainTile = mainTile;
            this.mainTilePos = mainTile.getPos();
            this.markForUpdate();
        }
    }

    public void formationClick(@Nonnull EntityPlayer player) {
        if(this.isFormed()) {
            return;
        }
        if(!formations.containsKey(player)) {
            formations.put(player, this);
            player.addChatComponentMessage(new TextComponentTranslation("pokernight:msg.started_formation"));
        } else {
            TileEntityPokerTable other = formations.get(player);
            this.tryFormation(other, player);
        }
    }

    public void tryFormation(TileEntityPokerTable other, @Nullable EntityPlayer player) {
        BlockPos ownPos = this.getPos();
        BlockPos otherPos = other.getPos();
        if(Math.abs(ownPos.getX() - otherPos.getX()) == 3 && Math.abs(ownPos.getZ() - otherPos.getZ()) == 1) {
            if(!checkBlocksInRage(otherPos, otherPos)) {
                if(player != null) {
                    player.addChatComponentMessage(new TextComponentTranslation("pokernight:msg.formation_failed.wrong_blocks"));
                }
            } else {
                this.doFormation(other, player);
            }
        } else if(Math.abs(ownPos.getX() - otherPos.getX()) == 1 && Math.abs(ownPos.getZ() - otherPos.getZ()) == 3) {
            if(!checkBlocksInRage(otherPos, otherPos)) {
                if(player != null) {
                    player.addChatComponentMessage(new TextComponentTranslation("pokernight:msg.formation_failed.wrong_blocks"));
                }
            } else {
                this.doFormation(other, player);
            }
        } else {
            if(player != null) {
                player.addChatComponentMessage(new TextComponentTranslation("pokernight:msg.formation_failed.wrong_range"));
            }
        }
        formations.remove(player);
    }

    protected boolean checkBlocksInRage(BlockPos a, BlockPos b) {
        int minX = Math.min(a.getX(), b.getX());
        int minY = Math.min(a.getY(), b.getY());
        int minZ = Math.min(a.getZ(), b.getZ());
        int maxX = Math.max(a.getX(), b.getX());
        int maxY = Math.max(a.getY(), b.getY());
        int maxZ = Math.max(a.getZ(), b.getZ());
        int dx = maxX - minX;
        int dy = maxY - minY;
        int dz = maxZ - minZ;
        BlockPos pos = new BlockPos(minX, minY, minZ);
        for(int x = 0; x <= dx; x++) {
            for(int y = 0; y <= dy; y++) {
                for(int z = 0; z <= dz; z++) {
                    IBlockState state = this.getWorld().getBlockState(pos.add(x, y, z));
                    if(state.getBlock() instanceof BlockPokerTable) {
                        for(InfinityProperty<Boolean> prop : BlockPokerTable.PROPERTIES) {
                            if(prop.getValue(state)) {
                                return false;
                            }
                        }
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected void  doFormation(TileEntityPokerTable other, @Nullable EntityPlayer player) {
        BlockPos ownPos = this.getPos();
        BlockPos otherPos = other.getPos();
        int minX = Math.min(ownPos.getX(), otherPos.getX());
        int minZ = Math.min(ownPos.getZ(), otherPos.getZ());
        int maxX = Math.max(ownPos.getX(), otherPos.getX());
        int maxZ = Math.max(ownPos.getZ(), otherPos.getZ());
        int dx = maxX - minX;
        int dz = maxZ - minZ;
        EnumFacing.Axis axis = dx > dz ? EnumFacing.Axis.X : EnumFacing.Axis.Z;
        BlockPos mainPos = new BlockPos(minX, ownPos.getY(), minZ);
        BlockPokerTable table = (BlockPokerTable) BlockRegistry.getInstance().blockPokerTable;
        TileEntityPokerTable mainTile = table.getTileEntity(this.getWorld(), mainPos);
        mainTile.tables = Lists.newArrayList();
        int id = 0;
        for(int x = 0; x <= dx; x++) {
            for(int z = 0; z <= dz; z++) {
                IBlockState state = table.getDefaultState();
                //x connections
                if(x == 0) {
                    state = BlockPokerTable.Properties.CONNECTION_EAST.applyToBlockState(state, true);
                    state = BlockPokerTable.Properties.CONNECTION_WEST.applyToBlockState(state, false);
                } else if(x == dx) {
                    state = BlockPokerTable.Properties.CONNECTION_EAST.applyToBlockState(state, false);
                    state = BlockPokerTable.Properties.CONNECTION_WEST.applyToBlockState(state, true);
                } else {
                    state = BlockPokerTable.Properties.CONNECTION_EAST.applyToBlockState(state, true);
                    state = BlockPokerTable.Properties.CONNECTION_WEST.applyToBlockState(state, true);
                }
                //z connections
                if(z == 0) {
                    state = BlockPokerTable.Properties.CONNECTION_SOUTH.applyToBlockState(state, true);
                    state = BlockPokerTable.Properties.CONNECTION_NORTH.applyToBlockState(state, false);
                } else if(z == dz) {
                    state = BlockPokerTable.Properties.CONNECTION_SOUTH.applyToBlockState(state, false);
                    state = BlockPokerTable.Properties.CONNECTION_NORTH.applyToBlockState(state, true);
                } else {
                    state = BlockPokerTable.Properties.CONNECTION_SOUTH.applyToBlockState(state, true);
                    state = BlockPokerTable.Properties.CONNECTION_NORTH.applyToBlockState(state, true);
                }
                //update
                BlockPos pos = mainPos.add(x, 0, z);
                TileEntityPokerTable tileTable = table.getTileEntity(this.getWorld(), pos);
                tileTable.setMainTile(mainTile, id, axis);
                this.getWorld().setBlockState(pos, state);
                mainTile.tables.add(tileTable);
                id++;
            }
        }
        if(player != null) {
            player.addChatComponentMessage(new TextComponentTranslation("pokernight:msg.formation_complete"));
        }
    }

    public void tryJoinGame(EntityPlayer player) {
        if(!this.getWorld().isRemote && player != null && this.isFormed() && !getCurrentPlayer().isPresent()) {
            this.currentPlayerId = player.getUniqueID();
        }
    }

    public void setGameRules(EntityPlayer player) {
        if(!this.isMainTile()) {
            TileEntityPokerTable main = this.getMainTile();
            if(main != null) {
                main.setGameRules(player);
            }
        }
        GuiHandler.getInstance().openGameRuleGui(player, this);
    }

    public void startGame() {

    }

    public boolean canChangeSettings() {
        return this.getCurrentGame().getPhase() == GamePhase.PRE_GAME;
    }

    public boolean trySetAllowStackAdding(boolean status) {
        if(this.canChangeSettings()) {
            this.allowStackAdding = status;
        }
        return this.allowStackAdding;
    }

    public int trySetBigBlind(int blind) {
        if(this.canChangeSettings()) {
            this.bigBlind = blind;
        }
        return blind;
    }

    public int trySetBigBlindIncrease(int amount) {
        if(this.canChangeSettings()) {
            this.blindIncrease = Math.max(0, amount);
        }
        return this.blindIncrease;
    }

    public ItemStack trySetBettingItem(ItemStack stack) {
        if(this.canChangeSettings()) {
            this.setBettingItem(stack);
        }
        return this.getBettingItem();
    }

    public Optional<EntityPlayer> getCurrentPlayer() {
        if(this.currentPlayerId == null) {
            return Optional.empty();
        } else {
            EntityPlayer player = this.getWorld().getPlayerEntityByUUID(this.currentPlayerId);
            return player == null ? Optional.empty() : Optional.of(player);
        }
    }

    protected Optional<PokerPlayer> getPokerPlayer() {
        if(this.player == null) {
            Optional<EntityPlayer> player = this.getCurrentPlayer();
            if (player.isPresent()) {
                this.player = new PokerPlayer(this.getCurrentGame(), this.getTableId());
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(this.player);
    }

    public ItemStack getBettingItem() {
        if(!this.isMainTile()) {
            TileEntityPokerTable main = this.getMainTile();
            if(main != null) {
                return main.getBettingItem();
            }
        }
        return this.bettingItem.copy();
    }

    public boolean setBettingItem(ItemStack stack) {
        if(!this.isMainTile()) {
            TileEntityPokerTable main = this.getMainTile();
            if(main != null) {
                return main.setBettingItem(stack);
            }
        }
        if(!this.isFormed()) {
            return false;
        }
        this.bettingItem = stack.copy();
        this.bettingItem.stackSize = 1;
        return true;
    }

    @Override
    public TileEntity getTile() {
        return this.getMainTile();
    }

    public PokerGame getCurrentGame() {
        if(!this.isMainTile()) {
            TileEntityPokerTable main = this.getMainTile();
            if(main != null) {
                return main.getCurrentGame();
            }
        }
        return this.currentGame;
    }

    @Override
    public Collection<PokerPlayer> getPlayers(PokerGame game) {
        if(!this.isMainTile()) {
            TileEntityPokerTable main = this.getMainTile();
            if(main != null) {
                return main.getPlayers(game);
            }
        }
        this.currentGame = game;
        return this.getTables().stream()
                .map(table -> getPokerPlayer())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public void onGameCompleted() {
        if(!this.isMainTile()) {
            TileEntityPokerTable main = this.getMainTile();
            if(main != null) {
                main.onGameCompleted();
            }
        }
        //TODO
    }

    @Override
    public int getBlind() {
        if(!this.isMainTile()) {
            TileEntityPokerTable main = this.getMainTile();
            if(main != null) {
                return main.getBlind();
            }
        }
        return this.bigBlind;
    }

    protected List<TileEntityPokerTable> getTables() {
        if(!this.isMainTile()) {
            TileEntityPokerTable main = this.getMainTile();
            if(main != null) {
                return main.getTables();
            }
        }
        if(!this.isFormed()) {
            return Collections.emptyList();
        } else {
            if(this.tables == null) {
                this.tables = Lists.newArrayList();
                int dx;
                int dz;
                if (this.getTableOrientation() == EnumFacing.Axis.X) {
                    dx = 4;
                    dz = 2;
                } else {
                    dx = 2;
                    dz = 4;
                }
                for (int x = 0; x < dx; x++) {
                    for (int z = 0; z < dz; z++) {
                        TileEntity tile = getWorld().getTileEntity(this.getPos().add(x, 0, z));
                        if (tile instanceof TileEntityPokerTable) {
                            this.tables.add((TileEntityPokerTable) tile);
                        }
                    }
                }
            }
            return this.tables;
        }
    }

    @Override
    protected void writeTileNBT(NBTTagCompound tag) {
        tag.setBoolean(Names.NBT.TABLE_FORMED, this.isFormed);
        tag.setInteger(Names.NBT.TABLE_ID, this.tableId);
        if(this.axis != null) {
            tag.setInteger(Names.NBT.TABLE_AXIS, this.axis.ordinal());
        }
        if(this.mainTilePos != null) {
            tag.setInteger(Names.NBT.TABLE_MAIN_X, this.mainTilePos.getX());
            tag.setInteger(Names.NBT.TABLE_MAIN_Y, this.mainTilePos.getY());
            tag.setInteger(Names.NBT.TABLE_MAIN_Z, this.mainTilePos.getZ());
        }
        if(this.currentPlayerId != null) {
            tag.setString(Names.NBT.TABLE_PLAYER_ID, this.currentPlayerId.toString());
            tag.setTag(Names.NBT.TABLE_PLAYER, this.player.writeToNBT());
        }
        if(this.bettingItem != null) {
            tag.setTag(Names.NBT.TABLE_BET_ITEM, this.bettingItem.writeToNBT(new NBTTagCompound()));
        }
        if(this.currentGame != null) {
            tag.setTag(Names.NBT.TABLE_GAME, this.currentGame.writeToNBT());
        }
    }

    @Override
    protected void readTileNBT(NBTTagCompound tag) {
        this.isFormed = tag.getBoolean(Names.NBT.TABLE_FORMED);
        this.tableId = tag.getInteger(Names.NBT.TABLE_ID);
        if(tag.hasKey(Names.NBT.TABLE_AXIS)) {
            this.axis = EnumFacing.Axis.values()[tag.getInteger(Names.NBT.TABLE_AXIS)];
        } else {
            this.axis = null;
        }
        if(tag.hasKey(Names.NBT.TABLE_MAIN_X) && tag.hasKey(Names.NBT.TABLE_MAIN_Y) && tag.hasKey(Names.NBT.TABLE_MAIN_Z)) {
            int x = tag.getInteger(Names.NBT.TABLE_MAIN_X);
            int y = tag.getInteger(Names.NBT.TABLE_MAIN_Y);
            int z = tag.getInteger(Names.NBT.TABLE_MAIN_Z);
            this.mainTilePos = new BlockPos(x, y, z);
        } else {
            this.mainTilePos = null;
        }
        if(tag.hasKey(Names.NBT.TABLE_PLAYER_ID)) {
            this.currentPlayerId = UUID.fromString(tag.getString(Names.NBT.TABLE_PLAYER_ID));
            this.player = this.getCurrentGame().getPokerPlayerFromId(this.getTableId()).readFromNBT(tag.getCompoundTag(Names.NBT.TABLE_PLAYER));
        } else {
            this.currentPlayerId = null;
            this.player = null;
        }
        if(tag.hasKey(Names.NBT.TABLE_BET_ITEM)) {
            this.bettingItem = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(Names.NBT.TABLE_BET_ITEM));
        } else {
            this.bettingItem = null;
        }
        if(this.isMainTile()) {
            if (tag.hasKey(Names.NBT.TABLE_GAME)) {
                this.currentGame = new PokerGame(this).readFromNBT(tag.getCompoundTag(Names.NBT.TABLE_GAME));
            } else {
                this.currentGame = new PokerGame(this);
            }
        } else {
            this.currentGame = null;
        }
    }

    @Override
    public void addServerDebugInfo(List<String> lines) {
        this.addDebugInfo(lines);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addClientDebugInfo(List<String> lines) {
        this.addDebugInfo(lines);
    }

    public void addDebugInfo(List<String> lines) {
        IBlockState state = this.getState();
        lines.add("formed: " + this.isFormed());
        lines.add("isMain: " + this.isMainTile());
        if(this.mainTilePos != null) {
            lines.add("main pos: (" + this.mainTilePos.getX() + ", " + this.mainTilePos.getY() + ", " + this.mainTilePos.getZ() + ")");
        } else {
            lines.add("main pos: n.a.");
        }
        if(state.getBlock() instanceof BlockPokerTable) {
            lines.add("NORTH: " + BlockPokerTable.Properties.CONNECTION_NORTH.getValue(state));
            lines.add("EAST: " + BlockPokerTable.Properties.CONNECTION_EAST.getValue(state));
            lines.add("SOUTH: " + BlockPokerTable.Properties.CONNECTION_SOUTH.getValue(state));
            lines.add("WEST: " + BlockPokerTable.Properties.CONNECTION_WEST.getValue(state));
        }
    }
}

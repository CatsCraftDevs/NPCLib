package npc.brainsynder;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class NPC {

    private static AtomicInteger atomicInteger;
    private final String hideTeam;
    private final int entityID; //unique entityID the server holds to find/modify existing entities. Be careful when assigning values that they do not overlap
    private GameProfile profile;
    private final NPCMetaData metadata = new NPCMetaData();
    private final Location location;
    private boolean tabListVisible = true;
    private NPCPing ping = NPCPing.FIVE_BARS;
    private NPCGamemode gamemode = NPCGamemode.CREATIVE;
    private String displayName;

    static {
        try {
            Field field = Entity.class.getDeclaredField("b");
            field.setAccessible(true);
            atomicInteger = (AtomicInteger) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public NPC(UUID uuid, Location location, String displayName) {
        this.entityID = atomicInteger.incrementAndGet();

        this.profile = new GameProfile(uuid, displayName);
        this.location = location;
        this.displayName = displayName;
        this.hideTeam = "hide-" + Integer.toHexString(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
    }

    public NPC(Location location, String displayName) {
        this(UUID.randomUUID(), location, displayName);
    }

    public void spawnNPC(Player player) {
        this.addToTabList(player);
        this.sendPacket(player, this.getEntitySpawnPacket());
        this.updateMetadata(player);
        if (!this.tabListVisible) this.removeFromTabList(player);
    }

    public void destroyNPC(Player player) {
        this.sendPacket(player, this.getPlayerInfoPacket(NPCPlayerInfo.REMOVE_PLAYER));
        this.sendPacket(player, this.getEntityDestroyPacket());
    }

    public void reloadNPC(Player player) {
        this.destroyNPC(player);
        this.spawnNPC(player);
    }

    public void teleportNPC(Player player, Location location, boolean onGround) {
        this.location.setX(location.getX());
        this.location.setY(location.getY());
        this.location.setZ(location.getZ());
        this.location.setPitch(location.getPitch());
        this.location.setYaw(location.getYaw());
        this.sendPacket(player, this.getEntityTeleportPacket(onGround));
    }

    public void updateMetadata(Player player) {
        this.sendPacket(player, this.getEntityMetadataPacket());
    }

    public void updateGameMode(Player player) {
        this.sendPacket(player, this.getPlayerInfoPacket(NPCPlayerInfo.UPDATE_GAME_MODE));
    }

    public void updatePing(Player player) {
        this.sendPacket(player, this.getPlayerInfoPacket(NPCPlayerInfo.UPDATE_LATENCY));
    }

    public void updateTabListName(Player player) {
        this.sendPacket(player, this.getPlayerInfoPacket(NPCPlayerInfo.UPDATE_DISPLAY_NAME));
    }

    public void removeFromTabList(Player player) {
        this.sendPacket(player, this.getPlayerInfoPacket(NPCPlayerInfo.REMOVE_PLAYER));
    }

    public void addToTabList(Player player) {
        this.sendPacket(player, this.getPlayerInfoPacket(NPCPlayerInfo.ADD_PLAYER));
    }

    public void playAnimation(Player player, NPCAnimation animation) {
        this.sendPacket(player, this.getEntityAnimationPacket(animation));
    }

    public void rotateHead(Player player, float pitch, float yaw) {
        this.location.setPitch(pitch);
        this.location.setYaw(yaw);
        this.sendPacket(player, this.getEntityLookPacket());
        this.sendPacket(player, this.getEntityHeadRotatePacket());
    }

    public void setTabListName(String name) {
        this.displayName = name;
    }

    public void setEquipment(Player player, NPCItemSlot slot, org.bukkit.inventory.ItemStack itemStack) {
        this.sendPacket(player, this.getEntityEquipmentPacket(slot.getSlot(), CraftItemStack.asNMSCopy(itemStack)));
    }

    public void setPassenger(Player player, int... entityIDs) {
        this.sendPacket(player, getEntityAttachPacket(entityIDs));
    }

    private void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) (player)).getHandle().b.sendPacket(packet);
    }

    public void setNameTagVisibility(Player player, boolean show) {
        ScoreboardTeam team = new ScoreboardTeam(new Scoreboard(), this.hideTeam);
        if (show) {
            PacketPlayOutScoreboardTeam leavePacket = PacketPlayOutScoreboardTeam.a(team, this.profile.getName(), PacketPlayOutScoreboardTeam.a.b);
            this.sendPacket(player, leavePacket);
        } else {
            team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.b);
            PacketPlayOutScoreboardTeam createPacket = PacketPlayOutScoreboardTeam.a(team, true);
            PacketPlayOutScoreboardTeam joinPacket = PacketPlayOutScoreboardTeam.a(team, this.profile.getName(), PacketPlayOutScoreboardTeam.a.a);
            this.sendPacket(player, createPacket);
            this.sendPacket(player, joinPacket);
        }
    }
    
    private PacketPlayOutMount getEntityAttachPacket(int[] entityIDs) {
        return NPCUtilities.createDataSerializer(data -> {
            data.d(this.entityID);
            data.a(entityIDs);
            return new PacketPlayOutMount(data);
        });
    }

    private PacketPlayOutEntity.PacketPlayOutEntityLook getEntityLookPacket() {
        return new PacketPlayOutEntity.PacketPlayOutEntityLook(this.entityID, (byte) ((int) (this.location.getYaw() * 256.0F / 360.0F)), (byte) ((int) (this.location.getPitch() * 256.0F / 360.0F)), true);
    }

    private PacketPlayOutEntityTeleport getEntityTeleportPacket(boolean onGround) {
        return NPCUtilities.createDataSerializer(data -> {
            data.d(this.entityID);
            data.writeDouble(this.location.getX());
            data.writeDouble(this.location.getY());
            data.writeDouble(this.location.getZ());
            data.writeByte((byte) ((int) (this.location.getYaw() * 256.0F / 360.0F)));
            data.writeByte((byte) ((int) (this.location.getPitch() * 256.0F / 360.0F)));
            data.writeBoolean(onGround);
            return new PacketPlayOutEntityTeleport(data);
        });
    }

    private PacketPlayOutEntityHeadRotation getEntityHeadRotatePacket() {
        return NPCUtilities.createDataSerializer(data -> {
            data.d(this.entityID);
            data.writeByte((byte) ((int) (this.location.getYaw() * 256.0F / 360.0F)));
            return new PacketPlayOutEntityHeadRotation(data);
        });
    }

    private PacketPlayOutEntityEquipment getEntityEquipmentPacket(EnumItemSlot slot, ItemStack itemStack) {
        return new PacketPlayOutEntityEquipment(this.entityID, Arrays.asList(new Pair<EnumItemSlot, ItemStack>(slot, itemStack)));
    }

    private PacketPlayOutAnimation getEntityAnimationPacket(NPCAnimation animation) {
        return NPCUtilities.createDataSerializer((data) -> {
            data.d(this.entityID);
            data.writeByte((byte) animation.getType());
            return new PacketPlayOutAnimation(data);
        });
    }

    private PacketPlayOutEntityDestroy getEntityDestroyPacket() {
        return new PacketPlayOutEntityDestroy(this.entityID);
    }

    private PacketPlayOutEntityMetadata getEntityMetadataPacket() {
        return NPCUtilities.createDataSerializer((data) -> {
            data.d(this.entityID);
            DataWatcher.a(this.metadata.getList(), data);
            return new PacketPlayOutEntityMetadata(data);
        });
    }

    private PacketPlayOutNamedEntitySpawn getEntitySpawnPacket() {
        return NPCUtilities.createDataSerializer((data) -> {
            data.d(this.entityID);
            data.a(this.profile.getId());
            data.writeDouble(this.location.getX());
            data.writeDouble(this.location.getY());
            data.writeDouble(this.location.getZ());
            data.writeByte((byte) ((int) (this.location.getYaw() * 256.0F / 360.0F)));
            data.writeByte((byte) ((int) (this.location.getPitch() * 256.0F / 360.0F)));
            return new PacketPlayOutNamedEntitySpawn(data);
        });
    }

    public PacketPlayOutPlayerInfo getPlayerInfoPacket(NPCPlayerInfo playerInfo) {
        return NPCUtilities.createDataSerializer((data) -> {
            PacketPlayOutPlayerInfo.EnumPlayerInfoAction action = playerInfo.getPlayerInfo();
            PacketPlayOutPlayerInfo.PlayerInfoData playerInfoData = new PacketPlayOutPlayerInfo.PlayerInfoData(this.profile, this.ping.getMilliseconds(), this.gamemode.getGamemode(), CraftChatMessage.fromString(this.displayName)[0]);
            List<PacketPlayOutPlayerInfo.PlayerInfoData> list = Arrays.asList(playerInfoData);
            data.a(playerInfo.getPlayerInfo());
            Method method = playerInfo.getPlayerInfo().getDeclaringClass().getDeclaredMethod("a", PacketDataSerializer.class, PacketPlayOutPlayerInfo.PlayerInfoData.class);
            method.setAccessible(true);
            data.a(list, (a, b) -> NPCUtilities.unsafe(() -> method.invoke(action, a, b)));
            return new PacketPlayOutPlayerInfo(data);
        });
    }

    public int getEntityID() {
        return entityID;
    }

    public GameProfile getProfile() {
        return profile;
    }

    public NPCMetaData getMetadata() {
        return metadata;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isTabListVisible() {
        return tabListVisible;
    }

    public NPCPing getPing() {
        return ping;
    }

    public NPCGamemode getGameMode() {
        return gamemode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setSkin(NPCSkinTextures skinTextures) {
        this.profile.getProperties().put("textures", new Property("textures", skinTextures.getTexture(), skinTextures.getSignature()));
    }

    public void setASyncSkinByUsername(Plugin plugin, Player player, String username) {
        this.setASyncSkinByUsername(plugin, player, username, null);
    }

    public void setASyncSkinByUUID(Plugin plugin, Player player, UUID uuid) {
        this.setASyncSkinByUUID(plugin, player, uuid, null);
    }

    public void setASyncSkinByUsername(Plugin plugin, Player player, String username, BiConsumer<Boolean, NPC> callback) {
        NPCSkinTextures.getByUsername(plugin, username, (success, skin) -> setASyncSkin(success, skin, player, callback));
    }

    public void setASyncSkinByUUID(Plugin plugin, Player player, UUID uuid, BiConsumer<Boolean, NPC> callback) {
        NPCSkinTextures.getByUUID(plugin, uuid, (success, skin) -> setASyncSkin(success, skin, player, callback));
    }

    private void setASyncSkin(boolean success, NPCSkinTextures skin, Player player, BiConsumer<Boolean, NPC> callback) {
        if (success) {
            this.setSkin(skin);
            this.reloadNPC(player);
        }
        callback.accept(success, this);
    }

    public void setTabListVisible(boolean tabListVisible) {
        this.tabListVisible = tabListVisible;
    }

    public void setPing(NPCPing ping) {
        this.ping = ping;
    }

    public void setGameMode(NPCGamemode gamemode) {
        this.gamemode = gamemode;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        GameProfile swapProfile = new GameProfile(this.profile.getId(), displayName);
        swapProfile.getProperties().putAll(this.profile.getProperties());
        this.profile = swapProfile;
    }
}
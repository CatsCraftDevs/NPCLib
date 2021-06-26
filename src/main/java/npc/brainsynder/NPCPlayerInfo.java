package npc.brainsynder;

import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;

public enum NPCPlayerInfo {
    ADD_PLAYER(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a),
    UPDATE_GAME_MODE(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.b),
    UPDATE_LATENCY(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.c),
    UPDATE_DISPLAY_NAME(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.d),
    REMOVE_PLAYER(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e);

    private final PacketPlayOutPlayerInfo.EnumPlayerInfoAction playerInfo;

    NPCPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction a) {
        this.playerInfo = a;
    }

    public PacketPlayOutPlayerInfo.EnumPlayerInfoAction getPlayerInfo() {
        return playerInfo;
    }
}

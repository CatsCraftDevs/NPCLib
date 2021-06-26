package npc.brainsynder;

import net.minecraft.world.level.EnumGamemode;

public enum NPCGamemode {
    SURVIVAL(EnumGamemode.a),
    CREATIVE(EnumGamemode.b),
    ADVENTURE(EnumGamemode.c),
    SPECTATOR(EnumGamemode.d);

    private final EnumGamemode gamemode;

    NPCGamemode(EnumGamemode gamemode) {
        this.gamemode = gamemode;
    }

    public EnumGamemode getGamemode() {
        return gamemode;
    }
}
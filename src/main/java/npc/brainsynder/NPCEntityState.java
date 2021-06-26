package npc.brainsynder;

import java.util.ArrayList;
import java.util.List;

public enum NPCEntityState {
    ON_FIRE(0x01),
    @Deprecated CROUCHING(0x02),
    @Deprecated UNUSED(0x04),
    SPRINTING(0x08),
    SWIMMING(0x10),
    INVISIBLE(0x20),
    GLOWING(0x40),
    FLYING(0x80),
    ALL(0xFF);

    private final int mask;

    NPCEntityState(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return mask;
    }

    public static int createMask(NPCEntityState... entityStates) {
        int mask = 0;
        for (NPCEntityState entityState : entityStates) {
            mask |= entityState.mask;
        }
        return mask;
    }

    public static NPCEntityState[] fromMask(int mask) {
        List<NPCEntityState> list = new ArrayList<>();
        for (NPCEntityState entityState : values()) {
            if ((entityState.mask & mask) == entityState.mask) {
                list.add(entityState);
            }
        }
        return list.toArray(new NPCEntityState[list.size()]);
    }
}

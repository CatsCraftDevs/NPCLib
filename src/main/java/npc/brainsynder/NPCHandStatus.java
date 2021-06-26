package npc.brainsynder;

import java.util.ArrayList;
import java.util.List;

public enum NPCHandStatus {
    MAIN_HAND(0x00),
    HAND_ACTIVE(0x01),
    OFF_HAND(0x02),
    RIPTIDE_SPIN_ATTACK(0x04),
    ALL(0x07);

    private final int mask;

    NPCHandStatus(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return mask;
    }

    public static int createMask(NPCHandStatus... handStatuses) {
        int mask = 0;
        for (NPCHandStatus handStatus : handStatuses) {
            mask |= handStatus.mask;
        }
        return mask;
    }

    public static NPCHandStatus[] fromMask(int mask) {
        List<NPCHandStatus> list = new ArrayList<>();
        for (NPCHandStatus handStatus : values()) {
            if ((handStatus.mask & mask) == handStatus.mask) {
                list.add(handStatus);
            }
        }
        return list.toArray(new NPCHandStatus[list.size()]);
    }
}
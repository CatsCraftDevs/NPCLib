package npc.brainsynder;

import java.util.ArrayList;
import java.util.List;

public enum NPCSkinStatus {
    CAPE_ENABLED(0x01),
    JACKET_ENABLED(0x02),
    LEFT_SLEEVE_ENABLED(0x04),
    RIGHT_SLEEVE_ENABLED(0x08),
    LEFT_PANTS_LEG_ENABLED(0x10),
    RIGHT_PANTS_LEG_ENABLED(0x20),
    HAT_ENABLED(0x40),
    @Deprecated UNUSED(0x80),
    ALL_ENABLED(0xFF);

    private final int mask;

    NPCSkinStatus(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return mask;
    }

    public static int createMask(NPCSkinStatus... skinStatuses) {
        int mask = 0;
        for (NPCSkinStatus handStatus : skinStatuses) {
            mask |= handStatus.mask;
        }
        return mask;
    }

    public static NPCSkinStatus[] fromMask(int mask) {
        List<NPCSkinStatus> list = new ArrayList<>();
        for (NPCSkinStatus skinStatus : values()) {
            if ((skinStatus.mask & mask) == skinStatus.mask) {
                list.add(skinStatus);
            }
        }
        return list.toArray(new NPCSkinStatus[list.size()]);
    }
}
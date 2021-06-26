package npc.brainsynder;

public enum NPCHand {
    LEFT(0),
    RIGHT(1);

    private final int type;

    NPCHand(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static NPCHand fromByte(byte type) {
        for (NPCHand hand : values()) {
            if (type == hand.type) {
                return hand;
            }
        }
        return null;
    }
}
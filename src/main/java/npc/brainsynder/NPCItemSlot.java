package npc.brainsynder;

import net.minecraft.world.entity.EnumItemSlot;

public enum NPCItemSlot {
    MAIN_HAND(EnumItemSlot.a),
    OFF_HAND(EnumItemSlot.b),
    BOOTS(EnumItemSlot.c),
    LEGGINGS(EnumItemSlot.d),
    CHESTPLATE(EnumItemSlot.e),
    HELMET(EnumItemSlot.f);

    private final EnumItemSlot slot;

    NPCItemSlot(EnumItemSlot slot) {
        this.slot = slot;
    }

    public EnumItemSlot getSlot() {
        return slot;
    }
}
package npc.brainsynder;

import net.minecraft.world.entity.EntityPose;

public enum NPCPose {
    STANDING(EntityPose.a),
    FALL_FLYING(EntityPose.b),
    SLEEPING(EntityPose.c),
    SWIMMING(EntityPose.d),
    SPIN_ATTACK(EntityPose.e),
    CROUCHING(EntityPose.f),
    LONG_JUMPING(EntityPose.g),
    DYING(EntityPose.h);

    private final EntityPose pose;

    NPCPose(EntityPose pose) {
        this.pose = pose;
    }

    public EntityPose getPose() {
        return pose;
    }

    public static NPCPose fromPose(EntityPose entityPose) {
        for (NPCPose pose : values()) {
            if (entityPose == pose.pose) {
                return pose;
            }
        }
        return null;
    }
}
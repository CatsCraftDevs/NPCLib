package npc.brainsynder;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;

public class NPCUtilities {
    public static void unsafe(UnsafeRunnable run) {
        try {
            run.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T createDataSerializer(UnsafeFunction<PacketDataSerializer, T> callback) {
        PacketDataSerializer data = new PacketDataSerializer(Unpooled.buffer());
        T result = null;
        try {
            result = callback.apply(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            data.release();
        }
        return result;
    }
}

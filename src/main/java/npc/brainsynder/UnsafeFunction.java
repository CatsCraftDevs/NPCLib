package npc.brainsynder;

@FunctionalInterface
public interface UnsafeFunction<K, T> {
    T apply(K k) throws Exception;
}
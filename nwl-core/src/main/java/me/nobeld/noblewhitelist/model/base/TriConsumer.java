package me.nobeld.noblewhitelist.model.base;

public interface TriConsumer<K, V, S> {
    void accept(K k, V v, S s);
}

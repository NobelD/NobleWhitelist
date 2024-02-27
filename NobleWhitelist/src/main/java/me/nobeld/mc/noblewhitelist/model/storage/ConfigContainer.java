package me.nobeld.mc.noblewhitelist.model.storage;

public record ConfigContainer<T>(String path, T def) {
}

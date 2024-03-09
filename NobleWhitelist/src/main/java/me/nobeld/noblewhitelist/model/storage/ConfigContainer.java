package me.nobeld.noblewhitelist.model.storage;

public record ConfigContainer<T>(String path, T def) {
}
